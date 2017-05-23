package com.xmd.cashier.activity;

/**
 * 核销界面，可以核销优惠券（优惠券，点钟鏞，现金券），请客授权码
 */

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.VerificationListAdapter;
import com.xmd.cashier.contract.VerificationContract;
import com.xmd.cashier.dal.bean.VerificationItem;
import com.xmd.cashier.presenter.VerificationPresenter;
import com.xmd.cashier.widget.CustomEditText;
import com.xmd.cashier.widget.CustomKeyboardView;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;
import com.xmd.cashier.widget.OnMyKeyboardCallback;

import java.util.List;

public class VerificationActivity extends BaseActivity implements VerificationContract.View {
    private VerificationContract.Presenter mPresenter;
    private CustomEditText mNumberEditText;
    private VerificationListAdapter mAdapter;
    private CustomKeyboardView mKeyboardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        mPresenter = new VerificationPresenter(this, this);

        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, R.string.verification_title);

        initKeyboard();

        mNumberEditText = (CustomEditText) findViewById(R.id.edt_number);
        mNumberEditText.setInputType(0);
        String text = mNumberEditText.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            mNumberEditText.setSelection(text.length());
        }
        mNumberEditText.requestFocus();
        showKeyboard();


        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new VerificationListAdapter(mPresenter);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new CustomRecycleViewDecoration(32));
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });


        mNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showKeyboard();
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
                mPresenter.onClickSearch();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onScanResult(data);
    }

    public void onClickSearch(View view) {
        mPresenter.onClickSearch();
    }

    public void onClickScan(View view) {
        mPresenter.onClickScan();
    }

    @Override
    public String getNumber() {
        return mNumberEditText.getText().toString();
    }

    @Override
    public void showLoadingView() {
        showLoading();
    }

    @Override
    public void hideLoadingView() {
        hideLoading();
    }

    @Override
    public void showVerificationData(List<VerificationItem> verificationItems) {
        mAdapter.setData(verificationItems);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(VerificationContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    private void showKeyboard() {
        mKeyboardView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
    }

    public void onClickOk(View view) {
        mPresenter.onClickOk();
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onNavigationBack();
        return true;
    }

    public void onClickCleanAll(View view) {
        mPresenter.onClickCleanAll();
    }
}
