package com.example.test;

import android.app.Application;

/**
 * Created by Talon on 2021-05-23.
 */
public class MyApplication extends Application {

    static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
