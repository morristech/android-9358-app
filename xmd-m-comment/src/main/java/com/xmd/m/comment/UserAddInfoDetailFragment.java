package com.xmd.m.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.app.BaseFragment;
import com.xmd.app.widget.RoundImageView;
import com.xmd.m.R;
import com.xmd.m.R2;
import com.xmd.m.comment.bean.TechUserDetailBean;
import com.xmd.m.comment.bean.TechUserDetailResult;
import com.xmd.m.comment.bean.UserInfoBean;
import com.xmd.m.comment.event.ShowCustomerHeadEvent;
import com.xmd.m.comment.httprequest.DataManager;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-7-5.
 */

public class UserAddInfoDetailFragment extends BaseFragment {

    @BindView(R2.id.img_customer_head)
    RoundImageView imgCustomerHead;
    @BindView(R2.id.tv_customer_name)
    TextView tvCustomerName;
    @BindView(R2.id.img_customer_type_01)
    ImageView imgCustomerType01;
    @BindView(R2.id.tv_customer_phone)
    TextView tvCustomerPhone;
    @BindView(R2.id.ll_customer_phone)
    LinearLayout llCustomerPhone;
    @BindView(R2.id.tv_customer_label)
    TextView tvCustomerLabel;
    @BindView(R2.id.ll_customer_label)
    LinearLayout llCustomerLabel;
    @BindView(R2.id.tv_customer_mark)
    TextView tvCustomerMark;
    @BindView(R2.id.ll_customer_mark)
    LinearLayout llCustomerMark;
    Unbinder unbinder;
    private String userId;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_add_info_detail, container, false);
        userId = getArguments().getString(CustomerInfoDetailActivity.CURRENT_USER_ID);
        unbinder = ButterKnife.bind(this, view);
        getUserData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void getUserData() {
        DataManager.getInstance().loadTechUserDetail(userId, new NetworkSubscriber<TechUserDetailResult>() {
            @Override
            public void onCallbackSuccess(TechUserDetailResult result) {
                initUserView(result.getRespData());
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }

    private void initUserView(TechUserDetailBean respData) {
        EventBus.getDefault().post(new UserInfoBean(respData.userDetailModel.id, respData.userDetailModel.userLoginName, respData.userDetailModel.emchatId, respData.userDetailModel.userName, respData.userDetailModel.avatarUrl, "contact",
                respData.userDetailModel.userId, TextUtils.isEmpty(respData.userDetailModel.userNoteName) ? "" : respData.userDetailModel.userNoteName, TextUtils.isEmpty(respData.userDetailModel.remark) ? "" : respData.userDetailModel.remark,
                TextUtils.isEmpty(respData.userDetailModel.impression) ? "" : respData.userDetailModel.impression));
        tvCustomerName.setText(respData.userDetailModel.userNoteName);
        if (TextUtils.isEmpty(respData.userDetailModel.userLoginName)) {
            llCustomerPhone.setVisibility(View.GONE);
        } else {
            llCustomerPhone.setVisibility(View.VISIBLE);
            tvCustomerPhone.setText(respData.userDetailModel.userLoginName);
        }
        imgCustomerType01.setImageResource(R.drawable.icon_contact_tech_add);
        tvCustomerMark.setText(TextUtils.isEmpty(respData.userDetailModel.remark) ? "-" : respData.userDetailModel.remark);
        tvCustomerLabel.setText(TextUtils.isEmpty(respData.userDetailModel.impression) ? "-" : respData.userDetailModel.impression);
    }

//    @OnClick(R2.id.img_customer_head)
//    public void onImageCustomerHeadClicked(){
//        if(TextUtils.isEmpty(userHeadUrl)){
//            return;
//        }
//        EventBus.getDefault().post(new ShowCustomerHeadEvent(userHeadUrl));
//    }
}
