package de.arvato.stratego;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import de.arvato.stratego.services.AudioService;
import de.arvato.stratego.util.LocaleHelper;

public class MyApplication extends Application {

    public static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        LocaleHelper.applyLanguageSettings(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleObserver());
    }

    public class AppLifecycleObserver implements LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void onBackground() {
            // Handle app going to background
            Log.d(TAG, "OnBackground stopService AudioService");
            Intent intent = new Intent(MyApplication.this, AudioService.class);
            stopService(intent);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void onForeground() {
            // Handle app coming to foreground
            Log.d(TAG, "onForeground startService AudioService");
            Intent intent = new Intent(MyApplication.this, AudioService.class);
            startService(intent);
        }
    }
}