package com.cosmicode.roomie;

import android.view.MenuItem;
import android.view.View;

import com.cosmicode.roomie.service.UserInterface;
import com.cosmicode.roomie.util.Core;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    public UserInterface getJhiUsers() {
        return ((RoomieApplication) this.getApplication()).getUserInterface();
    }

    public final Core getCore() {
        return ((RoomieApplication) this.getApplication()).getCore();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onContextItemSelected(item);
    }

    public void showUserMessage(String message, SnackMessageType type){
        View view = findViewById(android.R.id.content);
        Snackbar snackbar;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();

        switch (type){
            case INFO:
                snackBarView.setBackgroundColor(getColor(R.color.info));
                break;
            case ERROR:
                snackBarView.setBackgroundColor(getColor(R.color.danger));
                break;
            case WARNING:
                snackBarView.setBackgroundColor(getColor(R.color.warning));
                break;
            case SUCCESS:
                snackBarView.setBackgroundColor(getColor(R.color.success));
                break;
            default:
                break;
        }
        snackbar.show();
    }

    public enum SnackMessageType {
        SUCCESS,
        INFO,
        WARNING,
        ERROR
    }
}
