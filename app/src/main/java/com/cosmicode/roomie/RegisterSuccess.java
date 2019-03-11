package com.cosmicode.roomie;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisterSuccess extends AppCompatActivity {

    @BindView(R.id.success_text)
    TextView succes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);

        Intent intent = getIntent();
        String name = intent.getStringExtra(RegisterActivity.USER_NAME);
        Button button = findViewById(R.id.button_return);
        succes.setText(getString(R.string.succes_register, name));
        button.setOnClickListener(this::onClickLogin);
    }

    public void onClickLogin(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }
}
