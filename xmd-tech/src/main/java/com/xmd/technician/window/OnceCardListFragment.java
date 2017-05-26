package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.technician.Adapter.MallPackageListAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.OnceCardItemBean;
import com.xmd.technician.common.OnceCardHelper;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.OnceCardResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 2017/2/9.
 */

public class OnceCardListFragment extends BaseFragment {

    @Bind(R.id.empty_view_widget)
    EmptyView mEmptyViewWidget;
    @Bind(R.id.tv_once_card_table)
    TextView tvOnceCardTable;
    @Bind(R.id.tv_package_table)
    TextView tvPackageTable;
    @Bind(R.id.tv_credit_gift_table)
    TextView tvCreditGiftTable;
    @Bind(R.id.list)
    RecyclerView recyclerView;

    private Subscription mOnceCardListSubscription;
    private OnceCardHelper mOnceCardHelper;
    private int mTotalAmount;
    private List<View> tableViews;
    private List<OnceCardItemBean> onCardList;
    private MallPackageListAdapter adapter;
    Map<String, Object> params = new HashMap<>();


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
        View view = inflater.inflate(R.layout.fragment_once_card_list, container, false);
        ButterKnife.bind(this, view);
        mEmptyViewWidget.setStatus(EmptyView.Status.Loading);
        tableViews = new ArrayList<>();
        tableViews.add(tvOnceCardTable);
        tableViews.add(tvPackageTable);
        tableViews.add(tvCreditGiftTable);
        tvOnceCardTable.setSelected(true);
        initView();
        return view;
    }

    protected void initView() {
        onCardList = new ArrayList<>();
        mOnceCardListSubscription = RxBus.getInstance().toObservable(OnceCardResult.class).subscribe(
                onceCardResult -> handleCardResult(onceCardResult)
        );
        adapter = new MallPackageListAdapter(getActivity(), onCardList);
        adapter.setItemClickedInterface(new MallPackageListAdapter.ItemClickedInterface() {

            @Override
            public void onShareClicked(OnceCardItemBean bean) {
                ShareController.doShare(bean.imageUrl, bean.shareUrl, bean.name,
                        bean.shareDescription, Constant.SHARE_TYPE_ONCE_CARD, bean.id);
            }

            @Override
            public void onPositiveButtonClicked(OnceCardItemBean bean) {
                params.clear();
                params.put(Constant.SHARE_CONTEXT, getActivity());
                params.put(Constant.PARAM_SHARE_THUMBNAIL, bean.imageUrl);
                params.put(Constant.PARAM_SHARE_URL, bean.shareUrl);
                params.put(Constant.PARAM_SHARE_TITLE, bean.name);
                params.put(Constant.PARAM_SHARE_DESCRIPTION, bean.shareDescription);
                params.put(Constant.PARAM_SHARE_TYPE, Constant.SHARE_TYPE_ONCE_CARD);
                params.put(Constant.PARAM_ACT_ID, bean.id);
                if(bean.cardType.equals(Constant.ITEM_CARD_TYPE)){
                    params.put(Constant.PARAM_SHARE_DIALOG_TITLE, ResourceUtils.getString(R.string.chat_timescard_message_type));
                }else if(bean.cardType.equals(Constant.ITEM_PACKAGE_TYPE)){
                    params.put(Constant.PARAM_SHARE_DIALOG_TITLE, ResourceUtils.getString(R.string.chat_package_message_type));
                }else{
                    params.put(Constant.PARAM_SHARE_DIALOG_TITLE, ResourceUtils.getString(R.string.chat_gift_message_type));
                }
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEG_SHARE_QR_CODE, params);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        dispatchRequest();

    }

    protected void dispatchRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(1));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(100));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ONCE_CARD_LIST_DETAIL, params);
    }

    private void handleCardResult(OnceCardResult onceCardResult) {
        if (onceCardResult.statusCode == 200) {
            mEmptyViewWidget.setStatus(EmptyView.Status.Gone);
            if (onceCardResult.respData == null) {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
                mEmptyViewWidget.setEmptyViewWithDescription(R.drawable.ic_failed, "活动已下线");
            } else {
                mEmptyViewWidget.setStatus(EmptyView.Status.Gone);
                if (onceCardResult.respData.activityList.size() != mTotalAmount) {
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
                }
                if (mOnceCardHelper == null) {
                    mOnceCardHelper = OnceCardHelper.getInstance();
                }
                adapter.setData(mOnceCardHelper.getCardItemBeanList(onceCardResult));
            }

        } else {
            mEmptyViewWidget.setStatus(EmptyView.Status.Failed);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mOnceCardListSubscription);
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.tv_once_card_table, R.id.tv_package_table, R.id.tv_credit_gift_table})
    public void onViewClicked(View view) {
        for (int i = 0; i < tableViews.size(); i++) {
            tableViews.get(i).setSelected(false);
        }
        switch (view.getId()) {
            case R.id.tv_once_card_table:
                tvOnceCardTable.setSelected(true);
                if (mOnceCardHelper.itemCardSize > 0) {
                    recyclerView.getLayoutManager().scrollToPosition(0);
                } else {
                    Utils.makeShortToast(getActivity(), "暂无此类型");
                }

                break;
            case R.id.tv_package_table:
                if (mOnceCardHelper.packageSize > 0) {
                    ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPositionWithOffset(mOnceCardHelper.itemCardSize,0);

                } else {
                    Utils.makeShortToast(getActivity(), "暂无此类型");
                }
                tvPackageTable.setSelected(true);
                break;
            case R.id.tv_credit_gift_table:
                if (mOnceCardHelper.creditSize > 0) {
                   ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPositionWithOffset(mOnceCardHelper.itemCardSize + mOnceCardHelper.packageSize,0);
                } else {
                    Utils.makeShortToast(getActivity(), "暂无此类型");
                }

                tvCreditGiftTable.setSelected(true);
                break;
        }
    }
}
