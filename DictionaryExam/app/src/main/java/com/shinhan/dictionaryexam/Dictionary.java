package com.shinhan.dictionaryexam;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.name;
import static android.R.attr.version;

/**
 * Created by IC-INTPC-087109 on 2017-03-24.
 */

public class Dictionary extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "dictionary.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "voca";

    public Dictionary(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//DB파일이 존재하지 않을때 최초생성
        String query = "create table " + TABLE_NAME + " (" +
                "_id integer PRIMARY KEY autoincrement, " +
                "word text, definition text)";
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
            db.execSQL("drop table if exists " + TABLE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onCreate(db);
    }
}
