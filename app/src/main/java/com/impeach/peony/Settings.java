package com.impeach.peony;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.List;

public class Settings extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        //there are 5 buttons and 3 tips.
        //1,button to insert the numbers of potential friends.
        //2,tips of the amount of currently valid numbers.
        //3,button for statistics.
        //4,button for the amount of friends for each round.
        //5,tips of the current amount.
        //6,button to put in activation code.
        //7,tips of current expired date.
        //8,button to show the user manual.

        //1,button to insert the numbers of potential friends.
        //if there are numbers which have been used, they will be deleted after the insert operation.
        Button button7 = (Button)findViewById(R.id.bt_Settings_insertData);
        button7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if (ContextCompat.checkSelfPermission(Settings.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Toast("请为本软件打开“读写手机存储”的授权，然后再试！");
                    return;
                }
                SQLiteDatabase db = MyDatabase.getWritableDatabase();
                ContentValues values = new ContentValues();
                List gorilla = txtContentHandler.Txt();
                if (isNumberExist()) {
                    for (int i = 0; i < gorilla.size(); i++) {
                        values.put("QQNumber", txtContentHandler.Txt().get(i));
                        values.put("Used", "N");
                        values.put("AfterUseStatus", "foe");
                        values.put("HandleTime", "mare");
                        values.put("HandleWeChat", "foal");
                        db.insert("QQNumberForWeChat", null, values);
                    }
                    Log.d("kankan", "what are you doing?");
                    Toast.makeText(Settings.this, "数据已成功导入!", Toast.LENGTH_SHORT).show();
                }else {
                    Log.d("silly", "you are wrong");
                    Toast.makeText(Settings.this, "数据已存在，请勿重复导入!", Toast.LENGTH_SHORT).show();
                }

                db.delete("QQNumberForWeChat","Used=?",new String[]{"Y"});
            }
        });

        //2, tips of the amount of currently valid numbers.
        LinearLayout rawCode=(LinearLayout) findViewById(R.id.ll_Settings_currentlyValidNumberAmount);
        TextView donkey=new TextView(this);
        donkey.setText(String.format(getResources().getString(R.string.ll_Settings_currentlyValidNumberAmount),getCurrentlyValidNumbersAmount()));
        rawCode.addView(donkey);

        //3,button for statistics.
        Button buttonshow=(Button)findViewById(R.id.bt_Settings_statistics);
        buttonshow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                Intent intent=new Intent(Settings.this,Statistics.class);
                startActivity(intent);
            }
        });

        //4,button for the amount of friends for each round.
        Button buttonAmount=(Button)findViewById(R.id.bt_Settings_amountOfFriendsForEachRound);
        buttonAmount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                Intent intent=new Intent(Settings.this,AmountEachRound.class);
                startActivity(intent);
            }
        });

        //5, tips of the current amount.
        //linearLayout can use the addView method. TextView doesn't have this method. i don't know about other layout modes.
        LinearLayout amountRound=(LinearLayout) findViewById(R.id.ll_Settings_amountOfFriendsForEachRound);
        TextView dog=new TextView(this);
        dog.setText(String.format(getResources().getString(R.string.ll_Settings_amountOfFriendsForEachRound),getAmountOfAddFriendsEachRound()));
        amountRound.addView(dog);

        //6,button to put in activation code.
        Button buttonact=(Button)findViewById(R.id.bt_Settings_activationCode);
        buttonact.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                Intent intent=new Intent(Settings.this,ActivationCode.class);
                startActivity(intent);
            }
        });

        //7, tips of current expired date.
        LinearLayout activation=(LinearLayout) findViewById(R.id.ll_Settings_activationCode);
        TextView cat=new TextView(this);
        cat.setText(String.format(getResources().getString(R.string.ll_Settings_activationCode),getAppExpiredDate()));
        activation.addView(cat);

        //8,button to show the user manual.
        Button user_manual=(Button)findViewById(R.id.bt_Settings_userManual);
        user_manual.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                Intent intent=new Intent(Settings.this,UserManual.class);
                startActivity(intent);
            }
        });

    }
}
