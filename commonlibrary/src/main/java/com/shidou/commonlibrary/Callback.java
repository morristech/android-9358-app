package com.shidou.commonlibrary;

/**
 * Created by mo on 17-6-21.
 * 通用回调接口
 */

public interface Callback<T> {
    void onResponse(T result,Throwable error);
}
