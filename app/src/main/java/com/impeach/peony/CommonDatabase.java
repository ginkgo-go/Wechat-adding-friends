package com.impeach.peony;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CommonDatabase extends SQLiteOpenHelper {
    private Context mContext;
    private static final String DATABASE_NAME = "CommonDatabase.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "QQNumberForWeChat";
    private static final String TABLE_NAME01 = "TemporaryTable";
    private static final String TABLE_NAME02 = "RecordTable";
    private static final String TABLE_NAME03 = "Settings";
    private static final String TABLE_NAME04 = "ActivationCode";

    private static CommonDatabase instance;

    public CommonDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static CommonDatabase Instance(Context context) {
        if (instance == null) {
            instance = new CommonDatabase(context);
        }
        return instance;
    }

    @Override//创建表
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + " (QQNumber, Used, AfterUseStatus, HandleTime, HandleWeChat, Occupied01, Occupied02);";
        Log.i("createDB=", sql);
        db.execSQL(sql);

        //Toast.makeText(mContext,"Database is created successfully", Toast.LENGTH_SHORT).show();
//      db.close();
        String sql01 = "CREATE TABLE " + TABLE_NAME01
                + " (Name, Total, NotExist, AlreadyFriend, DirectSuccess, InvitationSent, StatusAbnormal,ValidData);";
        Log.i("createDB=", sql01);
        db.execSQL(sql01);

        String sql02 = "CREATE TABLE " + TABLE_NAME02
                + " (Time,Total,NotExist,Success,Number);";
        Log.i("createDB=", sql02);
        db.execSQL(sql02);

        //ServiceSwitch: on/off. it is to control whether the procedure should be kicked off.
        //GoBackTimes: integer. it is to avoid  repeating go-back operation in vain.
        //CaptureToast: integer. it is to distinguish whether it can begin the next step after sending an invitation request.
        String sql03 = "CREATE TABLE " + TABLE_NAME03
                + " (Amount,Wechat,CaptureToast,ServiceSwitch,GoBackTimes);";
        Log.i("createDB=", sql03);
        db.execSQL(sql03);

        String sql04 = "CREATE TABLE " + TABLE_NAME04
                + " (Code,InputDate,Duration,BegenningDate,ExpiredDate);";
        Log.i("createDB=", sql04);
        db.execSQL(sql04);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }

}
