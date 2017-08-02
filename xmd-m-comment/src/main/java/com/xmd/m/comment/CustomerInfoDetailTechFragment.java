package com.xmd.m.comment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseFragment;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.widget.RoundImageView;
import com.xmd.m.R;
import com.xmd.m.R2;
import com.xmd.m.comment.bean.ManagerUserTagListBean;
import com.xmd.m.comment.bean.TechUserDetailModelBean;
import com.xmd.m.comment.bean.TechUserDetailResult;
import com.xmd.m.comment.bean.UserInfoBean;
import com.xmd.m.comment.event.EditCustomerRemarkSuccessEvent;
import com.xmd.m.comment.event.ShowCustomerHeadEvent;
import com.xmd.m.comment.httprequest.ConstantResources;
import com.xmd.m.comment.httprequest.DataManager;
import com.xmd.m.comment.httprequest.RequestConstant;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-7-5.
 */

public class CustomerInfoDetailTechFragment extends BaseFragment {


    @BindView(R2.id.img_customer_head)
    RoundImageView imgCustomerHead;
    @BindView(R2.id.tv_customer_name)
    TextView tvCustomerName;
    @BindView(R2.id.img_customer_type_01)
    ImageView imgCustomerType01;
    @BindView(R2.id.img_customer_type_02)
    ImageView imgCustomerType02;
    @BindView(R2.id.tv_customer_phone)
    TextView tvCustomerPhone;
    @BindView(R2.id.ll_customer_phone)
    LinearLayout llCustomerPhone;
    @BindView(R2.id.tv_customer_nick_name)
    TextView tvCustomerNickName;
    @BindView(R2.id.ll_customer_nick_name)
    LinearLayout llCustomerNickName;
    @BindView(R2.id.tv_customer_label)
    TextView tvCustomerLabel;
    @BindView(R2.id.ll_customer_label)
    LinearLayout llCustomerLabel;
    @BindView(R2.id.tv_customer_mark)
    TextView tvCustomerMark;
    @BindView(R2.id.ll_customer_mark)
    LinearLayout llCustomerMark;
    @BindView(R2.id.customer_type_label)
    LinearLayout customerTypeLabel;
    @BindView(R2.id.ll_customer_type_label)
    LinearLayout llCustomerTypeLabel;
    @BindView(R2.id.tv_customer_register_time)
    TextView tvCustomerRegisterTime;
    @BindView(R2.id.ll_customer_register_time)
    LinearLayout llCustomerRegisterTime;
    @BindView(R2.id.tv_customer_visit_time)
    TextView tvCustomerVisitTime;
    @BindView(R2.id.ll_customer_visit_time)
    LinearLayout llCustomerVisitTime;
    @BindView(R2.id.tv_customer_membership_grade)
    TextView tvCustomerMembershipGrade;
    @BindView(R2.id.ll_customer_membership_grade)
    LinearLayout llCustomerMembershipGrade;
    @BindView(R2.id.img_right)
    ImageView imgRight;
    @BindView(R2.id.tv_comment_note)
    TextView tvCommentNote;
    @BindView(R2.id.tv_comment_times)
    TextView tvCommentTimes;
    @BindView(R2.id.rl_customer_comment)
    RelativeLayout rlCustomerComment;
    @BindView(R2.id.frame_consume_tech)
    FrameLayout frameConsumeTech;


