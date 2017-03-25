package com.shinhan.phonefindhelper;

import android.content.ContentValues;
import android.content.Context;
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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class UpdateInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        readDataBase();

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

    public void onRequestPermissionsResult(int requestCode, String permission[], int[] grantResults){
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "GPS 권한 승인 !", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "GPS 권한 거부 !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onButtonRegist(View view){
        EditText editTextPhoneNumner = (EditText) findViewById(R.id.textPhoneNumber);
        EditText editTextContents = (EditText) findViewById(R.id.textContents);
        EditText editTextPassword = (EditText) findViewById(R.id.textPassword);

        writeDatabase(editTextPhoneNumner.getText().toString(),
                      editTextContents.getText().toString(),
                      editTextPassword.getText().toString());

        TelephonyManager telephonyManager = (TelephonyManager) UpdateInfoActivity.this.getSystemService(UpdateInfoActivity.TELEPHONY_SERVICE);
        String myPhoneNumber = telephonyManager.getLine1Number();

        String x = "";
        String y = "";
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null) {
                x = location.getLatitude() + "";
                y = location.getLongitude() + "";
            }
        }

        Log.i("GPS:", "|REG|" + myPhoneNumber.trim() + "|" + x + ":" + y);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(editTextPhoneNumner.getText().toString().trim(), null, "|REG|" + myPhoneNumber.trim() + "|" + x + ":" + y, null, null);

        InputMethodManager imm = (InputMethodManager) getSystemService(UpdateInfoActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);
    }


    public void writeDatabase(String phoneNumber, String contents, String password){
        try {
            PhoneDB db = new PhoneDB(UpdateInfoActivity.this);//DB파일 열기
            SQLiteDatabase database = db.getWritableDatabase();//쓰기모드로 열기
            //database.delete(PhoneDB.TABLE_NAME_MYINFO, null, null);
            database.delete(PhoneDB.TABLE_NAME_MYINFO, null, null);
            ContentValues values = new ContentValues(); //저장 객체 생성
            values.put("id", "0");
            values.put("phoneNumber", phoneNumber);
            values.put("contents", contents);
            values.put("password", password);
            values.put("lock", "0");//0 unlock, 1 lock
            database.insert(PhoneDB.TABLE_NAME_MYINFO, null, values); //DB에 데이터 insert
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void readDataBase(){
        try {
            PhoneDB db = new PhoneDB(UpdateInfoActivity.this);
            SQLiteDatabase database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("select * from " + PhoneDB.TABLE_NAME_MYINFO, null);
            Log.i("count", cursor.getCount() + "");

            EditText editTextPhoneNumner = (EditText) findViewById(R.id.textPhoneNumber);
            EditText editTextContents = (EditText) findViewById(R.id.textContents);
            EditText editTextPassword = (EditText) findViewById(R.id.textPassword);

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                Log.i("myInfo-0:", cursor.getString(0));
                Log.i("myInfo-1:", cursor.getString(1));
                Log.i("myInfo-2:", cursor.getString(2));
                Log.i("myInfo-3:", cursor.getString(3));
                Log.i("myInfo-4:", cursor.getString(4));

                editTextPhoneNumner.setText(cursor.getString(1));
                editTextContents.setText(cursor.getString(2));
                editTextPassword.setText(cursor.getString(3));

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
