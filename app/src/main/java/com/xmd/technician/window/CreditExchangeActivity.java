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
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.BaseResult;
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
public class CreditExchangeActivity extends BaseActivity implements TextWatcher{
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
    private String exchange;

    private Subscription mExchangeCreditResultSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_exchange);
        ButterKnife.bind(this);
        setTitle(ResourceUtils.getString(R.string.credit_exchange));
        setBackVisible(true);
        Intent intent = getIntent();
        mTotalCredit = intent.getIntExtra(RequestConstant.KEY_UER_CREDIT_AMOUNT,-1);
        initView();
    }
    private void initView(){
        mCreditTotal.setText(String.valueOf(mTotalCredit));
        mExchangeConvert.setText(String.valueOf(mTotalCredit/10f));
        String exchange = String.format(ResourceUtils.getString(R.string.credit_exchange_max), String.valueOf(mTotalCredit/100));
        SpannableString spannableString = new SpannableString(exchange);
        spannableString.setSpan(new TextAppearanceSpan(this,R.style.text_credit),8,exchange.lastIndexOf("元"),SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMaxExchange.setText(exchange);
        mMoneyExchange.addTextChangedListener(this);
        mExchangeCreditResultSubscription = RxBus.getInstance().toObservable(BaseResult.class).subscribe(
          result ->{
              if(result.statusCode==200){
                  makeShortToast(result.msg);
              }
          }
        );
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
       exchange =  mMoneyExchange.getText().toString();
        if(exchange.length()>0){
            mImgClose.setVisibility(View.VISIBLE);
            mBtnSent.setEnabled(true);
        }else{
            mImgClose.setVisibility(View.GONE);
            mBtnSent.setEnabled(false);
        }

    }
    @OnClick(R.id.btn_sent)
    public void sentExchange(){
        if(Integer.parseInt(exchange)<0){
            return;
        }
        if(Integer.parseInt(exchange)>mTotalCredit){
            makeShortToast(ResourceUtils.getString(R.string.credit_alert_shortage)+(mTotalCredit/10));
            return;
        }
            Map<String,String> mParams = new HashMap<>();
            mParams.put(RequestConstant.KEY_UER_CREDIT_AMOUNT,exchange);
             MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_CREDIT_EXCHANGE,mParams);
            mMoneyExchange.setText("");

    }
    @OnClick(R.id.img_close)
    public void cleadEditText(){
        mMoneyExchange.setText("");
        mBtnSent.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mExchangeCreditResultSubscription);
    }
}
