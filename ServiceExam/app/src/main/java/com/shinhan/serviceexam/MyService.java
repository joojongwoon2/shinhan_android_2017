package com.shinhan.serviceexam;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
    private static final String TAG = "MyService";

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate()------------------------!!!!!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand-------------------------!!!!!");

        if(intent == null){
            return Service.START_STICKY;
        }else{
            String command = intent.getStringExtra("command");
            String name = intent.getStringExtra("name");

            Log.d(TAG, "command:"+command+" ,name:"+name);

            Intent showIntent = new Intent(getApplicationContext(), MainActivity.class);
            showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP);
            showIntent.putExtra("command", command);
            showIntent.putExtra("name", name+" from service");
            startActivity(showIntent);

            /*if(command.equals("show")){
                Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
            }else{

            }*/
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy---------------------------------!!!!!");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "inBind---------------------!!!!!");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
