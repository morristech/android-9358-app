package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.contract.McardSuccessContract;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.event.CardFinishEvent;
import com.xmd.cashier.manager.MemberManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zr on 17-7-11.
 */

public class McardSuccessPresenter implements McardSuccessContract.Presenter {
    private Context mContext;
    private McardSuccessContract.View mView;
    private MemberInfo info;

    public McardSuccessPresenter(Context context, McardSuccessContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        info = MemberManager.getInstance().getCardMemberInfo();
        mView.showInfo(info);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onConfirm() {
        MemberManager.getInstance().setRechargeMemberInfo(info);
        UiNavigation.gotoMemberRechargeActivity(mContext);
    }

    @Override
    public void onFinish() {
        MemberManager.getInstance().newCardProcess();
        EventBus.getDefault().post(new CardFinishEvent());
        mView.finishSelf();
    }
}
