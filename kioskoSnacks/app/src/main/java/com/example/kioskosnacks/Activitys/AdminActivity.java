package com.example.kioskosnacks.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kioskosnacks.DataBase.TokenDB;
import com.example.kioskosnacks.R;

public class AdminActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button btnInventory = findViewById(R.id.btnInventory);
        btnInventory.setOnClickListener(this);
        Button btnReports = findViewById(R.id.btnReports);
        btnReports.setOnClickListener(this);
        Button btnExit = findViewById(R.id.btnExitAdmin);
        btnExit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnInventory:
                activityInventory();
                break;
            case R.id.btnReports:
                activityReports();
                break;
            case R.id.btnExitAdmin:
                showDialogExit();
                break;
        }
    }

    private void showDialogExit() {
        Dialog confirmDialog = new Dialog(this);
        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmDialog.setCancelable(false);
        confirmDialog.setContentView(R.layout.layout_exit);

        confirmDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button exit = confirmDialog.findViewById(R.id.btnExitDialog);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog.dismiss();
                closeActivity();
            }
        });

        Button cancel = confirmDialog.findViewById(R.id.btnCancelAdminDialog);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog.dismiss();
            }
        });
        confirmDialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        confirmDialog.show();
    }

    private void activityReports() {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void activityInventory() {
        Intent intent = new Intent(this, InventoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void closeActivity() {
        this.finish();
    }
}