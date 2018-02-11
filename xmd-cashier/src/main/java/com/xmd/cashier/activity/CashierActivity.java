package com.xmd.cashier.activity;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.contract.CashierContract;
import com.xmd.cashier.presenter.CashierPresenter;
import com.xmd.cashier.widget.CustomKeyboardView;
import com.xmd.cashier.widget.CustomMoneyEditText;
import com.xmd.cashier.widget.OnMyKeyboardCallback;

/**
 * Created by zr on 17-4-13.
 * 收银页面
 */

public class CashierActivity extends BaseActivity implements CashierContract.View {
    private CashierContract.Presenter mPresenter;
    private CustomMoneyEditText mOriginCustomMoneyEditText;
    private CustomMoneyEditText mCouponCustomMoneyEditText;
    private Button mSetCouponInfoButton;
    private TextView mFinallyMoneyTextView;
    private CustomKeyboardView mKeyboardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier);
        initView();
        mPresenter = new CashierPresenter(this, this);
        mPresenter.onCreate();
    }

    public void initView() {
        showToolbar(R.id.toolbar, R.string.cashier_title, TOOL_BAR_NAV_BACK);
        mOriginCustomMoneyEditText = (CustomMoneyEditText) findViewById(R.id.edit_origin_money);
        mCouponCustomMoneyEditText = (CustomMoneyEditText) findViewById(R.id.edit_coupon_money);
        mFinallyMoneyTextView = (TextView) findViewById(R.id.tv_finally_money);
        mSetCouponInfoButton = (Button) findViewById(R.id.btn_coupon);
        mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboardview);

        mKeyboardView.setKeyboard(new Keyboard(this, R.xml.keyboard_number));
        mKeyboardView.setOnKeyboardActionListener(new OnMyKeyboardCallback(this, new OnMyKeyboardCallback.Callback() {
            @Override
            public boolean onKeyEnter() {
                mPresenter.onChannel();
                return true;
            }
        }));
        mKeyboardView.setKeyTextColor("收银", 0xffffffff);
        mKeyboardView.setKeyBackgroundDrawable("收银", getResources().getDrawable(R.drawable.state_keyboard_cashier));
        mKeyboardView.setKeyTextColor("清空", 0xffffffff);
        mKeyboardView.setKeyBackgroundDrawable("清空", getResources().getDrawable(R.drawable.state_keyboard_clear_del));
        mKeyboardView.setKeyBackgroundDrawable("delete", getResources().getDrawable(R.drawable.state_keyboard_clear_del));
        mOriginCustomMoneyEditText.setInputType(0);
        mCouponCustomMoneyEditText.setInputType(0);

        mOriginCustomMoneyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.onOriginMoneyChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
        mOriginCustomMoneyEditText.moveCursorToEnd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(CashierContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    public void onClickGetCouponInfo(View view) {
        mPresenter.onClickSetCouponInfo();
    }

    @Override
    public void setOriginMoney(int value) {
        mOriginCustomMoneyEditText.requestFocus();
        mOriginCustomMoneyEditText.setMoney(value);
    }

    @Override
    public void setDiscountMoney(int value) {
        mCouponCustomMoneyEditText.setMoney(value);
    }

    @Override
    public int getOriginMoney() {
        return mOriginCustomMoneyEditText.getMoney();
    }

    @Override
    public void setFinallyMoney(String value) {
        mFinallyMoneyTextView.setText(value);
    }

    @Override
    public void setCouponButtonText(String text) {
        mSetCouponInfoButton.setText(text);
    }

    public void onClickTriggerOriginMoney(View view) {
        mOriginCustomMoneyEditText.requestFocus();
    }
}
