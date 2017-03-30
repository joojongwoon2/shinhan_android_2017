package com.shinhan.phonefindhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by IC-INTPC-087109 on 2017-03-30.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            try {
                PhoneDB db = new PhoneDB(context);
                SQLiteDatabase database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("select * from " + PhoneDB.TABLE_NAME_MYINFO, null);
                Log.i("BootReceiver", cursor.getCount() + "");

                if (cursor.getCount() == 1) {
                    cursor.moveToFirst();

                    if( cursor.getString(4).trim().equals("1") ){//잠금상태로 잠금화면 실행
                        Intent showIntent = new Intent(context, LockActivity.class);
                        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        Log.i("BootReceiver LOCKED", cursor.getString(1).trim());
                        showIntent.putExtra("sender", cursor.getString(1).trim());
                        showIntent.putExtra("contents", "");

                        context.startActivity(showIntent);
                        Log.i("BootReceiver LOCKED", "잠금상태로 잠금화면 실행");
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
