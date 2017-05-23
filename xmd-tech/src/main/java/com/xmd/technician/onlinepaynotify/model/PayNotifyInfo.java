package com.xmd.technician.onlinepaynotify.model;

import java.util.List;

/**
 * Created by heyangya on 17-1-17.
 */

public class PayNotifyInfo {
    public static final int STATUS_UNVERIFIED = 1;
    public static final int STATUS_ACCEPTED = 2;
    public static final int STATUS_REJECTED = 4;
    public static final int STATUS_ALL = STATUS_UNVERIFIED | STATUS_ACCEPTED | STATUS_REJECTED;

    public long id;
    public String userName;
    public String userAvatar;
    public long amount;
    public List<String> combineTechs;
    public long payTime;
    public int status;
    public boolean isArchived; //归档，用户点击“知道了”表示将数据归档，不在首页显示，另外过去N时间的数据自动归档
}
