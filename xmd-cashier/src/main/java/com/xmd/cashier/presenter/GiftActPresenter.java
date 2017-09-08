package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.xmd.cashier.R;
import com.xmd.cashier.contract.GiftActContract;
import com.xmd.cashier.dal.bean.GiftActivityInfo;
import com.xmd.cashier.manager.AccountManager;

/**
 * Created by zr on 17-9-8.
 */

public class GiftActPresenter implements GiftActContract.Presenter {
    private Context mContext;
    private GiftActContract.View mView;

    public GiftActPresenter(Context context, GiftActContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        GiftActivityInfo info = mView.getActivityInfo();
        if (info == null) {
            mView.showToast("获取活动详情失败...");
            mView.finishSelf();
            return;
        }

        if (TextUtils.isEmpty(info.startTime) || TextUtils.isEmpty(info.endTime)) {
            mView.setActivityTime("长期有效");
        } else {
            mView.setActivityTime(info.startTime + " ~ " + info.endTime);
        }

        mView.setActivityCopyRight(String.format(mContext.getResources().getString(R.string.activity_copy_right_text), AccountManager.getInstance().getClubName(), AccountManager.getInstance().getClubName()));
        mView.setActivityData(info.packageList);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }
}
