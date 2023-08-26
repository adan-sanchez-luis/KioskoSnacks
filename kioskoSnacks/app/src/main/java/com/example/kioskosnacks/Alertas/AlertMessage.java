package com.example.kioskosnacks.Alertas;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.kioskosnacks.R;

public class AlertMessage {

    private TextView title;
    private LottieAnimationView icon;
    private Button btnAccept;
    private Button btnCancel;
    private Dialog alert;

    public AlertMessage(Context context) {
        alert = new Dialog(context);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setCancelable(false);
        alert.setContentView(R.layout.alert_message);
        icon = alert.findViewById(R.id.alertWarning);

        title = alert.findViewById(R.id.alertTitle);

        btnAccept = alert.findViewById(R.id.alertConfirm);
        btnCancel = alert.findViewById(R.id.alertCancel);

        alert.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alert.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void setAnimation(int resIcon) {
        icon.setAnimation(resIcon);
    }

    public void setLoop(boolean loop) {
        icon.loop(loop);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setListener(View.OnClickListener listener) {
        btnAccept.setOnClickListener(listener);
        btnCancel.setOnClickListener(listener);
    }

    public void setAceptText(String text) {
        btnAccept.setText(text);
    }

    public void setCancelText(String text) {
        btnCancel.setText(text);
    }

    public void setBackgroundAccept(int res) {
        btnAccept.setBackgroundResource(res);
    }

    public void setBackgroundCancel(int res) {
        btnAccept.setBackgroundResource(res);
    }

    public void disableCancel(boolean disable) {
        if (disable) {
            btnCancel.setVisibility(View.GONE);
            return;
        }
        btnCancel.setVisibility(View.VISIBLE);
    }

    public void showAlert() {
        alert.show();
        icon.playAnimation();
    }

    public void alertDismiss() {
        alert.dismiss();
    }
}
