package com.justzht.unity.lwp;

import android.util.Log;


public class LiveWallpaperUtils
{

    public static void logD(String msg)
    {
        if (Thread.currentThread().getStackTrace().length > 3) {
            String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
            Log.d(LiveWallpaperDefine.Tag, className + "." + methodName + " (line " + lineNumber + ") --> " + msg);
        }else
        {
            Log.d(LiveWallpaperDefine.Tag,msg);
        }
    }

    public static void logW(String msg)
    {
        if (Thread.currentThread().getStackTrace().length > 3) {
            String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
            Log.w(LiveWallpaperDefine.Tag, className + "." + methodName + " (line " + lineNumber + ") --> " + msg);
        }else
        {
            Log.w(LiveWallpaperDefine.Tag,msg);
        }
    }

    public static void logE(String msg)
    {
        if (Thread.currentThread().getStackTrace().length > 3)
        {
            String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
            Log.e(LiveWallpaperDefine.Tag, className + "." + methodName + " (line " + lineNumber + ") --> " + msg);
        }else
        {
            Log.e(LiveWallpaperDefine.Tag,msg);
        }
    }
}
