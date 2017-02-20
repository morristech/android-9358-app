package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.LimitGrabBean;
import com.xmd.technician.http.gson.LimitGrabResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;

import rx.Subscription;

/**
 * Created by Lhj on 2017/2/9.
 */

public class LimitGrabListFragment extends BaseListFragment<LimitGrabBean> {

    private Subscription mLimitGrabListSubscription;

    public static LimitGrabListFragment getInstance() {
        LimitGrabListFragment lf = new LimitGrabListFragment();
        return lf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_view, container, false);
    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_SERVICE_ITEM_LIST_DETAIL);
    }

    @Override
    protected void initView() {
        mLimitGrabListSubscription = RxBus.getInstance().toObservable(LimitGrabResult.class).subscribe(
                limitGrabResult -> handleLimitGrabResult(limitGrabResult)
        );
    }

    private void handleLimitGrabResult(LimitGrabResult limitGrabResult) {
        if (limitGrabResult.statusCode == 200) {
            onGetListSucceeded(1, limitGrabResult.respData);
        } else {
            onGetListFailed(limitGrabResult.msg);
        }
    }

    @Override
    public void onShareClicked(LimitGrabBean bean) {
        super.onShareClicked(bean);
        ShareController.doShare(bean.image, bean.shareUrl, SharedPreferenceHelper.getUserClubName() + "-" + bean.itemName,
               "超值优惠，超值享受。快来购买吧。", Constant.SHARE_COUPON, "");
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mLimitGrabListSubscription);
    }


}
