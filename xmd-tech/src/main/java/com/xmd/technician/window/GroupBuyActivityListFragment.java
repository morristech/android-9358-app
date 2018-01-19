package com.xmd.technician.window;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.GroupBuyBean;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.GroupBuyListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;
import com.xmd.technician.widget.EmptyView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Lhj on 18-1-16.
 */

public class GroupBuyActivityListFragment extends BaseListFragment<GroupBuyBean> {

    @BindView(R.id.empty_view_widget)
    EmptyView mEmptyViewWidget;

    private Subscription mGroupBuyListSubscription;
    private int mTotalAmount;
    private Map<String, Object> params = new HashMap<>();

    public static GroupBuyActivityListFragment getInstance(int totalAmount) {
        GroupBuyActivityListFragment gf = new GroupBuyActivityListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ShareDetailListActivity.SHARE_TOTAL_AMOUNT, totalAmount);
        gf.setArguments(bundle);
        return gf;
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
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GROUP_BUY_ACTIVITY);
    }

    @Override
    protected void initView() {
        mGroupBuyListSubscription = RxBus.getInstance().toObservable(GroupBuyListResult.class).subscribe(
                result -> handleGroupBuyResult(result)
        );
    }

    private void handleGroupBuyResult(GroupBuyListResult result) {
        if(result.statusCode == 200){
            if(result.respData == null){
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                mEmptyViewWidget.setEmptyViewWithDescription(R.drawable.ic_failed,"活动已下线");
            }else {
                mEmptyViewWidget.setStatus(EmptyView.Status.Gone);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                if(result.respData.size() != mTotalAmount){
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
                }
                onGetListSucceeded(result.pageCount,result.respData);
            }
        }else{
            onGetListFailed(result.msg);
        }
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public void onShareClicked(GroupBuyBean bean) {
        super.onShareClicked(bean);
        ShareController.doShare(bean.itemImageUrl, bean.shareUrl,bean.name, getDescribeMessage(bean),
                 RequestConstant.KEY_GROUP_BUY, bean.itemId);

    }

    //扫码分享
    @Override
    public void onPositiveButtonClicked(GroupBuyBean bean) {
        super.onPositiveButtonClicked(bean);
        params.clear();
        params.put(Constant.SHARE_CONTEXT, getActivity());
        params.put(Constant.PARAM_SHARE_THUMBNAIL, bean.itemImageUrl);
        params.put(Constant.PARAM_SHARE_URL, bean.shareUrl);
        params.put(Constant.PARAM_SHARE_TITLE, bean.name);
        params.put(Constant.PARAM_SHARE_DESCRIPTION, getDescribeMessage(bean));
        params.put(Constant.PARAM_SHARE_TYPE, RequestConstant.KEY_GROUP_BUY);
        params.put(Constant.PARAM_ACT_ID, bean.itemId);
        params.put(Constant.PARAM_SHARE_DIALOG_TITLE, "拼团活动");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEG_SHARE_QR_CODE, params);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mGroupBuyListSubscription);
    }

    private String getDescribeMessage(GroupBuyBean bean){
        String itemName = bean.itemName;
        String itemPrice = String.format("原价%1.2f",bean.itemPrice / 100f );
        String price = String.format("现仅需%1.2f",bean.price / 100f);
        return String.format(ResourceUtils.getString(R.string.group_buy_share_des)+"%s,%s,%s,立刻点击参与！",itemName,itemPrice,price);

    }
}
