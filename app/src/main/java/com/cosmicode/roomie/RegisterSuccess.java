package com.cosmicode.roomie;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterSuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);

        Intent intent = getIntent();
        String name = intent.getStringExtra(RegisterActivity.USER_NAME);
        Button button = findViewById(R.id.button_return);
        button.setOnClickListener(this::onClickLogin);
    }

    public void onClickLogin(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }
}
