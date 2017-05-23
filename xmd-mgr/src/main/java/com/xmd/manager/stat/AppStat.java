package com.xmd.manager.stat;

/**
 * Created by sdcm on 15-12-17.
 */
public class AppStat {

    private AppStat() {

    }

    private static class AppStatHolder {
        private static AppStat sInstance = new AppStat();
    }

    public static AppStat getInstance() {
        return AppStatHolder.sInstance;
    }

    public void statAppStart() {

    }

}
