package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.FavourableActivityBean;
import com.xmd.manager.common.FileSizeUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.widget.AlertDialogBuilder;
import com.xmd.manager.widget.CircularBeadImageView;
import com.xmd.manager.widget.LoadingDialog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sdcm on 17-5-23.
 */
public class GMessageConfirmFragment extends BaseFragment {
    @Bind(R.id.customer_count_text)
    TextView customerCountText;
    @Bind(R.id.group_pic)
    CircularBeadImageView groupPic;
    @Bind(R.id.group_detail_message_content)
    TextView contentText;

    @Bind(R.id.group_activity_title)
    TextView activityTitleText;
    @Bind(R.id.group_activity_info)
    TextView activityInfoText;
    @Bind(R.id.customer_group_type)
    TextView customerGroupNameText;

    private String imageUrl;
    private String messageContent;
    private int selectCustomerCount;
    private FavourableActivityBean activityInfo;
    private String useGroupType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_confirm, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            initView();
        }
    }

    @Override
    protected void initView() {
        if(Utils.isNotEmpty(imageUrl)){
            Glide.with(getActivity()).load(imageUrl).into(groupPic);
            groupPic.setVisibility(View.VISIBLE);
        }else {
            groupPic.setVisibility(View.GONE);
        }

        if(Utils.isNotEmpty(messageContent)){
            contentText.setText(messageContent);
            contentText.setVisibility(View.VISIBLE);
        }else {
            if(Utils.isEmpty(imageUrl)){ //图片文字都为空
                contentText.setText(ResourceUtils.getString(R.string.text_no_content));
                contentText.setVisibility(View.VISIBLE);
            }else {
                contentText.setVisibility(View.GONE);
            }
        }

        String s = "(" + String.valueOf(selectCustomerCount) + "个)";
        customerCountText.setText(Utils.changeColor(s, ResourceUtils.getColor(R.color.colorMain), 1, s.length() - 2));

        if(activityInfo != null){
            activityTitleText.setVisibility(View.VISIBLE);
            activityTitleText.setText(Constant.MESSAGE_ACTIVITY_LABELS.get(activityInfo.msgType));
            activityInfoText.setText(activityInfo.name);
        }else {
            activityTitleText.setVisibility(View.GONE);
            activityInfoText.setText(ResourceUtils.getString(R.string.text_no_activity_selected));
        }

        customerGroupNameText.setText(useGroupType);
    }

    public void initData(Map<String, Object> params){
        if(params != null){
            imageUrl = (String) params.get(RequestConstant.KEY_GROUP_IMAGE_ID);
            messageContent = (String) params.get(RequestConstant.KEY_GROUP_MESSAGE_CONTENT);
            selectCustomerCount = (int) params.get(RequestConstant.KEY_GROUP_SEND_COUNT);
            activityInfo = (FavourableActivityBean) params.get(RequestConstant.KEY_GROUP_COUPON_CONTENT);
            useGroupType = (String) params.get(RequestConstant.KEY_GROUP_NAME);
        }
    }

    @OnClick({R.id.btn_previous_step, R.id.btn_send})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous_step:
                ((GroupMessageCustomerActivity) getActivity()).gotoEditContentFragment();
                break;
            case R.id.btn_send:
                ((GroupMessageCustomerActivity) getActivity()).send();
                break;
        }
    }
}
