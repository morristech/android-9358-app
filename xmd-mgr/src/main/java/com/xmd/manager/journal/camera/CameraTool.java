package com.xmd.manager.journal.camera;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by mo on 16-11-29.
 */

public class CameraTool {
    private static final int REQUEST_VIDEO_PERMISSIONS = 1;
    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //检查是否拥有摄像头
    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }

    //检查是否拥有相关权限
    private static boolean hasPermissionsGranted(Context contexts, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(contexts, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //是否应该显示权限说明对话框，向用户解释为什么需要这个权限
    //第一次申请或者用户勾选不再提醒后申请，返回false
    //用户未勾选不再提醒，并且选择了拒绝，下次申请时返回true
    private static boolean shouldShowRequestPermissionRationale(Activity activity, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (activity.shouldShowRequestPermissionRationale(permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    //权限申请结果，需要在activity的onRequestPermissionsResult中调用此函数进行检查
    public static boolean onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_VIDEO_PERMISSIONS) {
            if (grantResults.length == VIDEO_PERMISSIONS.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

    //申请权限，如果需要展示权限说明，返回true
    public static boolean requestVideoPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(activity, VIDEO_PERMISSIONS)) {
                return true;
            } else {
                activity.requestPermissions(VIDEO_PERMISSIONS, REQUEST_VIDEO_PERMISSIONS);
            }
        }
        return false;
    }

    //检查是否拥有video录制权限
    public static boolean hasVideoPermissions(Context context) {
        return hasPermissionsGranted(context, VIDEO_PERMISSIONS);
    }


    //获取最佳预览大小
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Size chooseOptimalSize5x(Size[] choices, int width, int height, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
                }
            });
        } else {
            //没有找到合适的preview大小
            return choices[0];
        }
    }

    public static Camera.Size chooseOptimalSize4x(List<Camera.Size> previewSizes, int width, int height, Camera.Size aspectRatio) {
        List<Camera.Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.width;
        int h = aspectRatio.height;
        for (Camera.Size option : previewSizes) {
            if (option.height == option.width * h / w) {
                bigEnough.add(option);
            }
        }
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size lhs, Camera.Size rhs) {
                    return Long.signum((long) lhs.width * lhs.height - (long) rhs.width * rhs.height);
                }
            });
        } else {
            //没有找到合适的preview大小
            return previewSizes.get(0);
        }
    }

}
