package com.xmd.technician.window;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.xmd.technician.Adapter.ExpandableMarketListViewAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.OnceCardItemBean;
import com.xmd.technician.bean.OnceCardType;
import com.xmd.technician.common.OnceCardHelper;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.OnceCardResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Lhj on 17-5-15.
 */

public class MallDiscountChatShareActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.view_emptyView)
    EmptyView viewEmptyView;
    @Bind(R.id.list)
    ExpandableListView mListView;


    private ExpandableMarketListViewAdapter marketAdapter;
    private List<OnceCardItemBean> mSelectedBeans;
    private TextView sentMessage;
    private OnceCardHelper mOnceCardHelper;
    private Subscription mOnceCardListSubscription;
    private List<OnceCardType> mOnceCardTypes;
    private List<List<OnceCardItemBean>> onceCardItemBeanList;
    private List<OnceCardItemBean> mOnceCardList;
    private List<OnceCardItemBean> mPackageList;
    private List<OnceCardItemBean> mCreditGiftList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_discount_chat_share);
        ButterKnife.bind(this);
        sentMessage = (TextView) findViewById(R.id.toolbar_right_share);
        sentMessage.setVisibility(View.VISIBLE);
        sentMessage.setEnabled(false);
        sentMessage.setOnClickListener(this);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void initView() {
        setTitle(ResourceUtils.getString(R.string.mall_discount_title));
        setBackVisible(true);
        mOnceCardListSubscription = RxBus.getInstance().toObservable(OnceCardResult.class).subscribe(
                onceCardResult -> handleOnceCardListResult(onceCardResult)
        );

        mSelectedBeans = new ArrayList<>();
        mOnceCardTypes = new ArrayList<>();
        onceCardItemBeanList = new ArrayList<>();
        mOnceCardList = new ArrayList<>();
        mPackageList = new ArrayList<>();
        mCreditGiftList = new ArrayList<>();
        marketAdapter = new ExpandableMarketListViewAdapter(this);
        marketAdapter.setOnChildrenItemClicked(new ExpandableMarketListViewAdapter.OnChildrenItemClickedInterface() {
            @Override
            public void onChildrenClickedListener(OnceCardItemBean bean, int groupPosition, int childPosition, boolean isSelected) {
                if (isSelected) {
                    mSelectedBeans.add(bean);
                    onceCardItemBeanList.get(groupPosition).get(childPosition).selectedStatus = 0;
                    marketAdapter.refreshChildData(onceCardItemBeanList);
                } else {
                    onceCardItemBeanList.get(groupPosition).get(childPosition).selectedStatus = 1;
                    marketAdapter.refreshChildData(onceCardItemBeanList);
                    mSelectedBeans.remove(bean);
                }
                if (mSelectedBeans.size() > 0) {
                    sentMessage.setEnabled(true);
                    sentMessage.setText(String.format("分享(%s)", mSelectedBeans.size()));
                } else {
                    sentMessage.setEnabled(false);
                    sentMessage.setText("分享");
                }
            }
        });
        mListView.setAdapter(marketAdapter);
        mListView.setDivider(null);
        viewEmptyView.setStatus(EmptyView.Status.Loading);

        getOnceCardListData();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_right_share) {
            Intent resultIntent = new Intent();
            resultIntent.putParcelableArrayListExtra(TechChatActivity.REQUEST_PREFERENTIAL_TYPE, (ArrayList<? extends Parcelable>) mSelectedBeans);
            setResult(RESULT_OK, resultIntent);
        }
        this.finish();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleOnceCardListResult(OnceCardResult onceCardResult) {
        mOnceCardHelper = OnceCardHelper.getInstance();
        viewEmptyView.setStatus(EmptyView.Status.Gone);

        if (onceCardResult.statusCode == 200) {
            mOnceCardHelper.getCardItemBeanList(onceCardResult);
            mOnceCardList = mOnceCardHelper.getOnceCardList();
            mPackageList = mOnceCardHelper.getPackageList();
            mCreditGiftList = mOnceCardHelper.getCreditList();
            if (mOnceCardList.size() > 0) {
                mOnceCardTypes.add(new OnceCardType(Constant.ITEM_CARD_TYPE, "单项次卡"));
                onceCardItemBeanList.add(mOnceCardList);
            }
            if (mPackageList.size() > 0) {
                mOnceCardTypes.add(new OnceCardType(Constant.ITEM_PACKAGE_TYPE, "超值套餐"));
                onceCardItemBeanList.add(mPackageList);
            }
            if (mCreditGiftList.size() > 0) {
                mOnceCardTypes.add(new OnceCardType(Constant.CREDIT_GIFT_TYPE, "积分礼物"));
                onceCardItemBeanList.add(mCreditGiftList);
            }
            if (onceCardItemBeanList.size() > 0) {
                marketAdapter.setData(mOnceCardTypes, onceCardItemBeanList);
                mListView.expandGroup(0, true);
            } else {
                viewEmptyView.setStatus(EmptyView.Status.Empty);
            }
        } else {
            viewEmptyView.setStatus(EmptyView.Status.Failed);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mOnceCardListSubscription);
    }


    private void getOnceCardListData() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(1));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(100));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ONCE_CARD_LIST_DETAIL, params);
    }


}
