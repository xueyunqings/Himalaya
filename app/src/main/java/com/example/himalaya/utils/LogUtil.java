package com.example.himalaya.utils;

import android.util.Log;

public class LogUtil {

    private static String TAG_TEST = "xueyunqing";
    private static String TAG_NORMAL = "NORMAL";

    public static void printI_TEST(String msg){
        Log.i(TAG_TEST,msg);
    }

    public static void printI_NORMA(String msg){
        Log.i(TAG_NORMAL,msg);
    }

    public static void printE_TEST(String msg){
        Log.e(TAG_TEST,msg);
    }

    public static void printE_NORMAL(String msg){
        Log.e(TAG_NORMAL,msg);
    }
}
