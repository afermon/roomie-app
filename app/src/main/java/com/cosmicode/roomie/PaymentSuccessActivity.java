package com.cosmicode.roomie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.cosmicode.roomie.domain.Room;

public class PaymentSuccessActivity extends AppCompatActivity {

    private Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);
        room = getIntent().getParcelableExtra("room");
        ImageButton cancelSuccess = findViewById(R.id.cancel_success);
        cancelSuccess.setOnClickListener( l -> finish());
    }
}
