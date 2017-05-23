package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.AwardVerificationBean;
import com.xmd.manager.common.Utils;
import com.xmd.manager.common.VerificationManagementHelper;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lhj on 2016/12/6.
 */

public class AwardVerificationFragment extends BaseFragment {

    @Bind(R.id.reward_verification_code)
    TextView rewardVerificationCode;
    @Bind(R.id.reward_verification_name)
    TextView rewardVerificationName;
    @Bind(R.id.btn_reward_verification)
    Button btnRewardVerification;
    @Bind(R.id.reward_name)
    TextView rewardName;
    @Bind(R.id.verification_award_introduce)
    WebView verificationAwardIntroduce;
    @Bind(R.id.text_supplement_null)
    TextView supplementNull;

    private AwardVerificationBean mAward;
    private String mVerifyCode;

    public static AwardVerificationFragment getInstance(AwardVerificationBean award) {
        AwardVerificationFragment f = new AwardVerificationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VerificationManagementHelper.VERIFICATION_AWARD_TYPE, award);
        f.setArguments(bundle);
        return f;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_award_verification, container, false);
        ButterKnife.bind(this, view);
        mAward = (AwardVerificationBean) getArguments().getSerializable(VerificationManagementHelper.VERIFICATION_AWARD_TYPE);
        return view;
    }

    @Override
    protected void initView() {
        mVerifyCode = mAward.verifyCode;
        rewardVerificationCode.setText(mAward.verifyCode);
        rewardVerificationName.setText(mAward.activityName);
        rewardName.setText(String.format("奖品名称：%s", mAward.prizeName));
        verificationAwardIntroduce.getSettings().setJavaScriptEnabled(false);
        verificationAwardIntroduce.getSettings().setTextZoom(Constant.WEBVIEW_TEXT_ZOOM);
        if (Utils.isNotEmpty(mAward.description)) {
            verificationAwardIntroduce.loadDataWithBaseURL(null, mAward.description, Constant.MIME_TYPE_HTML, Constant.DEFAULT_ENCODE, null);
            verificationAwardIntroduce.setVisibility(View.VISIBLE);
        } else {
            verificationAwardIntroduce.setVisibility(View.GONE);
            supplementNull.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_reward_verification)
    public void onClick() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_VERIFICATION_AWARD_SAVE, mVerifyCode);
    }
}
