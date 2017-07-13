package com.xmd.manager.window;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.manager.R;
import com.xmd.manager.adapter.VerificationRecordDetailAdapter;
import com.xmd.manager.beans.VerificationDetailBean;
import com.xmd.manager.beans.VerificationRecordCouponDetailBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.VerificationRecordDetailResult;
import com.xmd.manager.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Lhj on 2017/1/11.
 */

public class VerificationRecordDetailActivity extends BaseActivity {

    @BindView(R.id.user_head)
    CircleImageView mUserHead;
    @BindView(R.id.user_phone)
    TextView mUserPhone;
    @BindView(R.id.user_verification_type)
    TextView mUserVerificationType;
    @BindView(R.id.user_verification_time)
    TextView mUserVerificationTime;
    @BindView(R.id.user_verification_handler)
    TextView mUserVerificationHandler;
    @BindView(R.id.user_verification_code)
    TextView mUserVerificationCode;
    @BindView(R.id.user_verification_list)
    ListView mUserVerificationList;
    @BindView(R.id.user_name)
    TextView mUserName;


    private String mRecordId;
    private VerificationRecordDetailAdapter mDetailAdapter;
    private Subscription mRecordDetailSubscription;
    private List<VerificationRecordCouponDetailBean> mRecordData;
    private VerificationDetailBean mRecordBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_record_detail);
        ButterKnife.bind(this);
        mRecordId = getIntent().getStringExtra(RequestConstant.KEY_VERIFICATION_RECORD_ID);
        initView();

    }

    private void initView() {
        getVerificationDetail();
        mRecordData = new ArrayList<>();
        mRecordDetailSubscription = RxBus.getInstance().toObservable(VerificationRecordDetailResult.class).subscribe(
                recordResult -> handlerRecordResult(recordResult)
        );
    }

    private void handlerRecordResult(VerificationRecordDetailResult recordResult) {
        if (recordResult.statusCode == 200) {
            mRecordData = recordResult.respData.detail;
            mDetailAdapter = new VerificationRecordDetailAdapter(VerificationRecordDetailActivity.this, mRecordData);
            mRecordBean = recordResult.respData.record;
            Glide.with(VerificationRecordDetailActivity.this).load(mRecordBean.avatarUrl).into(mUserHead);
            if (Utils.isNotEmpty(mRecordBean.userName)) {
                mUserName.setText(mRecordBean.userName);
            } else {
                mUserName.setText(ResourceUtils.getString(R.string.visit_default_name));
            }
            if (Utils.isNotEmpty(mRecordBean.telephone)) {
                mUserPhone.setText(mRecordBean.telephone);
            } else {
                mUserPhone.setText("-");
            }
            mUserVerificationType.setText(mRecordBean.businessTypeName);
            mUserVerificationTime.setText(mRecordBean.verifyTime);
            mUserVerificationHandler.setText(mRecordBean.operatorName);
            mUserVerificationCode.setText(mRecordBean.verifyCode);
            mUserVerificationList.setAdapter(mDetailAdapter);

        } else {
            makeShortToast(recordResult.msg);
            this.finish();
        }
    }

    private void getVerificationDetail() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CHECK_INFO_RECORD_DETAIL, mRecordId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mRecordDetailSubscription);
    }
}
