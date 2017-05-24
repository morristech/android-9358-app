package com.xmd.app.alive;

import android.content.Context;
import android.content.Intent;

import com.xmd.app.IInit;

/**
 * Created by heyangya on 17-5-23.
 * 初始化心跳汇报功能
 * 心跳汇报功能支持事件：
 * 1.开始汇报
 * 2.停止汇报
 */

public class InitAliveReport implements IInit {
    @Override
    public void init(Context context) {
        Intent service = new Intent(context, AliveReportService.class);
        context.startService(service);
    }
}
