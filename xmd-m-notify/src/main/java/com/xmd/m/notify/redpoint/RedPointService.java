package com.xmd.m.notify.redpoint;

import android.widget.TextView;

/**
 * Created by mo on 17-6-29.
 * 小红点管理
 */

public interface RedPointService {
    int SHOW_TYPE_DIGITAL = 0; //显示数字
    int SHOW_TYPE_POINT = 1; //显示小红点

    /**
     * 绑定id和view
     *
     * @param view
     * @param showType
     */
    void bind(String id, TextView view, int showType);

    void unBind(String id, TextView view);

    void inc(String id);

    void set(String id, int count);

    void clear(String id);
}
