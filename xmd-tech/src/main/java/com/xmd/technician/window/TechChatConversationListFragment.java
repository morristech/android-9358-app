package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.chat.view.ConversationListFragment;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.http.NetService;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.widget.AlertDialogBuilder;

import rx.Observable;
import rx.Subscription;

/**
 * Created by mo on 17-7-19.
 * 会话列表
 */

public class TechChatConversationListFragment extends ConversationListFragment {

    private LoginTechnician technician = LoginTechnician.getInstance();
    private boolean isChangingCustomerStatus;
    private Subscription changeCustomerStatusSubscription;

    public static TechChatConversationListFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITLE, title);
        TechChatConversationListFragment fragment = new TechChatConversationListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initToolBarCustomerService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (changeCustomerStatusSubscription != null) {
            changeCustomerStatusSubscription.unsubscribe();
        }
    }

    //显示客服上下线开关
    private void initToolBarCustomerService() {
        if (technician.getCustomerService() == null) {
            return;
        }
        LinearLayout container = (LinearLayout) getView().findViewById(R.id.rightLinearLayout);
        container.setVisibility(View.VISIBLE);
        ImageView imageView = new ImageView(getContext());
        container.addView(imageView);
        imageView.setImageResource(R.drawable.ic_service);
        TextView checkBox = new TextView(getContext());
        if (Constant.CUSTOMER_STATUS_WORKING.equals(technician.getCustomerService())) {
            checkBox.setTag("checked");
            checkBox.setBackgroundResource(R.drawable.nav_top_open);
        } else {
            checkBox.setTag(null);
            checkBox.setBackgroundResource(R.drawable.nav_top_close);
        }
        container.addView(checkBox);
        ((LinearLayout.LayoutParams) checkBox.getLayoutParams()).leftMargin = 16;
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChangingCustomerStatus) {
                    return;
                }
                isChangingCustomerStatus = true;
                if (checkBox.getTag() == null) {
                    Observable<BaseBean> observable = XmdNetwork.getInstance()
                            .getService(NetService.class)
                            .changeCustomerStatus(Constant.CUSTOMER_STATUS_WORKING);
                    changeCustomerStatusSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
                        @Override
                        public void onCallbackSuccess(BaseBean result) {
                            isChangingCustomerStatus = false;
                            checkBox.setTag("checked");
                            checkBox.setBackgroundResource(R.drawable.nav_top_open);
                        }

                        @Override
                        public void onCallbackError(Throwable e) {
                            isChangingCustomerStatus = false;
                            XToast.show("修改状态失败！");
                        }
                    });
                } else {
                    new AlertDialogBuilder(getContext())
                            .setTitle("提示")
                            .setMessage("关闭后，不再接收新客服消息，不影响其他消息的接收，是否确定关闭？")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Observable<BaseBean> observable = XmdNetwork.getInstance()
                                            .getService(NetService.class)
                                            .changeCustomerStatus(Constant.CUSTOMER_STATUS_REST);
                                    changeCustomerStatusSubscription = XmdNetwork.getInstance()
                                            .request(observable, new NetworkSubscriber<BaseBean>() {
                                                @Override
                                                public void onCallbackSuccess(BaseBean result) {
                                                    isChangingCustomerStatus = false;
                                                    checkBox.setTag(null);
                                                    checkBox.setBackgroundResource(R.drawable.nav_top_close);
                                                }

                                                @Override
                                                public void onCallbackError(Throwable e) {
                                                    isChangingCustomerStatus = false;
                                                    XToast.show("修改状态失败！");
                                                }
                                            });
                                }
                            })
                            .show();
                }
            }
        });
    }
}
