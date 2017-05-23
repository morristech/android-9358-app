package com.xmd.cashier.activity;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.ConfirmListAdapter;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.ConfirmContract;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.presenter.ConfirmPresenter;
import com.xmd.cashier.widget.CustomKeyboardView;
import com.xmd.cashier.widget.CustomMoneyEditText;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;
import com.xmd.cashier.widget.OnMyKeyboardCallback;

public class ConfirmActivity extends BaseActivity implements ConfirmContract.View {
    public static final String EXTRA_MESSAGE = "extra_message";
    private ConfirmContract.Presenter mPresenter;

    private TextView mOriginMoneyTextView;
    private CustomMoneyEditText mCouponCustomMoneyEditText;
    private Button mOkButton;

    private ConfirmListAdapter mAdapter;

    private CustomKeyboardView mKeyboardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        mPresenter = new ConfirmPresenter(this, this);
        initView();
        mPresenter.onCreate();
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

    private void initView() {
        showToolbar(R.id.toolbar, R.string.confirm_title, TOOL_BAR_NAV_NONE);
        mOriginMoneyTextView = (TextView) findViewById(R.id.tv_origin_money);
        mCouponCustomMoneyEditText = (CustomMoneyEditText) findViewById(R.id.edt_coupon_money);
        mCouponCustomMoneyEditText.setInputType(0);
        mCouponCustomMoneyEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showKeyboard();
                return false;
            }
        });
        mOkButton = (Button) findViewById(R.id.btn_ok);
        mCouponCustomMoneyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.onCouponMoneyChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new CustomRecycleViewDecoration(32));
        mAdapter = new ConfirmListAdapter(mPresenter);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboardview);
        mKeyboardView.setKeyboard(new Keyboard(this, R.xml.keyboard_number));
        mKeyboardView.setOnKeyboardActionListener(new OnMyKeyboardCallback(this, new OnMyKeyboardCallback.Callback() {
            @Override
            public boolean onKeyEnter() {
                hideKeyboard();
                return true;
            }
        }));
        mKeyboardView.setKeyLableMap("收银", "确定");
        mKeyboardView.setKeyTextColor("确定", 0xffffffff);
        mKeyboardView.setKeyBackgroundDrawable("确定", getResources().getDrawable(R.drawable.state_keyboard_cashier));
        mKeyboardView.setKeyTextColor("清空", 0xffffffff);
        mKeyboardView.setKeyBackgroundDrawable("清空", getResources().getDrawable(R.drawable.state_keyboard_clear_del));
        mKeyboardView.setKeyBackgroundDrawable("delete", getResources().getDrawable(R.drawable.state_keyboard_clear_del));
    }


    public void onClickCancel(View view) {
        mPresenter.onClickCancel();
    }

    public void onClickContinue(View view) {
        mPresenter.onClickOk();
    }


    @Override
    public void setOriginMoney(int value) {
        mOriginMoneyTextView.setText(Utils.moneyToString(value));
    }

    @Override
    public void setDiscountMoney(int value) {
        mCouponCustomMoneyEditText.setMoney(value);
    }

    @Override
    public void setFinallyMoney(int value) {
        if (value > 0) {
            mOkButton.setText("支付￥" + Utils.moneyToString(value));
        } else {
            mOkButton.setText("完成");
        }
    }

    @Override
    public void showTradeStatusInfo(Trade trade) {
        mAdapter.setData(trade);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public String getStartShowMessage() {
        return getIntent().getStringExtra(EXTRA_MESSAGE);
    }

    @Override
    public int getDiscountMoney() {
        return mCouponCustomMoneyEditText.getMoney();
    }


    @Override
    public void setPresenter(ConfirmContract.Presenter presenter) {

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

    @Override
    public void hideCouponView() {
        findViewById(R.id.layout_coupon_view).setVisibility(View.GONE);
    }

    @Override
    public void hideCancelButton() {
        findViewById(R.id.btn_cancel).setVisibility(View.GONE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideKeyboard();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onClickCancel();
        return true;
    }
}
