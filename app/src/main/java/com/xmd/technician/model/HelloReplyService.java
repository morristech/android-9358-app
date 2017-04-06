package com.xmd.technician.model;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.Adapter.FloatItemAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.TechApplication;
import com.xmd.technician.bean.HelloReplyInfo;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.widget.CircleImageView;

import java.util.List;

/**
 * Created by zr on 17-4-6.
 */

public class HelloReplyService extends Service {
    private final static int DISMISS_INTERVAL = 5 * 1000;
    private final static int VIBRATE_INTERVAL = 500;
    private List<HelloReplyInfo> mReplyList;
    private HelloReplyReceiver mReceiver;

    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    private LinearLayout mLayout;

    private boolean isShow;

    public static void start() {
        Context context = TechApplication.getAppContext();
        context.startService(new Intent(context, HelloReplyService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mReceiver = new HelloReplyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_HELLO_REPLY_RECEIVER);
        registerReceiver(mReceiver, filter);

        mLayoutParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);

        String packageName = HelloReplyService.this.getPackageName();
        PackageManager packageManager = HelloReplyService.this.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", packageName));
        if (permission) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }

        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;

        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public void showSingleData() {
        if (isShow) {
            return;
        }
        mLayout = (LinearLayout) LayoutInflater.from(TechApplication.getAppContext()).inflate(R.layout.layout_float_single, null);
        mWindowManager.addView(mLayout, mLayoutParams);
        isShow = true;
        vibrate();
        CircleImageView img = (CircleImageView) mLayout.findViewById(R.id.img_single_avatar);
        TextView text = (TextView) mLayout.findViewById(R.id.tv_single_nickname);
        Glide.with(TechApplication.getAppContext()).load(mReplyList.get(0).receiverAvatar).into(img);
        text.setText(mReplyList.get(0).receiverName);
        mLayout.setOnClickListener(v -> {
            hide();
            UINavigation.gotoChatActivityFromService(TechApplication.getAppContext(), mReplyList.get(0).receiverEmChatId);
        });
        ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, () -> hide(), DISMISS_INTERVAL);
    }

    public void showMultiData() {
        if (isShow) {
            return;
        }
        mLayout = (LinearLayout) LayoutInflater.from(TechApplication.getAppContext()).inflate(R.layout.layout_float_multi, null);
        mWindowManager.addView(mLayout, mLayoutParams);
        isShow = true;
        vibrate();
        RecyclerView rv = (RecyclerView) mLayout.findViewById(R.id.rv_multi_avatar);
        TextView text = (TextView) mLayout.findViewById(R.id.tv_multi_desc);
        FloatItemAdapter adapter = new FloatItemAdapter(TechApplication.getAppContext());
        if (mReplyList.size() > 5) {
            adapter.setData(mReplyList.subList(0, 5));
            text.setText(String.format(getResources().getString(R.string.hello_reply_multi_more_tips), String.valueOf(mReplyList.size())));
        } else {
            adapter.setData(mReplyList);
            text.setText(String.format(getResources().getString(R.string.hello_reply_multi_tips), String.valueOf(mReplyList.size())));
        }
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
        mLayout.setOnClickListener(v -> {
            hide();
            UINavigation.gotoMainActivityIndexFragmentFromService(TechApplication.getAppContext(), 1);
        });
        ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, () -> hide(), DISMISS_INTERVAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLayout != null && isShow) {
            mWindowManager.removeView(mLayout);
        }
        unregisterReceiver(mReceiver);
    }

    public void hide() {
        if (isShow) {
            if (mLayout != null) {
                mWindowManager.removeViewImmediate(mLayout);
            }
            isShow = false;
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) TechApplication.getAppContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATE_INTERVAL);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class HelloReplyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mReplyList = (List<HelloReplyInfo>) intent.getSerializableExtra(Constant.EXTRA_HELLO_REPLY_INFO);
            if (mReplyList.size() == 1) {
                showSingleData();
            } else {
                showMultiData();
            }
        }
    }
}
