package com.example.kioskosnacks.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.kioskosnacks.MainActivity;
import com.example.kioskosnacks.R;

public class InactiveActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout layoutInactive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inactive);
        layoutInactive = findViewById(R.id.layout_inactive);
        layoutInactive.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        this.finish();
    }
}