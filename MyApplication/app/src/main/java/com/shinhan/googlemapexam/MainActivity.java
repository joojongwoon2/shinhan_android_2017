package com.shinhan.googlemapexam;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    SupportMapFragment mapFragment;
    GoogleMap map;
    int idx = -1;

    class MyMarker{
        String name;
        LatLng latLng;
        MyMarker(String name, LatLng latLng){
            this.name = name;
            this.latLng = latLng;
        }
    }

    MyMarker[] markers = {
            new MyMarker("광나루", new LatLng(37.5453362, 127.1014173)),
            new MyMarker("목동역", new LatLng(37.526319,  126.864322 )),
            new MyMarker("오목교", new LatLng(37.5245572, 126.8728613)),
            new MyMarker("부평역", new LatLng(37.4894612, 126.7223953)),
            new MyMarker("연수구", new LatLng(37.4088752,126.6761853)),
            new MyMarker("문산역", new LatLng(37.854569,  126.7853425)),
            new MyMarker("평양", new LatLng(39.0293211, 125.6020258)),
            new MyMarker("보홀", new LatLng(9.915795,  124.219214)),
            new MyMarker("두바이", new LatLng(25.074718,  54.9479062)),
            new MyMarker("해운대", new LatLng(35.1586689, 129.1581501))
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;//비동기방식으로 구글지도 객체 얻기

                PolylineOptions rectOptions = new PolylineOptions();
                rectOptions.color(Color.RED);

                for(int i = 0; i < markers.length; i++){
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(markers[i].latLng);
                    marker.title(markers[i].name);

                    map.addMarker(marker);
                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            for(int j=0; j < markers.length; j++){
                                if( markers[j].name.equals(marker.getTitle()) ){
                                    idx = j;
                                }
                            }

                            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                            return false;
                        }
                    });
                    rectOptions.add(markers[i].latLng);
                }

                Polyline polyline = map.addPolyline(rectOptions);

            }
        });

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "GPS 연동 권한 필요합니다.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
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

    private class GPSListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();//위도
            double longitude = location.getLongitude();//경도

            TextView textView = (TextView)findViewById(R.id.location);
            textView.setText("내 위치:"+latitude+","+longitude);
            Toast.makeText(MainActivity.this, "위도:"+latitude+",경도:"+longitude, Toast.LENGTH_SHORT).show();
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
                TextView textView = (TextView) findViewById(R.id.location);
                textView.setText("내 위치:" + location.getLatitude() + "," +
                        location.getLongitude());
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

    public void onWorldMapButtonClicked(View view){
        if(map != null){
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.moveCamera(CameraUpdateFactory.zoomTo(1));
        }
    }

    public void onNextButtonClicked(View view){
        idx++;
        if(idx >= markers.length){
            idx = 0;
        }

        if(map != null){
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(markers[idx].latLng, 16));
        }
    }

}
