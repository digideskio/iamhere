package com.adriansoghoian.iamhere;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by adrian on 9/15/14.
 */

public class ReceiveTransitionsIntentService extends IntentService {

    public static int ID = 0;
    public static String NAME = "";
    public static boolean checkedIn = false;
    public static final String TRANSITION_INTENT_SERVICE = "ReceiveTransitionsIntentService";

    public ReceiveTransitionsIntentService() {
        super(TRANSITION_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationClient.hasError(intent)) {
            System.out.println("Location client has a mofucking error");
        }
        else {
            Intent intentAlarm = new Intent(this, AlarmReceiver.class);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            NAME = intent.getStringExtra("name");
            int transition = LocationClient.getGeofenceTransition(intent);

            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                ID = postName(NAME);
                checkedIn = true;

                alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 10000, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
            }
            if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                departName(ID);
                checkedIn = false;

                alarmManager.cancel(PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
            }
            if (transition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            }
        }
    }

    public int postName(String name) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://iamhere.smalldata.io/occupancy");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("name", name));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String responseString = EntityUtils.toString(entity);
                JSONObject result = new JSONObject(responseString);
                return result.getInt("id");
            }

        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void departName(int id) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://iamhere.smalldata.io/occupancy/" + id + "/depart");

        try {
            httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
