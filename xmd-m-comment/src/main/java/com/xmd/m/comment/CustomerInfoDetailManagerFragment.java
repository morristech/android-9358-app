package com.xmd.m.comment;

import android.content.Intent;
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
import com.xmd.app.utils.Utils;
import com.xmd.app.widget.RoundImageView;
import com.xmd.m.R;
import com.xmd.m.R2;
import com.xmd.m.comment.bean.AllGroupListBean;
import com.xmd.m.comment.bean.ManagerCreditStatInfoBean;
import com.xmd.m.comment.bean.ManagerGroupListBean;
import com.xmd.m.comment.bean.ManagerMemberInfoBean;
import com.xmd.m.comment.bean.ManagerUserDetailModelBean;
import com.xmd.m.comment.bean.ManagerUserDetailResult;
import com.xmd.m.comment.bean.ManagerUserTagListBean;
import com.xmd.m.comment.bean.UserEditGroupResult;
import com.xmd.m.comment.bean.UserGroupListBean;
import com.xmd.m.comment.bean.UserInfoBean;
import com.xmd.m.comment.event.EditCustomerGroupEvent;
import com.xmd.m.comment.event.ShowCustomerHeadEvent;
import com.xmd.m.comment.httprequest.ConstantResources;
import com.xmd.m.comment.httprequest.DataManager;
import com.xmd.m.comment.httprequest.RequestConstant;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-7-5.
 */

