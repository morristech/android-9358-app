package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.CreditAccountDetailResult;
import com.xmd.technician.bean.CreditAccountResult;
import com.xmd.technician.bean.CreditBean;
import com.xmd.technician.bean.CreditDetailBean;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Administrator on 2016/8/8.
 */
public class UserCreditCenterActivity extends BaseListActivity {

    @Bind(R.id.credit_total)
    TextView mCreditAmount;
    @Bind(R.id.credit_rule)
    TextView mCreditRule;
    @Bind(R.id.credit_exchange)
    Button mCreditExchange;
    @Bind(R.id.get_credit_way)
    RelativeLayout mCreditIsEmpty;
    @Bind(R.id.credit_record)
    LinearLayout mCreditRecord;

    @Bind(R.id.list)
    RecyclerView list;

    private Subscription getCreditUserRecordsSubscription;
    private Subscription getCreditUserAccountSubscription;
    private int mTechCreditTotal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_USER_RECORDE);
    }

    @Override
    protected void initView() {

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CREDIT_ACCOUNT);

        getCreditUserAccountSubscription = RxBus.getInstance().toObservable(CreditAccountResult.class).subscribe(
                result ->handlerCreditAmount(result)
        );
        getCreditUserRecordsSubscription = RxBus.getInstance().toObservable(CreditAccountDetailResult.class).subscribe(
                detailResult ->handlerDetailResult(detailResult)
        );
    }

    @Override
    protected void setContentViewLayout() {
        setContentView(R.layout.credit_center_activity);
        setTitle(R.string.personal_fragment_layout_credit);
        setBackVisible(true);
    }

    @OnClick({R.id.credit_rule, R.id.credit_exchange, R.id.get_credit_way})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.credit_rule:
                Intent intentRule = new Intent(UserCreditCenterActivity.this, CreditRuleExplainActivity.class);
                startActivity(intentRule);
                break;
            case R.id.credit_exchange:
                //兑换积分
                if(mTechCreditTotal>0){
                    Intent intent = new Intent(UserCreditCenterActivity.this, CreditExchangeActivity.class);
                    intent.putExtra(RequestConstant.KEY_UER_CREDIT_AMOUNT,mTechCreditTotal);
                    startActivity(intent);
                }else{
                    makeShortToast("暂无可兑换的积分");
                }
                break;
            case R.id.get_credit_way:
                Intent intentMethod = new Intent(UserCreditCenterActivity.this, CreditRuleExplainActivity.class);
                startActivity(intentMethod);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(getCreditUserAccountSubscription,getCreditUserRecordsSubscription);
    }

    @Override
    public boolean isPaged() {
        return true;
    }
    private void handlerCreditAmount(CreditAccountResult result){
        if(result.respData.size()>0){
            mTechCreditTotal = result.respData.get(0).amount;
            mCreditAmount.setText(String.valueOf(mTechCreditTotal));
        }
    }
    private void handlerDetailResult(CreditAccountDetailResult result){
        if(result.respData.size()>0){
            mCreditRecord.setVisibility(View.VISIBLE);
            mCreditIsEmpty.setVisibility(View.GONE);
        }else{
            mCreditRecord.setVisibility(View.GONE);
            mCreditIsEmpty.setVisibility(View.VISIBLE);
        }
        mData.clear();
        mData.addAll(result.respData);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
