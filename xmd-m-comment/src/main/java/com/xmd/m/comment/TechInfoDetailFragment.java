package com.xmd.m.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.BaseFragment;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.app.widget.RoundImageView;
import com.xmd.m.R;
import com.xmd.m.R2;
import com.xmd.m.comment.bean.ClubEmployeeBean;
import com.xmd.m.comment.bean.ClubEmployeeDetailResult;
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

public class TechInfoDetailFragment extends BaseFragment {

    @BindView(R2.id.img_tech_head)
    RoundImageView imgTechHead;
    @BindView(R2.id.tv_tech_name)
    TextView tvTechName;
    @BindView(R2.id.tv_tech_no)
    TextView tvTechNo;
    @BindView(R2.id.tv_tech_phone)
    TextView tvTechPhone;
    @BindView(R2.id.ll_tech_phone)
    LinearLayout llTechPhone;
    @BindView(R2.id.tv_tech_mark)
    TextView tvTechMark;
    @BindView(R2.id.ll_tech_mark)
    LinearLayout llTechMark;

    Unbinder unbinder;
    private String userId;
    private String userHeadUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userId = getArguments().getString(CustomerInfoDetailActivity.CURRENT_USER_ID);
        View view = inflater.inflate(R.layout.fragment_tech_info_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        getInfoData();
        getCurrentUserInfo();
        return view;
    }

    private void getCurrentUserInfo() {
        UserInfoServiceImpl.getInstance().loadUserInfoByUserIdFromServer(userId, new Callback<User>() {
            @Override
            public void onResponse(User result, Throwable error) {
                if (result != null) {

                    if (result.getContactPermission().isCall()) {
                        llTechPhone.setVisibility(View.VISIBLE);
                    } else {
                        llTechPhone.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void getInfoData() {
        DataManager.getInstance().loadClubEmployeeDetail(userId, new NetworkSubscriber<ClubEmployeeDetailResult>() {
            @Override
            public void onCallbackSuccess(ClubEmployeeDetailResult result) {
                handlerEmployeeView(result.getRespData());

            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }

    private void handlerEmployeeView(ClubEmployeeBean respData) {
        EventBus.getDefault().post(new UserInfoBean(respData.id, respData.telephone, respData.emchatId, respData.name, respData.avatarUrl, respData.roles, respData.id, "", "", ""));
        userHeadUrl = respData.avatarUrl;
        Glide.with(getActivity()).load(respData.avatarUrl).error(R.drawable.img_default_avatar).into(imgTechHead);
        tvTechName.setText(TextUtils.isEmpty(respData.name) ? "技师" : respData.name);
        tvTechNo.setText(TextUtils.isEmpty(respData.techNo) ? "" : String.format("[%s]", respData.techNo));
        if (!TextUtils.isEmpty(respData.telephone)) {
            tvTechPhone.setText(respData.telephone);
        }
        User user = UserInfoServiceImpl.getInstance().getCurrentUser();
        XLogger.i(">>>", "roles" + user.getUserRoles());
        llTechPhone.setVisibility(View.GONE);
        if (respData.roles.equals("tech")) {
            llTechMark.setVisibility(View.VISIBLE);
            tvTechMark.setText(TextUtils.isEmpty(respData.description) ? "该用户尚未添加个人简介信息..." : respData.description);
        } else {
            llTechMark.setVisibility(View.GONE);
        }
    }

    @OnClick(R2.id.img_tech_head)
    public void onImageCustomerHeadClicked() {
        if (TextUtils.isEmpty(userHeadUrl)) {
            return;
        }
        EventBus.getDefault().post(new ShowCustomerHeadEvent(userHeadUrl));
    }


}
