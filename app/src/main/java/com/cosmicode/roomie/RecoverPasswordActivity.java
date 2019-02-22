package com.cosmicode.roomie;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecoverPasswordActivity extends BaseActivity {

    public static final Intent openIntent(Context from) {
        return new Intent(from, RecoverPasswordActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);
    }
}
