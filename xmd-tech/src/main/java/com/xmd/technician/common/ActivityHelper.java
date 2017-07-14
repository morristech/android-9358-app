package com.xmd.technician.common;

/**
 * Created by sdcm on 15-10-26.
 */

import android.app.Activity;

import java.util.Stack;

public class ActivityHelper {

    private static ActivityHelper activityHelper;
    private static Stack<Activity> activities;
    public static Activity sRootActivity;

    private ActivityHelper() {
    }

    public static ActivityHelper getInstance() {
        if (activityHelper == null) {
            activityHelper = new ActivityHelper();
        }
        return activityHelper;
    }

    public void removeActivity(Activity activity) {
        if (activity != null && activities != null) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
            activities.remove(activity);
        }
    }

    //获得当前栈顶Activity
    public Activity getCurrentActivity() {
        Activity activity = null;
        if (activities != null && !activities.empty()) {
            activity = activities.lastElement();
        }
        return activity;
    }

    //将当前Activity推入栈中
    public void pushActivity(Activity activity) {
        if (activities == null) {
            activities = new Stack<Activity>();
        }
        activities.push(activity);
    }

    //退出栈顶元素
    public void popActivity() {
        if (activities != null) {
            Activity activity = activities.lastElement();
            if (!activity.isFinishing()) {
                activity.finish();
            }
            activities.pop();
        }

    }

    public void removeAllActivities() {
        while (true) {
            Activity activity = getCurrentActivity();
            if (activity == null) {
                break;
            }
            removeActivity(activity);
        }
    }

    public void exitAndClearApplication() {
        while (true) {
            Activity activity = getCurrentActivity();
            if (activity == null) {
                break;
            }
            removeActivity(activity);
        }
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}