package com.shinhan.phonefindhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
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

            Log.i(TAG, "sender:" + sender + ", contents:" + contents);

            if(contents.equals("LOCK")){
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>1");
                Intent showIntent = new Intent(context, LockActivity.class);
                showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //showIntent.putExtra("command", command);
                //showIntent.putExtra("name", name+" from service");
                context.startActivity(showIntent);
            }else if(contents.equals("UNLOCK")){
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>2");

            }else{
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>3");
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
