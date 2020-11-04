package com.impeach.peony;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ActivationCode extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activation_code_layout);


        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
                getAllValidActivationCodes(),
                R.layout.activation_code_layout_list_show_codes,
                new String[]{"Code", "InputDate", "Duration","BegenningDate","ExpiredDate"},
                new int[]{R.id.tv_ActivationCode_list_activationCode,
                        R.id.tv_ActivationCode_list_activationDate,
                        R.id.tv_ActivationCode_list_codeDuration,
                        R.id.tv_ActivationCode_list_codeStartDate,
                        R.id.tv_ActivationCode_list_codeExpiredDate});

        ListView ll = (ListView) findViewById(R.id.lv_ActivationCode_container_showCurrentlyValidActivationCode);
        ll.setAdapter(adapter);

        Button acti=(Button)findViewById(R.id.bt_ActivationCode_putInNewCode);
        acti.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(!isConnected()){
                    Toast("当前没有网络连接，请稍后再试！");
                    return;
                }

                EditText editText = (EditText) findViewById(R.id.et_ActivationCode_putInNewCode);
                String str = editText.getText().toString();
                if (str.isEmpty()){
                    Toast("请输入激活码！");
                }else if(str.length()!=16){
                    Toast("您的输入有误，请重新输入！请注意，激活码为16位字母和数字组合！");
                }else if(str.equals(CONSTANT_TEST_ACTIVATION_CODE)) {
                    Toast("此处不能使用测试码，请重新输入");
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
                                            break;
                                        case 88802:
                                            internetSucceededDialog("88802");
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

    }
}
