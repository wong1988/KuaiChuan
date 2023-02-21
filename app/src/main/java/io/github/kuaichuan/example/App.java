package io.github.kuaichuan.example;

import android.app.Application;

import io.github.wong1988.kit.AndroidKit;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidKit.init(this);
    }
}
