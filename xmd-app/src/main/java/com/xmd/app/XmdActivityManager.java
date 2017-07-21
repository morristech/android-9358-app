package com.xmd.app;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by mo on 17-7-21.
 * 所有的activity管理
 */

public class XmdActivityManager {
    private static final XmdActivityManager ourInstance = new XmdActivityManager();

    public static XmdActivityManager getInstance() {
        return ourInstance;
    }

    private XmdActivityManager() {
    }

    private static Stack<Activity> activities = new Stack<>();

    //获得当前栈顶Activity
    public Activity getCurrentActivity() {
        return !activities.empty() ? activities.lastElement() : null;
    }

    //将当前Activity推入栈中
    public void addActivity(Activity activity) {
        activities.push(activity);
    }

    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    //结束所有activity
    public void finishAll() {
        for (Activity activity : activities) {
            activity.finish();
        }
        activities.clear();
    }

    //退出app
    public void exitApplication() {
        finishAll();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public int getActivityCount() {
        return activities.size();
    }
}