    Unbinder unbinder;
    private String customerId;
    private String userId;
    private CustomerConsumeFragment mFragment;
    private String techId;
    private String userPhone;
    private String userName;
    private UserInfoBean userBean;
    private String userHeadUrl;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_info_detail_tech, container, false);
        userId = getArguments().getString(CustomerInfoDetailActivity.CURRENT_USER_ID);
        EventBusSafeRegister.register(this);
        initConsumeView();
        getUserInfo();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void initConsumeView() {
        FragmentManager fg = getChildFragmentManager();
        FragmentTransaction ft = fg.beginTransaction();
        mFragment = new CustomerConsumeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(RequestConstant.KEY_USER_ID, userId);
        bundle.putSerializable(ConstantResources.CUSTOMER_CONSUME_TYPE, ConstantResources.INTENT_TYPE_TECH);
        mFragment.setArguments(bundle);
        ft.replace(R.id.frame_consume_tech, mFragment);
        ft.commit();
    }

    public void getUserInfo() {
        DataManager.getInstance().loadTechUserDetail(userId, new NetworkSubscriber<TechUserDetailResult>() {
            @Override
            public void onCallbackSuccess(TechUserDetailResult result) {
                if (result.getRespData().memberInfo != null) {
                    tvCustomerMembershipGrade.setText(result.getRespData().memberInfo.memberTypeName);
                } else {
                    tvCustomerMembershipGrade.setText("非会员");
                }
                initTypeLabelView(result.getRespData().userTagList);
                initUserModelView(result.getRespData().userDetailModel);

            }

            @Override
            public void onCallbackError(Throwable e) {
               XToast.show(e.getLocalizedMessage());
            }
        });
    }


    private void initTypeLabelView(List<ManagerUserTagListBean> userTagList) {
        if (userTagList == null || userTagList.size() == 0) {
            return;
        }
        customerTypeLabel.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 0, 0);
        for (ManagerUserTagListBean bean : userTagList) {
            TextView tv = new TextView(getActivity());
            tv.setText(bean.tagName);
            tv.setBackgroundResource(R.drawable.bg_contact_mark);
            tv.setTextSize(14);
            tv.setTextColor(Color.parseColor("#ff8909"));
            tv.setPadding(14, 0, 14, 0);
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(lp);
            customerTypeLabel.addView(tv);
        }
    }

    private void initUserModelView(TechUserDetailModelBean userDetailModel) {
        userBean = new UserInfoBean(userDetailModel.id, userDetailModel.userLoginName, userDetailModel.emchatId, userDetailModel.userName, userDetailModel.avatarUrl, "contact",
                userDetailModel.userId, TextUtils.isEmpty(userDetailModel.userNoteName) ? "" : userDetailModel.userNoteName, TextUtils.isEmpty(userDetailModel.remark) ? "" : userDetailModel.remark,
                TextUtils.isEmpty(userDetailModel.impression) ? "" : userDetailModel.impression);
        EventBus.getDefault().post(userBean);
        customerId = userDetailModel.userId;
        techId = userDetailModel.techId;
        userPhone = userDetailModel.userLoginName;
        userName = userDetailModel.userName;
        userHeadUrl = userDetailModel.avatarUrl;
        Glide.with(getActivity()).load(userDetailModel.avatarUrl).error(R.drawable.img_default_avatar).into(imgCustomerHead);
        if (TextUtils.isEmpty(userDetailModel.userNoteName)) {
            llCustomerNickName.setVisibility(View.INVISIBLE);
            tvCustomerName.setText(TextUtils.isEmpty(userDetailModel.userName) ? "匿名用户" : userDetailModel.userName);
        } else {
            llCustomerNickName.setVisibility(View.VISIBLE);
            tvCustomerName.setText(userDetailModel.userNoteName);
            tvCustomerNickName.setText(userDetailModel.userName);
        }
        if (userDetailModel.customerType.equals(RequestConstant.CUSTOMER_TYPE_FANS_WX)) {
            imgCustomerType01.setVisibility(View.VISIBLE);
            imgCustomerType02.setVisibility(View.VISIBLE);
            imgCustomerType01.setImageResource(R.drawable.icon_contact_fans);
            imgCustomerType02.setImageResource(R.drawable.icon_contact_wx);
        } else {
            imgCustomerType01.setVisibility(View.VISIBLE);
            imgCustomerType02.setVisibility(View.GONE);
            if (userDetailModel.customerType.equals(RequestConstant.CUSTOMER_TYPE_FANS)) {
                imgCustomerType01.setImageResource(R.drawable.icon_contact_fans);
            } else if (userDetailModel.customerType.equals(RequestConstant.CUSTOMER_TYPE_WX)) {
                imgCustomerType01.setImageResource(R.drawable.icon_contact_wx);
            } else {
                imgCustomerType01.setImageResource(R.drawable.icon_contact_tech_add);
            }
        }
        if (TextUtils.isEmpty(userDetailModel.userLoginName) || !userDetailModel.userLoginName.startsWith("1")) {
            llCustomerPhone.setVisibility(View.GONE);
        } else {
            llCustomerPhone.setVisibility(View.VISIBLE);
            tvCustomerPhone.setText(userDetailModel.userLoginName);
        }

        tvCustomerLabel.setText(TextUtils.isEmpty(userDetailModel.impression) ? "您尚未为该用户添加标签" : userDetailModel.impression);
        tvCustomerMark.setText(TextUtils.isEmpty(userDetailModel.remark) ? "您尚未为该用户添加备注信息" : userDetailModel.remark);
        tvCustomerRegisterTime.setText(userDetailModel.registerDate);
        tvCustomerVisitTime.setText(TextUtils.isEmpty(userDetailModel.recentVisitDate)?"无":userDetailModel.recentVisitDate);
        mFragment.setViewData(String.valueOf(userDetailModel.shopCount), String.format("%1.2f", userDetailModel.consumeAmount / 100f), String.valueOf(userDetailModel.rewardCount));
        tvCommentTimes.setText(String.valueOf(userDetailModel.commentCount));

    }

    @Subscribe
    public void onRemarkChanged(EditCustomerRemarkSuccessEvent event) {
        if (TextUtils.isEmpty(event.remarkName)) {
            llCustomerNickName.setVisibility(View.INVISIBLE);
            tvCustomerName.setText(TextUtils.isEmpty(event.userName) ? "匿名用户" : event.userName);
        } else {
            llCustomerNickName.setVisibility(View.VISIBLE);
            tvCustomerName.setText(event.remarkName);
            tvCustomerNickName.setText(event.userName);
        }
        tvCustomerLabel.setText(TextUtils.isEmpty(event.remarkImpression) ? "您尚未为该用户添加标签" : event.remarkImpression);
        tvCustomerMark.setText(TextUtils.isEmpty(event.remarkMessage) ? "您尚未为该用户添加备注信息" : event.remarkMessage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBusSafeRegister.unregister(this);
    }


    @OnClick(R2.id.rl_customer_comment)
    public void onRlCustomerCommentClicked() {
        if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(userPhone)) {
            XToast.show("匿名用户无评价详情");
        } else {
            CommentSearchActivity.startCommentSearchActivity(getActivity(), false, false, techId, "",customerId);
        }

    }

    @OnClick(R2.id.ll_label_and_mark)
    public void onLLLabelAndMarkClicked() {
        if (userBean == null) {
            return;
        }
        EditCustomerInformationActivity.startEditCustomerInformationActivity(getActivity(),userBean.userId, userBean.id, ConstantResources.INTENT_TYPE_TECH, userBean.emChatName, userBean.userNoteName, userBean.contactPhone, userBean.remarkMessage, userBean.remarkImpression);
    }

    @OnClick(R2.id.img_customer_head)
    public void onImageCustomerHeadClicked() {
        if (TextUtils.isEmpty(userHeadUrl)) {
            return;
        }
        EventBus.getDefault().post(new ShowCustomerHeadEvent(userHeadUrl));
    }


}