public class CustomerInfoDetailManagerFragment extends BaseFragment {

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
    @BindView(R2.id.tv_customer_mark)
    TextView tvCustomerMark;
    @BindView(R2.id.ll_customer_mark)
    LinearLayout llCustomerMark;
    @BindView(R2.id.tv_customer_group)
    TextView tvCustomerGroup;
    @BindView(R2.id.ll_customer_group)
    LinearLayout llCustomerGroup;
    @BindView(R2.id.customer_type_label)
    LinearLayout customerTypeLabel;
    @BindView(R2.id.img_customer_type_label)
    ImageView imgCustomerTypeLabel;
    @BindView(R2.id.ll_customer_type_label)
    LinearLayout llCustomerTypeLabel;
    @BindView(R2.id.tv_customer_register_time)
    TextView tvCustomerRegisterTime;
    @BindView(R2.id.ll_customer_register_time)
    LinearLayout llCustomerRegisterTime;
    @BindView(R2.id.tv_customer_consume_money)
    TextView tvCustomerConsumeMoney;
    @BindView(R2.id.ll_customer_consume_money)
    LinearLayout llCustomerConsumeMoney;
    @BindView(R2.id.tv_customer_consume_times)
    TextView tvCustomerConsumeTimes;
    @BindView(R2.id.ll_customer_consume_times)
    LinearLayout llCustomerConsumeTimes;
    @BindView(R2.id.tv_customer_visit_time)
    TextView tvCustomerVisitTime;
    @BindView(R2.id.ll_customer_visit_time)
    LinearLayout llCustomerVisitTime;
    @BindView(R2.id.tv_customer_belong_tech_name)
    TextView tvCustomerBelongTechName;
    @BindView(R2.id.tv_customer_belong_tech_no)
    TextView tvCustomerBelongTechNo;
    @BindView(R2.id.ll_customer_belong_tech)
    LinearLayout llCustomerBelongTech;
    @BindView(R2.id.ll_customer_label_detail)
    LinearLayout llCustomerLabelDetail;
    @BindView(R2.id.tv_customer_membership_grade)
    TextView tvCustomerMembershipGrade;
    @BindView(R2.id.tv_customer_membership_grade_money)
    TextView tvCustomerMembershipGradeMoney;
    @BindView(R2.id.img_customer_membership_grade)
    ImageView imgCustomerMembershipGrade;
    @BindView(R2.id.ll_customer_membership_grade)
    LinearLayout llCustomerMembershipGrade;
    @BindView(R2.id.tv_customer_card_create_time)
    TextView tvCustomerCardCreateTime;
    @BindView(R2.id.ll_customer_card_create_time)
    LinearLayout llCustomerCardCreateTime;
    @BindView(R2.id.tv_customer_card_number)
    TextView tvCustomerCardNumber;
    @BindView(R2.id.ll_customer_card_number)
    LinearLayout llCustomerCardNumber;
    @BindView(R2.id.tv_customer_card_user_birthday)
    TextView tvCustomerCardUserBirthday;
    @BindView(R2.id.ll_customer_card_user_birthday)
    LinearLayout llCustomerCardUserBirthday;
    @BindView(R2.id.tv_customer_card_handler)
    TextView tvCustomerCardHandler;
    @BindView(R2.id.ll_customer_card_handler)
    LinearLayout llCustomerCardHandler;
    @BindView(R2.id.iv_customer_card_recharge_total)
    TextView ivCustomerCardRechargeTotal;
    @BindView(R2.id.iv_customer_card_reward_total)
    TextView ivCustomerCardRewardTotal;
    @BindView(R2.id.iv_customer_card_consume_total)
    TextView ivCustomerCardConsumeTotal;
    @BindView(R2.id.ll_customer_membership_detail)
    LinearLayout llCustomerMembershipDetail;
    @BindView(R2.id.tv_customer_credit)
    TextView tvCustomerCredit;
    @BindView(R2.id.img_customer_credit)
    ImageView imgCustomerCredit;
    @BindView(R2.id.ll_customer_credit)
    LinearLayout llCustomerCredit;
    @BindView(R2.id.iv_customer_credit_total)
    TextView ivCustomerCreditTotal;
    @BindView(R2.id.iv_customer_credit_consume_total)
    TextView ivCustomerCreditConsumeTotal;
    @BindView(R2.id.iv_customer_credit_reward_total)
    TextView ivCustomerCreditRewardTotal;
    @BindView(R2.id.ll_customer_credit_detail)
    LinearLayout llCustomerCreditDetail;
    @BindView(R2.id.img_right)
    ImageView imgRight;
    @BindView(R2.id.tv_comment_note)
    TextView tvCommentNote;
    @BindView(R2.id.tv_comment_times)
    TextView tvCommentTimes;
    @BindView(R2.id.rl_customer_comment)
    RelativeLayout rlCustomerComment;
    @BindView(R2.id.frame_consume_manager)
    FrameLayout frameConsumeManager;
    @BindView(R2.id.ll_card_view)
    LinearLayout llCardView;
    @BindView(R2.id.ll_credit_view)
    LinearLayout llCreditView;
//    @BindView(R2.id.main_scroll_view)
//    ScrollView mainScrollView;

