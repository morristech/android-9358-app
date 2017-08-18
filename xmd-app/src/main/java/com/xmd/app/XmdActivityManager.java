package com.xmd.app;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

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

    private static int startCount;

    private XmdActivityManager() {
    }

    private static Stack<AppCompatActivity> activities = new Stack<>();

    //获得当前栈顶Activity
    public AppCompatActivity getCurrentActivity() {
        return !activities.empty() ? activities.lastElement() : null;
    }

    //将当前Activity推入栈中
    public void addActivity(AppCompatActivity activity) {
        activities.push(activity);
    }

    public void removeActivity(AppCompatActivity activity) {
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

    public void onStart(Activity activity) {
        startCount++;
    }

    public void onStop(Activity activity) {
        startCount--;
    }

    public boolean isForeground() {
        return startCount > 0;
    }

}
