package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.technician.Adapter.TechAccountListAdapter;
import com.xmd.technician.R;
import com.xmd.technician.bean.TechAccountBean;
import com.xmd.technician.bean.WithdrawRuleBean;
import com.xmd.technician.http.gson.TechAccountListResult;
import com.xmd.technician.http.gson.WithdrawRuleResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by sdcm on 17-3-10.
 */

public class TechAccountActivity extends BaseActivity {

    public static String INTENT_RULE_BEAN = "rule";

    @BindView(R.id.view_recycler)
    RecyclerView viewRecycler;
    @BindView(R.id.toolbar_right_rule)
    TextView toolbarRightRule;

    private Subscription mTechAccountListSubscription;
    private Subscription mWithdrawRuleSubscription;
    private TechAccountListAdapter mTechAccountListAdapter;
    private List<TechAccountBean> mTechAccountList;
    private String mWithdrawal = "";
    private WithdrawRuleBean mRuleBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_account);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle(R.string.personal_fragment_layout_account);
        setBackVisible(true);
        toolbarRightRule.setVisibility(View.VISIBLE);

        mTechAccountList = new ArrayList<>();
        mTechAccountListSubscription = RxBus.getInstance().toObservable(TechAccountListResult.class).subscribe(
                techAccountListResult -> handleTechAccountList(techAccountListResult)
        );
        mWithdrawRuleSubscription = RxBus.getInstance().toObservable(WithdrawRuleResult.class).subscribe(
                result -> handleWithdrawRule(result)
        );
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_ACCOUNT_LIST);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_WITHDRAW_RULE);
        viewRecycler.setLayoutManager(new LinearLayoutManager(this));
        viewRecycler.setItemAnimator(new DefaultItemAnimator());
        viewRecycler.setHasFixedSize(true);
        mTechAccountListAdapter = new TechAccountListAdapter(TechAccountActivity.this, mTechAccountList);
        viewRecycler.setAdapter(mTechAccountListAdapter);
        mTechAccountListAdapter.setOnWithDrawClickedListener(new TechAccountListAdapter.CallBack() {
            @Override
            public void onWithDrawClicked(TechAccountBean bean) {
                if (mWithdrawal.equals("Y") && bean.status.equals("normal")) {
                    showDrawMoneyView();
                } else {
                    makeShortToast("不可提现");
                }

            }

            @Override
            public void onItemClicked(TechAccountBean bean) {
                gotoDetailActivity(bean.accountType, bean.name);
            }
        });
    }

    private void handleWithdrawRule(WithdrawRuleResult result) {
        if(result.statusCode == 200){
            mRuleBean = result.respData;
            if(mRuleBean == null){
                toolbarRightRule.setVisibility(View.GONE);
            }
        }else {
            XToast.show(result.msg);
        }

    }

    private void handleTechAccountList(TechAccountListResult result) {
        if (result.statusCode == 200) {
            mTechAccountList.clear();
            mTechAccountList.addAll(result.respData.accountList);
            mWithdrawal = result.respData.withdrawal;
            mTechAccountListAdapter.setData(mTechAccountList, result.respData.withdrawal);
        } else {
            makeShortToast(result.msg);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mTechAccountListSubscription,mWithdrawRuleSubscription);
    }

    private void gotoDetailActivity(String type, String name) {
        Intent intent = new Intent(TechAccountActivity.this, ConsumeDetailActivity.class);
        intent.putExtra(ConsumeDetailActivity.EXTRA_CONSUME_TYPE, type);
        intent.putExtra(ConsumeDetailActivity.EXTRA_CONSUME_NAME, name);
        startActivity(intent);
    }

    private void showDrawMoneyView() {
        Intent intent = new Intent(TechAccountActivity.this, IntroduceAccountActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.toolbar_right_rule)
    public void onViewClicked() {
        Intent intent = new Intent(this,WithdrawCashActivity.class);
        intent.putExtra(INTENT_RULE_BEAN,mRuleBean);
        startActivity(intent);
    }
}
