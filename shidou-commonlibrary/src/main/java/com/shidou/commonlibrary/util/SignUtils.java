package com.shidou.commonlibrary.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

/**
 * 获取APP签名信息
 */
public class SignUtils {
    public static String getSign(Context context, String packageName) {
        Signature[] signs = getRawSignature(context, packageName);
        if ((signs == null) || (signs.length == 0)) {
            return null;
        } else {
            return MD5Utils.MD5(signs[0].toByteArray());
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    public static Signature[] getRawSignature(Context context,
                                              String packageName) {
        if ((packageName == null) || (packageName.length() == 0)) {
            return null;
        }
        PackageManager pkgMgr = context.getPackageManager();
        PackageInfo info;
        try {
            info = pkgMgr.getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        if (info == null) {
            return null;
        }
        return info.signatures;
    }
}
