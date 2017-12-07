package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.app.Constants;
import com.xmd.manager.R;
import com.xmd.manager.adapter.ReportDetailAdapter;
import com.xmd.manager.beans.CommissionTechInfo;
import com.xmd.manager.beans.TechBaseInfo;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CommissionAmountResult;
import com.xmd.manager.service.response.TechCommissionListResult;
import com.xmd.manager.widget.CircleImageView;
import com.xmd.manager.widget.CustomRecycleViewDecoration;
import com.xmd.manager.widget.DateTimePickDialog;
import com.xmd.manager.widget.EmptyView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by zr on 17-11-21.
 * 某个技师提成统计明细
 */

public class TechSalaryDetailActivity extends BaseActivity {
    private static final int DEFAULT_PAGE_SIZE = 10;
    public static final String EXTRA_CURRENT_DATE = "current_date";
    public static final String EXTRA_TECH_ID = "tech_id";
    public static final String EXTRA_TECH_NAME = "tech_name";
    public static final String EXTRA_TECH_NO = "tech_no";

    public static final String TYPE_SERVICE = "serviceCommission";
    public static final String TYPE_SALES = "salesCommission";
    public static final String TYPE_ALL = TYPE_SERVICE + "," + TYPE_SALES;

    public static final String TYPE_REQUEST_INIT = "type_init";
    public static final String TYPE_REQUEST_LOAD_MORE = "type_load_more";

    @BindView(R.id.img_tech_avatar)
    CircleImageView mTechAvatar;
    @BindView(R.id.tv_tech_nick)
    TextView mTechNickText;
    @BindView(R.id.tv_tech_no)
    TextView mTechNoText;
    @BindView(R.id.tv_tech_phone)
    TextView mTechPhone;

    @BindView(R.id.ev_empty)
    EmptyView mEmptyView;

    @BindView(R.id.tv_current_time)
    TextView mTimeText;
    @BindView(R.id.tv_total_amount)
    TextView mTotalAmount;

    @BindView(R.id.layout_amount)
    LinearLayout mAmountLayout;
    @BindView(R.id.layout_left_data)
    LinearLayout mServiceLayout;
    @BindView(R.id.tv_left_title)
    TextView mServiceTitle;
    @BindView(R.id.tv_left_content)
    TextView mServiceAmount;
    @BindView(R.id.layout_right_data)
    LinearLayout mSaleLayout;
    @BindView(R.id.tv_right_title)
    TextView mSaleTitle;
    @BindView(R.id.tv_right_content)
    TextView mSaleAmount;

    @BindView(R.id.rv_detail_data)
    RecyclerView mTechDetailList;

    // 参数
    private String mCurrentDate;
    private String mTechId;
    private String mTechName;
    private String mTechNo;
    private String mType;
    private int mCurrentPage;
    private int mPageSize;
    private String mRequestType;

    private int mLastVisibleItem;
    private LinearLayoutManager mLayoutManager;
    private boolean isLoadMore;
    private boolean hasMore;
    private ReportDetailAdapter<CommissionTechInfo> mAdapter;

    private boolean mServiceSelected;
    private boolean mSaleSelected;

