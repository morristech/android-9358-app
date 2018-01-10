package com.xmd.technician.window;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.xmd.technician.R;
import com.xmd.technician.bean.WithdrawRuleBean;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.databinding.ActivityWithdrawCashBinding;

/**
 * Created by Lhj on 18-1-10.
 */

public class WithdrawCashActivity extends BaseActivity {

    private ActivityWithdrawCashBinding mBinding;
    private String rule01 = "自9358服务公众号创建以来，一直秉承着用心服务技师的宗旨，小摩豆也一直勉力承担着9358服务提现每笔%s‰的手续费。经过这一年与技师朋友们的共同努力，我们欣喜地看到技师队伍的壮大与服务水平的提升，经小摩豆慎重考虑，现决定对提现规则做如下调整：";
    private String ruleO3 = "收费方案： 每月提现超过%s元部分才收取手续费，手续费率%s‰。";
    private WithdrawRuleBean mRuleBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_withdraw_cash);
        initView();
    }

    private void initView() {
        setBackVisible(true);
        setTitle(ResourceUtils.getString(R.string.withdraw_cash_rule));
        mRuleBean = getIntent().getParcelableExtra(TechAccountActivity.INTENT_RULE_BEAN);
        if (mRuleBean != null) {
            mBinding.tvWithdraw01.setText(String.format(rule01, String.valueOf(mRuleBean.serviceChargeRate * 1000)));
            String part = String.format(ruleO3,String.valueOf(mRuleBean.noServiceChargeAmount / 100),String.valueOf(mRuleBean.serviceChargeRate * 1000));
            Spannable spannable = new SpannableString(part);
            spannable.setSpan(new ForegroundColorSpan(ResourceUtils.getColor(R.color.colorAccent)),6,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(ResourceUtils.getColor(R.color.colorAccent)),part.indexOf("现")+1,part.indexOf("元"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(ResourceUtils.getColor(R.color.colorAccent)),part.indexOf("率")+1,part.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBinding.tvPart.setText(spannable);
        }else{
            return;
        }
    }

    public void userKnowClicked(View view) {
        this.finish();
    }

}
