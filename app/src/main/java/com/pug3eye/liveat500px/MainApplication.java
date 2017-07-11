package com.pug3eye.liveat500px;

import android.app.Application;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;


/**
 * Created by pug3eye on 30/06/2017.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize thing(s) here  & Contextor is OuterLibrary
        Contextor.getInstance().init(getApplicationContext());  // getApplicationContext
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
    }
}
