package com.xmd.cashier.activity;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.BillRecyclerAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.BillSearchContract;
import com.xmd.cashier.dal.bean.BillInfo;
import com.xmd.cashier.presenter.BillSearchPresenter;
import com.xmd.cashier.widget.CustomEditText;
import com.xmd.cashier.widget.CustomKeyboardView;
import com.xmd.cashier.widget.CustomLoadingLayout;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;
import com.xmd.cashier.widget.OnMyKeyboardCallback;

import java.util.List;

/**
 * Created by zr on 16-11-28.
 * 交易搜索:按照交易号搜索
 */

public class BillSearchActivity extends BaseActivity implements BillSearchContract.View, BillRecyclerAdapter.RecyclerCallBack {
    private BillSearchContract.Presenter mPresenter;
    private CustomKeyboardView mKeyboardView;
    private CustomEditText mNumberEditText;
    private CustomLoadingLayout mLoadLayout;
    private RecyclerView mRecyclerView;
    private BillRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private int mLastVisibleItem;
    private boolean mHasMore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_search);
        mPresenter = new BillSearchPresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, R.string.bill_record_title);
        initKeyboard();
        mNumberEditText = (CustomEditText) findViewById(R.id.edt_trade_no);
        mLoadLayout = (CustomLoadingLayout) findViewById(R.id.layout_load);
        mRecyclerView = (RecyclerView) findViewById(R.id.rc_search_bill);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new BillRecyclerAdapter(this, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new CustomRecycleViewDecoration(2));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mAdapter.getItemCount()) {
                    if (mHasMore) {
                        mPresenter.searchMoreBills();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });

        mNumberEditText.setInputType(0);
        String text = mNumberEditText.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            mNumberEditText.setSelection(text.length());
        }
        mNumberEditText.requestFocus();
        showKeyBoard();

        mNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showKeyBoard();
                return false;
            }
        });
    }

    private void initKeyboard() {
        mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboardview);
        mKeyboardView.setKeyboard(new Keyboard(this, R.xml.keyboard_number));
        mKeyboardView.setOnKeyboardActionListener(new OnMyKeyboardCallback(this, new OnMyKeyboardCallback.Callback() {
            @Override
            public boolean onKeyEnter() {
                refreshData();
                mPresenter.searchBills();
                return true;
            }
        }));
        mKeyboardView.setKeyLableMap("收银", "搜索");
        mKeyboardView.setKeyTextColor("搜索", 0xffffffff);
        mKeyboardView.setKeyBackgroundDrawable("搜索", getResources().getDrawable(R.drawable.state_keyboard_cashier));
        mKeyboardView.setKeyTextColor("清空", 0xffffffff);
        mKeyboardView.setKeyBackgroundDrawable("清空", getResources().getDrawable(R.drawable.state_keyboard_clear_del));
        mKeyboardView.setKeyBackgroundDrawable("delete", getResources().getDrawable(R.drawable.state_keyboard_clear_del));
    }

    @Override
    public void setPresenter(BillSearchContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
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

    public void onClickSearch(View view) {
        refreshData();
        mPresenter.searchBills();
    }

    @Override
    public String getTradeNo() {
        return mNumberEditText.getText().toString().trim();
    }

    @Override
    public void showKeyBoard() {
        mKeyboardView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideKeyBoard() {
        mKeyboardView.setVisibility(View.GONE);
    }

    @Override
    public void showLoadIng() {
        mLoadLayout.setStatus(CustomLoadingLayout.STATUS_LOADING);
        showLoading();
    }

    @Override
    public void hideLoadIng() {
        hideLoading();
    }


    @Override
    public void showLoadEmpty() {
        mLoadLayout.setStatus(CustomLoadingLayout.STATUS_EMPTY);
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
    public void setHasMore(boolean hasMore) {
        mHasMore = hasMore;
    }

    @Override
    public void showBillData(List<BillInfo> list) {
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Object o) {
        mPresenter.onBillItemClick((BillInfo) o);
    }

    @Override
    public void onLoadMore() {
        if (mHasMore) {
            mPresenter.searchMoreBills();
        }
    }

    @Override
    public void showToast(String toast) {
        super.showToast(toast);
    }

    private void refreshData() {
        if (mAdapter != null) {
            mAdapter.getData().clear();
        }
        if (mRecyclerView != null) {
            mRecyclerView.removeAllViews();
        }
    }
}
