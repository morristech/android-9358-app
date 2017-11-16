package com.xmd.manager.window;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.manager.ClubData;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.ClubInfo;
import com.xmd.manager.beans.CouponBean;
import com.xmd.manager.common.ImageLoader;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.event.CouponFilterEvent;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CouponListResult;
import com.xmd.manager.widget.EmptyView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

/**
 * Created by Lhj on 17-10-19.
 */

public class OperateCouponListFragment extends BaseListFragment<CouponBean> {

    public static String COUPON_LIST_STATUS_TYPE = "listType";
    public static String COUPON_LIST_TYPE_ONLINE = "online";
    public static String COUPON_LIST_TYPE_OFFLINE = "offline";
    public static String KEY_INTENT_COUPON_BEAN = "couponBean";

    private String mCouponListType;//在线，不在线
    private String mCouponType; //现金券“cash"，体验券“coupon”，礼品券“gift”，折扣券"discount”，多个用“,”连接
    private Map<String, String> mParams;
    private EmptyView mEmptyView;
    private Subscription mCouponListSubscribe;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_operate_coupon_list, container, false);
        getArgumentsData();
        EventBus.getDefault().register(this);
        mEmptyView = (EmptyView) view.findViewById(R.id.empty_view);
        return view;
    }

    @Override
    protected void dispatchRequest() {
        if (mParams == null) {
            mParams = new HashMap<>();
        } else {
            mParams.clear();
        }
        mParams.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        mParams.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        mParams.put(RequestConstant.KEY_COUPON_TYPE, TextUtils.isEmpty(mCouponType) ? Constant.COUPON_ALL_TYPE : mCouponType);
        mParams.put(RequestConstant.KEY_COUPON_ONLINE, mCouponListType.equals(COUPON_LIST_TYPE_ONLINE) ? Constant.COUPON_ONLINE_TRUE : Constant.COUPON_ONLINE_FALSE);
        mParams.put(RequestConstant.KEY_COUPON_LIST_TYPE, mCouponListType);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_COUPON_LIST_DATA, mParams);
    }

    @Override
    protected void initView() {
        mEmptyView.setStatus(EmptyView.Status.Loading);
        mCouponType = Constant.COUPON_ALL_TYPE;
        mCouponListSubscribe = RxBus.getInstance().toObservable(CouponListResult.class).subscribe(
                result -> handleCouponListResult(result)
        );
    }

    private void handleCouponListResult(CouponListResult result) {
        mEmptyView.setStatus(EmptyView.Status.Gone);
        if (result.onLineType == null || !result.onLineType.equals(mCouponListType)) {
            return;
        }
        if (result.statusCode == 200) {
            for (CouponBean bean : result.respData) {
                bean.online = mCouponListType.equals(COUPON_LIST_TYPE_ONLINE) ? Constant.COUPON_ONLINE_TRUE : Constant.COUPON_ONLINE_FALSE;
            }
            onGetListSucceeded(result.pageCount, result.respData);
        } else {
            onGetListFailed(result.msg);
        }
    }

    public void getArgumentsData() {
        mCouponListType = getArguments().getString(COUPON_LIST_STATUS_TYPE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mCouponListSubscribe);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClicked(CouponBean bean) {
        Intent intent = new Intent(getActivity(), CouponInfoDetailActivity.class);
        intent.putExtra(KEY_INTENT_COUPON_BEAN, bean);
        startActivity(intent);
    }

    @Override
    public void onPositiveButtonClicked(CouponBean bean) {
        doShare(bean.shareUrl, bean.actTitle);
    }

    @Subscribe
    public void CouponFilterEvent(CouponFilterEvent event) {
        mCouponType = event.filterType;
        onRefresh();
    }

    public void doShare(String mShareUrl, String actTitle) {
        Map<String, Object> params = new HashMap<>();
        Bitmap thumbnail = ImageLoader.readBitmapFromFile(ClubData.getInstance().getClubImageLocalPath());
        params.put(Constant.PARAM_SHARE_THUMBNAIL, thumbnail);
        params.put(Constant.PARAM_SHARE_URL, mShareUrl);
        params.put(Constant.PARAM_SHARE_TITLE, actTitle);
        ClubInfo clubInfo = ClubData.getInstance().getClubInfo();
        if (clubInfo != null) {
            String shareDescription = String.format(ResourceUtils.getString(R.string.share_description), clubInfo.name);
            params.put(Constant.PARAM_SHARE_DESCRIPTION, shareDescription);
        } else {
            params.put(Constant.PARAM_SHARE_DESCRIPTION, ResourceUtils.getString(R.string.share_description_without_club_name));
        }
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM, params);
    }


}
