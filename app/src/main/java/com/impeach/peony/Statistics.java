package com.impeach.peony;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Statistics extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_layout);

        LinearLayout blank_show=(LinearLayout)findViewById(R.id.ll_Statistics_blankShow);
        LinearLayout menu_show=(LinearLayout)findViewById(R.id.ll_Statistics_contentShow);
        SQLiteDatabase db=MyDatabase.getReadableDatabase();
        Cursor cursor=db.query("RecordTable",null,null,null,null,null,null,null);
        if(cursor.getCount()>0){
            SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
                    getTenOperationRecords(),
                    R.layout.statistics_layout_list_show_operation_records,
                    new String[]{"Time", "Total","Success", "NotExist"},
                    new int[]{R.id.tv_Statistics_list_operationTime,
                            R.id.tv_Statistics_list_totalNumbers,
                            R.id.tv_Statistics_list_validOperations,
                            R.id.tv_Statistics_list_badNumbers});

            ListView ll = (ListView) findViewById(R.id.lv_Statistics_container_showOperationRecords);
            ll.setAdapter(adapter);
            cursor.close();
        }else{
            blank_show.setVisibility(View.VISIBLE);
            menu_show.setVisibility(View.GONE);
        }

    }
}
