package com.zhiliantiandi.io;

import com.zhiliantiandi.twodtest.R;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.String;
import java.lang.StringBuffer;
import java.lang.System;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveFileToSDCard {

    private static final String TAG = "SaveFileToSDCard";

    public SaveFileToSDCard(){}

    public boolean saveFile(ArrayList<HashMap<String, String>> mapArrayList){
        StringBuffer mResult = new StringBuffer();
        mResult.append("Time\t\t\tResult\r\n");
        Log.d(TAG, "saveFile format start");
        for (int i = 0; i < mapArrayList.size(); i++){
            //mResult = mapArrayList.get(i).get("decodeTime") + "\t\t\t" + mapArrayList.get(i).get("decodeResult") + "\r\n";
            mResult.append(mapArrayList.get(i).get("decodeTime"));
            mResult.append("\t\t\t");
            mResult.append(mapArrayList.get(i).get("decodeResult"));
            mResult.append("\r\n");
        }
        Log.d(TAG, "saveFile format end");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String fileName = dateFormat.format(date) + ".txt";
        File sdcardDir = Environment.getExternalStorageDirectory();
        String path = sdcardDir.getPath()+"/results";
        File resultPath = new File(path);
        if (!resultPath.exists()){
            resultPath.mkdir();
        }
        File saveFile = new File(resultPath, fileName);
        Log.d(TAG, "saveFile create file end");
        try {
            FileOutputStream outStream = new FileOutputStream(saveFile);
            outStream.write(mResult.toString().getBytes());
            outStream.close();
            Log.d(TAG, "saveFile save file end");
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

}