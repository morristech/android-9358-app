package com.soundcloud.android.crop;

class Log {

    private static final String TAG = "android-com.soundcloud.android.crop";

    public static void e(String msg) {
        android.util.Log.e(TAG, msg);
    }

    public static void e(String msg, Throwable e) {
        android.util.Log.e(TAG, msg, e);
    }

}
