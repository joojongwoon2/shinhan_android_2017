package com.shinhan.phonefindhelper;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class ConfirmationLocationActivity extends AppCompatActivity {

    SupportMapFragment mapFragment;
    GoogleMap map;

    ArrayList<String> arraylist;

    String x = "";
    String y = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_location);

        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;//비동기방식으로 구글지도 객체 얻기

            }
        });

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "GPS 연동 권한 필요합니다.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        arraylist = new ArrayList<String>();

        Log.i("LocationActivity", "onCreate 실행");
        try {
            PhoneDB db = new PhoneDB(ConfirmationLocationActivity.this);
            SQLiteDatabase databaseRead = db.getReadableDatabase();
            Cursor cursor = databaseRead.rawQuery("select * from " + PhoneDB.TABLE_NAME_PHONELIST, null);
            Log.i("LocationActivity", cursor.getCount() + "");

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                arraylist.add(cursor.getString(0));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arraylist);
        //스피너 속성
        Spinner sp = (Spinner) this.findViewById(R.id.spinner);
        sp.setPrompt("골라봐"); // 스피너 제목
        sp.setAdapter(adapter);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
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

    private class GPSListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();//위도
            double longitude = location.getLongitude();//경도

            Toast.makeText(ConfirmationLocationActivity.this, "위도:"+latitude+",경도:"+longitude, Toast.LENGTH_SHORT).show();
            LatLng curPoint = new LatLng(latitude, longitude);
            if(map != null){
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
            }
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

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null) {
                Toast.makeText(this, "Last Known Location 위도:" +
                        location.getLatitude() + "경도:" + location.getLongitude(), Toast.LENGTH_SHORT).show();

                LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
                if(map != null){
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
                }
            }

            GPSListener gpsListener = new GPSListener();
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, gpsListener);
        }
    }

    public void onButtonLocationClicked(View view){
        try {
            Spinner sp = (Spinner) this.findViewById(R.id.spinner);

            if(sp.getSelectedItem() == null){
                Toast.makeText(ConfirmationLocationActivity.this, "선택한 전화번호가 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }else {
                Log.i("선택된 전화번호", sp.getSelectedItem().toString() + "");
            }

            PhoneDB db = new PhoneDB(ConfirmationLocationActivity.this);
            SQLiteDatabase databaseRead = db.getReadableDatabase();
            Cursor cursor = databaseRead.rawQuery("select * from " + PhoneDB.TABLE_NAME_PHONELIST + " WHERE phoneNumber = '" + sp.getSelectedItem().toString()  + "'", null);
            Log.i("LocationActivity", cursor.getCount() + "");

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                Log.i("선택된 전화기 위치 x", cursor.getString(1));
                Log.i("선택된 전화기 위치 y", cursor.getString(2));

                /*String strX = "";
                String strY = "";
                if( cursor.getString(1).indexOf(".") >= 0 ) {
                    if( cursor.getString(1).substring(cursor.getString(1).indexOf(".") + 1).length() > 5 ){
                        strX = cursor.getString(1).substring(0, cursor.getString(1).indexOf(".") + 1) + cursor.getString(1).substring(cursor.getString(1).indexOf(".") + 1).substring(0, 5);
                    }
                }

                if( cursor.getString(2).indexOf(".") >= 0 ) {
                    if( cursor.getString(2).substring(cursor.getString(2).indexOf(".") + 1).length() > 5 ){
                        strY = cursor.getString(2).substring(0, cursor.getString(2).indexOf(".") + 1) + cursor.getString(2).substring(cursor.getString(2).indexOf(".") + 1).substring(0, 5);
                    }
                }

                Log.i("선택된 전화기 위치 x", strX);
                Log.i("선택된 전화기 위치 y", strY);*/

                double doubleX = Double.parseDouble(cursor.getString(1));
                double doubleY = Double.parseDouble(cursor.getString(2));

                LatLng curPoint = new LatLng(doubleX, doubleY);
                if(map != null){
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(new LatLng(doubleX,  doubleY ));
                    marker.title(sp.getSelectedItem().toString() + "핸드폰 위치");

                    map.addMarker(marker);

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
