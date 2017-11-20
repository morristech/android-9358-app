package com.xmd.manager.window;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.common.OrderFilterManager;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.widget.ClearableEditText;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-9-11.
 */

public class OnlineOrderActivity extends BaseActivity {

    @BindView(R.id.et_order_search)
    ClearableEditText etOrderSearch;
    @BindView(R.id.btn_search_cancel)
    Button btnSearchCancel;
    @BindView(R.id.tv_time_filter)
    TextView tvTimeFilter;
    @BindView(R.id.fm_order_view)
    FrameLayout fmOrderView;

    public static final String INTENT_ORDER_STATUS_LIST = "orderStatusList";
    public static final String INTENT_ORDER_TECH_NO_LIST = "orderTechNo";
    public static final String INTENT_ORDER_PROJECT_LIST = "orderProject";
    public static final String INTENT_ORDER_START_TIME = "startTime";
    public static final String INTENT_ORDER_END_TIME = "endTime";

    public static final int INTENT_REQUEST_CODE = 0x001;

    private String mStartTime;
    private String mEndTime;
    private String mOrderStatus;
    private String mOrderProject;
    private String mOrderTechNo;
    private String mSearchPhone;
    private OrderListFilterFragment mOrderListFilterFragment;
    private OrderFilterManager mOrderFilterManager;

    public static void startOnlineOrderActivity(Activity activity, String startTime, String endTime, String orderStatus) {
        Intent intent = new Intent(activity, OnlineOrderActivity.class);
        intent.putExtra(Constant.ONLINE_ORDER_START_TIME, startTime);
        intent.putExtra(Constant.ONLINE_ORDER_END_TIME, endTime);
        intent.putExtra(Constant.ONLINE_ORDER_STATUS, orderStatus);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_order);
        getIntentData();
        initView();
        mOrderFilterManager = OrderFilterManager.getInstance();
        mOrderFilterManager.initData();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.staff_data_order_list_activity_title));
        setBackVisible(true);
        setRightVisible(true, R.drawable.ic_record_filter, filterListener);

        etOrderSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchOrder();
                    return true;
                }
                return false;
            }
        });
        tvTimeFilter.setText(mStartTime + "～" + mEndTime);
        initOrderListFilterFragment();
    }

    private void searchOrder() {
        mSearchPhone = etOrderSearch.getText().toString();

        if (TextUtils.isEmpty(mSearchPhone)) {
            XToast.show(ResourceUtils.getString(R.string.search_pay_activity_search_hint));
        } else {
            btnSearchCancel.setVisibility(View.VISIBLE);
            mOrderListFilterFragment.searchOrder(mSearchPhone);
            ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    View.OnClickListener filterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(OnlineOrderActivity.this, OrderFilterActivity.class);
            intent.putParcelableArrayListExtra(INTENT_ORDER_STATUS_LIST, (ArrayList<? extends Parcelable>) mOrderFilterManager.getOrderStatusList());
            intent.putParcelableArrayListExtra(INTENT_ORDER_PROJECT_LIST, (ArrayList<? extends Parcelable>) mOrderFilterManager.getOrderProjectList());
            intent.putParcelableArrayListExtra(INTENT_ORDER_TECH_NO_LIST, (ArrayList<? extends Parcelable>) mOrderFilterManager.getTechNoList());
            intent.putExtra(INTENT_ORDER_START_TIME, mStartTime);
            intent.putExtra(INTENT_ORDER_END_TIME, mEndTime);
            startActivityForResult(intent, INTENT_REQUEST_CODE);
        }
    };

    private void getIntentData() {
        mOrderProject = "";
        mOrderTechNo = "";
        mSearchPhone = "";
        Intent data = getIntent();
        mStartTime = data.getStringExtra(Constant.ONLINE_ORDER_START_TIME);
        mEndTime = data.getStringExtra(Constant.ONLINE_ORDER_END_TIME);
        mOrderStatus = data.getStringExtra(Constant.ONLINE_ORDER_STATUS);
    }

    @OnClick(R.id.btn_search_cancel)
    public void onViewClicked() {
        etOrderSearch.setText("");
        btnSearchCancel.setVisibility(View.GONE);
        mOrderListFilterFragment.cancelSearchOrder();
    }

    private void initOrderListFilterFragment() {
        mOrderListFilterFragment = new OrderListFilterFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(RequestConstant.KEY_ORDER_FILTER_START_DATE, mStartTime);
        bundle.putSerializable(RequestConstant.KEY_ORDER_FILTER_END_DATE, mEndTime);
        bundle.putSerializable(RequestConstant.KEY_ORDER_FILTER_STATUS, mOrderStatus);
        bundle.putSerializable(RequestConstant.KEY_ORDER_FILTER_ITEM_ID, mOrderProject);
        bundle.putSerializable(RequestConstant.KEY_ORDER_FILTER_TECH_ID, mOrderTechNo);
        bundle.putSerializable(RequestConstant.KEY_ORDER_FILTER_TELEPHONE, mSearchPhone);
        mOrderListFilterFragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fm_order_view, mOrderListFilterFragment);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OrderFilterManager.getInstance().managerDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_CODE && resultCode == RESULT_OK) {
            mOrderFilterManager.setOrderStatusList(data.getParcelableArrayListExtra(INTENT_ORDER_STATUS_LIST));
            mOrderFilterManager.setOrderProject(data.getParcelableArrayListExtra(INTENT_ORDER_PROJECT_LIST));
            mOrderFilterManager.setTechNoList(data.getParcelableArrayListExtra(INTENT_ORDER_TECH_NO_LIST));
            mStartTime = data.getStringExtra(INTENT_ORDER_START_TIME);
            mEndTime = data.getStringExtra(INTENT_ORDER_END_TIME);
            tvTimeFilter.setText(mStartTime + "～" + mEndTime);
            mOrderListFilterFragment.setRefreshData(mStartTime, mEndTime, mOrderFilterManager.getFilterOrderStatus(), mOrderFilterManager.getFilterOrderProject(),
                    mOrderFilterManager.getFilterOrderTechNo(), mSearchPhone);
        }
    }
}
