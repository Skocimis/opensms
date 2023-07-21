package com.textflow.smssender;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HeadlessSmsSendService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // A Service is not a valid Context to bind to an Intent, so return null
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Handle any tasks that need to occur when the Service starts
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Handle any cleanup that needs to occur when the Service is stopped
    }
}
