package com.xmd.technician.common;

/**
 * Created by heyangya on 16-10-31.
 */

public interface Callback<T> {
    /**
     * @param error  如果不为null，表示调用出错，error为出错信息
     * @param result 调用成功，返回结果
     */
    void onResult(Throwable error, T result);
}
