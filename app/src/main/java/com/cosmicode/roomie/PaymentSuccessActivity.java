package com.cosmicode.roomie;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.cosmicode.roomie.domain.Room;

public class PaymentSuccessActivity extends BaseActivity {

    private Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);
        room = getIntent().getParcelableExtra("room");
        Button go = findViewById(R.id.go_room);
        go.setOnClickListener(l -> {
            Intent intent = new Intent(this, PremiumToolsAcitivity.class);
            intent.putExtra("room", room);
            startActivity(intent);
        });
        ImageButton cancelSuccess = findViewById(R.id.cancel_success);
        cancelSuccess.setOnClickListener( l -> startActivity(new Intent(this, MainActivity.class)));
    }
}
