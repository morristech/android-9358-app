package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.PayForMeBean;
import com.xmd.technician.http.gson.PayForMeListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;

import rx.Subscription;

/**
 * Created by Lhj on 2017/2/9.
 */

public class PayForMeListFragment extends BaseListFragment<PayForMeBean> {

    private Subscription mPayForMeListSubscription;

    public static PayForMeListFragment getInstance(){
        return  new PayForMeListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_view, container, false);
    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_PAY_FOR_ME_LIST_DETAIL);
    }

    @Override
    protected void initView() {
        mPayForMeListSubscription = RxBus.getInstance().toObservable(PayForMeListResult.class).subscribe(
                payForMeListResult -> handlePayForMeListResult(payForMeListResult)
        );
    }

    private void handlePayForMeListResult(PayForMeListResult payForMeListResult) {
        if(payForMeListResult.statusCode == 200){
            onGetListSucceeded(1,payForMeListResult.respData);
        }else{
            onGetListFailed(payForMeListResult.msg);
        }
    }

    @Override
    public void onShareClicked(PayForMeBean bean) {
        super.onShareClicked(bean);
        ShareController.doShare(bean.image, bean.shareUrl, SharedPreferenceHelper.getUserClubName() + "-" + bean.actName,
              String.format("原价%s,现价%s，超值优惠，超值享受。快来抢购吧。",bean.prizePrice,bean.prizePrice), Constant.SHARE_COUPON, "");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mPayForMeListSubscription);
    }

    @Override
    public boolean isPaged() {
        return false;

    }
}
