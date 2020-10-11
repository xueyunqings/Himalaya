package com.example.himalaya.utils;

import android.util.Log;

public class LogUtil {

    private static String TAG = "xueyunqing";

    public static void printI(String msg){
        Log.i(TAG,msg);
    }

    public static void printE(String msg){
        Log.e(TAG,msg);
    }
}
