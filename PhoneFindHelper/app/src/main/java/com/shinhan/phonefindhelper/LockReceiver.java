package com.shinhan.phonefindhelper;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
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

            PhoneDB phoneDB = new PhoneDB(context);
            String fixedPhoneNumber = "";
            try {
                SQLiteDatabase database = phoneDB.getReadableDatabase();
                Cursor cursor = database.rawQuery("select * from " + PhoneDB.TABLE_NAME_MYINFO, null);
                Log.i(TAG, cursor.getCount() + "");

                if (cursor.getCount() == 1) {
                    cursor.moveToFirst();
                    fixedPhoneNumber = cursor.getString(1).trim();
                    Log.i("fixedPhoneNumber", fixedPhoneNumber);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            Log.i(TAG, "myPhoneNumber:" + myPhoneNumber + ", sender:" + sender + ", contents:" + contents + ", fixedPhoneNumber:" + fixedPhoneNumber);
            //if( sender.trim().equals(phoneNumber.trim()) ) {//메시지를 보낸 전화번호가 등록된 번호와 같으면

            String contentsStr = "";
            if(contents.trim().startsWith("|SMS|")) {//잠금 LOCK화면에서 메시지 전송 시 수신받는 부분
                contentsStr = contents.trim().substring(5);
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>|SMS|" + contentsStr);

                Intent showIntent = new Intent(context, RegistLostActivity.class);
                showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

                showIntent.putExtra("sender", sender.trim());
                showIntent.putExtra("contents", contentsStr.trim());
                context.startActivity(showIntent);
            }else if(contents.trim().startsWith("|SMSLOCK|")) {//잠금등록 화면에서 메시지 전송 시 수신받는 부분
                contentsStr = contents.trim().substring(9);
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>|SMSLOCK|" + contentsStr);

                Intent showIntent = new Intent(context, LockActivity.class);
                showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

                showIntent.putExtra("sender", sender.trim());
                showIntent.putExtra("contents", contentsStr.trim());
                context.startActivity(showIntent);
            }else if (contents.trim().startsWith("|LOCK|") && fixedPhoneNumber.equals(sender.trim())) {//잠금등록 화면에서 잠금등록 버튼 클릭으로 전송 시 수신받는 부분
                contentsStr = contents.trim().substring(6);
                Log.i(TAG, "번호동일>>>>>>>>>>>>>>>>>>>>>>>>>>>>|LOCK|" + contentsStr);

                try {
                    SQLiteDatabase databaseWrite = phoneDB.getWritableDatabase();//쓰기모드로 열기
                    databaseWrite.execSQL("UPDATE " + PhoneDB.TABLE_NAME_MYINFO + " SET lock = '1' WHERE id = '0';");//1 잠금, 0 해제
                }catch(Exception e){
                    e.printStackTrace();
                }

                try{
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(sender, null, "|SMS|분실폰 잠금설정 됬습니다.", null, null);
                }catch(Exception ex){
                    ex.printStackTrace();
                }

                Intent showIntent = new Intent(context, LockActivity.class);
                showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

                showIntent.putExtra("sender", sender.trim());
                showIntent.putExtra("contents", contentsStr.trim());
                context.startActivity(showIntent);
            } else if (contents.trim().startsWith("|UNLOCK|")) {//잠금등록 화면에서 잠금해제 버튼 클릭으로 전송 시 수신받는 부분
                contentsStr = contents.trim().substring(8);
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>|UNLOCK|" + contentsStr);

                try {
                    SQLiteDatabase databaseWrite = phoneDB.getWritableDatabase();//쓰기모드로 열기
                    databaseWrite.execSQL("UPDATE " + PhoneDB.TABLE_NAME_MYINFO + " SET lock = '0' WHERE id = '0';");//1 잠금, 0 해제
                }catch(Exception e){
                    e.printStackTrace();
                }

                try{
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(sender, null, "|SMS|분실폰 잠금해제 됬습니다.", null, null);
                }catch(Exception ex){
                    ex.printStackTrace();
                }

                Intent showIntent = new Intent(context, LockActivity.class);
                showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

                showIntent.putExtra("sender", sender.trim());
                showIntent.putExtra("contents", contentsStr.trim());
                context.startActivity(showIntent);
            } else if (contents.trim().startsWith("|REG|")) {//기본정보 등록화면에서 등록버튼 클릭으로 전송 시 수신받는 부분
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
            } else if (contents.trim().startsWith("|GPS|")) {//분실폰에서 잠금 LOCK화면 Open 시점에 GPS정보 전송시 수신받는 부분
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
