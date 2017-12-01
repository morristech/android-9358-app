package com.xmd.inner.httprequest;

/**
 * Created by Lhj on 17-11-22.
 * 用于发起网络请求
 */

public class DataManager {

    private static final DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {

    }

}
