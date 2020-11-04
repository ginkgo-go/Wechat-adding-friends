package com.impeach.peony;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class BaseActivity extends Activity {
    CommonDatabase MyDatabase=new CommonDatabase(this);;
    TxtContentHandler txtContentHandler=new TxtContentHandler();
    String szImei=null;
    final String CONSTANT_URL="http://211.149.130.197:8080/Peony/servlet/LoginDateServlet";
    //final String CONSTANT_URL="http://192.168.42.229:8080/Peony/servlet/LoginDateServlet";
    final String CONSTANT_AGENT="1049030715";      //QQ号或微信号
    final String CONSTANT_TYPE="QQ";
    final String CONSTANT_TEST_ACTIVATION_CODE="aaaabbbbccccdddd";
    String TAG="BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
    }

    public void Toast(String str)
    {
        android.widget.Toast.makeText(this, str, android.widget.Toast.LENGTH_SHORT).show();
    }

    //to judge whether current time is valid
    public int isTimeValid(String endDate){
        SimpleDateFormat validTime = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis()); //获取当前时间
        String currentDate = validTime.format(curDate);   //格式转换
        int i=0;
        try {
            Date date = validTime.parse(endDate);
            Date now = validTime.parse(currentDate);
            if (now.getTime()<=date.getTime()){
                i=1;
            }else {
                i=2;
            }
            String fly = Long.toString(now.getTime());
            String fly01 = Long.toString(date.getTime());
            Log.d("now",fly);
            Log.d("limit",fly01);
        }catch (Exception e){
        }
        return i;
    }

    public String getAppExpiredDate() {

        SQLiteDatabase db = MyDatabase.getReadableDatabase();
        Cursor cursor = db.query("ActivationCode", new String[]{"ExpiredDate"}, null, null, null, null, "InputDate desc", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String date=cursor.getString(cursor.getColumnIndex("ExpiredDate"));
            cursor.close();
            return date;
        }else{
            return "2020-06-25";//if there is no records in the database, the data i provided here must have been expired.
            //this is to avoid to provide a null to the next method.
        }
    }

    public void log(String s){
        Log.d("The information is :",s);
    }

    public void refresh() {
        onCreate(null);
    }

    //获取当前时间前后几天的时间
    public static String beforeAfterDate(int days) {
        long nowTime = System.currentTimeMillis();
        long changeTimes = days * 24L * 60 * 60 * 1000;
        return getStrTime(String.valueOf(nowTime + changeTimes), "yyyy-MM-dd");
    }

    //时间戳转字符串
    public static String getStrTime(String timeStamp, String format) {
        String timeString= null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        long l = Long.valueOf(timeStamp);
        timeString= sdf.format(new Date(l));//单位秒
        return timeString;
    }

    public String getCurrentTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()); //设置时间格式
        //formatter.setTimeZone(TimeZone.getTimeZone("GMT+08")); //设置时区
        Date curDate = new Date(System.currentTimeMillis()); //获取当前时间
        return formatter.format(curDate);   //格式转换
    }

    public String getCurrentDay(){
        SimpleDateFormat validTime = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date curDate01 = new Date(System.currentTimeMillis()); //获取当前时间
        return validTime.format(curDate01);
    }



    public static String getDayFromAPoint(String time,int days) throws java.text.ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(time +" 12:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long anchorTime=date.getTime();
        long changeTimes = days * 24L * 60 * 60 * 1000;
        return getStrTime(String.valueOf(anchorTime + changeTimes), "yyyy-MM-dd");

    }

    public void internetSucceededDialog(String status) //这个方法是弹出一个对话框
    {
        log("the alterDialog begins.");
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.ic_launcher);//设置图标
        switch(status){
            case "88801":
                builder.setTitle("激活码状态");//设置对话框的标题
                builder.setMessage("对不起，每台设备只能试用1次，本设备不能再次试用！");//设置对话框的内容
                break;
            case "88802":
                builder.setTitle("激活码状态");//设置对话框的标题
                builder.setMessage("本设备已激活，欢迎您试用！");//设置对话框的内容
                insertTestData();
                break;
            case "88803":
                builder.setTitle("激活码状态");//设置对话框的标题
                builder.setMessage("对不起，您输入的激活码无效，请您重新输入！");//设置对话框的内容
                break;
            case "88804":
                builder.setTitle("激活码状态");//设置对话框的标题
                builder.setMessage("该激活码已在其他设备使用，请重新输入！");//设置对话框的内容
                break;
            case "88805":
                builder.setTitle("激活码状态");//设置对话框的标题
                builder.setMessage("恭喜您，该激活码已成功激活，祝您使用愉快！");//设置对话框的内容
                break;
        }

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  //这个是设置确定按钮

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(MainActivity.this, "自宫成功", Toast.LENGTH_SHORT).show();
                refresh();
            }
        });
        /*builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  //取消按钮

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(MainActivity.this, "取消自宫",Toast.LENGTH_SHORT).show();

            }
        });*/
        AlertDialog b=builder.create();
        b.show();  //必须show一下才能看到对话框，跟Toast一样的道理
    }

    public void internetFailedDialog() //这个方法是弹出一个对话框
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("网络连接状态");//设置对话框的标题
        builder.setMessage("对不起，服务器访问失败，请稍后重试！");//设置对话框的内容
        Looper.prepare();
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  //这个是设置确定按钮
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //do nothing.
            }
        });
        AlertDialog b=builder.create();
        b.show();  //必须show一下才能看到对话框，跟Toast一样的道理
        Looper.loop();
    }

    public void insertTestData(){

        SQLiteDatabase db=MyDatabase.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("Code",CONSTANT_TEST_ACTIVATION_CODE );
        values.put("InputDate",getCurrentTime());
        values.put("Duration","3天");
        values.put("BegenningDate",getCurrentDay());
        values.put("ExpiredDate",beforeAfterDate(3));
        db.insert("ActivationCode", null, values);
    }

    public void insertTestDataForReActivate(String a,String b){

        SQLiteDatabase db=MyDatabase.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("Code",CONSTANT_TEST_ACTIVATION_CODE );
        values.put("InputDate",getCurrentTime());
        values.put("Duration","3天");
        values.put("BegenningDate",a);
        values.put("ExpiredDate",b);
        db.insert("ActivationCode", null, values);
    }


    public void insertFormalData(String code,int year){
        SQLiteDatabase db=MyDatabase.getWritableDatabase();
        ContentValues values=new ContentValues();
        Log.d("i'm","inserting formal data!");
        //String d=Integer.toString(year*365);
        values.put("Code",code);
        values.put("InputDate",getCurrentTime());
        values.put("Duration", year+"年");
        if(isValidCodeExist()){
            String m=getAppExpiredDate();
            String n=Integer.toString(Integer.parseInt(m.substring(0,4))+year)+m.substring(4);
            values.put("BegenningDate",m);
            values.put("ExpiredDate",n);
        }else{
            String m=getCurrentDay();
            String n=Integer.toString(Integer.parseInt(m.substring(0,4))+year)+m.substring(4);
            values.put("BegenningDate",m);
            values.put("ExpiredDate",n);
        }
        db.insert("ActivationCode", null, values);
    }

    public void insertFormalDataForReActivate(String code,int year,String date){
        SQLiteDatabase db=MyDatabase.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("Code",code);
        values.put("InputDate",getCurrentTime());
        values.put("Duration", year+"年");
        if(isValidCodeExist()){
            String m=getAppExpiredDate();
            String n=Integer.toString(Integer.parseInt(m.substring(0,4))+year)+m.substring(4);
            values.put("BegenningDate",m);
            values.put("ExpiredDate",n);
        }else{
            String m=date;
            String n=Integer.toString(Integer.parseInt(m.substring(0,4))+year)+m.substring(4);
            values.put("BegenningDate",m);
            values.put("ExpiredDate",n);
        }
        db.insert("ActivationCode", null, values);
    }

    //does the user now have valid activation code?
    public boolean isValidCodeExist(){
        SQLiteDatabase db=MyDatabase.getReadableDatabase();
        Cursor cursor=db.query("ActivationCode",null,null,null,null,null,"InputDate desc",null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            String time=cursor.getString(cursor.getColumnIndex("ExpiredDate"));
            cursor.close();
            if(isTimeValid(time)==1){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    //should be deleted.**************************************************
    public String getAmountOfAddFriendsEachRound(){
        SQLiteDatabase db=MyDatabase.getReadableDatabase();
        Cursor cursor=db.query("Settings",null,null,null,null,null,null,null);
        cursor.moveToFirst();
        int amount=cursor.getInt(cursor.getColumnIndex("Amount"));
        String s = String.valueOf(amount);//将int转化为字符串
        cursor.close();
        return s;
    }

    public int getCurrentlyValidNumbersAmount(){
        SQLiteDatabase db=MyDatabase.getReadableDatabase();
        Cursor cursor=db.query("QQNumberForWeChat",new String[]{"Used"},"Used=?",new String[]{"N"},null,null,null,null);
        int result=cursor.getCount();
        cursor.close();
        return result;
    }

    //when insert the number of potential friends into the SQLite database, verify whether the numbers have existed in the database.
    public Boolean isNumberExist(){
        List gorilla= txtContentHandler.Txt();
        String qq=gorilla.get(0).toString();
        CommonDatabase MyDatabase =new CommonDatabase(this);

        SQLiteDatabase db=MyDatabase.getReadableDatabase();
        Cursor cursor = db.query("QQNumberForWeChat", new String[]{"QQNumber"}, "QQNumber = ?", new String[]{qq}, null, null, null);
        if(cursor.getCount()==0) {
            cursor.close();
            //db.close();
            return true;
        }else {
            cursor.close();
            //db.close();
            return false;
        }
    }

    //get the amount for each round of potential friends.
    public int getAmountEachRound(){
        int amount=0;
        SQLiteDatabase db=MyDatabase.getReadableDatabase();
        Cursor cursor=db.query("Settings",null,null,null,null,null,null,null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            amount=cursor.getInt(cursor.getColumnIndex("Amount"));
        }
        cursor.close();
        //db.close();
        return amount;
    }

    //set the amount for each round of potential friends.
    public void setAmountEachRound(int i){
        SQLiteDatabase db = MyDatabase.getWritableDatabase();
        ContentValues valuesNew = new ContentValues();
        valuesNew.put("Amount",i);
        db.update("Settings", valuesNew, "Wechat=?", new String[]{"change to your own"});
        //db.close();
    }


    //to get all valid activation codes currently.
    public ArrayList<HashMap<String, Object>> getAllValidActivationCodes(){
        CommonDatabase MyDatabase =new CommonDatabase(this);
        ArrayList<HashMap<String,Object>> QQNum=new ArrayList<HashMap<String,Object>>();
        SQLiteDatabase db=MyDatabase.getReadableDatabase();
        Cursor cursor=db.query("ActivationCode",null,null,null,null,null,null,null);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            for ( int i = 0;i<cursor.getCount();i++){
                String Code = cursor.getString(cursor.getColumnIndex("Code"));
                String InputDate = cursor.getString(cursor.getColumnIndex("InputDate")).substring(2,10);
                String Duration = cursor.getString(cursor.getColumnIndex("Duration"));
                String BegenningDate = cursor.getString(cursor.getColumnIndex("BegenningDate")).substring(2,10);
                String ExpiredDate = cursor.getString(cursor.getColumnIndex("ExpiredDate")).substring(2,10);
                /*Log.d(TAG,"code is "+Code);
                Log.d(TAG,"InputDate is "+InputDate);
                Log.d(TAG,"Duration is "+Duration);
                Log.d(TAG,"BegenningDate is "+BegenningDate);
                Log.d(TAG,"ExpiredDate is "+ExpiredDate);*/
                if(Code.equals(CONSTANT_TEST_ACTIVATION_CODE)){
                    Code="测试码";
                }else{
                    Code="..."+Code.substring(12);
                }

                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("Code", Code);
                map.put("InputDate", InputDate);
                map.put("Duration", Duration);
                map.put("BegenningDate", BegenningDate);
                map.put("ExpiredDate", ExpiredDate);

                QQNum.add(map);
                cursor.moveToNext();
            }
        }
        cursor.close();
        //db.close();
        return QQNum;
    }

    //to get the last 10 pieces of operation records.
    public ArrayList<HashMap<String, Object>> getTenOperationRecords(){
        CommonDatabase MyDatabase =new CommonDatabase(this);
        ArrayList<HashMap<String,Object>> QQNum=new ArrayList<HashMap<String,Object>>();
        SQLiteDatabase db=MyDatabase.getReadableDatabase();
        Cursor cursor=db.query("RecordTable",null,null,null,null,null,"Time asc",null);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            for ( int i = 0;i<cursor.getCount();i++){
                String Time = cursor.getString(cursor.getColumnIndex("Time"));
                String Total = cursor.getString(cursor.getColumnIndex("Total"));
                String NotExist = cursor.getString(cursor.getColumnIndex("NotExist"));
                String Success = cursor.getString(cursor.getColumnIndex("Success"));

                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("Time", Time);
                map.put("Total", Total);
                map.put("NotExist", NotExist);
                map.put("Success", Success);

                QQNum.add(map);
                cursor.moveToNext();
            }
        }
        cursor.close();
        //db.close();
        return QQNum;
    }

    public String getDeviceId(){
        String amount=null;
        SQLiteDatabase db=MyDatabase.getReadableDatabase();
        Cursor cursor=db.query("Settings",null,null,null,null,null,null,null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            amount=cursor.getString(cursor.getColumnIndex("DeviceId"));
        }
        cursor.close();
        //db.close();
        return amount;
    }

    public void setDeviceId(String s){
        SQLiteDatabase db = MyDatabase.getWritableDatabase();
        ContentValues valuesNew = new ContentValues();
        valuesNew.put("DeviceId",s);
        db.update("Settings", valuesNew, "Wechat=?", new String[]{"change to your own"});
        //db.close();
    }

    public void handleTemporaryTableBeforeNewRound(){
        SQLiteDatabase db = MyDatabase.getWritableDatabase();
        Cursor cursor=db.query("TemporaryTable",null,"Name=?",new String[]{"giraffe"},null,null,null,null);
        cursor.moveToFirst();
        int total = cursor.getInt(cursor.getColumnIndex("Total"));
        /*if(total>0){
            ContentValues values=new ContentValues();
            values.put("Total", 0);
            values.put("NotExist",0);
            values.put("AlreadyFriend", 0);
            values.put("DirectSuccess", 0);
            values.put("InvitationSent", 0);
            values.put("StatusAbnormal", 0);
            values.put("ValidData", 0);
            db.update("TemporaryTable",values,"Name=?",new String[]{"giraffe"});
        }else{
            log("just do it.");
        }*/

        ContentValues values=new ContentValues();
        values.put("Total", 0);
        values.put("NotExist",0);
        values.put("AlreadyFriend", 0);
        values.put("DirectSuccess", 0);
        values.put("InvitationSent", 0);
        values.put("StatusAbnormal", 0);
        values.put("ValidData", 0);
        db.update("TemporaryTable",values,"Name=?",new String[]{"giraffe"});

        cursor.close();
        //db.close();
    }

    public void setServiceSwitch (String status) {
        SQLiteDatabase db_setSwitch=MyDatabase.getWritableDatabase();
        ContentValues values_setSwitch=new ContentValues();
        values_setSwitch.put("ServiceSwitch",status );
        db_setSwitch.update("Settings", values_setSwitch, "Wechat=?", new String[]{"change to your own"});
        //db_setSwitch.close();
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (NullPointerException e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    public void initializeGoBackTimes(){
        CommonDatabase MyDatabase =new CommonDatabase(this);
        SQLiteDatabase db=MyDatabase.getWritableDatabase();
        ContentValues values=new ContentValues();
        Cursor cursor=db.query("Settings",null,"Wechat=?",new String[]{"change to your own"},null,null,null,null);
        cursor.moveToFirst();//if you don't manually move the cursor to first, the index will be -1.
        values.put("GoBackTimes",0);
        db.update("Settings",values,"Wechat=?",new String[]{"change to your own"});
        cursor.close();
    }

    public void setCaptureToast(int i){
        CommonDatabase MyDatabase =new CommonDatabase(this);
        SQLiteDatabase db=MyDatabase.getWritableDatabase();
        ContentValues values=new ContentValues();
        Cursor cursor=db.query("Settings",null,"Wechat=?",new String[]{"change to your own"},null,null,null,null);
        cursor.moveToFirst();//if you don't manually move the cursor to first, the index will be -1.
        values.put("CaptureToast",i);
        db.update("Settings",values,"Wechat=?",new String[]{"change to your own"});
        cursor.close();
    }


    public void reactivationDialog(String status) //this method is for reactivation.
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        switch(status){
            case "001":
                builder.setTitle("正式激活码");//设置对话框的标题
                builder.setMessage("欢迎回来，祝您使用愉快！");//设置对话框的内容
                break;
            case "002":
                builder.setTitle("正式激活码");//设置对话框的标题
                builder.setMessage("您的激活码均已过期，请您购买新的激活码");//设置对话框的内容
                break;
            case "003":
                builder.setTitle("测试激活码");//设置对话框的标题
                builder.setMessage("欢迎回来，请继续您的测试！");//设置对话框的内容
                break;
            case "004":
                builder.setTitle("测试激活码");//设置对话框的标题
                builder.setMessage("您用激活码测试过，但已经过期了！");//设置对话框的内容
                break;
            case "005":
                builder.setTitle("没有记录");//设置对话框的标题
                builder.setMessage("对不起，没有查询到本设备的激活记录，您可以使用测试激活码进行激活！");//设置对话框的内容
                break;
        }

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  //这个是设置确定按钮

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(MainActivity.this, "自宫成功", Toast.LENGTH_SHORT).show();
                refresh();
            }
        });
        AlertDialog b=builder.create();
        b.show();  //必须show一下才能看到对话框，跟Toast一样的道理
    }

    public void initializeTable(String name){
        SQLiteDatabase db=MyDatabase.getWritableDatabase();
        db.execSQL("DELETE FROM" + name);
    }


}

