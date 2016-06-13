package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.xmd.technician.AppConfig;
import com.xmd.technician.R;
import com.xmd.technician.http.gson.QuitClubResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.RewardConfirmDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class SettingActivity extends BaseActivity {

    @Bind(R.id.app_version) TextView mAppVersion;

    private Subscription mQuitClubSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);

        setTitle(R.string.personal_fragment_layout_setting);
        setBackVisible(true);

        mAppVersion.setText("v"+AppConfig.getAppVersionNameAndCode());

        mQuitClubSubscription = RxBus.getInstance().toObservable(QuitClubResult.class).subscribe(
                result -> makeShortToast(getString(R.string.quit_club_success_tips)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mQuitClubSubscription);
    }

    @OnClick(R.id.settings_activity_about_us)
    public void gotoAboutUs(){
        startActivity(new Intent(this, AppInfoActivity.class));
    }

    @OnClick(R.id.settings_activity_suggest)
    public void gotoFeedbackActivity(){
        startActivity(new Intent(this, FeedbackActivity.class));
    }

    @OnClick(R.id.settings_activity_modify_pw)
    public void gotoModifyPassword(){
        startActivity(new Intent(this, ModifyPasswordActivity.class));
    }

    @OnClick(R.id.settings_activity_logout)
    public void logout(){
        new RewardConfirmDialog(SettingActivity.this,"",getString(R.string.logout_tips)){
            @Override
            public void onConfirmClick() {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGOUT);
                super.onConfirmClick();
            }
        }.show();
    }

    @OnClick(R.id.settings_activity_quit_club)
    public void quitClub(){
        new RewardConfirmDialog(SettingActivity.this,getString(R.string.quit_club_title), getString(R.string.quit_club_tips)){
            @Override
            public void onConfirmClick() {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_QUIT_CLUB);
                super.onConfirmClick();
            }
        }.show();
    }
}
