package com.textflow.smssender;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendSmsWorker extends Worker {
    public SendSmsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            String sender = getInputData().getString("sender");
            String messageBody = getInputData().getString("messageBody");

            // Provide webhook address here
            URL url = new URL("YOUR_WEBHOOK_ADDRESS");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("phone_number", sender);
            jsonParam.put("text", messageBody);
            jsonParam.put("secret", "WEBHOOK_SECRET");
            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            os.write(jsonParam.toString());
            os.flush();
            os.close();
            int responseCode = conn.getResponseCode();
            conn.disconnect();

            if(responseCode == 200) {
                return Result.success();
            } else {
                return Result.retry();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}
