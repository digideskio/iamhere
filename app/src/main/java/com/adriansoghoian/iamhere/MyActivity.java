package com.adriansoghoian.iamhere;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import java.util.ArrayList;

public class MyActivity extends Activity implements View.OnClickListener, GooglePlayServicesClient.OnConnectionFailedListener, GooglePlayServicesClient.ConnectionCallbacks, LocationClient.OnAddGeofencesResultListener {

    public String STUDENT = "";
    public int ID = 0;

    TextView prompt;
    TextView txtLat;
    TextView location;
    EditText username;
    Button mainButton;
    Geofence cornell;
    LocationClient locationClient;
    ArrayList<Geofence> mCurrentGeofences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        int response = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (response == ConnectionResult.SUCCESS) {
            locationClient = new LocationClient(this, this, this);
            locationClient.connect();
        }

        prompt = (TextView) findViewById(R.id.prompt);
        txtLat = (TextView) findViewById(R.id.location);
        username = (EditText) findViewById(R.id.username);
        mainButton = (Button) findViewById(R.id.main_button);
        location = (TextView) findViewById(R.id.prompt);
        mainButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        prompt.setText("Signed in as: " + username.getText().toString());
        PendingIntent mTransitionPendingIntent = PendingIntent.getService(this, 0, new Intent(this, ReceiveTransitionsIntentService.class).putExtra("name", username.getText().toString()), PendingIntent.FLAG_UPDATE_CURRENT);
        locationClient.addGeofences(mCurrentGeofences, mTransitionPendingIntent, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        cornell = new Geofence.Builder().setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT).setCircularRegion(40.74093, -74.002158, 100).setExpirationDuration(Geofence.NEVER_EXPIRE).setRequestId("ID").setLoiteringDelay(30000).build();
        System.out.println(cornell);

        mCurrentGeofences = new ArrayList<Geofence>();
        mCurrentGeofences.add(cornell);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onAddGeofencesResult(int i, String[] strings) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationClient != null) {
            locationClient.disconnect();
        }
    }
}


