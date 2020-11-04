package com.impeach.peony;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.content.ContentValues.TAG;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String TAG = "MainActivity";
        //there are 4 parts in this activity.
        //1,initialize the TemporaryTable and Settings table.
        //2,to decide which interface should be display.
        //3,handle the elements on the interface before activation.
        //4,handle the elements on the interface after activation.

        //1,initialize the TemporaryTable and Settings table.
        SQLiteDatabase db=MyDatabase.getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put("Name","giraffe" );
        values.put("Total", 0);
        values.put("NotExist",0);
        values.put("AlreadyFriend", 0);
        values.put("DirectSuccess", 0);
        values.put("InvitationSent", 0);
        values.put("StatusAbnormal", 0);
        values.put("ValidData", 0);
        db.insert("TemporaryTable", null, values);

        ContentValues values01=new ContentValues();
        values01.put("Amount",10);
        values01.put("Wechat","change to your own");
        values01.put("ServiceSwitch","off");
        values01.put("GoBackTimes",0);
        values01.put("CaptureToast",1);
        db.insert("Settings", null, values01);
        //db.close();

        //2,to decide which interface should be display.
        LinearLayout beforeActivation=(LinearLayout)findViewById(R.id.ll_MainActivity_beforeActivation);
        LinearLayout afterActivation=(LinearLayout)findViewById(R.id.ll_MainActivity_afterActivation);
        if(isTimeValid(getAppExpiredDate())==1){
            log("it's good.");
        }else{
            beforeActivation.setVisibility(View.VISIBLE);
            afterActivation.setVisibility(View.GONE);
        }

        //3,handle the elements on the interface before activation.
        //3.1,activation button.
        //3.2,tips before activation.
        //3.3, re-activate current device.

        //3.1,activation button.
        Button acti=(Button)findViewById(R.id.bt_MainActivity_putInActivationCode);
        acti.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(!isConnected()){
                    Toast("当前没有网络连接，请稍后再试！");
                    return;
                }
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
                    Toast("请为本软件打开“获取手机信息”的授权，然后再试！");
                    return;
                }

                EditText editText = (EditText) findViewById(R.id.et_MainActivity_putInActivationCode);
                String str = editText.getText().toString();
                if (str.isEmpty()){
                    Toast("请输入激活码！");
                }else if(str.length()!=16){
                    Toast("您的输入有误，请重新输入！请注意，激活码为16位字母和数字组合！");
                }else{
                    Toast("激活码已提交，请等待！");
                    OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10,TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .build();

                    try{
                        //唯一imei地址，需要添加授权。<uses-permission android:name="android.permission.READ_PHONE_STATE" />
                        TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
                        szImei = TelephonyMgr.getDeviceId();
                    }catch (SecurityException e){
                           Toast("你没有开启对此APP的授权，请仔细阅读使用说明！");
                    }

                    //MediaType  设置Content-Type 标头中包含的媒体类型值
                    FormBody formBody=new FormBody.Builder()
                            .add("activation_code",str)
                            .add("device_id",szImei)
                            .add("agent",CONSTANT_AGENT)
                            .add("current_time",getCurrentTime())
                            .build();

                    //RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8") , str);

                    Request request = new Request.Builder()
                            .url(CONSTANT_URL)//请求的url
                            .post(formBody)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        Handler handler = new Handler(Looper.getMainLooper()) { //重点在此处
                            @Override
                            public void handleMessage(Message msg) {
                                Bundle bundle=msg.getData();
                                String data=bundle.getString("res");
                                try{
                                    JSONObject jsonObject = new JSONObject(data);
                                    //int code = gson.fromJson(array.get(0), int.class);
                                    int code=jsonObject.optInt("code", 0);
                                    switch(code){
                                        case 88801:
                                            internetSucceededDialog("88801");
                                            log("i'm 8001");
                                            break;
                                        case 88802:
                                            internetSucceededDialog("88802");
                                            log("i'm 8002");
                                            break;
                                        case 88803:
                                            internetSucceededDialog("88803");
                                            break;
                                        case 88804:
                                            internetSucceededDialog("88804");
                                            break;
                                        case 88805:
                                            Log.d("hello","i'm 88805!");
                                            int duration=jsonObject.optInt("duration", 0);
                                            String z=jsonObject.optString("msg","sorry");
                                            insertFormalData(z,duration);
                                            internetSucceededDialog("88805");
                                            break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        //请求错误回调方法
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            Log.d("response","failed");
                            if (e instanceof SocketTimeoutException) {
                                Log.d(TAG,"连接超时啦！");
                            }
                            if (e instanceof ConnectException) {
                                ////判断连接异常，
                                Log.d(TAG,"连接异常！");
                            }
                            internetFailedDialog();
                        }
                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            Log.d("response","successful");
                            String data = response.body().string();
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("res", data);
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        }
                    });
                }
            }
        });

        //3.2,tips before activation.
        LinearLayout tipsBeforeActivation=(LinearLayout) findViewById(R.id.ll_MainActivity_tipsBeforeActivation);
        TextView crocodile=new TextView(this);
        crocodile.setText(String.format(getResources().getString(R.string.ll_MainActivity_tipsBeforeActivation),CONSTANT_TYPE,CONSTANT_AGENT));
        crocodile.setTextColor(getResources().getColor(R.color.purple));
        crocodile.setTextSize(2,20);
        tipsBeforeActivation.addView(crocodile);

        //3.3, re-activate current device.
        Button tern=(Button)findViewById(R.id.bt_MainActivity_re_activate);
        tern.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(!isConnected()){
                    Toast("当前没有网络连接，请稍后再试！");
                    return;
                }
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
                    Toast("请为本软件打开“获取手机信息”的授权，然后再试！");
                    return;
                }

                /*EditText editText = (EditText) findViewById(R.id.et_MainActivity_putInActivationCode);
                String str = editText.getText().toString();
                if (str.isEmpty()){
                    Toast("请输入激活码！");
                }else if(str.length()!=16){
                    Toast("您的输入有误，请重新输入！请注意，激活码为16位字母和数字组合！");
                }else{*/
                    Toast("已向服务器发送请求，请等待！");
                    OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10,TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .build();

                    try{
                        //唯一imei地址，需要添加授权。<uses-permission android:name="android.permission.READ_PHONE_STATE" />
                        TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
                        szImei = TelephonyMgr.getDeviceId();
                    }catch (SecurityException e){
                        Toast("你没有开启对此APP的授权，请仔细阅读使用说明！");
                    }

                    //MediaType  设置Content-Type 标头中包含的媒体类型值
                    FormBody formBody=new FormBody.Builder()
                            .add("activation_code","grieved")
                            .add("device_id",szImei)
                            .add("current_time",getCurrentTime())
                            .build();

                    //RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8") , str);

                    Request request = new Request.Builder()
                            .url(CONSTANT_URL)//请求的url
                            .post(formBody)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        Handler handler = new Handler(Looper.getMainLooper()) { //重点在此处
                            @Override
                            public void handleMessage(Message msg) {
                                Bundle bundle=msg.getData();
                                String data=bundle.getString("res");
                                try{
                                    JSONObject jsonObject = new JSONObject(data);
                                    //int code = gson.fromJson(array.get(0), int.class);
                                    int code=jsonObject.optInt("code", 0);

                                    switch(code){
                                        case 88806:
                                            Log.d(TAG,"88806");
                                            JSONArray jsonArray =jsonObject.optJSONArray("list");
                                            if (jsonArray != null && jsonArray.length() > 0){
                                                for(int i = 0; i < jsonArray.length(); i++){
                                                    Log.d(TAG,"insert No "+i);
                                                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                                                    //JSONObject jsonObjectItem = jsonArray.getJSONObject(i);
                                                    /*JSONObject jsonObjectItem =jsonArray.toJSONObject(jsonArray);
                                                    jsonObjectItem=jsonObject.get(i);
                                                    JSONObject jsonOb =jsonArray.get(i);*/

                                                    if (jsonObjectItem != null){
                                                        Log.d(TAG,"No "+i+" is not empty.");
                                                        String activation_code=jsonObjectItem.optString("activation_code");
                                                        String s=jsonObjectItem.optString("duration");
                                                        String activation_time=jsonObjectItem.optString("activation_time");
                                                        int duration=Integer.parseInt(s);
                                                        String activation_date=activation_time.substring(0,10);
                                                        insertFormalDataForReActivate(activation_code,duration,activation_date);
                                                        Log.d(TAG,"activation_code is "+activation_code);
                                                        Log.d(TAG,"activation_time is "+activation_time);
                                                        Log.d(TAG,"duration is "+duration);
                                                        try {
                                                            Thread.sleep(1000);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                            if(isTimeValid(getAppExpiredDate())==1){
                                                reactivationDialog("001");
                                            }else{
                                                reactivationDialog("002");
                                                initializeTable("ActivationCode");
                                            }
                                            break;
                                        case 88807:
                                            String t=jsonObject.optString("msg");
                                            String time=t.substring(0,10);
                                            String s=getDayFromAPoint(time,3);
                                            if(isTimeValid(s) == 1){
                                                insertTestDataForReActivate(time,s);
                                                reactivationDialog("003");
                                        }else{
                                                reactivationDialog("004");
                                            }
                                            break;
                                        case 88808:
                                            reactivationDialog("005");
                                            break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        //请求错误回调方法
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            Log.d("response","failed");
                            internetFailedDialog();
                        }
                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            Log.d("response","successful");
                            String data = response.body().string();
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("res", data);
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        }
                    });
                }
           // }
        });


        //4,handle the elements on the interface after activation.
        //4.1,settings button.
        //4.2,begin work button.
        //4.3,tips after activation.

        //4.1,settings button.
        Button button001 = (Button)findViewById(R.id.bt_MainActivity_settings);
        button001.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });

        //4.2,begin work button.
        //4.2.1 it will check whether the accessibility service has been opened.
        //4.2.2 if opened, it will handle the TemporaryTable and switch to Wechat App.
        //4.2.3 if closed, it will switch to the corresponding interface for user to open it.
        Button bt_begin_work = (Button)findViewById(R.id.bt_MainActivity_beginWork);
        bt_begin_work.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                Context context=MainActivity.this;
                String name="Iamaservice";
                AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
                List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
                for (AccessibilityServiceInfo info : serviceInfos) {
                    String id = info.getId();
                    log(id);
                    if (id.contains(name)) {
                        log("the accessibility service has been opened.");

                        //set the serviceSwitch in the Settings table to be the status of On.
                        setServiceSwitch("on");
                        initializeGoBackTimes();
                        setCaptureToast(0);

                        /*String serviceStatus="what";
                        SQLiteDatabase db_switch=MyDatabase.getReadableDatabase();
                        Cursor cursor = db_switch.query("Settings", null,null, null, null, null, null);
                        if(cursor.getCount()>0){
                            cursor.moveToFirst();
                            serviceStatus=cursor.getString(cursor.getColumnIndex("ServiceSwitch"));
                        }
                        cursor.close();
                        db_switch.close();
                        log("serviceStatus is "+ serviceStatus);*/

                        handleTemporaryTableBeforeNewRound();
                        Intent intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                        startActivity(intent);
                        return;
                    }
                }
                Toast("现在将跳转至辅助功能页面，请开启服务。");
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        //4.3,tips after activation.
        LinearLayout amountRound=(LinearLayout) findViewById(R.id.ll_MainActivity_showTipsAfterActivation);
        TextView dog=new TextView(this);
        dog.setText(String.format(getResources().getString(R.string.ll_MainActivity_tipsAfterActivation),getAppExpiredDate(),CONSTANT_TYPE,CONSTANT_AGENT));
        dog.setTextColor(getResources().getColor(R.color.purple));
        dog.setTextSize(2,20);
        amountRound.addView(dog);

    }
}
