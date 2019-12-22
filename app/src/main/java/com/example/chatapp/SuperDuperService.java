package com.example.chatapp;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;


public class SuperDuperService extends IntentService {
    Client client;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * Used to name the worker thread, important only for debugging.
     */
    public SuperDuperService() {
        super("SuperDuperService");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public static volatile boolean shouldContinue = true;
    @Override
    protected void onHandleIntent(Intent intent) {
        doStuff();
    }

    private void doStuff() {
        Thread thread = new Thread(client);
        thread.start();
        // check the condition
        while(true) {
            if (shouldContinue == false) {
                stopSelf();
                return;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        client = new Client();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
