package com.xmd.cashier.manager;

/**
 * Created by heyangya on 16-9-6.
 */

public interface PayCallback<T> {
    void onResult(String error, T o); //error==null表示成功，其他情况表示失败
}
