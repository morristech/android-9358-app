package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;

import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.http.gson.ModifyPasswordResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import rx.Subscription;

public class ModifyPasswordActivity extends BaseActivity {

    private Subscription mModifyPasswordSubscription;
    private Subscription mLoginSubscription;

    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        ButterKnife.bind(this);

        mModifyPasswordSubscription = RxBus.getInstance().toObservable(ModifyPasswordResult.class).subscribe(
                result -> login());

        mLoginSubscription = RxBus.getInstance().toObservable(LoginResult.class).subscribe(
                loginResult -> handleLoginResult(loginResult));

        modifyPassword();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mModifyPasswordSubscription);
        RxBus.getInstance().unsubscribe(mLoginSubscription);
    }

    public void modifyPassword(){
        String nPassword = null,oPassword = null;

        nPassword = "111111";
        oPassword = "111111";
        mPassword = nPassword;
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_OLD_PASSWORD, oPassword);
        params.put(RequestConstant.KEY_NEW_PASSWORD, nPassword);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MODIFY_PASSWORD, params);
    }

    private void login(){
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USERNAME, SharedPreferenceHelper.getUserAccount());
        params.put(RequestConstant.KEY_PASSWORD, mPassword);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN, params);
    }

    private void handleLoginResult(LoginResult loginResult) {
        if (loginResult.status.equals("fail")) {
            makeShortToast(loginResult.msg);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            SharedPreferenceHelper.setUserToken(loginResult.token);
            SharedPreferenceHelper.setUserName(loginResult.name);
            SharedPreferenceHelper.setUserId(loginResult.userId);
            finish();
        }
    }
}
