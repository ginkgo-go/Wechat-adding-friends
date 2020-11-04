package com.impeach.peony;

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import static android.content.ContentValues.TAG;


public class Iamaservice extends AccessibilityService {
    BaseActivity baseActivity= new BaseActivity();
    CommonDatabase MyDatabase =new CommonDatabase(this);
    final public int amount01=10000;
    private static final String TAG = "Iamaservice";

    @Override
    public void onInterrupt(){

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event){


        if(getCaptureToast()>0) {
            //this step is to get the toast from Wechat after clicking the "send" button.
            if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                Log.d(TAG, "检测到通知啦");
                String sourcePackageName = (String) event.getPackageName();
                String message = "";
                Parcelable parcelable = event.getParcelableData();
                if (!(parcelable instanceof Notification)) {
                    message = event.getText().get(0).toString();
                    String awful = "操作过于频繁，请稍后再试";
                    String good = "已发送";
                    if (message.contains(awful)) {
                        Log.d(TAG, "操作频繁啦");
                        setValidData();
                        setCaptureToast(0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    Looper.prepare();
                                    AlertDialog dialog = new AlertDialog.Builder(getApplicationContext()).setTitle("请注意：")
                                            .setMessage("微信已提示操作频繁，请控制添加好友的频率！")
                                            .setCancelable(false)
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface argu0, int argu1) {
                                                }
                                            })
                                            .create();
                                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                                    dialog.show();
                                    Looper.loop();
                                }
                            };
                            Thread thread = new Thread(runnable);
                            thread.start();
                        }else{
                            goBack(5);
                        }
                    } else if (message.contains(good)) {
                        Log.d(TAG, "发送成功啦");
                        updateDoingStatus("已发送");
                        updateTemporaryTable("已发送");
                        setCaptureToast(0);
                    }
                }
            }
        }else {


            if (getServiceSwitch().equals("on")) {
                Log.d(TAG, "服务启动了");

                if (baseActivity.isTimeValid(getAppExpiredDate()) == 1) {
                    //for (;getAmountEachRound()>queryValidData() ;) {
                    if (getAmountEachRound() > queryValidData()) {

                        addFriend();
                    /*secondStep();
                    thirdStep();
                    fourthStep();
                    fifthStep();
                    lastStep();*/
                    }
                    if (queryValidData() == getAmountEachRound()) {
                        SQLiteDatabase db = MyDatabase.getWritableDatabase();
                        Cursor cursor01 = db.query("TemporaryTable", null, "Name=?", new String[]{"giraffe"}, null, null, null, null);
                        cursor01.moveToFirst();
                        int total = cursor01.getInt(cursor01.getColumnIndex("Total"));
                        int notExist = cursor01.getInt(cursor01.getColumnIndex("NotExist"));
                        int directSuccess = cursor01.getInt(cursor01.getColumnIndex("DirectSuccess"));
                        int invitationSent = cursor01.getInt(cursor01.getColumnIndex("InvitationSent"));
                        int alreadyFriend = cursor01.getInt(cursor01.getColumnIndex("AlreadyFriend"));
                        int statusAbnormal = cursor01.getInt(cursor01.getColumnIndex("StatusAbnormal"));
                        cursor01.close();

                        if(total>0) {
                            ContentValues values = new ContentValues();
                            values.put("Time", baseActivity.getCurrentTime());
                            values.put("Total", total);
                            values.put("NotExist", notExist + statusAbnormal);
                            values.put("Success", directSuccess + invitationSent + alreadyFriend);
                            int a = 0, b = 0;
                            Cursor cursor02 = db.query("RecordTable", null, null, null, null, null, "Time desc", null);
                            if (cursor02.getCount() > 0) {
                                if (cursor02.getCount() == 10) {
                                    cursor02.moveToFirst();
                                    a = cursor02.getInt(cursor02.getColumnIndex("Number"));
                                    if (a != 10) {
                                        String q = Integer.toString(a + 1);
                                        db.update("RecordTable", values, "Number=?", new String[]{q});
                                    } else {
                                        db.update("RecordTable", values, "Number=?", new String[]{"1"});
                                    }
                                } else {
                                    cursor02.moveToFirst();
                                    b = cursor02.getInt(cursor02.getColumnIndex("Number"));
                                    values.put("Number", b + 1);
                                    db.insert("RecordTable", null, values);
                                }
                            } else {
                                values.put("Number", 1);
                                db.insert("RecordTable", null, values);
                            }
                            cursor02.close();
                        }

                        db.close();
                        updateTemporaryTable("本轮已结束");
                        Log.d("round", "is over!");
                        //disableSelf();
                        //System.exit(0);

                        //set the serviceSwitch in the Settings table to be the status of Off.
                        setServiceSwitch("off");

                        setCaptureToast(0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //while the android API level is above 26, this function is available.
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    Looper.prepare();
                                    AlertDialog dialog = new AlertDialog.Builder(getApplicationContext()).setTitle("请注意：")
                                            .setMessage("本轮添加好友已结束！")
                                            .setCancelable(false)
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface argu0, int argu1) {
                                                }
                                            })
                                            .create();
                                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                                    dialog.show();
                                    Looper.loop();
                                }
                            };
                            Thread thread = new Thread(runnable);
                            thread.start();
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//while the android API level is above 24, this function is available.
                            this.disableSelf();
                        }

                        /*try {
                            Log.d(TAG, "开始休眠10分钟啦");
                            Thread.sleep(600000);//休眠10分钟
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/

                    /*try {
                        Thread.sleep(36000000);//休眠10个小时
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    }
                }
            } else {
                /*try {
                    Log.d(TAG, "开始休眠10分钟啦");
                    Thread.sleep(600000);//休眠10分钟
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                setCaptureToast(0);
            }
        }
    }



    public int queryValidData (){
        SQLiteDatabase db=MyDatabase.getReadableDatabase();
        Cursor cursor=db.query("TemporaryTable",new String[]{"ValidData"},"Name=?",new String[]{"giraffe"},null,null,null,null);
        cursor.moveToFirst();
        int validData = cursor.getInt(cursor.getColumnIndex("ValidData"));
        cursor.close();
        return validData;
    }

    public void setValidData (){
        SQLiteDatabase db=MyDatabase.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("ValidData",getAmountEachRound() );
        db.update("TemporaryTable", values, "Name=?", new String[]{"giraffe"});
        //db.close();
    }

    public void setServiceSwitch (String status) {
        SQLiteDatabase db_setSwitch=MyDatabase.getWritableDatabase();
        ContentValues values_setSwitch=new ContentValues();
        values_setSwitch.put("ServiceSwitch",status );
        db_setSwitch.update("Settings", values_setSwitch, "Wechat=?", new String[]{"change to your own"});
        //db_setSwitch.close();
    }

    public String getServiceSwitch (){
        String serviceStatus="helmet";
        SQLiteDatabase db_switch=MyDatabase.getReadableDatabase();
        Cursor cursor = db_switch.query("Settings", null,null, null, null, null, null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            serviceStatus=cursor.getString(cursor.getColumnIndex("ServiceSwitch"));
        }
        cursor.close();
        return serviceStatus;
    }

    public void addFriend () {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> myNodeInfo = nodeInfo.findAccessibilityNodeInfosByText("通讯录");
        List<AccessibilityNodeInfo> myNodeInfo01 = nodeInfo.findAccessibilityNodeInfosByText("微信");
        List<AccessibilityNodeInfo> myNodeInfo02 = nodeInfo.findAccessibilityNodeInfosByText("发现");
        List<AccessibilityNodeInfo> myNodeInfo03 = nodeInfo.findAccessibilityNodeInfosByText("我");

        int hl = myNodeInfo.size();
        String fly = Integer.toString(hl);
        Log.d("找到的list数量", fly);

        //this step is to make sure reaching the proper interface while beginning the procedure no matter what the original interface is.
        if(myNodeInfo.size()>0&&myNodeInfo01.size()>0&&myNodeInfo02.size()>0&&myNodeInfo03.size()>0){
            try{
                AccessibilityNodeInfo plusSign = myNodeInfo.get(0);
                plusSign.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

                try {
                    Thread.sleep(amount01);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initializeGoBackTimes();
                secondStep();

            }catch (IndexOutOfBoundsException e){
                setValidData();
            }
        }else{
            CommonDatabase MyDatabase =new CommonDatabase(this);
            SQLiteDatabase db=MyDatabase.getWritableDatabase();
            ContentValues values=new ContentValues();
            Cursor cursor=db.query("Settings",null,"Wechat=?",new String[]{"change to your own"},null,null,null,null);
            cursor.moveToFirst();
            int num=cursor.getInt(cursor.getColumnIndex("GoBackTimes"));
            if(num<6){
                addGoBackTimes();
                goBack(1);
            }else {
                setValidData();
            }

        }


        //NullPointerException
        //ArrayIndexOutOfBoundsException
        //IndexOutOfBoundsException

    }

    public void secondStep() {
        Log.d(TAG, "secondStep begins");
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> myNodeInfo = nodeInfo.findAccessibilityNodeInfosByText("新的朋友");
        int hl = myNodeInfo.size();
        String fly = Integer.toString(hl);
        Log.d("secondStep找到的list数量", fly);

        //this step is to try recovering while some unexpected mistakes have happened.
        if(myNodeInfo.size()==0){
            goBack(1);
            try {
                Thread.sleep(amount01);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }


        try{
            AccessibilityNodeInfo plusSign = myNodeInfo.get(0);
            plusSign.getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

            try {
                Thread.sleep(amount01);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            thirdStep();

        }catch (IndexOutOfBoundsException e){
            setValidData();
        }

    }

    public void thirdStep() {
        Log.d(TAG, "thirdStep begins");
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();

        try{
            AccessibilityNodeInfo plusSign = nodeInfo.getChild(0).getChild(3).getChild(0).getChild(0);
            plusSign.performAction(AccessibilityNodeInfo.ACTION_CLICK);

            try {
                Thread.sleep(amount01);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            fourthStep();

        }catch (NullPointerException e){
            setValidData();
        }

    }

    public void fourthStep(){
        Log.d(TAG, "fourthStep begins");
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();

        try{
            AccessibilityNodeInfo plusSign = nodeInfo.getChild(1);
            Bundle arguments = new Bundle();
            //2020年11月2日，碰到提示“对方被添加好友过于频繁”，就一直卡死在这个号码上。修改根据不同提示进行操作过于繁琐，只好放弃检测遗留Doing状态的号码。
            /*if(isDoingQQNumberExist()){
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, getDoingQQNumber());
                plusSign.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                Log.d(TAG,"有遗留的DoingQQNumber。");
            }else {*/
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, getNewQQNumber());
                plusSign.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                String qq = getNewQQNumber();
                changeToDoing(qq);//change the status of the current QQ number as Doing.
                Log.d("qq is", qq);
                Log.d("doing number is", getDoingQQNumber());
            //}
            try {
                Thread.sleep(amount01);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            fifthStep();
        }catch (NullPointerException e){
            setValidData();
        }

    }

    public void fifthStep(){
        Log.d(TAG,"fifthStep begins");
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> myNodeInfo = nodeInfo.findAccessibilityNodeInfosByText("搜索");
        int hl = myNodeInfo.size();
        String fly = Integer.toString(hl);
        Log.d("fifthStep找到的list数量", fly);

        //this step is to try recovering while some unexpected mistakes have happened.
        if(myNodeInfo.size()==0){
            goBack(1);
            try {
                Thread.sleep(amount01);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        try{
            AccessibilityNodeInfo plusSign = myNodeInfo.get(0);
            plusSign.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

            try {
                Thread.sleep(amount01);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            lastStep();
        }catch (ArrayIndexOutOfBoundsException e){
            setValidData();
        }

    }

    public void lastStep(){
        AccessibilityNodeInfo nodeInfo=getRootInActiveWindow();

        try{
            List<AccessibilityNodeInfo> myNodeInfo=nodeInfo.findAccessibilityNodeInfosByText("添加到通讯录");
            List<AccessibilityNodeInfo> myNodeInfo01=nodeInfo.findAccessibilityNodeInfosByText("该用户不存在");
            List<AccessibilityNodeInfo> myNodeInfo02=nodeInfo.findAccessibilityNodeInfosByText("被搜帐号状态异常，无法显示");
            List<AccessibilityNodeInfo> myNodeInfo03=nodeInfo.findAccessibilityNodeInfosByText("发消息");

            int hl = myNodeInfo.size();
            String fly = Integer.toString(hl);
            Log.d("添加到通讯录的list数量", fly);

            int hll = myNodeInfo01.size();
            String fly01 = Integer.toString(hll);
            Log.d("该用户不存在的list数量", fly01);

            int hlll = myNodeInfo02.size();
            String fly02 = Integer.toString(hlll);
            Log.d("帐号状态异常的list数量", fly02);

            if (myNodeInfo.size()>0) {//用myNodeInfo!=null的方式判断不行，屏幕中即使没有这个字段，它也不是null。
                try{
                    AccessibilityNodeInfo plusSign=myNodeInfo.get(0);//list里面没有元素，却要取第1个元素，就会报IndexOutOfBoundsException的错。这个服务崩溃，多半是因为这个。
                    plusSign.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    //Log.d(TAG,"i am true");
                } catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(amount01);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                seventhStep();
            }else if (myNodeInfo01.size()>0) {
                updateDoingStatus("该用户不存在");
                updateTemporaryTable("该用户不存在");
                goBack(2);
            }else if (myNodeInfo02.size()>0) {
                updateDoingStatus("被搜帐号状态异常，无法显示");
                updateTemporaryTable("被搜帐号状态异常，无法显示");
                goBack(2);
            }else if(myNodeInfo03.size()>0) {
                updateDoingStatus("本来就是好友");
                updateTemporaryTable("本来就是好友");
                goBack(3);
            }else {
                Log.d(TAG, "有病吧，执行这个");
            }
        }catch (ArrayIndexOutOfBoundsException e){
            setValidData();
        }

    }

    public void seventhStep(){
        Log.d(TAG,"seventhStep begins");
        AccessibilityNodeInfo nodeInfo=getRootInActiveWindow();
        List<AccessibilityNodeInfo> myNodeInfo01=nodeInfo.findAccessibilityNodeInfosByText("发消息");

        if(myNodeInfo01.size()>0){
            updateDoingStatus("直接通过");
            updateTemporaryTable("直接通过");
            goBack(3);
            try {
                Thread.sleep(amount01);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            nodeInfo.getChild(0).getChild(2).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            /*try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            //goBack(3);
            setCaptureToast(1);
        }

    }

    public void goBack(int i) {
        Log.d(TAG, "goBack begins");
        for (int j = 0; j < i; j++) {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            try {
                Thread.sleep(amount01);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String getNewQQNumber(){//get a qq number to handle.
        CommonDatabase MyDatabase =new CommonDatabase(this);
        SQLiteDatabase db=MyDatabase.getReadableDatabase();
        Cursor cursor=db.query("QQNumberForWeChat",new String[]{"QQNumber"},"Used=?",new String[]{"N"},null,null,"QQNumber asc",null);
        cursor.moveToFirst();
        String qqNumber = cursor.getString(cursor.getColumnIndex("QQNumber"));
        cursor.close();
        return qqNumber;
    }

    public void changeToDoing(String qq){//update the status of the current QQ number as Doing
        CommonDatabase MyDatabase =new CommonDatabase(this);
        SQLiteDatabase db=MyDatabase.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("Used","Doing");
        db.update("QQNumberForWeChat",values,"QQNumber=?",new String[]{qq});
    }

    public String getDoingQQNumber(){//get the number which status is Doing..
        CommonDatabase MyDatabase =new CommonDatabase(this);
        SQLiteDatabase db=MyDatabase.getReadableDatabase();
        Cursor cursor=db.query("QQNumberForWeChat",new String[]{"QQNumber"},"Used=?",new String[]{"Doing"},null,null,"QQNumber asc",null);
        cursor.moveToFirst();
        String qqNumber = cursor.getString(cursor.getColumnIndex("QQNumber"));
        cursor.close();
        return qqNumber;
    }

    public Boolean isDoingQQNumberExist(){
        CommonDatabase MyDatabase =new CommonDatabase(this);
        SQLiteDatabase db=MyDatabase.getReadableDatabase();
        Cursor cursor=db.query("QQNumberForWeChat",new String[]{"QQNumber"},"Used=?",new String[]{"Doing"},null,null,"QQNumber asc",null);
        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }else{
            cursor.close();
            return false;
        }

    }

    public void updateDoingStatus(String result) {
        CommonDatabase MyDatabase =new CommonDatabase(this);
        SQLiteDatabase db=MyDatabase.getWritableDatabase();
        Cursor cursor=db.query("Settings",null,null,null,null,null,null,null);
        cursor.moveToFirst();
        String wechat = cursor.getString(cursor.getColumnIndex("Wechat"));
        ContentValues values=new ContentValues();
        if (result.equals("该用户不存在")) {
            values.put("Used", "Y");
            values.put("AfterUseStatus", "NotExist");
            values.put("HandleTime", baseActivity.getCurrentTime());
            values.put("HandleWeChat", wechat);
        }else if (result.equals("被搜帐号状态异常，无法显示")) {
            values.put("Used", "Y");
            values.put("AfterUseStatus", "StatusAbnormal");
            values.put("HandleTime", baseActivity.getCurrentTime());
            values.put("HandleWeChat", wechat);
        }else if (result.equals("已发送")) {
            values.put("Used", "Y");
            values.put("AfterUseStatus", "InvitationSent");
            values.put("HandleTime", baseActivity.getCurrentTime());
            values.put("HandleWeChat", wechat);
        }else if (result.equals("直接通过")) {
            values.put("Used", "Y");
            values.put("AfterUseStatus", "DirectSuccess");
            values.put("HandleTime", baseActivity.getCurrentTime());
            values.put("HandleWeChat", wechat);
        }else if (result.equals("本来就是好友")){
            values.put("Used", "Y");
            values.put("AfterUseStatus", "AlreadyFriend");
            values.put("HandleTime", baseActivity.getCurrentTime());
            values.put("HandleWeChat", wechat);}
        db.update("QQNumberForWeChat",values,"Used=?",new String[]{"Doing"});
        cursor.close();
    }

    public void updateTemporaryTable(String result){
        CommonDatabase MyDatabase =new CommonDatabase(this);
        SQLiteDatabase db=MyDatabase.getWritableDatabase();
        ContentValues values=new ContentValues();
        Cursor cursor=db.query("TemporaryTable",null,"Name=?",new String[]{"giraffe"},null,null,null,null);
        cursor.moveToFirst();//if you don't manually move the cursor to first, the index will be -1.
        if (result.equals("该用户不存在")){
            values.put("Total",cursor.getInt(cursor.getColumnIndex("Total"))+1);
            values.put("NotExist",cursor.getInt(cursor.getColumnIndex("NotExist"))+1);
        }else if (result.equals("被搜帐号状态异常，无法显示")) {
            values.put("Total",cursor.getInt(cursor.getColumnIndex("Total"))+1);
            values.put("StatusAbnormal",cursor.getInt(cursor.getColumnIndex("StatusAbnormal"))+1);
        }else if (result.equals("已发送")) {
            values.put("Total",cursor.getInt(cursor.getColumnIndex("Total"))+1);
            values.put("InvitationSent",cursor.getInt(cursor.getColumnIndex("InvitationSent"))+1);
            values.put("ValidData",cursor.getInt(cursor.getColumnIndex("ValidData"))+1);
        }else if (result.equals("直接通过")){
            values.put("Total",cursor.getInt(cursor.getColumnIndex("Total"))+1);
            values.put("DirectSuccess",cursor.getInt(cursor.getColumnIndex("DirectSuccess"))+1);
            values.put("ValidData",cursor.getInt(cursor.getColumnIndex("ValidData"))+1);
        }else if (result.equals("本来就是好友")){
            values.put("Total",cursor.getInt(cursor.getColumnIndex("Total"))+1);
            values.put("AlreadyFriend",cursor.getInt(cursor.getColumnIndex("AlreadyFriend"))+1);
        }else if(result.equals("本轮已结束")){
            values.put("ValidData",cursor.getInt(cursor.getColumnIndex("ValidData"))+100);
        }
        db.update("TemporaryTable",values,"Name=?",new String[]{"giraffe"});
        cursor.close();
    }

    //the following are all repeated methods with BaseActivity.
    //the following are all repeated methods with BaseActivity.
    //the following are all repeated methods with BaseActivity.
    //the following are all repeated methods with BaseActivity.
    // i guess all the methods with database involved can only work with some special settings. my current settings don't work.
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


    public void addGoBackTimes(){
        CommonDatabase MyDatabase =new CommonDatabase(this);
        SQLiteDatabase db=MyDatabase.getWritableDatabase();
        ContentValues values=new ContentValues();
        Cursor cursor=db.query("Settings",null,"Wechat=?",new String[]{"change to your own"},null,null,null,null);
        cursor.moveToFirst();//if you don't manually move the cursor to first, the index will be -1.
        values.put("GoBackTimes",cursor.getInt(cursor.getColumnIndex("GoBackTimes"))+1);
        db.update("Settings",values,"Wechat=?",new String[]{"change to your own"});
        cursor.close();
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

    public Integer getCaptureToast(){
        int i=0;
        SQLiteDatabase db_switch=MyDatabase.getReadableDatabase();
        Cursor cursor = db_switch.query("Settings", null,null, null, null, null, null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            i=cursor.getInt(cursor.getColumnIndex("CaptureToast"));
        }
        cursor.close();
        return i;
    }


}