    Unbinder unbinder;
    private String userId;
    private CustomerConsumeFragment mFragment;
    private boolean typeLabelViewIsOpen, memberShipViewIsOpen, creditViewIsOpen;
    private List<String> allGroups;
    private List<String> userGroups;
    private List<AllGroupListBean> allGroupList;
    private List<UserGroupListBean> userGroupList;
    private String userTelephone;
    private String userHeadUrl;
    private boolean hasMembershipCard;
    private String customerId;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_info_detail_manager, container, false);
        userId = getArguments().getString(CustomerInfoDetailActivity.CURRENT_USER_ID);
        initGroupData();
        initConsumeView();
        getUserInfo();
        unbinder = ButterKnife.bind(this, view);
        EventBusSafeRegister.register(this);
        return view;
    }

    private void initGroupData() {
        allGroups = new ArrayList<>();
        userGroups = new ArrayList<>();
        allGroupList = new ArrayList<>();
        userGroupList = new ArrayList<>();
        editUserGroupData();
    }

    private void editUserGroupData() {
        DataManager.getInstance().editUserGroup(userId, new NetworkSubscriber<UserEditGroupResult>() {
            @Override
            public void onCallbackSuccess(UserEditGroupResult userGroup) {
                userGroups.clear();
                allGroups.clear();
                userGroupList.clear();
                allGroupList.clear();

                if (userGroup.getRespData().userGroupList != null && userGroup.getRespData().userGroupList.size() > 0) {
                    for (int i = 0; i < userGroup.getRespData().userGroupList.size(); i++) {
                        userGroups.add(userGroup.getRespData().userGroupList.get(i).name);
                    }
                    userGroupList.clear();
                    userGroupList.addAll(userGroup.getRespData().userGroupList);
                }
                if (userGroup.getRespData().allGroupList != null && userGroup.getRespData().allGroupList.size() > 0) {
                    for (int i = 0; i < userGroup.getRespData().allGroupList.size(); i++) {
                        allGroups.add(userGroup.getRespData().allGroupList.get(i).name);
                    }
                    allGroupList.clear();
                    allGroupList.addAll(userGroup.getRespData().allGroupList);
                }
                if (userGroups.size() > 0) {
                    tvCustomerGroup.setText(Utils.listToString(userGroups, ","));
                } else {
                    tvCustomerGroup.setText("尚未添加分组,赶紧去添加吧");
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XToast.show(e.getLocalizedMessage());
                tvCustomerGroup.setText("尚未添加分组,赶紧去添加吧");
            }
        });
    }

    @Subscribe
    public void editGroupSubscribe(EditCustomerGroupEvent event) {
        editUserGroupData();
    }

    private void initConsumeView() {
        typeLabelViewIsOpen = true;
        memberShipViewIsOpen = false;
        creditViewIsOpen = false;
        FragmentManager fg = getChildFragmentManager();
        FragmentTransaction ft = fg.beginTransaction();
        mFragment = new CustomerConsumeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantResources.CUSTOMER_CONSUME_TYPE, ConstantResources.INTENT_TYPE_MANAGER);
        bundle.putSerializable(RequestConstant.KEY_USER_ID, userId);
        mFragment.setArguments(bundle);
        ft.replace(R.id.frame_consume_manager, mFragment);
        ft.commit();
    }

    public void getUserInfo() {
        DataManager.getInstance().loadManagerUserDetail(userId, new NetworkSubscriber<ManagerUserDetailResult>() {
            @Override
            public void onCallbackSuccess(ManagerUserDetailResult result) {
                initUserModelView(result.getRespData().userDetailModel);
                initGroupView(result.getRespData().groupList);
                initTypeLabelView(result.getRespData().userTagList);
                initMemberShipView(result.getRespData().memberInfo, result.getRespData().memberSwitchOn);
                initCreditView(result.getRespData().creditStatInfo, result.getRespData().creditSwitchOn);
            }

            @Override
            public void onCallbackError(Throwable e) {
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    private void initUserModelView(ManagerUserDetailModelBean userDetailModel) {
        userTelephone = userDetailModel.userLoginName;
        customerId = userDetailModel.userId;
        EventBus.getDefault().post(new UserInfoBean(userDetailModel.id, userTelephone, userDetailModel.emchatId, userDetailModel.userName, userDetailModel.avatarUrl, "contact",
                userDetailModel.userId, TextUtils.isEmpty(userDetailModel.userNoteName) ? "" : userDetailModel.userNoteName, TextUtils.isEmpty(userDetailModel.remark) ? "" : userDetailModel.remark,
                TextUtils.isEmpty(userDetailModel.impression) ? "" : userDetailModel.impression));
        userHeadUrl = userDetailModel.avatarUrl;
        Glide.with(getActivity()).load(userDetailModel.avatarUrl).error(R.drawable.img_default_avatar).into(imgCustomerHead);
        if (TextUtils.isEmpty(userDetailModel.userNoteName)) {
            llCustomerNickName.setVisibility(View.INVISIBLE);
            tvCustomerName.setText(TextUtils.isEmpty(userDetailModel.userName) ? "匿名用户" : userDetailModel.userName);
        } else {
            tvCustomerName.setText(TextUtils.isEmpty(userDetailModel.userNoteName) ? userDetailModel.userName : userDetailModel.userNoteName);
            tvCustomerNickName.setText(userDetailModel.userNoteName);
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
        tvCustomerMark.setText(TextUtils.isEmpty(userDetailModel.remark) ? "您尚未为该用户添加备注信息" : userDetailModel.remark);
        //注册时间
        tvCustomerRegisterTime.setText(userDetailModel.registerDate);
        //消费金额
        tvCustomerConsumeMoney.setText(String.format("%1.2f 元", userDetailModel.consumeAmount / 100f));
        //消费次数
        if (TextUtils.isEmpty(userDetailModel.consumeDate)) {
            tvCustomerConsumeTimes.setText(String.format("%s次", userDetailModel.consumeAmount));
        } else {
            tvCustomerConsumeTimes.setText(String.format("%s次（最近消费%s)", userDetailModel.consumeCount, userDetailModel.consumeDate));
        }

        //网店访问
        if (userDetailModel.visitCount > 0) {
            tvCustomerVisitTime.setText(String.format("%s次（最近访问%s）", userDetailModel.visitCount, userDetailModel.recentVisitDate));
        } else {
            tvCustomerVisitTime.setText("0次");
        }

        //拓客者
        if (TextUtils.isEmpty(userDetailModel.belongsTechName)) {
            tvCustomerBelongTechName.setText("-");
            tvCustomerBelongTechNo.setVisibility(View.INVISIBLE);
        } else {
            tvCustomerBelongTechName.setText(userDetailModel.belongsTechName);
            tvCustomerBelongTechNo.setText(TextUtils.isEmpty(userDetailModel.belongsTechSerialNo) ? "" : String.format("[%s]", userDetailModel.belongsTechSerialNo));
        }
        mFragment.setViewData(String.valueOf(userDetailModel.shopCount), String.format("%1.2f", userDetailModel.consumeAmount / 100f), String.valueOf(userDetailModel.rewardCount));
        //评论数
        tvCommentTimes.setText(String.valueOf(userDetailModel.commentCount));
    }

    private void initGroupView(List<ManagerGroupListBean> groupList) {
        if (groupList == null || groupList.size() == 0) {
            tvCustomerGroup.setText("尚未添加分组,赶紧去添加吧");
            return;
        }
        List<String> mGroupNames = new ArrayList<>();
        mGroupNames.clear();
        for (ManagerGroupListBean bean : groupList) {
            mGroupNames.add(bean.name);
        }
        tvCustomerGroup.setText(Utils.listToString(mGroupNames, ","));
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

    private void initMemberShipView(ManagerMemberInfoBean memberInfo, boolean memberSwitchOn) {
        if (!memberSwitchOn) {
            llCardView.setVisibility(View.GONE);
            return;
        }
        llCardView.setVisibility(View.VISIBLE);
        if (memberInfo == null) {
            hasMembershipCard = false;
            tvCustomerMembershipGrade.setText("非会员");
            return;
        } else {
            hasMembershipCard = true;
        }
        tvCustomerMembershipGrade.setText(TextUtils.isEmpty(memberInfo.memberTypeName) ? "" : String.format("%s会员", memberInfo.memberTypeName));
        String gradeMoney = String.format("余额　%1.2f元", memberInfo.amount / 100f);
        tvCustomerMembershipGradeMoney.setText(Utils.changeColor(gradeMoney, "#ff9a0c", 2, gradeMoney.length() - 1));
        tvCustomerCardCreateTime.setText(memberInfo.createTime);
        tvCustomerCardNumber.setText(memberInfo.cardNo);
        //生日
        tvCustomerCardUserBirthday.setText(TextUtils.isEmpty(memberInfo.birth)?"-":memberInfo.birth);
        //开卡
        tvCustomerCardHandler.setText(TextUtils.isEmpty(memberInfo.creatorName) ? "会所" : memberInfo.creatorName);
        //累计充值
        ivCustomerCardRechargeTotal.setText(String.format("%1.2f", memberInfo.cumulativeAmount / 100f));
        //累计赠送
        ivCustomerCardRewardTotal.setText(String.format("%1.2f", memberInfo.giveAmount / 100f));
        //次均消费d
        if(memberInfo.consumeCount>0){
            ivCustomerCardConsumeTotal.setText(String.format("%1.2f", memberInfo.consumeAmount / (100f * memberInfo.consumeCount)));
        }else{
            ivCustomerCardConsumeTotal.setText("-");
        }

    }

    private void initCreditView(ManagerCreditStatInfoBean creditStatInfo, boolean creditSwitchOn) {
        if (creditStatInfo == null || !creditSwitchOn) {
            llCreditView.setVisibility(View.GONE);
            return;
        }
        String creditAccount = String.format("余额 %s", creditStatInfo.amount);
        tvCustomerCredit.setText(Utils.changeColor(creditAccount, "#ff9a0c", 2, creditAccount.length()));
        ivCustomerCreditTotal.setText(String.valueOf(creditStatInfo.totalAmount));
        ivCustomerCreditConsumeTotal.setText(String.valueOf(creditStatInfo.usedAmount));
        ivCustomerCreditRewardTotal.setText(String.valueOf(creditStatInfo.rewardAmount));

    }

    @OnClick(R2.id.ll_customer_group)
    public void onLlCustomerGroupClicked() {
        Intent intentAdd = new Intent();
        intentAdd.setClassName(getActivity(), "com.xmd.manager.window.AddGroupActivity");
        intentAdd.putExtra(ConstantResources.KEY_USER_GROUPS, (Serializable) userGroupList);
        intentAdd.putExtra(ConstantResources.KEY_ALL_GROUPS, (Serializable) allGroupList);
        intentAdd.putExtra(ConstantResources.KEY_USER_ID, userId);
        startActivity(intentAdd);
    }


    @OnClick(R2.id.ll_customer_type_label)
    public void onLlCustomerTypeLabelClicked() {
        if (typeLabelViewIsOpen) {
            typeLabelViewIsOpen = false;
            llCustomerLabelDetail.setVisibility(View.GONE);
            imgCustomerTypeLabel.setImageResource(R.drawable.arrow_down);
        } else {
            typeLabelViewIsOpen = true;
            llCustomerLabelDetail.setVisibility(View.VISIBLE);
            imgCustomerTypeLabel.setImageResource(R.drawable.arrow_up);
        }
    }

    @OnClick(R2.id.ll_customer_membership_grade)
    public void onLlCustomerMembershipGradeClicked() {
        if (!hasMembershipCard) {
            XToast.show("该用户尚未开通会员卡服务");
            return;
        }
        if (memberShipViewIsOpen) {
            memberShipViewIsOpen = false;
            llCustomerMembershipDetail.setVisibility(View.GONE);
            imgCustomerMembershipGrade.setImageResource(R.drawable.arrow_down);
        } else {
            memberShipViewIsOpen = true;
            llCustomerMembershipDetail.setVisibility(View.VISIBLE);
            imgCustomerMembershipGrade.setImageResource(R.drawable.arrow_up);
        }
    }

    @OnClick(R2.id.ll_customer_credit)
    public void onLlCustomerCreditClicked() {
        if (creditViewIsOpen) {
            creditViewIsOpen = false;
            llCustomerCreditDetail.setVisibility(View.GONE);
            imgCustomerCredit.setImageResource(R.drawable.arrow_down);
        } else {
            llCustomerCreditDetail.setVisibility(View.VISIBLE);
            creditViewIsOpen = true;
            imgCustomerCredit.setImageResource(R.drawable.arrow_up);
        }

    }

    @OnClick(R2.id.rl_customer_comment)
    public void onCommentClicked() {
        CommentSearchActivity.startCommentSearchActivity(getActivity(), true, false, "", "", customerId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBusSafeRegister.unregister(this);
    }

    @OnClick(R2.id.img_customer_head)
    public void onImageCustomerHeadClicked() {
        if (TextUtils.isEmpty(userHeadUrl)) {
            return;
        }
        EventBus.getDefault().post(new ShowCustomerHeadEvent(userHeadUrl));
    }


}
