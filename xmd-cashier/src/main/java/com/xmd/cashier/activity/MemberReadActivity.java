package com.xmd.cashier.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberReadContract;
import com.xmd.cashier.dal.event.CardFinishEvent;
import com.xmd.cashier.dal.event.MagneticReaderEvent;
import com.xmd.cashier.presenter.MemberReadPresenter;
import com.xmd.cashier.widget.ClearableEditText;
import com.xmd.cashier.widget.StepView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zr on 17-7-11.
 * 刷会员卡 扫二维码 输入卡号 获取会员信息
 */

public class MemberReadActivity extends BaseActivity implements MemberReadContract.View {
    private MemberReadContract.Presenter mPresenter;
    private String mReadType;
    private Button mReadConfirm;
    private ImageView mScanImage;
    private ClearableEditText mMemberInput;

    private StepView mStepView;

    private ImageView imgCircle;
    private ImageView imgCard;
    private Animation animCircle;
    private Animation animCard;

    private LinearLayout mRootView;
    private RelativeLayout mReadBg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_read);
        mPresenter = new MemberReadPresenter(this, this);
        mReadType = getIntent().getStringExtra(AppConstants.EXTRA_MEMBER_BUSINESS_TYPE);
        initView();
        mPresenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
        imgCard.startAnimation(animCard);
        imgCircle.startAnimation(animCircle);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
        imgCard.clearAnimation();
        imgCircle.clearAnimation();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mPresenter.onNewIntent(intent);
        super.onNewIntent(intent);
    }

    private void initView() {
        String title = Utils.getStringFromResource(R.string.app_name);
        imgCircle = (ImageView) findViewById(R.id.img_member_circle);
        imgCard = (ImageView) findViewById(R.id.img_member_card);
        animCircle = AnimationUtils.loadAnimation(this, R.anim.anim_scale_repeat);
        animCard = AnimationUtils.loadAnimation(this, R.anim.anim_traslate_single);
        mMemberInput = (ClearableEditText) findViewById(R.id.et_member_input);
        mReadConfirm = (Button) findViewById(R.id.btn_member_read_confirm);
        mStepView = (StepView) findViewById(R.id.sv_step_read);
        mScanImage = (ImageView) findViewById(R.id.img_scan);
        mRootView = (LinearLayout) findViewById(R.id.ll_root);
        mReadBg = (RelativeLayout) findViewById(R.id.rl_read_bg);
        switch (mReadType) {
            case AppConstants.MEMBER_BUSINESS_TYPE_PAYMENT:
                title = "会员消费";
                mStepView.setVisibility(View.GONE);
                break;
            case AppConstants.MEMBER_BUSINESS_TYPE_RECHARGE:
                title = "会员充值";
                mScanImage.setVisibility(View.GONE);
                mStepView.setVisibility(View.GONE);
                break;
            case AppConstants.MEMBER_BUSINESS_TYPE_CARD:
                title = "会员开卡";
                mMemberInput.setHint("输入会员卡号");
                mScanImage.setVisibility(View.GONE);
                mStepView.setVisibility(View.VISIBLE);
                mStepView.setSteps(AppConstants.MEMBER_CARD_STEPS_NORMAL);
                mStepView.selectedStep(3);
                break;
            default:
                break;
        }
        showToolbar(R.id.toolbar, title);

        mReadConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onConfirm(mReadType);
            }
        });

        mMemberInput.setKeyListener(DigitsKeyListener.getInstance(AppConstants.INPUT_DIGITS));
        mMemberInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mPresenter.onConfirm(mReadType);
                    return true;
                }
                return false;
            }
        });

        //获取屏幕高度
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int screenHeight = metrics.heightPixels;
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        mRootView.getWindowVisibleDisplayFrame(r);
                        int deltaHeight = screenHeight - r.bottom;
                        if (deltaHeight > 150) {
                            mReadBg.setVisibility(View.GONE);
                        } else {
                            mReadBg.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    public void onClickScan(View view) {
        mPresenter.onClickScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void showEnterAnim() {
        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
    }

    @Override
    public void showExitAnim() {
        overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
    }

    @Override
    public String getReadType() {
        return mReadType;
    }

    @Override
    public void setPresenter(MemberReadContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void setInputContent(String content) {
        mMemberInput.setText(content);
    }

    @Override
    public String getInputContent() {
        return mMemberInput.getText().toString().replace(" ", "");
    }

    @Override
    public boolean onKeyEventBack() {
        finishSelf();
        if (mReadType.equals(AppConstants.MEMBER_BUSINESS_TYPE_CARD)) {
            showExitAnim();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MagneticReaderEvent event) {
        mMemberInput.setText(event.getResult());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CardFinishEvent event) {
        finishSelf();
    }
}
