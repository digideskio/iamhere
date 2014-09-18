package com.adriansoghoian.iamhere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import static android.hardware.SensorManager.getAltitude;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class AlarmReceiver extends BroadcastReceiver implements SensorEventListener {

    private SensorManager mSensorManager;
    private Context context;
    public static float currentPressure;
    public SensorEventListener sensorEventListener;
    public Sensor sensor;
    public static String floor;
    public List<Sensor> sensors;


    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       this.context = context;
       mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

       if (ReceiveTransitionsIntentService.checkedIn) {
           sensorEventListener = new SensorEventListener() {
               @Override
               public void onSensorChanged(SensorEvent event) {
                   currentPressure = event.values[0];
                   System.out.println(currentPressure);
                   mSensorManager.unregisterListener(this);
               }
               @Override
               public void onAccuracyChanged(Sensor sensor, int accuracy) {
               }

           };
           sensors = mSensorManager.getSensorList(Sensor.TYPE_PRESSURE);

           if (sensors.size() > 0) {
               sensor = sensors.get(0);
               mSensorManager.registerListener(sensorEventListener, sensor, 0);
           }

           if (currentPressure > 1012) {
               floor = "3rd";
               new asyncTask().execute();
           }

           if (currentPressure > 1009 && currentPressure < 1012) {
               floor = "8th";
               new asyncTask().execute();
           }

           if (currentPressure < 1009) {
               floor = "15th";
               new asyncTask().execute();
           }
       }


    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class asyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... floor) {

            try {
                System.out.println(floor);
                httpPut();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void httpPut() throws IOException {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://iamhere.smalldata.io/occupancy/" + ReceiveTransitionsIntentService.ID + "/update");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("floor", floor));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpclient.execute(httppost);
            httpResponse.getEntity().consumeContent();
        }

    }
}
