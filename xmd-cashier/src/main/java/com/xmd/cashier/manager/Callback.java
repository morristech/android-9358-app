package com.xmd.cashier.manager;

/**
 * Created by heyangya on 16-8-23.
 * manager 异步调用返回通用接口
 */

public interface Callback<T> {
    void onSuccess(T o);

    void onError(String error);
}
