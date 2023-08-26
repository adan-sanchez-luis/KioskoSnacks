package com.example.kioskosnacks.Marshall;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.bitmick.marshall.models.vmc_configuration;
import com.bitmick.marshall.vmc.vmc_framework;
import com.bitmick.marshall.vmc.vmc_vend_t;
import com.example.kioskosnacks.Marshall.app.PrintDataGlobal;
import com.example.kioskosnacks.Marshall.app.lowlevel_serial_ftdi;
import com.example.kioskosnacks.Marshall.driver.UsbSerialDriver;
import com.example.kioskosnacks.Marshall.driver.UsbSerialPort;
import com.example.kioskosnacks.Marshall.driver.UsbSerialProber;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Marshall2 implements Serializable {

    private final static String TAG = Marshall2.class.getSimpleName();
    private static String USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static vmc_framework m_vmc;
    private static vmc_configuration vmc_config;
    public static vmc_vend_t.vend_session_t session;
    //private static WriterLog writerLog;
    private static UsbSerialPort device;
    public static MutableLiveData<CashlessResponse.MessageType> observer;

    public Marshall2() {
        //writerLog = new WriterLog(activity);
    }

    public void startObserver(){
        observer = new MutableLiveData<>();
    }

    public void reset() {
        observer.setValue(CashlessResponse.MessageType.DEFAULT);
    }

    public MutableLiveData<CashlessResponse.MessageType> getObserver() {
        if (observer==null)
            observer.postValue(CashlessResponse.MessageType.DEFAULT);

        return observer;
    }

    public static UsbSerialPort usb2SerialEnum(Context context) {

        //writerLog.registerLog(TAG, "Iniciando conexcion con el puerto serial");
        device = null;

        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            //writerLog.registerLog(TAG, "No se encontron ningun puerto serial conectado");
            return null;
        }
        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        if (driver != null) {
            UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
            if (connection == null) {
                //writerLog.registerLog(TAG, "Pidiendo perisos para acceder al serial");
                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
                manager.requestPermission(driver.getDevice(), mPermissionIntent);
                // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
                return null;
            }

            try {
                device = driver.getPorts().get(0);
                //writerLog.registerLog("VID", device.getDriver().getDevice().getVendorId() + "");
                //writerLog.registerLog("PID", device.getDriver().getDevice().getProductId() + "");
                //writerLog.registerLog("devide", device.getDriver().getDevice().getDeviceId() + "");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //writerLog.registerLog("version", device.getDriver().getDevice().getVersion() + "");
                }
                //writerLog.registerLog("manufacture", device.getDriver().getDevice().getManufacturerName() + "");
                //writerLog.registerLog("device name", device.getDriver().getDevice().getDeviceName() + "");
                //writerLog.registerLog("hasPermision", manager.hasPermission((UsbDevice) device) + "");

                device.open(connection);
            } catch (IOException e) {
                //writerLog.registerLog(TAG, "Error al establecer conexcion con el serial: " + e.getMessage());
                Log.d(TAG, e.toString());
            }
        }

        return device;
    }

    public boolean VPOSConfig(Context context) {
        try {
            //writerLog.registerLog(TAG, "Se inicio la configuracion del cashless");
            UsbSerialPort port = usb2SerialEnum(context);
            if (port != null) {
                m_vmc = vmc_framework.getInstance();
                vmc_config = new vmc_configuration();

                vmc_config.port_vpos = port;
                vmc_config.port_vpos_baud = 115200;

                vmc_config.model = "android-marshall-demo";
                vmc_config.serial = "01234567";
                vmc_config.sw_ver = "1.0.0.0";

                vmc_config.multi_session_support = false;
                vmc_config.price_not_final_support = false;
                vmc_config.multi_vend_support = true;
                vmc_config.reader_always_on = false;
                vmc_config.always_idle = true;

                vmc_config.mifare_approved_by_vmc_support = false;
                vmc_config.mag_card_approved_by_vmc_support = false;

                vmc_config.debug = false;

                m_vmc.link.set_lowlevel(new lowlevel_serial_ftdi());
                m_vmc.link.configure(vmc_config);
                m_vmc.vend.register_callbacks(new vmc_vend_events());
                m_vmc.socket.register_callbacks(null);
                m_vmc.start();

            }
            return m_vmc.link.is_ready();
        } catch (Exception ex) {
            //writerLog.registerLog(TAG, "Hubo un error en la configuracion: " + ex.getMessage());
            ex.printStackTrace();
        }

        return false;
    }

    public void disconect() {
        if (device != null) {
            try {
                m_vmc.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean IsReady() {
        boolean validacion = false;

        try {
            if (m_vmc.link.is_ready()) {
                validacion = true;
            } else {
                validacion = false;
            }
        } catch (Exception e) {
            //e.printStackTrace();
            //writerLog.registerLog(TAG, "Error al obtener estado del cashless: " + e.getMessage());
        }

        return validacion;
    }

    public void Restart(Context context, Activity activity) {
        //writerLog.registerLog(TAG, "Reiniciando cashless");
        m_vmc.link.stop();
        //writerLog.registerLog(TAG, "Deteniendo el proceso");
        try {
            Contador contador = new Contador(5000, 100, context, activity);
            contador.start();
        } catch (Exception e) {
            e.printStackTrace();
            //writerLog.registerLog(TAG, "Hubo un problema en el reinicio del cashless: " + e.getMessage());
        }
    }

    public void cancelarVenta() {
        m_vmc.vend.session_cancel();
        //writerLog.registerLog(TAG, "Venta cancelada");
    }

    /**
     * Actualmente no funcionaa para recuperar el estado actual del cashless
     */
    public int getStatus() {
        //m_vmc.vend.session_cancel(); //cancela la transaccion en proceso
        System.out.println("codigo actual: " + m_vmc.link.get_config().currency_code + "    " + new String(m_vmc.link.get_config().currency_code, StandardCharsets.UTF_8));
        System.out.println(m_vmc.link.get_configuration().port_vpos);
        System.out.println(new String(m_vmc.link.get_config().vpos_serial, StandardCharsets.UTF_8));
        return m_vmc.vend.get_session_type();
    }

    public static class vmc_vend_events implements vmc_vend_t.vend_callbacks_t {


        @Override
        public void onReady(vmc_vend_t.vend_session_t vend_session_t) {
            //writerLog.registerLog(TAG, "marshall status: se inicio el cashless");
            System.out.println("marshall status: se inicio el cashless");
        }

        @Override
        public void onSessionBegin(int funds_avail) {
            // example: approve mifar/mag card externally (vmc authenticates)
            if (vmc_config.mag_card_approved_by_vmc_support || vmc_config.mifare_approved_by_vmc_support)
                m_vmc.vend.client_gateway_auth(true);
            //writerLog.registerLog(TAG, "marshall status: inserto tarjeta");
            System.out.println("marshall status: inserto tarjeta");
        }

        @Override
        public void onTransactionInfo(vmc_vend_t.vend_session_data_t vend_session_data_t) {
            System.out.println(vend_session_data_t.vmc_auth_status);
            System.out.println("marshall status: recibio la data:");
            //writerLog.registerLog(TAG, "marshall status: recibio la data");
        }

        @Override
        public boolean onVendApproved(vmc_vend_t.vend_session_t session) {
            PrintDataGlobal printDataGlobal = PrintDataGlobal.obtenerDatosPrinter();
            //writerLog.registerLog(TAG, "marshall status: venta aprobada");
            System.out.println("marshall status: venta aprobada");
            observer.postValue(CashlessResponse.MessageType.VendApproved);
            long transaction_id = session.data.transaction_id;
            printDataGlobal.setIdTransaccion(transaction_id);
            return true;
        }

        @Override
        public void onVendDenied(vmc_vend_t.vend_session_t session) {
            //writerLog.registerLog(TAG, "marshall status: venta denegada");
            observer.postValue(CashlessResponse.MessageType.VendDenied);
            System.out.println("marshall status: venta denegada");
        }

        @Override
        public void onSettlement(boolean success) {
            //writerLog.registerLog(TAG, "marshall status: agregando items?????");
            System.out.println("marshall status: agregando items?????");
        }
    }

    public void Maintenance() {
        m_vmc.link.stop();
        //writerLog.registerLog(TAG, "Deteniendo el la comunicacion con el cashless");
    }

    public void StartSession() {
        m_vmc.vend.session_start(vmc_vend_t.session_type_credit_e);
        //writerLog.registerLog(TAG, "Empiza el proceso de venta");
    }

    public void CloseSession() {
        m_vmc.vend.session_close(session);
        //writerLog.registerLog(TAG, "Cerrando cesion???????");
    }

    public void Vend_Start(ArrayList<vmc_vend_t.vend_item_t> list) {
        session = new vmc_vend_t.vend_session_t(list);
        m_vmc.vend.vend_request(session);
        for (vmc_vend_t.vend_item_t vend_item_t : list) {
            //writerLog.registerLog(TAG, "Se agreo un producto $" + vend_item_t.price);
        }
    }

    protected class Contador extends CountDownTimer {
        private Context context;

        protected Contador(long millisInFuture, long countDownInterval, Context context, Activity activity) {
            super(millisInFuture, countDownInterval);
            this.context = context;
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            //writerLog.registerLog(TAG, "Iniciando...");
            VPOSConfig(context);
        }
    }
}
