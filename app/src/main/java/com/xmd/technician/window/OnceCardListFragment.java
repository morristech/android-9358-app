package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.OnceCardItemBean;
import com.xmd.technician.common.OnceCardHelper;
import com.xmd.technician.http.gson.OnceCardResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;

import rx.Subscription;

/**
 * Created by Lhj on 2017/2/9.
 */

public class OnceCardListFragment extends BaseListFragment<OnceCardItemBean> {

    private Subscription mOnceCardListSubscription;
    private OnceCardHelper mOnceCardHelper;

    public static OnceCardListFragment getInstance() {
        OnceCardListFragment nf = new OnceCardListFragment();
        return nf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_view, container, false);
    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ONCE_CARD_LIST_DETAIL);
    }

    @Override
    protected void initView() {
        mOnceCardListSubscription = RxBus.getInstance().toObservable(OnceCardResult.class).subscribe(
                onceCardResult -> handleCardResult(onceCardResult)
        );
    }

    private void handleCardResult(OnceCardResult onceCardResult) {
        if (onceCardResult.statusCode == 200) {
            if (mOnceCardHelper == null) {
                mOnceCardHelper = new OnceCardHelper();
            }
            onGetListSucceeded(1, mOnceCardHelper.getCardItemBeanList(onceCardResult));
        } else {
            onGetListFailed(onceCardResult.msg);
        }
    }

    @Override
    public void onShareClicked(OnceCardItemBean bean) {
        super.onShareClicked(bean);
        ShareController.doShare("", bean.ShareUrl, SharedPreferenceHelper.getUserClubName() + "-" + bean.name,
                bean.comboDescription + "，超值优惠，超值享受。快来购买吧。", Constant.SHARE_COUPON, "");
    }

    @Override
    public boolean isPaged() {
        return false;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mOnceCardListSubscription);
    }
}
