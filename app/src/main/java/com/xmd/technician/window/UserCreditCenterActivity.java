package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.technician.Adapter.PageFragmentPagerAdapter;
import com.xmd.technician.R;
import com.xmd.technician.bean.CreditAccountResult;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Administrator on 2016/8/8.
 */
public class UserCreditCenterActivity extends BaseActivity implements BaseFragment.IFragmentCallback {
    @Bind(R.id.credit_total)
    TextView mCreditAmount;
    @Bind(R.id.freeze_total)
    TextView mCreditFreeze;
    @Bind(R.id.credit_rule)
    TextView mCreditRule;
    @Bind(R.id.credit_exchange)
    Button mCreditExchange;
    @Bind(R.id.get_credit_way)
    RelativeLayout mCreditIsEmpty;
    @Bind(R.id.credit_get)
    TextView mGetCredit;
    @Bind(R.id.iv_tab_bottom_img)
    ImageView mTabBottomImg;
    @Bind(R.id.credit_record)
    RelativeLayout mCreditRecord;
    @Bind(R.id.credit_apply)
    RelativeLayout mCreditApply;
    @Bind(R.id.table_apply)
    TextView mTableApply;
    @Bind(R.id.table_record)
    TextView mTableRecord;
    @Bind(R.id.vp_contact)
    ViewPager mViewpagerContact;
    @Bind(R.id.ll_detail)
    LinearLayout llDetail;
    private PageFragmentPagerAdapter mPageFragmentPagerAdapter;


    private Subscription getCreditUserAccountSubscription;
    private int mTechCreditTotal, mFreezeCredit;
    private int currentIndex;
    private int screenWidth;
    private List<Fragment> mFragmentViews;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchRequest();
        setContentViewLayout();
        initView();
    }

    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CREDIT_ACCOUNT);
    }

    protected void initView() {
        getCreditUserAccountSubscription = RxBus.getInstance().toObservable(CreditAccountResult.class).subscribe(
                result -> handlerCreditAmount(result)
        );
        initImageView();
        initViewPager();
    }

    private void resetTextView() {
        mTableApply.setTextColor(ResourceUtils.getColor(R.color.colorHead));
        mTableRecord.setTextColor(ResourceUtils.getColor(R.color.colorHead));
    }

    protected void setContentViewLayout() {
        setContentView(R.layout.credit_center_activity);
        setTitle(R.string.personal_fragment_layout_credit);
        setBackVisible(true);
        ButterKnife.bind(this);
    }

    private void initImageView() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabBottomImg.getLayoutParams();
        lp.width = screenWidth / 2;
        mTabBottomImg.setLayoutParams(lp);
    }

    private void initViewPager() {
        mFragmentViews = new ArrayList<>();
        mPageFragmentPagerAdapter = new PageFragmentPagerAdapter(getSupportFragmentManager(), UserCreditCenterActivity.this);
        mPageFragmentPagerAdapter.addFragment(new CreditRecordFragment());
        mPageFragmentPagerAdapter.addFragment(new ApplicationRecordFragment());
        mViewpagerContact.setAdapter(mPageFragmentPagerAdapter);
        mViewpagerContact.setCurrentItem(0);
        mTableRecord.setTextColor(ResourceUtils.getColor(R.color.colorMainBtn));
        mViewpagerContact.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int positionOffsetPixels) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabBottomImg.getLayoutParams();
                if (currentIndex == 0 && position == 0) {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));
                } else if (currentIndex == 1 && position == 0) {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));
                }
                mTabBottomImg.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mTableRecord.setTextColor(ResourceUtils.getColor(R.color.colorMainBtn));
                        mTableApply.setTextColor(ResourceUtils.getColor(R.color.colorHead));
                        break;
                    case 1:
                        mTableApply.setTextColor(ResourceUtils.getColor(R.color.colorMainBtn));
                        mTableRecord.setTextColor(ResourceUtils.getColor(R.color.colorHead));
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @OnClick({R.id.credit_rule, R.id.credit_exchange, R.id.get_credit_way, R.id.credit_apply, R.id.credit_record, R.id.credit_get})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.credit_rule:
                Intent intentRule = new Intent(UserCreditCenterActivity.this, CreditRuleExplainActivity.class);
                startActivity(intentRule);
                break;
            case R.id.credit_exchange:
                //兑换积分
                if (mTechCreditTotal > 0) {
                    Intent intent = new Intent(UserCreditCenterActivity.this, CreditExchangeActivity.class);
                    intent.putExtra(RequestConstant.KEY_UER_CREDIT_AMOUNT, mTechCreditTotal);
                    startActivity(intent);
                } else {
                    makeShortToast("暂无可兑换的积分");
                }
                break;
            case R.id.credit_get:
                Intent intent = new Intent(UserCreditCenterActivity.this, CreditRuleExplainActivity.class);
                startActivity(intent);
                break;
            case R.id.credit_apply:
                resetTextView();
                mViewpagerContact.setCurrentItem(1);
                currentIndex = 1;
                mTableApply.setTextColor(ResourceUtils.getColor(R.color.colorMainBtn));
                break;
            case R.id.credit_record:
                resetTextView();
                mViewpagerContact.setCurrentItem(0);
                currentIndex = 0;
                mTableRecord.setTextColor(ResourceUtils.getColor(R.color.colorMainBtn));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(getCreditUserAccountSubscription);
    }

    private void handlerCreditAmount(CreditAccountResult result) {
        if (result.respData.size() > 0) {
            mTechCreditTotal = result.respData.get(0).amount;
            mFreezeCredit = result.respData.get(0).freezeAmount;
            mCreditAmount.setText(String.valueOf(mTechCreditTotal));
            mCreditFreeze.setText(String.valueOf(mFreezeCredit));
        }
        if (result.respData.get(0).amount > 0 || result.respData.get(0).freezeAmount > 0) {
            llDetail.setVisibility(View.VISIBLE);
            mCreditIsEmpty.setVisibility(View.GONE);
        } else {
            llDetail.setVisibility(View.GONE);
            mCreditIsEmpty.setVisibility(View.VISIBLE);
            SpannableString msp = new SpannableString("你没有积分了，如何获取积分");
            msp.setSpan(new UnderlineSpan(), 7, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            msp.setSpan(new ForegroundColorSpan(ResourceUtils.getColor(R.color.get_credit_color)), 7, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mGetCredit.setText(msp);
            mGetCredit.setMovementMethod(LinkMovementMethod.getInstance());
            mGetCredit.setClickable(true);
        }
    }
}
