package com.textflow.smssender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Telephony.Sms.Intents.SMS_DELIVER_ACTION)) {
            String messageBody = "";
            String sender = "";

            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                messageBody += smsMessage.getMessageBody();
                sender = smsMessage.getDisplayOriginatingAddress();
            }

            if (sender.matches("^\\+?\\d+$")) {
                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SendSmsWorker.class)
                        .setInputData(new Data.Builder()
                                .putString("sender", sender)
                                .putString("messageBody", messageBody)
                                .build())
                        .setBackoffCriteria(BackoffPolicy.LINEAR, 18000, TimeUnit.SECONDS)
                        .addTag("ASsmsSendTag")
                        .build();

                WorkManager.getInstance(context).enqueue(workRequest);
            }
        }
    }
}