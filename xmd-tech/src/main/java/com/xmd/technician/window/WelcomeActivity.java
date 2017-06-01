package com.xmd.technician.window;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.http.gson.TechInfoResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.RxBus;

import org.greenrobot.eventbus.EventBus;

import rx.Subscription;

public class WelcomeActivity extends BaseActivity {
    private Subscription mTechInfoSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        if (TextUtils.isEmpty(LoginTechnician.getInstance().getToken())) {
            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                @Override
                public void run() {
                    UINavigation.gotoLogin(WelcomeActivity.this);
                    finish();
                }
            }, 500);
        } else {
            EventBus.getDefault().removeStickyEvent(EventLogout.class);
            EventBus.getDefault().postSticky(new EventLogin(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserId()));
            mTechInfoSubscription = RxBus.getInstance().toObservable(TechInfoResult.class).subscribe(
                    techInfoResult -> {
                        if (techInfoResult.statusCode == 200) {
                            LoginTechnician.getInstance().onLoadTechInfo(techInfoResult);
                        }
                        UINavigation.gotoMainActivityFromStart(this);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
            );
            LoginTechnician.getInstance().loadTechInfo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mTechInfoSubscription);
    }
}
