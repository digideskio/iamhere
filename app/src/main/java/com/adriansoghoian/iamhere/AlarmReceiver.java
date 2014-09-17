package com.adriansoghoian.iamhere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public class AlarmReceiver extends BroadcastReceiver {

    public static int ID = ReceiveTransitionsIntentService.ID;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       if (ReceiveTransitionsIntentService.checkedIn) {
           new asyncTask().execute();
       }
    }

    public class asyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                httpPut();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void httpPut() throws IOException {
            HttpClient httpclient = new DefaultHttpClient();
            System.out.println("http://iamhere.smalldata.io/occupancy/" + ReceiveTransitionsIntentService.ID + "/update");
            HttpPost httppost = new HttpPost("http://iamhere.smalldata.io/occupancy/" + ReceiveTransitionsIntentService.ID + "/update");

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
            System.out.println(responseString);

        }

    }
}
