package com.example.kioskosnacks.Log;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

public class FileLogTree extends Timber.Tree {

    private final File logFile;

    public FileLogTree(File logFile) {
        this.logFile = logFile;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        try {
            // Abrir un flujo de salida para escribir en el archivo
            FileOutputStream fileOutputStream = new FileOutputStream(logFile, true);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String currentTime = sdf.format(new Date());

            // Escribir el registro en el archivo
            writer.write(String.format("[%s - %s] %s\n", getLogLevelString(priority), currentTime, message));

            // Cerrar el flujo de salida
            writer.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getLogLevelString(int priority) {
        switch (priority) {
            case Log.VERBOSE:
                return "VERBOSE";
            case Log.DEBUG:
                return "DEBUG";
            case Log.INFO:
                return "INFO";
            case Log.WARN:
                return "WARN";
            case Log.ERROR:
                return "ERROR";
            default:
                return "UNKNOWN";
        }
    }
}

