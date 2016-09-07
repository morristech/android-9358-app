package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.bean.CreditAccountResult;
import com.xmd.technician.bean.CreditExchangeResult;
import com.xmd.technician.bean.CreditStatusResult;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Administrator on 2016/8/8.
 */
public class CreditExchangeActivity extends BaseActivity implements TextWatcher {
    @Bind(R.id.credit_total)
    TextView mCreditTotal;
    @Bind(R.id.max_exchange)
    TextView mMaxExchange;
    @Bind(R.id.money_exchange)
    EditText mMoneyExchange;
    @Bind(R.id.img_close)
    ImageButton mImgClose;
    @Bind(R.id.exchange_convert)
    TextView mExchangeConvert;
    @Bind(R.id.btn_sent)
    Button mBtnSent;
    private int mTotalCredit;
    private int mExchangeRatio;
    private int mExchangeLimitation;
    private String editExchange;
    private String clubSwitch;

    private Subscription mExchangeCreditResultSubscription;
    private Subscription mTotalCreditResultSubscription;
    private Subscription mCreditStatusSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_exchange);
        ButterKnife.bind(this);
        setTitle(ResourceUtils.getString(R.string.credit_exchange));
        setBackVisible(true);
        Intent intent = getIntent();
        mTotalCredit = intent.getIntExtra(RequestConstant.KEY_UER_CREDIT_AMOUNT, -1);
        initView();
    }

    private void initView() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CREDIT_ACCOUNT);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_SWITCH_STATUS);
        mMoneyExchange.addTextChangedListener(this);
        mExchangeCreditResultSubscription = RxBus.getInstance().toObservable(CreditExchangeResult.class).subscribe(
                result -> {
                    if (result.statusCode == 200) {
                        makeShortToast(ResourceUtils.getString(R.string.credit_exchange_game));
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CREDIT_ACCOUNT);
                        ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                            @Override
                            public void run() {
                                CreditExchangeActivity.this.finish();
                            }
                        },1500);
                    } else {
                        makeShortToast(result.msg);
                    }
                }
        );
        mCreditStatusSubscription = RxBus.getInstance().toObservable(CreditStatusResult.class).subscribe(
                statusResult -> handlerCreditStatus(statusResult)
        );
        mTotalCreditResultSubscription = RxBus.getInstance().toObservable(CreditAccountResult.class).subscribe(
                result -> handlerCreditAmount(result)
        );
    }

    private void handlerCreditStatus(CreditStatusResult result) {
        mExchangeRatio = result.respData.exchangeRatio;
        mExchangeLimitation = result.respData.exchangeLimitation;
        clubSwitch = result.respData.clubSwitch;
        mCreditTotal.setText(String.valueOf(result.respData.exchangeRatio));
    }

    private void handlerCreditAmount(CreditAccountResult result) {
        mTotalCredit = result.respData.get(0).amount;
        if (mExchangeRatio > 0) {
            String exchange = String.format(ResourceUtils.getString(R.string.credit_exchange_max), String.valueOf((int) (mTotalCredit - mExchangeLimitation) / mExchangeRatio));
            SpannableString spannableString = new SpannableString(exchange);
            spannableString.setSpan(new TextAppearanceSpan(this, R.style.text_credit), 6, exchange.lastIndexOf("元"), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            mMaxExchange.setText(exchange);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        editExchange = mMoneyExchange.getText().toString();
        if (editExchange.length() > 0) {
            mImgClose.setVisibility(View.VISIBLE);
            mBtnSent.setEnabled(true);
        } else {
            mImgClose.setVisibility(View.GONE);
            mBtnSent.setEnabled(false);
        }

    }

    @OnClick(R.id.btn_sent)
    public void sentExchange() {
        if (Integer.parseInt(editExchange) <= 0) {
            return;
        }
        if (!clubSwitch.equals("on")) {
            makeShortToast(ResourceUtils.getString(R.string.club_status_off));
            return;
        }
        if (Integer.parseInt(editExchange) > (mTotalCredit - mExchangeLimitation) / mExchangeRatio) {
            makeShortToast(ResourceUtils.getString(R.string.credit_alert_shortage) + ((mTotalCredit - mExchangeLimitation) / mExchangeRatio));
            return;
        }
        Map<String, String> mParams = new HashMap<>();
        mParams.put(RequestConstant.KEY_UER_CREDIT_AMOUNT, editExchange);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_CREDIT_EXCHANGE, mParams);
        mMoneyExchange.setText("");
    }

    @OnClick(R.id.img_close)
    public void cleadEditText() {
        mMoneyExchange.setText("");
        mBtnSent.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mExchangeCreditResultSubscription, mTotalCreditResultSubscription, mCreditStatusSubscription);
    }
}
