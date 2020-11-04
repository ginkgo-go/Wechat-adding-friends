package com.impeach.peony;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Temporary extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temporary_layout);

        Button p=(Button)findViewById(R.id.bt_Temporary_clearDatabase);
        p.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                SQLiteDatabase db=MyDatabase.getWritableDatabase();
                db.execSQL("delete from ActivationCode");
                //db.close();
                refresh();
            }
        });

        Button q=(Button)findViewById(R.id.bt_Temporary_showDatabase);
        q.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                SQLiteDatabase db=MyDatabase.getReadableDatabase();
                Cursor cursor=db.query("ActivationCode",null,null,null,null,null,null,null);
                if(cursor.getCount()>0){
                    //cursor.moveToFirst();
                    while(cursor.moveToNext()){
                        Log.d("Code",cursor.getString(cursor.getColumnIndex("Code")));
                        Log.d("InputDate",cursor.getString(cursor.getColumnIndex("InputDate")));
                        Log.d("Duration",cursor.getString(cursor.getColumnIndex("Duration")));
                        Log.d("BegenningDate",cursor.getString(cursor.getColumnIndex("BegenningDate")));
                        Log.d("ExpiredDate",cursor.getString(cursor.getColumnIndex("ExpiredDate")));
                    }
                    cursor.close();
                }else{
                    Log.d("database","is empty!");
                }
            }
        });

        Button mj=(Button)findViewById(R.id.bt_Temporary_fillRecordTable);
        mj.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                SQLiteDatabase db=MyDatabase.getWritableDatabase();
                ContentValues contentValues=new ContentValues();
                for(int i=1;i<11;i++){
                    contentValues.put("Time",beforeAfterDate(0-i)+"11:42:25");
                    contentValues.put("Total",3*i+7);
                    contentValues.put("NotExist",i+6);
                    contentValues.put("Success",2*i+1);
                    contentValues.put("Number",i);
                    db.insert("RecordTable", null, contentValues);
                }
                //db.close();
                Toast("Insert has been successful!");

                SQLiteDatabase db01=MyDatabase.getReadableDatabase();
                Cursor cursor01=db01.query("RecordTable",null,null,null,null,null,null,null);
                if(cursor01.getCount()>0){
                    Toast("the database has"+cursor01.getCount()+"pieces data now.");
                    cursor01.close();
                }
            }
        });

    }
}
