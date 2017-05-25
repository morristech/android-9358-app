package com.xmd.manager.journal.model;

import android.text.TextUtils;

/**
 * Created by heyangya on 16-12-12.
 */

public class JournalItemActivity extends JournalItemBase {
    private String startTime;
    private String endTime;
    private String content;

    public JournalItemActivity() {
        this(null);
    }

    public JournalItemActivity(String data) {
        super(data);
        if (!TextUtils.isEmpty(data)) {
            String[] datas = data.split(":");
            if (datas.length >= 1) {
                startTime = datas[0];
            }
            if (datas.length >= 2) {
                endTime = datas[1];
            }
            if (datas.length >= 3) {
                content = data.substring(startTime.length() + endTime.length() + 2);
            }
        } else {
            content = "";
            startTime = "";
            endTime = "";
        }
    }

    @Override
    public String contentToString() {
        if (TextUtils.isEmpty(startTime) && TextUtils.isEmpty(endTime) && TextUtils.isEmpty(content)) {
            return null;
        }
        return startTime + ":" + endTime + ":" + content;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String isDataReady() {
        if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime) && endTime.compareTo(startTime) < 0) {
            return "活动的开始时间大于结束时间，请检查";
        }
        if (!TextUtils.isEmpty(startTime) || !TextUtils.isEmpty(endTime)) {
            if (TextUtils.isEmpty(content)) {
                return "活动内容不能为空！";
            }
        }
        return super.isDataReady();
    }
}
