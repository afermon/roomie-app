package com.cosmicode.roomie;

import android.app.Application;
import android.content.Context;

import com.cosmicode.roomie.service.UserInterface;
import com.cosmicode.roomie.service.UserService;
import com.cosmicode.roomie.util.Core;
import com.cosmicode.roomie.util.CoreConfiguration;

public class RoomieApplication extends Application {
    private UserInterface userInterface;
    private Core core;
    private CoreConfiguration config;

    public void onCreate() {
        super.onCreate();
        /* TODO: Initialize restrofit */

        if (BuildConfig.DEBUG) {
            this.config = new CoreConfiguration("http://192.168.189.1:8080");
        } else {
            this.config = new CoreConfiguration("www.test.com");
        }

        userInterface = UserService.with(this, config.getServerUrl(), true, this.getSharedPreferences("UserInterface", 0));
        core = new Core(userInterface, config, getSharedPreferences("Core", Context.MODE_PRIVATE));

    }

    public UserInterface getUserInterface() {
        return userInterface;
    }

    public Core getCore() {
        return core;
    }
}
