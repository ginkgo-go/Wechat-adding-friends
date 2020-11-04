package com.impeach.peony;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AmountEachRound extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amount_each_round_layout);

        //show the current amount value.
        LinearLayout tv=(LinearLayout) findViewById(R.id.ll_AmountEachRound_currentAmount);
        TextView mtv=new TextView(this);
        String s = String.valueOf(getAmountEachRound());//将int转化为字符串
        mtv.setText(s);
        mtv.setTextSize(2,20);
        mtv.setTextColor(getResources().getColor(R.color.purple));
        tv.addView(mtv);

        //button to save the modification.
        Button button01=(Button)findViewById(R.id.bt_AmountEachRound_modifyAmount);
        button01.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                EditText editText = (EditText) findViewById(R.id.et_AmountEachRound_modifyAmount);
                String str = editText.getText().toString();
                if(str.isEmpty()){
                    log("it's empty.");
                    Toast("请输入你要修改的值！");
                }else if(str.equals("0")|str.equals("00")){
                    log("it's zero.");
                    Toast("不好意思，不能修改为0哦！");
                }else{
                    log("that's right.");
                    int n = Integer.parseInt(str);
                    setAmountEachRound(n);
                    goBack();
                }
            }
        });

    }

    public void goBack(){
        Intent intent=new Intent(AmountEachRound.this,Settings.class);
        startActivity(intent);
    }
}
