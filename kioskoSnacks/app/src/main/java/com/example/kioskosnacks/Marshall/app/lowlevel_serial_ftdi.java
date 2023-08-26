package com.example.kioskosnacks.Marshall.app;


import android.util.Log;

import com.bitmick.marshall.interfaces.lowlevel_i;
import com.bitmick.utils.ByteArrayUtils;
import com.bitmick.utils.Utils;
import com.example.kioskosnacks.Marshall.driver.UsbSerialPort;

import java.io.IOException;

public class lowlevel_serial_ftdi implements lowlevel_i {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final String TAG = lowlevel_serial_ftdi.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private link_events_t m_link_events;

    private int m_serial_port_baud_rate;

    private boolean m_client_thread_running;
    private Thread m_client_thread;

    public static UsbSerialPort m_serial_port_ftdi;

    // ===========================================================
    // Abstract methods
    // ===========================================================

    // ===========================================================b
    // Constructors
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass/Interfaces (and supporting methods)
    // ===========================================================

    @Override
    public void init(Object i_f, Object i_f_params) {
        m_serial_port_ftdi = (UsbSerialPort) i_f;
        m_serial_port_baud_rate = (Integer) i_f_params;
    }

    @Override
    public void register_link_events(link_events_t events) {
        m_link_events = events;
    }

    @Override
    public void start() {

        try {
            if (m_serial_port_ftdi != null && m_serial_port_baud_rate > 0) {
                m_serial_port_ftdi.setParameters(m_serial_port_baud_rate, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            }
        } catch (IOException e) {
            e.printStackTrace();

            m_serial_port_ftdi = null;
        }

        if (m_serial_port_ftdi == null) {
            Log.d(TAG, "failed opening serial port");

            return;
        }

        m_client_thread = new Thread(new ClientAltHandlingRunnable());
        m_client_thread.setPriority(Thread.MAX_PRIORITY);
        m_client_thread.start();

        Log.d(TAG, "started");

    }

    @Override
    public void stop() {

        if (m_serial_port_ftdi != null) {
            try {
                m_serial_port_ftdi.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void reset() {

    }

    @Override
    public boolean transmit(byte[] data, int len) {
        try {
            byte[] buf = new byte[len];
            System.arraycopy(data, 0, buf, 0, len);
            m_serial_port_ftdi.write(buf, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onLinkTimerTick(long ms) {
    }

    // ===========================================================
    // Methods
    // ===========================================================

    //////////////////////////////////////////////////////////////////////////////////////////////
    // class name:
    // notes:
    /////////////////////////////////////////////////////////////////////////////////////////////
    private class ClientAltHandlingRunnable implements Runnable {

        private int head;
        private int tail;
        private byte[] rx_buf;
        private boolean thread_running;
        byte[] temp_buf = new byte[1024];


        public void stop() {
            thread_running = true;
        }

        private void reset() {
            head = 0;
            tail = 0;
        }

        private void serial_reset() {
            head = 0;
            tail = 0;
        }


        private int serial_read(byte[] buf, int offset) {
            int read_len = 0;

            synchronized (m_serial_port_ftdi) {
                int to_read = Math.min(temp_buf.length, (buf.length - offset));
                if (to_read > 0) {
                    try {
                        read_len = m_serial_port_ftdi.read(temp_buf, 1000); //buf, offset, to_read);

                        System.arraycopy(temp_buf, 0, buf, offset, read_len);
                    } catch (Exception e) {
                        e.printStackTrace();
                        read_len = -1;
                    }
                }
            }

            return read_len;
        }

        @Override
        public void run() {
            m_client_thread_running = true;

            byte[] rx_buf = new byte[4096 * 4];

            serial_reset();

            while (m_client_thread_running) {
                int read_len = serial_read(rx_buf, head);

                if (read_len >= 0) {
                    head += read_len;

                    if (head != tail) {
                        Boolean cont = true;

                        // try to handle as many packets that might be inside buffer
                        do {
                            int exp_len = ByteArrayUtils.byteArrToShort(rx_buf, tail) + 2;
                            int rcv_len = head - tail;

                            if (exp_len < 9 || exp_len > 512) {
                                serial_reset();
                            } else if (rcv_len >= exp_len) {


                                // pass packet up
                                if (!m_link_events.onReceive(rx_buf, tail, exp_len)) {
                                    serial_reset();
                                } else {
                                    tail += exp_len;

                                    // wrap around, only after packet processed succesfully
                                    if ((rx_buf.length - tail) <= 412 && tail == head)
                                        serial_reset();
                                }

                            }

                            cont = (head != tail) && (rcv_len >= exp_len);
                        } while (cont);
                    } else {
                        Utils.threadSleep(1);
                    }
                } else {
                    m_client_thread_running = false;
                }
            }

            lowlevel_serial_ftdi.this.stop();
        }
    }
}
