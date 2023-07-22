package com.textflow.smssender;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.ArrayList;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("newToken", s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        String simName = message.getData().get("sim_name");
        String phoneNumber = message.getData().get("phone_number");
        String text = message.getData().get("text");
        String password = message.getData().get("password");
        if (password == null || !password.equals("YOUR_SECRET") || simName == null || phoneNumber == null
                || text == null)
            return;
        Context context = getApplicationContext();
        SubscriptionManager subscriptionManager;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            return;

        subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        SubscriptionInfo customSimSubscriptionInfo = null;
        for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            if (subscriptionInfo.getDisplayName().equals(simName)) {
                customSimSubscriptionInfo = subscriptionInfo;
                break;
            }
        }
        if (customSimSubscriptionInfo == null)
            return;

        SmsManager smsManager = SmsManager
                .getSmsManagerForSubscriptionId(customSimSubscriptionInfo.getSubscriptionId());

        ArrayList<String> parts = smsManager.divideMessage(text);

        smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }
}
