package com.cosmicode.roomie;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreatePremiumRoom extends AppCompatActivity {

    private Double amount;
    @BindView(R.id.amount)
    Button price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_premium_room);
        ButterKnife.bind(this);
        amount = getIntent().getDoubleExtra("amount", 0);
        price.setText(String.format("%s%s", "$", amount));
    }

    @OnClick(R.id.create_room_btn)
    public void continuePremium(View view){
        startActivity(new Intent(this, ChoosePremiumMembers.class));
    }

    @OnClick(R.id.cancel_info)
    public void cancelInfo(View view){
        finish();
    }


}
