package com.shinhan.phonefindhelper;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.Vector;

public class LockActivity extends AppCompatActivity {

    public Vector msgVector  = new Vector();
    String passwordStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);



        Log.i("LockActivity", "onCreate 실행");
        try {
            PhoneDB db = new PhoneDB(LockActivity.this);
            SQLiteDatabase database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("select * from " + PhoneDB.TABLE_NAME_MYINFO, null);
            Log.i("LockActivity", cursor.getCount() + "");

            TextView textlockstate = (TextView) findViewById(R.id.textlockstate);

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();

                passwordStr = cursor.getString(3).trim();

                if( cursor.getString(4).trim().equals("1") ){
                    textlockstate.setText("잠금");
                }else{
                    textlockstate.setText("해제");
                    finish();
                }

                textlockstate.setText(textlockstate.getText().toString() + "(" + cursor.getString(2) + ")");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        //HomeKeyLocker homeKeyLoader = new HomeKeyLocker();
        //homeKeyLoader.lock(this);

        if(getIntent().getStringExtra("contents") != null && !getIntent().getStringExtra("contents").trim().equals("")) {
            displayListView("<<", getIntent().getStringExtra("contents"));
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "GPS 연동 권한 필요합니다.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private class GPSListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();//위도
            double longitude = location.getLongitude();//경도

            Toast.makeText(LockActivity.this, "위도:"+latitude+",경도:"+longitude, Toast.LENGTH_SHORT).show();

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(getIntent().getStringExtra("sender"), null, "|GPS|" + latitude + ":" + longitude, null, null);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    }

    public void startLocationService(View view){
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null) {
                Toast.makeText(this, "Last Known Location 위도:" +
                        location.getLatitude() + "경도:" + location.getLongitude(), Toast.LENGTH_SHORT).show();

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(getIntent().getStringExtra("sender"), null, "|GPS|" + location.getLatitude() + ":" + location.getLongitude(), null, null);
            }

            GPSListener gpsListener = new GPSListener();
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, gpsListener);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permission[], int[] grantResults){
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "GPS 권한 승인 !", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "GPS 권한 거부 !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.i("LockActivity", "onNewIntent 실행");
        try {
            PhoneDB db = new PhoneDB(LockActivity.this);
            SQLiteDatabase database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("select * from " + PhoneDB.TABLE_NAME_MYINFO, null);
            Log.i("LockActivity", cursor.getCount() + "");

            TextView textlockstate = (TextView) findViewById(R.id.textlockstate);

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();

                passwordStr = cursor.getString(3).trim();

                if( cursor.getString(4).trim().equals("1") ){
                    textlockstate.setText("잠금");
                }else{
                    textlockstate.setText("해제");
                    finish();
                }
                textlockstate.setText(textlockstate.getText().toString() + "(" + cursor.getString(2) + ")");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        //HomeKeyLocker homeKeyLoader = new HomeKeyLocker();
        //homeKeyLoader.lock(this);

        if(intent.getStringExtra("contents") != null && !intent.getStringExtra("contents").trim().equals("")) {
            displayListView("<<", intent.getStringExtra("contents"));
        }
    }

    public void displayListView(String who, String contents){
        msgVector.add(who + " " + contents);

        ArrayAdapter<String> adaptor = new ArrayAdapter<String>(LockActivity.this, android.R.layout.simple_list_item_1, msgVector);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adaptor);
    }

    public void onButtonLogin(View view){
        EditText editText = (EditText) findViewById(R.id.textId);

        if( editText.getText().toString().trim().equals(passwordStr) ) {//로그인 OK
            finish();
        }else{
            Toast.makeText(LockActivity.this, "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
        }

        editText.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(LockActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void onButtonSend(View view){
        EditText editText = (EditText) findViewById(R.id.textContents);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(getIntent().getStringExtra("sender"), null, "|SMS|" + editText.getText().toString(), null, null);

        displayListView(">>", editText.getText().toString());
        editText.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(LockActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
