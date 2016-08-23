package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.technician.AppConfig;
import com.xmd.technician.R;
import com.xmd.technician.http.gson.InviteCodeResult;
import com.xmd.technician.http.gson.QuitClubResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.InviteDialog;
import com.xmd.technician.widget.RewardConfirmDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class SettingActivity extends BaseActivity {

    public static final String EXTRA_JIONED_CLUB = "joined_club";

    @Bind(R.id.app_version)
    TextView mAppVersion;
    @Bind(R.id.settings_activity_quit_club)
    View mQuitClubView;
    @Bind(R.id.settings_activity_join_club)
    View mJoinClubView;
    @Bind(R.id.config_message)
    RelativeLayout configMessage;


    private Subscription mQuitClubSubscription;
    private Subscription mSubmitInviteSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);

        setTitle(R.string.personal_fragment_layout_setting);
        setBackVisible(true);

        boolean joinedClub = getIntent().getBooleanExtra(EXTRA_JIONED_CLUB, false);
        if (joinedClub) {
            mQuitClubView.setVisibility(View.VISIBLE);
            mJoinClubView.setVisibility(View.GONE);
        } else {
            mQuitClubView.setVisibility(View.GONE);
            mJoinClubView.setVisibility(View.VISIBLE);
        }
        configMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Intent intent = new Intent(SettingActivity.this,ConfigurationMonitorActivity.class);
//                startActivity(intent);
                return false;
            }
        });

        mAppVersion.setText("v" + AppConfig.getAppVersionNameAndCode());

        mQuitClubSubscription = RxBus.getInstance().toObservable(QuitClubResult.class).subscribe(
                result -> doQuitClubResult());

        mSubmitInviteSubscription = RxBus.getInstance().toObservable(InviteCodeResult.class).subscribe(inviteCodeResult -> submitInviteResult(inviteCodeResult));
    }

    private void submitInviteResult(InviteCodeResult inviteCodeResult) {
        makeShortToast(String.format(getString(R.string.join_club_success_tips), inviteCodeResult.name));
        mQuitClubView.setVisibility(View.VISIBLE);
        mJoinClubView.setVisibility(View.GONE);
    }

    private void doQuitClubResult() {
        makeShortToast(getString(R.string.quit_club_success_tips));
        mQuitClubView.setVisibility(View.GONE);
        mJoinClubView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mQuitClubSubscription, mSubmitInviteSubscription);
    }

    @OnClick(R.id.settings_activity_about_us)
    public void gotoAboutUs() {
        startActivity(new Intent(this, AppInfoActivity.class));
    }

    @OnClick(R.id.settings_activity_suggest)
    public void gotoFeedbackActivity() {
        startActivity(new Intent(this, FeedbackActivity.class));
    }

    @OnClick(R.id.settings_activity_modify_pw)
    public void gotoModifyPassword() {
        startActivity(new Intent(this, ModifyPasswordActivity.class));
    }

    @OnClick(R.id.settings_activity_logout)
    public void logout() {
        new RewardConfirmDialog(SettingActivity.this, "", getString(R.string.logout_tips),"") {
            @Override
            public void onConfirmClick() {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GETUI_UNBIND_CLIENT_ID);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGOUT);
                super.onConfirmClick();
            }
        }.show();
    }

    @OnClick(R.id.settings_activity_quit_club)
    public void quitClub() {
        new RewardConfirmDialog(SettingActivity.this, getString(R.string.quit_club_title), getString(R.string.quit_club_tips),"") {
            @Override
            public void onConfirmClick() {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_QUIT_CLUB);
                super.onConfirmClick();
            }
        }.show();
    }

    @OnClick(R.id.settings_activity_join_club)
    public void showInviteDialog() {
        new InviteDialog(this, R.style.default_dialog_style).show();
    }
}
