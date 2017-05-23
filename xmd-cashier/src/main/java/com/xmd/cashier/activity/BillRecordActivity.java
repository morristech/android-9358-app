package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.BillRecyclerAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.BillRecordContract;
import com.xmd.cashier.dal.bean.BillInfo;
import com.xmd.cashier.presenter.BillRecordPresenter;
import com.xmd.cashier.widget.ArrayPopupWindow;
import com.xmd.cashier.widget.CustomLoadingLayout;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 16-11-23.
 * 交易记录
 */

public class BillRecordActivity extends BaseActivity implements BillRecordContract.View, BillRecyclerAdapter.RecyclerCallBack {
    private BillRecordContract.Presenter mPresenter;

    private TextView mFilterTime;
    private TextView mFilterType;
    private TextView mFilterStatus;
    private TextView mSumAccount;
    private TextView mSumAmount;
    private RecyclerView mBillList;
    private RelativeLayout mBillSum;

    private ArrayPopupWindow<String> mPopTime;
    private ArrayPopupWindow<String> mPopType;
    private ArrayPopupWindow<String> mPopStatus;

    private CustomLoadingLayout mLoadLayout;

    private BillRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private boolean mHasMore = false;
    private int mLastVisibleItem;

    private int mTimeSelect = 0;    //默认0,表示全部(近三月)
    private int mTypeSelect = 0;    //默认0,表示全部支付类型
    private int mStatusSelect = 0;  //默认0,表示全部订单状态

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_record);
        mPresenter = new BillRecordPresenter(this, this);
        initView();
        mPresenter.onCreate();

        onRefresh();
    }

    private void initView() {
        showToolbar(R.id.toolbar, R.string.bill_record_title);

        mLoadLayout = (CustomLoadingLayout) findViewById(R.id.layout_load);

        mFilterTime = (TextView) findViewById(R.id.tv_pop_time);
        mFilterType = (TextView) findViewById(R.id.tv_pop_type);
        mFilterStatus = (TextView) findViewById(R.id.tv_pop_status);

        mBillSum = (RelativeLayout) findViewById(R.id.ll_bill_sum);
        mSumAccount = (TextView) findViewById(R.id.tv_sum_count);
        mSumAmount = (TextView) findViewById(R.id.tv_sum_amount);

        mBillList = (RecyclerView) findViewById(R.id.rc_list_bill);
        mAdapter = new BillRecyclerAdapter(this, this);
        mLayoutManager = new LinearLayoutManager(this);
        mBillList.setHasFixedSize(true);
        mBillList.setLayoutManager(mLayoutManager);
        mBillList.setItemAnimator(new DefaultItemAnimator());
        mBillList.setAdapter(mAdapter);
        mBillList.addItemDecoration(new CustomRecycleViewDecoration(2));
        mBillList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mAdapter.getItemCount()) {
                    if (mHasMore) {
                        mPresenter.loadMoreBills(mTimeSelect, mTypeSelect, mStatusSelect);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });

        mLoadLayout.setOnReloadListener(new CustomLoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View view) {
                onRefresh();
            }
        });

        mPopTime = new ArrayPopupWindow<>(this,
                mFilterTime,
                null,
                getWindowManager().getDefaultDisplay().getWidth() / 3,
                R.style.anim_top_to_bottom_style,
                getResources().getDrawable(R.drawable.bg_ex_popup_window), 0);
        mPopTime.setDataSet(new ArrayList<>(AppConstants.PAY_TIME_FILTERS.keySet()));
        mPopTime.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp = ((TextView) view).getText().toString();
                mFilterTime.setText(temp);
                mTimeSelect = AppConstants.PAY_TIME_FILTERS.get(temp);
                onRefresh();
            }
        });
        mPopTime.setDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mPresenter.dismissTimePop();
            }
        });

        mPopType = new ArrayPopupWindow<>(this,
                mFilterType,
                null,
                getWindowManager().getDefaultDisplay().getWidth() / 3,
                R.style.anim_top_to_bottom_style,
                getResources().getDrawable(R.drawable.bg_ex_popup_window), 0);
        mPopType.setDataSet(new ArrayList<>(AppConstants.PAY_TYPE_FILTERS.keySet()));
        mPopType.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp = ((TextView) view).getText().toString();
                mFilterType.setText(temp);
                mTypeSelect = AppConstants.PAY_TYPE_FILTERS.get(temp);
                onRefresh();
            }
        });
        mPopType.setDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mPresenter.dismissTypePop();
            }
        });

        mPopStatus = new ArrayPopupWindow<>(this,
                mFilterStatus,
                null,
                getWindowManager().getDefaultDisplay().getWidth() / 3,
                R.style.anim_top_to_bottom_style,
                getResources().getDrawable(R.drawable.bg_ex_popup_window), 0);
        mPopStatus.setDataSet(new ArrayList<>(AppConstants.PAY_STATUS_FILTERS.keySet()));
        mPopStatus.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp = ((TextView) view).getText().toString();
                mFilterStatus.setText(temp);
                mStatusSelect = AppConstants.PAY_STATUS_FILTERS.get(temp);
                onRefresh();
            }
        });
        mPopStatus.setDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mPresenter.dismissStatusPop();
            }
        });

        mFilterTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onTimePopClick();
            }
        });

        mFilterType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onTypePopClick();
            }
        });

        mFilterStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onStatusPopClick();
            }
        });
    }

    public void onClickSearch(View view) {
        mPresenter.onClickSearch();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void setPresenter(BillRecordContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showTimePop() {
        mPopTime.showAsDownCenter(true);
        mFilterTime.setText("时间");
        mFilterTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_pop_down, 0);
    }

    @Override
    public void resetTimePop() {
        mFilterTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_pop_up, 0);
    }

    @Override
    public void showTypePop() {
        mPopType.showAsDownCenter(true);
        mFilterType.setText("类型");
        mFilterType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_pop_down, 0);
    }

    @Override
    public void resetTypePop() {
        mFilterType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_pop_up, 0);
    }

    @Override
    public void showStatusPop() {
        mPopStatus.showAsDownCenter(true);
        mFilterStatus.setText("状态");
        mFilterStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_pop_down, 0);
    }

    @Override
    public void resetStatusPop() {
        mFilterStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_pop_up, 0);
    }

    @Override
    public void showLoadIng() {
        mLoadLayout.setStatus(CustomLoadingLayout.STATUS_LOADING);
        hideSumInfo();
        showLoading();
    }

    @Override
    public void hideLoadIng() {
        hideLoading();
    }

    @Override
    public void showLoadError() {
        mLoadLayout.setStatus(CustomLoadingLayout.STATUS_ERROR);
    }

    @Override
    public void showLoadEmpty() {
        mLoadLayout.setStatus(CustomLoadingLayout.STATUS_EMPTY);
    }

    @Override
    public void showLoadNoNetwork() {
        mLoadLayout.setStatus(CustomLoadingLayout.STATUS_NONETWORK);
    }

    @Override
    public void showLoadSuccess() {
        mLoadLayout.setStatus(CustomLoadingLayout.STATUS_SUCCESS);
    }

    @Override
    public void showMoreIng() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_LOADING);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMoreError() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_ERROR);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMoreNoNetwork() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_NO_NETWORK);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMoreNone() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_NONE);
    }

    @Override
    public void showMoreSuccess() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_SUCCESS);
    }

    @Override
    public void showBillData(List<BillInfo> list) {
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        if (mAdapter != null) {
            mAdapter.getData().clear();
        }
        if (mBillList != null) {
            mBillList.removeAllViews();
        }
        mPresenter.loadBills(mTimeSelect, mTypeSelect, mStatusSelect);
    }

    @Override
    public void setHasMore(boolean hasMore) {
        mHasMore = hasMore;
    }

    @Override
    public void updateSumInfo(int account, int amount) {
        mBillSum.setVisibility(View.VISIBLE);
        mSumAccount.setText(String.valueOf(account));
        mSumAmount.setText(String.format(getString(R.string.cashier_money), Utils.moneyToStringEx(amount)));
    }

    @Override
    public void hideSumInfo() {
        mBillSum.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(Object o) {
        mPresenter.onBillItemClick((BillInfo) o);
    }

    @Override
    public void onLoadMore() {
        if (mHasMore) {
            mPresenter.loadMoreBills(mTimeSelect, mTypeSelect, mStatusSelect);
        }
    }
}
