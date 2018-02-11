package com.xmd.cashier.activity;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.InnerModifyContract;
import com.xmd.cashier.dal.bean.TradeRecordInfo;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.presenter.InnerModifyPresenter;
import com.xmd.cashier.widget.CustomKeyboardView;
import com.xmd.cashier.widget.CustomMoneyEditText;
import com.xmd.cashier.widget.OnMyKeyboardCallback;

/**
 * Created by zr on 17-12-18.
 */

public class InnerModifyActivity extends BaseActivity implements InnerModifyContract.View {
    private InnerModifyContract.Presenter mPresenter;

    private TextView mOriginAmount;
    private TextView mDiscountAmount;
    private TextView mAlreadyPayAmount;
    private TextView mLeftPayAmount;
    private CustomMoneyEditText mInputPayAmount;
    private TextView mPayMoneyDesc;
    private TextView mLeftMoneyDesc;

    private Button mBtnDetail;
    private Button mBtnCashier;

    private CustomKeyboardView mKeyboardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_modify);
        initView();
        mPresenter = new InnerModifyPresenter(this, this);
        mPresenter.onCreate();
        mPresenter.processData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mInputPayAmount.moveCursorToEnd();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mPresenter.processData();
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onEventBack();
        return true;
    }

    public void onClickInnerDetail(View view) {
        TradeRecordInfo info = TradeManager.getInstance().getCurrentTrade().innerRecordInfo;
        UiNavigation.gotoInnerDetailActivity(this, AppConstants.INNER_DETAIL_SOURCE_OTHER, info);
    }

    private void initView() {
        showToolbar(R.id.toolbar, "收银");
        mOriginAmount = (TextView) findViewById(R.id.tv_origin_amount);
        mDiscountAmount = (TextView) findViewById(R.id.tv_discount_amount);
        mAlreadyPayAmount = (TextView) findViewById(R.id.tv_already_pay_amount);
        mLeftPayAmount = (TextView) findViewById(R.id.tv_need_pay_amount);
        mPayMoneyDesc = (TextView) findViewById(R.id.tv_desc_pay_money);
        mLeftMoneyDesc = (TextView) findViewById(R.id.tv_desc_left_money);
        mBtnDetail = (Button) findViewById(R.id.btn_detail);
        mBtnCashier = (Button) findViewById(R.id.btn_cashier);

        mInputPayAmount = (CustomMoneyEditText) findViewById(R.id.edt_input_pay_amount);
        mInputPayAmount.setInputType(0);
        mInputPayAmount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showKeyboard();
                mInputPayAmount.requestFocus();
                return false;
            }
        });
        mInputPayAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.onRealPayChange();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboardview);
        mKeyboardView.setKeyboard(new Keyboard(this, R.xml.keyboard_number));
        mKeyboardView.setKeyLableMap("收银", "完成");
        mKeyboardView.setKeyTextColor("完成", 0xffffffff);
        mKeyboardView.setKeyBackgroundDrawable("完成", getResources().getDrawable(R.drawable.state_keyboard_cashier));
        mKeyboardView.setKeyTextColor("清空", 0xffffffff);
        mKeyboardView.setKeyBackgroundDrawable("清空", getResources().getDrawable(R.drawable.state_keyboard_clear_del));
        mKeyboardView.setKeyBackgroundDrawable("delete", getResources().getDrawable(R.drawable.state_keyboard_clear_del));
        mKeyboardView.setOnKeyboardActionListener(new OnMyKeyboardCallback(InnerModifyActivity.this, new OnMyKeyboardCallback.Callback() {
            @Override
            public boolean onKeyEnter() {
                hideKeyboard();
                return true;
            }
        }));

        mBtnCashier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCashier();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(InnerModifyContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showKeyboard() {
        mKeyboardView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
    }

    @Override
    public void setOrigin(String origin) {
        mOriginAmount.setText(origin);
    }

    @Override
    public void setDiscount(String discount) {
        mDiscountAmount.setText(discount);
    }

    @Override
    public void setAlreadyPay(String alreadyPay) {
        mAlreadyPayAmount.setText(alreadyPay);
    }

    @Override
    public void setLeftPay(String leftPay) {
        mLeftPayAmount.setText(leftPay);
    }

    @Override
    public void setInput(int input) {
        mInputPayAmount.setMoney(input);
    }

    @Override
    public void setDesc(int input, int left) {
        String tempInput = "本次支付金额￥" + Utils.moneyToStringEx(input);
        String tempLeft = "，剩余待付金额￥" + Utils.moneyToStringEx(left < 0 ? 0 : left);
        mPayMoneyDesc.setText(Utils.changeColor(tempInput, ResourceUtils.getColor(R.color.colorAccent), 6, tempInput.length()));
        mLeftMoneyDesc.setText(Utils.changeColor(tempLeft, ResourceUtils.getColor(R.color.colorAccent), 7, tempLeft.length()));
    }

    @Override
    public int getRealPayMoney() {
        return mInputPayAmount.getMoney();
    }
}
