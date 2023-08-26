package com.example.kioskosnacks.Alertas;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.kioskosnacks.R;

public class AlertInactive {

    private Button btnYes;
    private Button btnNo;
    private Dialog alert;

    public AlertInactive(Context context) {

        alert = new Dialog(context);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setCancelable(false);
        alert.setContentView(R.layout.alert_inactive);

        btnYes = alert.findViewById(R.id.inactiveYes);
        btnNo = alert.findViewById(R.id.inactiveNo);

        alert.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alert.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void setListener(View.OnClickListener listener) {
        btnYes.setOnClickListener(listener);
        btnNo.setOnClickListener(listener);
    }

    public void showAlert() {
        alert.show();
    }

    public void alertDismiss() {
        alert.dismiss();
    }
}
