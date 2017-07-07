package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.xmd.cashier.BuildConfig;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.LoginContract;
import com.xmd.cashier.dal.bean.User;
import com.xmd.cashier.dal.net.SpaRetrofit;
import com.xmd.cashier.dal.net.response.LoginResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;

import rx.Subscription;

/**
 * Created by heyangya on 16-8-22.
 */

public class LoginPresenter implements LoginContract.Presenter {
    private Context mContext;
    private LoginContract.View mView;
    private Subscription mLoginSubscription;
    private int mVersionViewClickTimes = 0;

    public LoginPresenter(Context context, LoginContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onClickLoginButton() {
        String username = mView.getUserName();
        String password = mView.getPassword();
        mView.onLoginStart();
        if (!Utils.checkUserName(username)) {
            mView.onLoginEnd("用户名输入错误！");
            return;
        }
        if (!Utils.checkPassword(password)) {
            mView.onLoginEnd("密码输入错误！");
            return;
        }
        if (mLoginSubscription != null) {
            mLoginSubscription.unsubscribe();
        }
        mLoginSubscription = AccountManager.getInstance().login(username, password, new Callback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult o) {
                mView.cleanPassword();
                mView.onLoginEnd(null);
                UiNavigation.gotoMainActivity(mContext);
                mView.finishSelf();
            }

            @Override
            public void onError(String error) {
                mView.onLoginEnd(error);
            }
        });
    }

    @Override
    public void onClickVersionView() {
        mVersionViewClickTimes++;
        if (mVersionViewClickTimes == 5) {
            mVersionViewClickTimes = 0;
            processChangeServerCMD();
        }
    }

    private void processChangeServerCMD() {
        final String[] serverList = new String[]{
                BuildConfig.SERVER_PUBLIC,
                BuildConfig.SERVER_SDCM210,
                BuildConfig.SERVER_SDCM105,
                BuildConfig.SERVER_SDCM103,
                BuildConfig.SERVER_SDCM100
        };
        final int[] selectedId = {0};
        String currentServer = SPManager.getInstance().getSpaServerAddress();
        for (int i = 0; i < serverList.length; i++) {
            if (currentServer.contains(serverList[i])) {
                selectedId[0] = i;
                break;
            }
        }
        new AlertDialog.Builder(mContext)
                .setSingleChoiceItems(serverList, selectedId[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedId[0] = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPManager.getInstance().setSpaServerAddress(serverList[selectedId[0]]);
                        SpaRetrofit.rebuildService();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    public void onCreate() {
        User user = AccountManager.getInstance().getUser();
        mView.setLoginName(user.loginName);
        mView.cleanPassword();
        mView.showVersionName(Utils.getAppVersionName());
        if (AccountManager.getInstance().isLogin()) {
            UiNavigation.gotoMainActivity(mContext);
            mView.finishSelf();
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mLoginSubscription != null) {
            mLoginSubscription.unsubscribe();
            mLoginSubscription = null;
        }
    }
}
