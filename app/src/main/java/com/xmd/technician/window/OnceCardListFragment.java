package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.OnceCardItemBean;
import com.xmd.technician.common.OnceCardHelper;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.OnceCardResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;
import com.xmd.technician.widget.EmptyView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Lhj on 2017/2/9.
 */

public class OnceCardListFragment extends BaseListFragment<OnceCardItemBean> {

    @Bind(R.id.empty_view_widget)
    EmptyView mEmptyViewWidget;
    private Subscription mOnceCardListSubscription;
    private OnceCardHelper mOnceCardHelper;
    private int mTotalAmount;

    public static OnceCardListFragment getInstance(int totalAmount) {
        OnceCardListFragment of = new OnceCardListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ShareDetailListActivity.SHARE_TOTAL_AMOUNT, totalAmount);
        of.setArguments(bundle);
        return of;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTotalAmount = getArguments().getInt(ShareDetailListActivity.SHARE_TOTAL_AMOUNT);
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        mEmptyViewWidget.setStatus(EmptyView.Status.Loading);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        return view;
    }

    @Override
    protected void dispatchRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ONCE_CARD_LIST_DETAIL,params);
    }

    @Override
    protected void initView() {
        mOnceCardListSubscription = RxBus.getInstance().toObservable(OnceCardResult.class).subscribe(
                onceCardResult -> handleCardResult(onceCardResult)
        );
    }

    private void handleCardResult(OnceCardResult onceCardResult) {
        if (onceCardResult.statusCode == 200) {

            if (onceCardResult.respData == null ) {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                mEmptyViewWidget.setEmptyViewWithDescription(R.drawable.ic_failed, "活动已下线");
            } else {
                mEmptyViewWidget.setStatus(EmptyView.Status.Gone);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                if (onceCardResult.respData.activityList.size() != mTotalAmount) {
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
                }
                if (mOnceCardHelper == null) {
                    mOnceCardHelper = new OnceCardHelper();
                }
                onGetListSucceeded(onceCardResult.pageCount, mOnceCardHelper.getCardItemBeanList(onceCardResult));
            }

        } else {
            onGetListFailed(onceCardResult.msg);
        }
    }

    @Override
    public void onShareClicked(OnceCardItemBean bean) {
        super.onShareClicked(bean);
        ShareController.doShare(bean.imageUrl, bean.shareUrl, bean.name,
                bean.shareDescription, Constant.SHARE_COUPON, "");
    }

    @Override
    public void onItemClicked(OnceCardItemBean bean) throws HyphenateException {
        super.onItemClicked(bean);
    }

    @Override
    public boolean isPaged() {
        return true;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mOnceCardListSubscription);
        ButterKnife.unbind(this);
    }
}
