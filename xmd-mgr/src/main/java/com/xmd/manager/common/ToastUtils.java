package com.xmd.manager.common;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;


/**
 * Created by Administrator on 2016/6/20.
 */
public class ToastUtils {

    private static Context mApplicationContext;
    private static Toast mToast;
    private static String oldMsg;
    private static long FirstTime = 0;
    private static long SecondTime = 0;

    public static void init(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    public static void showToastLong(String str) {
        showToastLong(mApplicationContext, str);
    }

    public static void showToastLong(Context context, String str) {
        if (mToast == null) {
            mToast = Toast.makeText(context, str, Toast.LENGTH_LONG);
            mToast.show();
            FirstTime = System.currentTimeMillis();
            oldMsg = str;
        } else {
            SecondTime = System.currentTimeMillis();
            if (str.equals(oldMsg)) {
                if (Math.abs(SecondTime - FirstTime) > Toast.LENGTH_LONG) {
                    mToast.show();
                }
            } else {
                oldMsg = str;
                mToast.setText(str);
            }
            FirstTime = SecondTime;
        }
        mToast.show();

    }

    public static void showToastShort(String str) {
        showToastShort(mApplicationContext, str);
    }

    public static void showToastShort(Context context, String str) {
        if (TextUtils.isEmpty(str)) return;

        if (mToast == null) {
            mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
            mToast.show();
            FirstTime = System.currentTimeMillis();
            oldMsg = str;

        } else {
            SecondTime = System.currentTimeMillis();
            if (str.equals(oldMsg)) {
                if (Math.abs(SecondTime - FirstTime) > Toast.LENGTH_SHORT) {
                    mToast.show();
                }
            } else {
                oldMsg = str;
                mToast.setText(str);
                mToast.show();
            }
            FirstTime = SecondTime;
        }
    }

    public static void removeToast() {
        if (mToast != null) {
            mToast.cancel();

        }
    }

}
