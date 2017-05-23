package com.xmd.cashier.activity;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.contract.PointsPhoneContract;
import com.xmd.cashier.presenter.PointsPhonePresenter;
import com.xmd.cashier.widget.CustomKeyboardView;
import com.xmd.cashier.widget.CustomMoneyPhoneText;
import com.xmd.cashier.widget.OnMyKeyboardCallback;

import java.util.Locale;

public class PointsPhoneActivity extends BaseActivity implements PointsPhoneContract.View {
    private PointsPhoneContract.Presenter mPresenter;
    private CustomKeyboardView mKeyboardView;

    private TextView mPointsTextView;
    private CustomMoneyPhoneText mPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_phone);

        mPresenter = new PointsPhonePresenter(this, this);

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
        initKeyboard();
        mPhoneEditText = (CustomMoneyPhoneText) findViewById(R.id.edt_phone);
        mPhoneEditText.setInputType(0);
        mPointsTextView = (TextView) findViewById(R.id.tv_points);
    }

    private void initKeyboard() {
        mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboardview);
        mKeyboardView.setKeyboard(new Keyboard(this, R.xml.keyboard_number));
        mKeyboardView.setOnKeyboardActionListener(new OnMyKeyboardCallback(this, new OnMyKeyboardCallback.Callback() {
            @Override
            public boolean onKeyEnter() {
                mPresenter.onClickOk();
                return true;
            }

            @Override
            public boolean onKey(int primaryCode) {
                if (primaryCode == '.') {
                    mPresenter.onClickCancel();
                    return true;
                } else {
                    return false;
                }
            }
        }));
        mKeyboardView.setKeyLableMap(".", "跳过");
        mKeyboardView.setKeyLableMap("收银", "确定");
        mKeyboardView.setKeyTextColor("确定", 0xffffffff);
        mKeyboardView.setKeyBackgroundDrawable("确定", getResources().getDrawable(R.drawable.state_keyboard_cashier));
        mKeyboardView.setKeyTextColor("清空", 0xffffffff);
        mKeyboardView.setKeyBackgroundDrawable("清空", getResources().getDrawable(R.drawable.state_keyboard_clear_del));
        mKeyboardView.setKeyBackgroundDrawable("delete", getResources().getDrawable(R.drawable.state_keyboard_clear_del));
    }

    @Override
    public void showPoints(int points) {
        mPointsTextView.setText(String.format(Locale.getDefault(), "恭喜获赠 %d 积分", points));
    }

    @Override
    public String getPhone() {
        return mPhoneEditText.getText().toString().replace(" ", "");
    }

    @Override
    public void setPhone(String phone) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(phone.substring(0, 3));
        stringBuilder.append(" ");
        stringBuilder.append(phone.substring(3, 7));
        stringBuilder.append(" ");
        stringBuilder.append(phone.substring(7, 11));
        mPhoneEditText.setText(stringBuilder);
    }

    @Override
    public void setPresenter(PointsPhoneContract.Presenter presenter) {

    }

    @Override
    public void finishSelf() {
        finish();
    }
}
