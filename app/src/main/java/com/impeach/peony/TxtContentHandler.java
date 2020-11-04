package com.impeach.peony;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TxtContentHandler {
    public static String readTxtFile(String fileName)
    {
        //String path = strFilePath;
        String content = ""; //文件内容字符串
        //打开文件
        //File file = new File(path);
        File file = new File(Environment.getExternalStorageDirectory(), fileName + ".txt");
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory())
        {
            Log.d("TestFile", "The File doesn't not exist.");
        }
        else
        {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null)
                {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while (( line = buffreader.readLine()) != null) {
                        content = line + "\n";
                    }
                    instream.close();
                }
            }
            catch (java.io.FileNotFoundException e)
            {
                Log.d("TestFile", "The File doesn't not exist.");
            }
            catch (IOException e)
            {
                Log.d("TestFile", e.getMessage());
            }
        }
        return content;
    }

    public List<String> Txt() {
        //将读出来的一行行数据使用List存储
        String filePath = "/sdcard/numbers.txt";

        List newList=new ArrayList<String>();
        try {
            File file = new File(filePath);
            int count = 0;//初始化 key值
            if (file.isFile() && file.exists()) {//文件存在
                //Log.d("i have found the txt","yes");
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
                BufferedReader br = new BufferedReader(isr);
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {
                    if (!"".equals(lineTxt)) {
                        String reds = lineTxt.split("\\+")[0];  //java 正则表达式
                        newList.add(count, reds);
                        count++;
                    }
                }
                isr.close();
                br.close();
            }else {
                Log.d("i have found the txt","No");
                //Toast.makeText(getApplicationContext(),"can not find file",0).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newList;
    }
}
