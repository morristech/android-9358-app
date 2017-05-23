package com.xmd.manager.beans;

/**
 * Created by sdcm on 16-1-22.
 */
public class SettingItemInfo {

    public static final int TYPE_DESCRIPTOIN = 0;
    public static final int TYPE_SPACE = 1;
    public static final int TYPE_LINE = 2;
    public static final int TYPE_SWITCH = 3;

    public int id;
    public int type;
    public String title;
    public String settingKey;
    /**
     * for Switch SettingItem
     */
    public String textOff;
    /**
     * for Switch SettingItem
     */
    public String textOn;

    public SettingItemInfo() {
        this(TYPE_SPACE);
    }

    public SettingItemInfo(int type) {
        this(type, "");
    }

    public SettingItemInfo(int type, String title) {
        this(type, title, "");
    }

    public SettingItemInfo(int type, String title, String settingKey) {
        this(type, title, settingKey, "", "");
    }

    /**
     * for swith type
     *
     * @param type
     * @param title
     * @param settingKey
     * @param textOff
     * @param textOn
     */
    public SettingItemInfo(int type, String title, String settingKey, String textOff, String textOn) {
        this.type = type;
        this.title = title;
        this.settingKey = settingKey;
        this.textOff = textOff;
        this.textOn = textOn;
    }
}