    private Subscription mGetTechCommissionAmountSubscription;
    private Subscription mGetTechCommissionDetailListSubscription;
    private Subscription mReceiveTechBaseInfoSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_salary_detail);
        mCurrentDate = getIntent().getStringExtra(EXTRA_CURRENT_DATE);
        mTechId = getIntent().getStringExtra(EXTRA_TECH_ID);
        mTechName = getIntent().getStringExtra(EXTRA_TECH_NAME);
        mTechNo = getIntent().getStringExtra(EXTRA_TECH_NO);

        ButterKnife.bind(this);

        mGetTechCommissionAmountSubscription = RxBus.getInstance().toObservable(CommissionAmountResult.class).subscribe(
                result -> handleTechCommissionAmount(result)
        );
        mGetTechCommissionDetailListSubscription = RxBus.getInstance().toObservable(TechCommissionListResult.class).subscribe(
                techCommissionListResult -> handleTechCommissionDetailList(techCommissionListResult)
        );
        mReceiveTechBaseInfoSubscription = RxBus.getInstance().toObservable(TechBaseInfo.class).subscribe(
                techBaseInfo -> initDataByTechInfo(techBaseInfo)
        );

        setTitle(ResourceUtils.getString(R.string.tech_salary_detail_title));
        setRightVisible(true, R.drawable.ic_time_filter, null);
        mServiceTitle.setText(ResourceUtils.getString(R.string.report_service_title));
        mSaleTitle.setText(ResourceUtils.getString(R.string.report_sales_title));

        mAdapter = new ReportDetailAdapter<>(this);
        mLayoutManager = new LinearLayoutManager(this);
        mTechDetailList.setHasFixedSize(true);
        mTechDetailList.setLayoutManager(mLayoutManager);
        mTechDetailList.addItemDecoration(new CustomRecycleViewDecoration(1));
        mTechDetailList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mAdapter.getItemCount()) {
                    mRequestType = TYPE_REQUEST_LOAD_MORE;
                    requestDetailList();    //获取明细列表
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
        mAdapter.setCallBack(new ReportDetailAdapter.CallBack() {
            @Override
            public void onLoadMore() {
                mRequestType = TYPE_REQUEST_LOAD_MORE;
                requestDetailList();    //获取明细列表
            }

            @Override
            public void onItemClick(Object info) {
                CommissionTechInfo commissionTechInfo = (CommissionTechInfo) info;
                Intent intent = new Intent(TechSalaryDetailActivity.this, ReportDetailDialogActivity.class);
                intent.putExtra(ReportDetailDialogActivity.EXTRA_TYPE_DETAIL, ReportDetailDialogActivity.TYPE_DETAIL_SALARY);
                intent.putExtra(ReportDetailDialogActivity.EXTRA_TECH_COMMISSION_ID, String.valueOf(commissionTechInfo.id));
                startActivity(intent);
            }
        });
        mTechDetailList.setAdapter(mAdapter);

        initView();
        initData();
    }

    private void handleTechCommissionAmount(CommissionAmountResult result) {
        mEmptyView.setVisibility(View.GONE);
        if (result.statusCode == 200) {
            mAmountLayout.setVisibility(View.VISIBLE);
            mTotalAmount.setVisibility(View.VISIBLE);
            mTotalAmount.setText(Constants.MONEY_TAG + Utils.moneyToStringEx(result.respData.getTotalCommission()));
            mServiceAmount.setText(Constants.MONEY_TAG + Utils.moneyToStringEx(result.respData.serviceCommission));
            mSaleAmount.setText(Constants.MONEY_TAG + Utils.moneyToStringEx(result.respData.salesCommission));
            // 设置头像和手机号码
            Glide.with(this).load(result.respData.techAvatar).dontAnimate().placeholder(R.drawable.img_default_avatar).into(mTechAvatar);
            mTechPhone.setText("手机号：" + result.respData.techPhone);
        } else {
            mAmountLayout.setVisibility(View.GONE);
            mTotalAmount.setVisibility(View.GONE);
        }
    }

    private void handleTechCommissionDetailList(TechCommissionListResult result) {
        if (result.statusCode == 200) {
            //请求成功
            switch (result.requestType) {
                case TYPE_REQUEST_INIT:
                    mEmptyView.setVisibility(View.GONE);
                    mAdapter.clearData();
                    mTechDetailList.removeAllViews();
                    if (result.respData != null && !result.respData.isEmpty()) {
                        mTechDetailList.setVisibility(View.VISIBLE);
                        if (mCurrentPage < result.pageCount) {
                            mCurrentPage++;
                            hasMore = true;
                            mAdapter.setStatus(ReportDetailAdapter.FOOTER_STATUS_SUCCESS);
                        } else {
                            hasMore = false;
                            mAdapter.setStatus(ReportDetailAdapter.FOOTER_STATUS_NONE);
                        }
                        mAdapter.setData(result.respData);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mTechDetailList.setVisibility(View.GONE);
                    }
                    break;
                case TYPE_REQUEST_LOAD_MORE:
                    if (mCurrentPage < result.pageCount) {
                        mCurrentPage++;
                        hasMore = true;
                        mAdapter.setStatus(ReportDetailAdapter.FOOTER_STATUS_SUCCESS);
                    } else {
                        hasMore = false;
                        mAdapter.setStatus(ReportDetailAdapter.FOOTER_STATUS_NONE);
                    }
                    isLoadMore = false;
                    mAdapter.setData(result.respData);
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        } else {
            //请求失败
            switch (result.requestType) {
                case TYPE_REQUEST_INIT:
                    mEmptyView.setVisibility(View.GONE);
                    mTechDetailList.removeAllViews();
                    mTechDetailList.setVisibility(View.GONE);
                    break;
                case TYPE_REQUEST_LOAD_MORE:
                    mAdapter.setStatus(ReportDetailAdapter.FOOTER_STATUS_ERROR);
                    mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
                    break;
                default:
                    break;
            }
        }
    }

    private void initView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setStatus(EmptyView.Status.Loading);
        mTimeText.setText(mCurrentDate);
        mTotalAmount.setVisibility(View.GONE);
    }

    private void initData() {
        mTechNickText.setText(mTechName);
        mTechNoText.setText("[" + mTechNo + "]");
        requestAmountInfo();   // 获取提成金额

        mCurrentPage = 1;
        mPageSize = DEFAULT_PAGE_SIZE;
        mType = TYPE_ALL;
        mRequestType = TYPE_REQUEST_INIT;
        requestDetailList();    // 获取明细列表
    }

    private void initDataByTechInfo(TechBaseInfo techBaseInfo) {
        mTechId = techBaseInfo.techId;
        mTechName = techBaseInfo.techName;
        mTechNo = techBaseInfo.techNo;
        mTechNickText.setText(mTechName);
        mTechNoText.setText("[" + mTechNo + "]");

        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setStatus(EmptyView.Status.Loading);
        updateServiceLayout(false);
        updateSaleLayout(false);
        mCurrentPage = 1;
        mPageSize = DEFAULT_PAGE_SIZE;
        mType = TYPE_ALL;
        mRequestType = TYPE_REQUEST_INIT;
        requestAmountInfo();   // 获取提成金额
        requestDetailList();    //获取明细列表
    }

    private void requestAmountInfo() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_START_DATE, mCurrentDate);
        params.put(RequestConstant.KEY_END_DATE, mCurrentDate);
        params.put(RequestConstant.KEY_TECH_ID, mTechId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_COMMISSION_AMOUNT, params);
    }

    private void requestDetailList() {
        if (TYPE_REQUEST_LOAD_MORE.equals(mRequestType)) {
            if (isLoadMore || !hasMore) {
                return;
            } else {
                isLoadMore = true;
            }
        }
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_START_DATE, mCurrentDate);
        params.put(RequestConstant.KEY_END_DATE, mCurrentDate);
        params.put(RequestConstant.KEY_TECH_ID, mTechId);
        params.put(RequestConstant.KEY_TYPE, mType);
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mCurrentPage));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(mPageSize));
        params.put(RequestConstant.KEY_REQUEST_TYPE, mRequestType);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_COMMISSION_DETAIL_LIST, params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetTechCommissionAmountSubscription);
        RxBus.getInstance().unsubscribe(mGetTechCommissionDetailListSubscription);
        RxBus.getInstance().unsubscribe(mReceiveTechBaseInfoSubscription);
    }

    @OnClick(R.id.toolbar_right_image)
    public void onTimeFilter() {
        DateTimePickDialog timePickDialog = new DateTimePickDialog(TechSalaryDetailActivity.this, mTimeText.getText().toString());
        timePickDialog.setButtonClickListener(date -> {
            mTimeText.setText(date);
            if (!mCurrentDate.equals(date)) {
                mCurrentDate = date;
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setStatus(EmptyView.Status.Loading);
                updateServiceLayout(false);
                updateSaleLayout(false);
                mCurrentPage = 1;
                mPageSize = DEFAULT_PAGE_SIZE;
                mType = TYPE_ALL;
                mRequestType = TYPE_REQUEST_INIT;
                requestAmountInfo();   // 获取提成金额
                requestDetailList();    //获取明细列表
            }
        });
        timePickDialog.dateTimePicKDialog(mTimeText);
    }

    @OnClick(R.id.tv_tech_exchange)
    public void onTechChange() {
        Intent intent = new Intent(this, TechListDialogActivity.class);
        startActivity(intent);
    }

    @OnClick({R.id.layout_left_data, R.id.layout_right_data})
    public void onLayoutClick(View view) {
        switch (view.getId()) {
            case R.id.layout_left_data:
                mServiceSelected = !mServiceSelected;
                if (mServiceSelected) {
                    mSaleSelected = !mServiceSelected;
                    mType = TYPE_SERVICE;
                } else {
                    mType = TYPE_ALL;
                }
                break;
            case R.id.layout_right_data:
                mSaleSelected = !mSaleSelected;
                if (mSaleSelected) {
                    mServiceSelected = !mSaleSelected;
                    mType = TYPE_SALES;
                } else {
                    mType = TYPE_ALL;
                }
                break;
            default:
                break;
        }

        updateServiceLayout(mServiceSelected);
        updateSaleLayout(mSaleSelected);

        mCurrentPage = 1;
        mPageSize = DEFAULT_PAGE_SIZE;
        mRequestType = TYPE_REQUEST_INIT;
        requestDetailList();
    }

    private void updateServiceLayout(boolean selected) {
        if (selected) {
            mServiceLayout.setBackgroundResource(R.drawable.bg_report_select);
            mServiceAmount.setTextColor(ResourceUtils.getColor(R.color.colorStatusYellow));
            mServiceTitle.setTextColor(ResourceUtils.getColor(R.color.colorText5));
        } else {
            mServiceLayout.setBackgroundResource(R.color.colorWhite);
            mServiceAmount.setTextColor(ResourceUtils.getColor(R.color.colorBlue));
            mServiceTitle.setTextColor(ResourceUtils.getColor(R.color.colorText3));
        }
    }

    private void updateSaleLayout(boolean selected) {
        if (selected) {
            mSaleLayout.setBackgroundResource(R.drawable.bg_report_select);
            mSaleAmount.setTextColor(ResourceUtils.getColor(R.color.colorStatusYellow));
            mSaleTitle.setTextColor(ResourceUtils.getColor(R.color.colorText5));
        } else {
            mSaleLayout.setBackgroundResource(R.color.colorWhite);
            mSaleAmount.setTextColor(ResourceUtils.getColor(R.color.colorBlue));
            mSaleTitle.setTextColor(ResourceUtils.getColor(R.color.colorText3));
        }
    }
}
