package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.adapter.PageFragmentPagerAdapter;
import com.xmd.manager.beans.SwitchIndex;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.OrderFilterChangeResult;
import com.xmd.manager.widget.ArrayBottomPopupWindow;
import com.xmd.manager.widget.DateTimePickDialog;
import com.xmd.manager.widget.ViewPagerTabIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj on 2016/12/23.
 */

public class OrderListFragment extends BaseFragment {

    @BindView(R.id.toolbar_left)
    ImageView mToolbarLeft;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar_customer_filter_btn)
    TextView mToolbarCustomerFilterBtn;
    @BindView(R.id.toolbar_right_text)
    TextView mToolbarRightText;
    @BindView(R.id.toolbar_right_image)
    ImageView mToolbarRightImage;
    @BindView(R.id.toolbar_notice_unread_msg)
    TextView mToolbarNoticeUnreadMsg;
    @BindView(R.id.toolbar_right)
    FrameLayout mToolbarRight;
    @BindView(R.id.rl_toolbar)
    RelativeLayout mRlToolbar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.startTime)
    TextView mStartTime;
    @BindView(R.id.endTime)
    TextView mEndTime;
    @BindView(R.id.btnSubmit)
    Button mBtnSubmit;
    @BindView(R.id.time_text_view)
    LinearLayout mTimeTextView;
    @BindView(R.id.tab_indicator)
    ViewPagerTabIndicator mTabIndicator;
    @BindView(R.id.vp_order)
    ViewPager mViewPager;

    private PageFragmentPagerAdapter mPageFragmentPagerAdapter;
    private ArrayBottomPopupWindow<String> mOrderPopupWindow;
    private View view;
    private String initStartDateTime;
    private String initEndDateTime;
    private String[] tabTexts;
    private Subscription mFilterOrderSubscription;
    private Subscription mSwitchRangeSubscription;
    private Map<String, String> params;
    private String mCurrentType;
    private String currentST;
    private String currentED;
    private boolean isFromOrderDetail;
    String sT;
    String eT;
    private boolean isTotal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_list, container, false);
        ButterKnife.bind(this, view);
        tabTexts = new String[]{
                ResourceUtils.getString(R.string.today),
                ResourceUtils.getString(R.string.current_week),
                ResourceUtils.getString(R.string.current_month),
                ResourceUtils.getString(R.string.accumulate)};

        return view;
    }

    @Override
    protected void initView() {
        params = new HashMap<>();
        initStartDateTime = DateUtil.getCurrentDate();
        initEndDateTime = DateUtil.getCurrentDate();
        mStartTime.setText(initStartDateTime);
        mEndTime.setText(initEndDateTime);
        mToolbarLeft.setVisibility(View.GONE);
        mToolbarRightImage.setImageDrawable(ResourceUtils.getDrawable(R.drawable.ic_search_selector));
        mToolbarRightImage.setVisibility(View.VISIBLE);
        mToolbarCustomerFilterBtn.setVisibility(View.VISIBLE);
        mToolbarCustomerFilterBtn.setText(ResourceUtils.getString(R.string.all_orders));
        mOrderPopupWindow = new ArrayBottomPopupWindow<>(mToolbarCustomerFilterBtn, null, ResourceUtils.getDimenInt(R.dimen.order_type_item_width), false);
        mOrderPopupWindow.setDataSet(new ArrayList<>(Constant.ORDER_TYPE_LABELS.keySet()), mToolbarCustomerFilterBtn.getText().toString());
        mOrderPopupWindow.setItemClickListener((parent, view, position, id) -> {
            String sTitle = (String) parent.getAdapter().getItem(position);
            initTitleData(sTitle);

        });
        mPageFragmentPagerAdapter = new PageFragmentPagerAdapter(getChildFragmentManager(), getActivity());
        for (int i = 0; i < tabTexts.length; i++) {
            Bundle args = new Bundle();
            args.putInt(NewOrderFragment.BIZ_TYPE, i);
            mPageFragmentPagerAdapter.addFragment(NewOrderFragment.class.getName(), args);
        }
        mViewPager.setAdapter(mPageFragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mTabIndicator.setTabTexts(tabTexts);
        mTabIndicator.setWithIndicator(true);
        mTabIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
        mTabIndicator.setViewPager(mViewPager);
        mTabIndicator.setup();
        mTabIndicator.setOnPageChangeListener(position -> refreshTimeText(position));
        mTabIndicator.setOnTabclickListener(position -> refreshTimeText(position));
        mFilterOrderSubscription = RxBus.getInstance().toObservable(OrderFilterChangeResult.class).subscribe(
                orderType -> handleOrderFilterResult(orderType)

        );
        mSwitchRangeSubscription = RxBus.getInstance().toObservable(SwitchIndex.class).subscribe(
                switchIndex -> {
                    mViewPager.setCurrentItem(switchIndex.selectedIndex);
                    isFromOrderDetail = true;
                    currentST = switchIndex.startTime;
                    currentED = switchIndex.endTime;
                    isTotal = true;
                }
        );
        getData(DateUtil.getCurrentDate(), DateUtil.getCurrentDate());
    }

    private void handleOrderFilterResult(OrderFilterChangeResult orderType) {
        mStartTime.setText(orderType.startTime);
        mEndTime.setText(orderType.endTime);
        mCurrentType = orderType.filterText;
        mToolbarCustomerFilterBtn.setText(mCurrentType);
        mOrderPopupWindow.setDataSet(new ArrayList<>(Constant.ORDER_TYPE_LABELS.keySet()), mToolbarCustomerFilterBtn.getText().toString());
    }

    private void initTitleData(String title) {
        mOrderPopupWindow.setDataSet(new ArrayList<>(Constant.ORDER_TYPE_LABELS.keySet()), mToolbarCustomerFilterBtn.getText().toString());
        params.clear();
        params.put(Constant.ORDER_STATUS_TYPE, title);
        if (isFromOrderDetail) {
            params.put(Constant.ORDER_START_TIME, currentST);
            params.put(Constant.ORDER_END_TIME, currentED);
        } else {
            params.put(Constant.ORDER_START_TIME, initStartDateTime);
            params.put(Constant.ORDER_END_TIME, initEndDateTime);
        }

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_FILTER_ORDER_LIST, params);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mFilterOrderSubscription, mSwitchRangeSubscription);
    }

    @OnClick({R.id.toolbar_customer_filter_btn, R.id.toolbar_right_image, R.id.startTime, R.id.endTime, R.id.btnSubmit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_customer_filter_btn:
                mOrderPopupWindow.showAsDownCenter(true);
                break;
            case R.id.toolbar_right_image:
                startActivity(new Intent(getActivity(), OrderSearchActivity.class));
                break;
            case R.id.startTime:
                DateTimePickDialog dataPickDialogStr = new DateTimePickDialog(getActivity(), mStartTime.getText().toString());
                dataPickDialogStr.dateTimePicKDialog(mStartTime);
                break;
            case R.id.endTime:
                DateTimePickDialog dataPickDialogEnd = new DateTimePickDialog(getActivity(), mEndTime.getText().toString());
                dataPickDialogEnd.dateTimePicKDialog(mEndTime);
                break;
            case R.id.btnSubmit:
                sT = mStartTime.getText().toString();
                eT = mEndTime.getText().toString();
                int str = Utils.dateToInt(sT);
                int end = Utils.dateToInt(eT);
                if (end >= str) {
                    if (isTotal) {
                        getData(sT, eT);
                    } else {
                        mViewPager.setCurrentItem(3);
                    }
                    mStartTime.setText(sT);
                    mEndTime.setText(eT);
                    isTotal = true;
                } else {
                    ToastUtils.showToastShort(getActivity(), ResourceUtils.getString(R.string.time_select_alert));
                    return;
                }
                break;
        }
    }

    private void refreshTimeText(int range) {

        switch (range) {
            case 0:
                isFromOrderDetail = false;
                initStartDateTime = DateUtil.getCurrentDate();
                mStartTime.setText(DateUtil.getCurrentDate());
                isTotal = false;
                break;
            case 1:
                isFromOrderDetail = false;
                isTotal = false;
                initStartDateTime = DateUtil.getMondayOfWeek();
                break;
            case 2:
                isFromOrderDetail = false;
                isTotal = false;
                initStartDateTime = DateUtil.getFirstDayOfMonth();
                break;
            case 3:
                if (Utils.isNotEmpty(SharedPreferenceHelper.getCurrentClubCreateTime())) {
                    initStartDateTime = SharedPreferenceHelper.getCurrentClubCreateTime();
                } else {
                    initStartDateTime = "2015-01-01";
                }
                break;
            case 4:
                isTotal = false;
                mStartTime.setText(sT);
                mEndTime.setText(eT);
                return;
        }
        if (!isFromOrderDetail) {
            mStartTime.setText(initStartDateTime);
            initEndDateTime = DateUtil.getCurrentDate();
            mEndTime.setText(initEndDateTime);
        }

    }

    private void getData(String startDate, String endDate) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_ORDER_STATUS, Constant.ORDER_TYPE_LABELS.get(mCurrentType));
        params.put(RequestConstant.KEY_PAGE, String.valueOf(1));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(20));
        params.put(RequestConstant.KEY_START_DATE, startDate);
        params.put(RequestConstant.KEY_END_DATE, endDate);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ORDER_LIST, params);
    }

}
