package com.xmd.chat.viewmodel;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.chat.NetService;
import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowNewOrderBinding;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.NewOrderChatMessage;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import rx.Observable;


/**
 * Created by mo on 17-7-1.
 * 新订单消息
 */

public class ChatRowViewModelNewOrder extends ChatRowViewModel {
    private NewOrderChatMessage orderChatMessage;
    private ChatRowNewOrderBinding binding;
    public ObservableBoolean inProcess = new ObservableBoolean();

    public ChatRowViewModelNewOrder(ChatMessage chatMessage) {
        super(chatMessage);
        orderChatMessage = (NewOrderChatMessage) chatMessage;
    }

    public static View createView(ViewGroup parent) {
        ChatRowNewOrderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_row_new_order, parent, false);
        return binding.getRoot();
    }

    @Override
    public ViewDataBinding onBindView(View view) {
        binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
        return binding;
    }

    @Override
    public void onUnbindView() {

    }

    public CharSequence getMessage() {
        return chatMessage.getContentText();
    }

    public String getInnerProcessed() {
        return chatMessage.getInnerProcessed();
    }

    //拒绝订单
    public void onClickRefuseOrder(View v) {

        new AlertDialog.Builder(v.getContext(), R.style.AppTheme_AlertDialog)
                .setMessage("确定拒绝此订单？")
                .setCancelable(false)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (inProcess.get()) {
                            return;
                        }
                        inProcess.set(true);
                        Observable<BaseBean> observable = XmdNetwork.getInstance()
                                .getService(NetService.class)
                                .manageOrder("reject", orderChatMessage.getOrderId());
                        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
                            @Override
                            public void onCallbackSuccess(BaseBean result) {
                                inProcess.set(false);
                                chatMessage.setInnerProcessed("已拒绝");
                                binding.setData(ChatRowViewModelNewOrder.this);
                                binding.executePendingBindings();
                            }

                            @Override
                            public void onCallbackError(Throwable e) {
                                inProcess.set(false);
                                XToast.show("操作失败：" + e.getMessage());
                                if (e.getMessage() != null && e.getMessage().contains("已处理")) {
                                    chatMessage.setInnerProcessed("已处理");
                                    binding.setData(ChatRowViewModelNewOrder.this);
                                    binding.executePendingBindings();
                                }
                            }
                        });
                    }
                })
                .create()
                .show();
    }

    //接受订单
    public void onClickAcceptOrder(View v) {
        if (inProcess.get()) {
            return;
        }
        inProcess.set(true);
        Observable<BaseBean> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .manageOrder("accept", orderChatMessage.getOrderId());
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                inProcess.set(false);
                chatMessage.setInnerProcessed("已授受");
                binding.setData(ChatRowViewModelNewOrder.this);
                binding.executePendingBindings();
            }

            @Override
            public void onCallbackError(Throwable e) {
                inProcess.set(false);
                XToast.show("操作失败：" + e.getMessage());
                if (e.getMessage() != null && e.getMessage().contains("已处理")) {
                    chatMessage.setInnerProcessed("已处理");
                    binding.setData(ChatRowViewModelNewOrder.this);
                    binding.executePendingBindings();
                }
            }
        });
    }
}
