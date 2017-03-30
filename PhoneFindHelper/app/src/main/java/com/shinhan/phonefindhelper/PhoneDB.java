package com.shinhan.phonefindhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.name;
import static android.R.attr.version;

/**
 * Created by IC-INTPC-087109 on 2017-03-24.
 */

public class PhoneDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "phone.db";
    public static final int DATABASE_VERSION = 8;
    public static final String TABLE_NAME_MYINFO = "myInfo";
    public static final String TABLE_NAME_PHONELIST = "phoneList";

    public PhoneDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//DB파일이 존재하지 않을때 최초생성
        String query = "create table " + TABLE_NAME_MYINFO + " (" +
                "id text, " +
                "phoneNumber text, contents text, password text, lock text)";
        try {
            db.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        query = "create table " + TABLE_NAME_PHONELIST + " (" +
                "phoneNumber text PRIMARY KEY, " +
                "x text, y text)";
        try {
            db.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //DB파일 버전이 변경되었을때
        try {
            db.execSQL("drop table if exists " + TABLE_NAME_MYINFO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            db.execSQL("drop table if exists " + TABLE_NAME_PHONELIST);
        } catch (Exception e) {
            e.printStackTrace();
        }

        onCreate(db);
    }
}
