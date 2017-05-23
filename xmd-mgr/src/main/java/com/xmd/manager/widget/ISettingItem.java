package com.xmd.manager.widget;

/**
 * Created by sdcm on 16-1-22.
 */
public interface ISettingItem {
    void setValue(String key, Object value);

    Object getValue(String key);
}
