package com.example.kioskosnacks.WebService;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.View;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class ConexionUtils {
    private static boolean connected;
    private static final Handler handler = new Handler();
    private static Timer timer = new Timer();
    private static TimerTask scanTask;

    //Verifica si la app se tiene conexión a Internet
    private static boolean isConnected(@NotNull Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    //Se verifica constantemente que la app se encuentre conectada a Internet
    public static void runConnectionStatus(Context context, @Nullable View connectionError,@Nullable View deshabilidar){
        new Thread(){
            public void run(){
                scanTask = new TimerTask() {
                    public void run() {
                        connected=isConnected(context);
                        handler.post(() -> handler.post(() -> applyConnected(connectionError, deshabilidar)));
                    }};
                timer.schedule(scanTask, 1000,1000);
            }
        }.start();
    }

    //Se muestra u oculta el view de advertencia cuando se conecta o desconecta a Internet
    private static void applyConnected(@Nullable View connectionError,@Nullable View deshabilitar){
        if(connected){
            if(connectionError!=null)
                connectionError.setVisibility(View.GONE);
            if(deshabilitar!=null)
                deshabilitar.setVisibility(View.VISIBLE);
        }else{
            if(connectionError!=null)
                connectionError.setVisibility(View.VISIBLE);
            if(deshabilitar!=null)
                deshabilitar.setVisibility(View.GONE);
        }
    }

    //Se cancela la tarea que verifica que la aplicacion este conectada a internet
    public static void endTaskConnection(){
        if(scanTask!=null)
            scanTask.cancel();
    }

    public static boolean getConnected(){ return connected; }
}
