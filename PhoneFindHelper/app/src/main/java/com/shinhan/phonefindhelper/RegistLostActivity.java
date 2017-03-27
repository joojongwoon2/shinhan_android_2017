package com.shinhan.phonefindhelper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Vector;

public class RegistLostActivity extends AppCompatActivity {

    public Vector msgVector  = new Vector();
    ArrayList<String> arraylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_lost);

        arraylist = new ArrayList<String>();

        Log.i("LockActivity", "onCreate 실행");
        try {
            PhoneDB db = new PhoneDB(RegistLostActivity.this);
            SQLiteDatabase databaseRead = db.getReadableDatabase();
            Cursor cursor = databaseRead.rawQuery("select * from " + PhoneDB.TABLE_NAME_PHONELIST, null);
            Log.i("RegistLostActivity", cursor.getCount() + "");

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

        if( getIntent().getStringExtra("contents") != null )
            displayListView("<<", getIntent().getStringExtra("contents"));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        arraylist = new ArrayList<String>();

        Log.i("LockActivity", "onNewIntent 실행");
        try {
            PhoneDB db = new PhoneDB(RegistLostActivity.this);
            SQLiteDatabase databaseRead = db.getReadableDatabase();
            Cursor cursor = databaseRead.rawQuery("select * from " + PhoneDB.TABLE_NAME_PHONELIST, null);
            Log.i("RegistLostActivity", cursor.getCount() + "");

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

        if( intent.getStringExtra("contents") != null )
            displayListView("<<", intent.getStringExtra("contents"));
    }

    public void onButtonSend(View view){
        EditText editText = (EditText) findViewById(R.id.textContents);

        Spinner sp = (Spinner) this.findViewById(R.id.spinner);

        if(sp.getSelectedItem() == null){
            Toast.makeText(RegistLostActivity.this, "등록된 전화번호가 없습니다.", Toast.LENGTH_SHORT).show();
        }else {
            Log.i("선택된 전화번호", sp.getSelectedItem().toString() + "");

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(sp.getSelectedItem().toString(), null, "|SMSLOCK|" + editText.getText().toString(), null, null);

            displayListView(">>", editText.getText().toString());
            editText.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(RegistLostActivity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public void displayListView(String who, String contents){
        msgVector.add(who + " " + contents);

        ArrayAdapter<String> adaptor = new ArrayAdapter<String>(RegistLostActivity.this, android.R.layout.simple_list_item_1, msgVector);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adaptor);

        listView.setAdapter(adaptor);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setSelection(adaptor.getCount() - 1);
    }

    public void onButtonLockClicked(View view){
        Spinner sp = (Spinner) this.findViewById(R.id.spinner);
        if(sp.getSelectedItem() == null){
            Toast.makeText(RegistLostActivity.this, "등록된 전화번호가 없습니다.", Toast.LENGTH_SHORT).show();
        }else {
            Log.i("선택된 전화번호", sp.getSelectedItem().toString() + "");

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(sp.getSelectedItem().toString(), null, "|LOCK|", null, null);
        }
    }

    public void onButtonUnLockClicked(View view){
        Spinner sp = (Spinner) this.findViewById(R.id.spinner);
        if(sp.getSelectedItem() == null){
            Toast.makeText(RegistLostActivity.this, "등록된 전화번호가 없습니다.", Toast.LENGTH_SHORT).show();
        }else {
            Log.i("선택된 전화번호", sp.getSelectedItem().toString() + "");

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(sp.getSelectedItem().toString(), null, "|UNLOCK|", null, null);
        }
    }
}
