package com.xmd.cashier.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.cashier.R;
import com.xmd.cashier.adapter.MemberPlanAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.MemberRechargeContract;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.bean.MemberPlanInfo;
import com.xmd.cashier.dal.bean.PackagePlanItem;
import com.xmd.cashier.dal.bean.TechInfo;
import com.xmd.cashier.dal.event.RechargeFinishEvent;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.presenter.MemberRechargePresenter;
import com.xmd.cashier.widget.CircleImageView;
import com.xmd.cashier.widget.ClearableEditText;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;
import com.xmd.cashier.widget.FullyGridLayoutManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zr on 17-7-11.
 * 会员充值选择套餐
 */

public class MemberRechargeActivity extends BaseActivity implements MemberRechargeContract.View {
    private static final String TAG = "MemberRechargePresenter";
    private MemberRechargeContract.Presenter mPresenter;

    private TextView mMemberName;
    private TextView mMemberCardNo;
    private TextView mMemberLevel;

    private ClearableEditText mMemberAmount;
    private ClearableEditText mMemberGiveAmount;
    private RelativeLayout mTechInfo;
    private CircleImageView mTechAvatar;
    private TextView mTechName;
    private TextView mTechNo;
    private TextView mTechHint;
    private ImageView mTechArrow;
    private ImageView mTechDelete;

    private RelativeLayout mPlanLoadingLayout;
    private LinearLayout mPlanLoadErrorLayout;
    private TextView mPlanErrorMsg;
    private TextView mPlanRefresh;

    private RecyclerView mPlanList;
    private MemberPlanAdapter mAdapter;

    private Button mConfirmBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_recharge);
        mPresenter = new MemberRechargePresenter(this, this);
        initView();
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "会员充值");

        mMemberName = (TextView) findViewById(R.id.tv_info_name);
        mMemberCardNo = (TextView) findViewById(R.id.tv_info_card_no);
        mMemberLevel = (TextView) findViewById(R.id.tv_info_level);

        mMemberAmount = (ClearableEditText) findViewById(R.id.edt_recharge_input);
        mMemberGiveAmount = (ClearableEditText) findViewById(R.id.edt_recharge_give_input);

        mTechInfo = (RelativeLayout) findViewById(R.id.layout_tech_info);
        mTechAvatar = (CircleImageView) findViewById(R.id.img_tech_avatar);
        mTechName = (TextView) findViewById(R.id.tv_tech_name);
        mTechNo = (TextView) findViewById(R.id.tv_tech_no);
        mTechHint = (TextView) findViewById(R.id.tv_tech_hint);
        mTechArrow = (ImageView) findViewById(R.id.img_tech_arrow);
        mTechDelete = (ImageView) findViewById(R.id.img_tech_delete);

        mPlanLoadingLayout = (RelativeLayout) findViewById(R.id.layout_plan_loading);
        mPlanLoadErrorLayout = (LinearLayout) findViewById(R.id.layout_plan_error);
        mPlanErrorMsg = (TextView) findViewById(R.id.tv_plan_error);
        mPlanRefresh = (TextView) findViewById(R.id.tv_plan_refresh);

        mPlanList = (RecyclerView) findViewById(R.id.rv_plan_list);
        mAdapter = new MemberPlanAdapter(this);
        mAdapter.setCallBack(new MemberPlanAdapter.CallBack() {
            @Override
            public void onItemClick(PackagePlanItem item, int position) {
                // 隐藏软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(mMemberGiveAmount.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(mMemberAmount.getWindowToken(), 0);
                }
                mPresenter.clearAmount();
                mPresenter.clearAmountGive();
                mPresenter.onPackageSet(item);
            }
        });
        mPlanList.setHasFixedSize(true);
        mPlanList.setNestedScrollingEnabled(false);
        mPlanList.setLayoutManager(new FullyGridLayoutManager(this, 1));
        mPlanList.setItemAnimator(new DefaultItemAnimator());
        mPlanList.addItemDecoration(new CustomRecycleViewDecoration(15));
        mPlanList.setAdapter(mAdapter);

        mConfirmBtn = (Button) findViewById(R.id.btn_recharge_confirm);

        mTechInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onTechClick();
            }
        });

        mTechDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onTechDelete();
            }
        });

        mPlanRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.loadPlanData();
            }
        });

        // 设置充值金额
        mMemberAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.onAmountSet(mMemberAmount.getText().toString());
            }
        });

        // 设置赠送金额
        mMemberGiveAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.onAmountGiveSet(mMemberGiveAmount.getText().toString());
            }
        });

        // 充值金额获取焦点
        mMemberAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPresenter.clearPackage();
                    MemberManager.getInstance().setAmountType(AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY);
                }
            }
        });

        // 赠送金额获取焦点
        mMemberGiveAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPresenter.clearPackage();
                    MemberManager.getInstance().setAmountType(AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY);
                }
            }
        });

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onConfirm();
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
    public void setPresenter(MemberRechargeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void showMemberInfo(MemberInfo info) {
        mMemberName.setText(info.name);
        mMemberCardNo.setText(info.cardNo);
        mMemberLevel.setText(info.memberTypeName);
    }

    @Override
    public void showPlanData(MemberPlanInfo info) {
        mAdapter.setData(info.packageList);
        mPlanLoadingLayout.setVisibility(View.GONE);
        mPlanList.setVisibility(View.VISIBLE);
    }

    @Override
    public void errorPlanData(String error) {
        mPlanLoadingLayout.setVisibility(View.GONE);
        mPlanLoadErrorLayout.setVisibility(View.VISIBLE);
        mPlanErrorMsg.setText(error);
    }

    @Override
    public void loadingPlanData() {
        mPlanLoadingLayout.setVisibility(View.VISIBLE);
        mPlanLoadErrorLayout.setVisibility(View.GONE);
        mPlanList.setVisibility(View.GONE);
    }

    @Override
    public void showTechInfo(TechInfo info) {
        mTechHint.setVisibility(View.GONE);
        mTechAvatar.setVisibility(View.VISIBLE);
        mTechName.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(info.techNo)) {
            mTechNo.setVisibility(View.VISIBLE);
            mTechNo.setText(info.techNo);
        } else {
            mTechNo.setVisibility(View.GONE);
        }
        mTechName.setText(info.name);
        Glide.with(this).load(info.avatarUrl).dontAnimate().placeholder(R.drawable.ic_avatar).into(mTechAvatar);
        mTechArrow.setVisibility(View.GONE);
        mTechDelete.setVisibility(View.VISIBLE);
    }

    @Override
    public void deleteTechInfo() {
        mTechHint.setVisibility(View.VISIBLE);
        mTechAvatar.setVisibility(View.GONE);
        mTechName.setVisibility(View.GONE);
        mTechNo.setVisibility(View.GONE);
        mTechArrow.setVisibility(View.VISIBLE);
        mTechDelete.setVisibility(View.GONE);
    }

    @Override
    public void clearAmountGive() {
        mMemberGiveAmount.setText(null);
        mMemberGiveAmount.clearFocus();
    }

    @Override
    public void clearAmount() {
        mMemberAmount.setText(null);
        mMemberAmount.clearFocus();
    }

    @Override
    public void clearPackage() {
        mAdapter.setSelectedPosition(-1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RechargeFinishEvent event) {
        finishSelf();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TechInfo info) {
        mPresenter.onTechSelect(info);
    }
}


