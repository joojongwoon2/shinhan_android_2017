package com.shinhan.phonefindhelper;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class LockReceiver extends BroadcastReceiver {
    public static final String TAG = "LockReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive()--------------------!!!!!!");

        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = parseSmsMessage(bundle);
        if(messages != null && messages.length > 0){
            String sender = messages[0].getOriginatingAddress();
            String contents = messages[0].getMessageBody().toString();

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            String myPhoneNumber = telephonyManager.getLine1Number();

            String phoneNumber = "";
            PhoneDB phoneDB = new PhoneDB(context);
            try {
                SQLiteDatabase databaseRead = phoneDB.getReadableDatabase();
                Cursor cursor = databaseRead.rawQuery("select * from " + PhoneDB.TABLE_NAME_PHONELIST, null);
                Log.i("LockReceiver count--", cursor.getCount() + "");

                for (int i = 0; i < cursor.getCount(); i++) {//01011112222|01022223333|
                    cursor.moveToNext();
                    phoneNumber += cursor.getString(0) + "|";
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            Log.i(TAG, "myPhoneNumber:" + myPhoneNumber + ", sender:" + sender + ", contents:" + contents + ", phoneNumber:" + phoneNumber);
            //if( sender.trim().equals(phoneNumber.trim()) ) {//메시지를 보낸 전화번호가 등록된 번호와 같으면

            String contentsStr = "";
            if(contents.trim().startsWith("|SMS|")) {
                contentsStr = contents.trim().substring(5);
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>|SMS|" + contentsStr);

                Intent showIntent = new Intent(context, RegistLostActivity.class);
                showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

                showIntent.putExtra("sender", sender.trim());
                showIntent.putExtra("contents", contentsStr.trim());
                context.startActivity(showIntent);
            }else if(contents.trim().startsWith("|SMSLOCK|")) {
                contentsStr = contents.trim().substring(9);
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>|SMSLOCK|" + contentsStr);

                Intent showIntent = new Intent(context, LockActivity.class);
                showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

                showIntent.putExtra("sender", sender.trim());
                showIntent.putExtra("contents", contentsStr.trim());
                context.startActivity(showIntent);
            }else if (contents.trim().startsWith("|LOCK|")) {
                contentsStr = contents.trim().substring(6);
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>|LOCK|" + contentsStr);

                try {
                    SQLiteDatabase databaseWrite = phoneDB.getWritableDatabase();//쓰기모드로 열기
                    databaseWrite.execSQL("UPDATE " + PhoneDB.TABLE_NAME_MYINFO + " SET lock = '1' WHERE id = '0';");
                }catch(Exception e){
                    e.printStackTrace();
                }

                Intent showIntent = new Intent(context, LockActivity.class);
                showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

                showIntent.putExtra("sender", sender.trim());
                showIntent.putExtra("contents", contentsStr.trim());
                context.startActivity(showIntent);
            } else if (contents.trim().startsWith("|UNLOCK|")) {
                contentsStr = contents.trim().substring(8);
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>|UNLOCK|" + contentsStr);

                try {
                    SQLiteDatabase databaseWrite = phoneDB.getWritableDatabase();//쓰기모드로 열기
                    databaseWrite.execSQL("UPDATE " + PhoneDB.TABLE_NAME_MYINFO + " SET lock = '0' WHERE id = '0';");
                }catch(Exception e){
                    e.printStackTrace();
                }

                Intent showIntent = new Intent(context, LockActivity.class);
                showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

                showIntent.putExtra("sender", sender.trim());
                showIntent.putExtra("contents", contentsStr.trim());
                context.startActivity(showIntent);
            } else if (contents.trim().startsWith("|REG|")) {
                contentsStr = contents.trim().substring(5);
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>|REG|" + contentsStr);
                //상대방 기본정보 등록 시 ...
                String x = "";
                String y = "";
                try {
                    String pNumber = "";
                    pNumber = contentsStr.substring(0, contentsStr.indexOf("|"));
                    String tempStr = contentsStr.substring(contentsStr.indexOf("|") + 1);
                    x = tempStr.substring(0, tempStr.indexOf(":"));
                    y = tempStr.substring(tempStr.indexOf(":") + 1);

                    SQLiteDatabase databaseWrite = phoneDB.getWritableDatabase();//쓰기모드로 열기
                    ContentValues values = new ContentValues(); //저장 객체 생성
                    values.put("phoneNumber", pNumber);
                    values.put("x", x);
                    values.put("y", y);
                    databaseWrite.insert(PhoneDB.TABLE_NAME_PHONELIST, null, values); //DB에 데이터 insert
                }catch(Exception e){

                    try {
                        SQLiteDatabase databaseWrite = phoneDB.getWritableDatabase();//쓰기모드로 열기
                        databaseWrite.execSQL("UPDATE " + PhoneDB.TABLE_NAME_PHONELIST + " SET x = '" + x + "', y = '" + y + "' WHERE phoneNumber = '" + sender.trim() + "';");
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                }
            } else if (contents.trim().startsWith("|GPS|")) {
                contentsStr = contents.trim().substring(5);
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>|GPS|" + contentsStr);
                //상대방 GPS 등록 시 ...

                String x = "";
                String y = "";
                x = contentsStr.substring(0, contentsStr.indexOf(":"));
                y = contentsStr.substring(contentsStr.indexOf(":") + 1);

                try {
                    SQLiteDatabase databaseWrite = phoneDB.getWritableDatabase();//쓰기모드로 열기
                    databaseWrite.execSQL("UPDATE " + PhoneDB.TABLE_NAME_PHONELIST + " SET x = '" + x + "', y = '" + y + "' WHERE phoneNumber = '" + sender.trim() + "';");
                }catch(Exception e){
                    e.printStackTrace();
                }
            } else {
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>else");
            }
        }
    }

    private SmsMessage[] parseSmsMessage(Bundle bundle){
        Object[] objs = (Object[])bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objs.length];
        for(int i=0; i<objs.length; i++){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[])objs[i], format);
            }else{
                messages[i] = SmsMessage.createFromPdu((byte[])objs[i]);
            }
        }
        return messages;
    }

}
