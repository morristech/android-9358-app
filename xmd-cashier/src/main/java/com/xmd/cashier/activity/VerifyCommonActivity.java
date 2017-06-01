package com.xmd.cashier.activity;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.VerifyCommonMsgAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyCommonContract;
import com.xmd.cashier.dal.bean.CommonVerifyInfo;
import com.xmd.cashier.presenter.VerifyCommonPresenter;
import com.xmd.cashier.widget.CustomEditText;
import com.xmd.cashier.widget.CustomKeyboardView;
import com.xmd.cashier.widget.OnMyKeyboardCallback;

/**
 * Created by zr on 16-12-12.
 * 通用核销
 */

public class VerifyCommonActivity extends BaseActivity implements VerifyCommonContract.View {
    private VerifyCommonContract.Presenter mPresenter;
    private String mCode;
    private String mType;
    private boolean mNeedAmount;
    private int mAmount;

    private VerifyCommonMsgAdapter mAdapter;

    private TextView mCodeText;
    private ListView mInfoList;
    private CustomEditText mAmountEdt;
    private LinearLayout mNeedAmountLayout;
    private CustomKeyboardView mKeyboardView;

    private CommonVerifyInfo mInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_common);
        mPresenter = new VerifyCommonPresenter(this, this);
        mInfo = (CommonVerifyInfo) getIntent().getSerializableExtra(AppConstants.EXTRA_COMMON_VERIFY_INFO);
        if (mInfo == null) {
            showToast("无效的核销信息");
            finishSelf();
            return;
        }

        showToolbar(R.id.toolbar, mInfo.title);
        initView();
        showVerifyInfo(mInfo);

        mPresenter.onCreate();
    }

    private void initKeyboard() {
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


    private void initView() {
        initKeyboard();
        mCodeText = (TextView) findViewById(R.id.tv_code);
        mInfoList = (ListView) findViewById(R.id.list_info);
        mNeedAmountLayout = (LinearLayout) findViewById(R.id.layout_need_amount);
        mAmountEdt = (CustomEditText) findViewById(R.id.edt_input_amount);
    }

    private void showVerifyInfo(CommonVerifyInfo info) {
        setCode(info.code);
        setType(info.type);
        setNeedAmount(info.needAmount);
        mCodeText.setText(info.code);
        if (info.info != null) {
            if (info.info.list != null && info.info.list.size() > 0) {
                mInfoList.setVisibility(View.VISIBLE);
                mAdapter = new VerifyCommonMsgAdapter(this);
                mAdapter.setData(info.info.list);
                mInfoList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } else {
                mInfoList.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(info.info.amount)) {
                mAmount = Integer.parseInt(info.info.amount);
            }
        }
        if (info.needAmount) {
            mNeedAmountLayout.setVisibility(View.VISIBLE);
            if (mAmount > 0) {
                mAmountEdt.setHint("应支付" + Utils.moneyToString(mAmount) + "元");
            }
            mAmountEdt.setInputType(0);
            mAmountEdt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    showKeyboard();
                    return false;
                }
            });
        } else {
            mNeedAmountLayout.setVisibility(View.GONE);
        }
    }

    public void onClickVerify(View view) {
        mPresenter.onClickVerify(mInfo);
    }

    private void showKeyboard() {
        mKeyboardView.setVisibility(View.VISIBLE);
    }

    private void hideKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPresenter != null) {
            mPresenter.onStart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void showLoadingView() {
        showLoading();
    }

    @Override
    public void hideLoadingView() {
        hideLoading();
    }

    private void setCode(String code) {
        mCode = code;
    }

    private void setType(String type) {
        mType = type;
    }

    private void setNeedAmount(boolean needAmount) {
        mNeedAmount = needAmount;
    }

    @Override
    public String getCode() {
        return mCode;
    }

    @Override
    public String getType() {
        return mType;
    }

    @Override
    public String getAmount() {
        return mAmountEdt.getText().toString();
    }

    @Override
    public boolean needAmount() {
        return mNeedAmount;
    }

    @Override
    public void setPresenter(VerifyCommonContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }
}
