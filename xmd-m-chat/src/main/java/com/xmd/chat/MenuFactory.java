package com.xmd.chat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.View;

import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.Constants;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.user.User;
import com.xmd.appointment.AppointmentData;
import com.xmd.appointment.AppointmentEvent;
import com.xmd.chat.beans.Location;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.OrderChatMessage;
import com.xmd.chat.order.OrderChatManager;
import com.xmd.chat.view.ChatActivity;
import com.xmd.chat.view.ShareListActivity;
import com.xmd.chat.view.SubmenuEmojiFragment;
import com.xmd.chat.view.SubmenuFastReplyFragment;
import com.xmd.chat.view.SubmenuMoreFragment;
import com.xmd.image_tool.ImageTool;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo on 17-7-6.
 * 创建各类聊天菜单
 */

public class MenuFactory {
    private static final String TAG = "ChatActivity";
    private ImageTool imageTool = new ImageTool();
    private List<ChatMenu> menus = new ArrayList<>();
    private List<ChatMenu> moreMenus = new ArrayList<>();

    private boolean isInSubmitAppointment;

    //创建菜单资源
    public List<ChatMenu> createMenuList(ChatActivity activity, User remoteUser, Editable editable) {

        //创建普通菜单
        createPictureMenu(activity, remoteUser);
        createEmojiMenu(editable);
        createFastReplyMenu(remoteUser);
        createCouponMenu(activity, remoteUser);
        createAppointmentMenu(remoteUser);

        //创建更多菜单
        createMoreRequestOrderMenu(activity, remoteUser);
        createMoreJournalMenu(activity, remoteUser);
        createMoreMallMenu(activity, remoteUser);
        createMoreLocationMenu(remoteUser);
        createMoreMenu();

        return menus;
    }

    //清除菜单资源
    public void cleanMenus() {
        menus.clear();
        moreMenus.clear();
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return imageTool.onActivityResult(requestCode, resultCode, data);
    }

    //创建图片菜单
    public void createPictureMenu(final Activity activity, final User remoteUser) {
        menus.add(new ChatMenu(R.drawable.chat_menu_image, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageTool.onlyPick(true).start(activity, new ImageTool.ResultListener() {
                    @Override
                    public void onResult(String s, Uri uri, Bitmap bitmap) {
                        MessageManager.getInstance()
                                .sendImageMessage(remoteUser.getChatId(), uri.getPath());
                    }
                });
            }
        }, null));
    }

    //创建表情菜单
    public void createEmojiMenu(Editable editable) {
        List<Fragment> emojiFragmentList = new ArrayList<>();
        SubmenuEmojiFragment submenuEmojiFragment = new SubmenuEmojiFragment();
        submenuEmojiFragment.setOutputView(editable);
        emojiFragmentList.add(submenuEmojiFragment);
        menus.add(new ChatMenu(R.drawable.chat_menu_emoji, null, emojiFragmentList));
    }

    //创建快捷回复菜单
    public void createFastReplyMenu(final User remoteUser) {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> messageList1 = new ArrayList<>();
        messageList1.add("很高兴能为您解决问题，客官给个好评哦，么么哒");
        messageList1.add("不好意思，现在暂时回答不了您的问题，稍后回复您");
        messageList1.add("店里搞活动啦！送您个优惠券，记得过来啊！");
        messageList1.add("好久不见了，有空过来玩玩啊！");
        messageList1.add("约定了，记得准时来，不见不散");
        List<String> messageList2 = new ArrayList<>();
        messageList2.add("客官，打赏几个铜板鼓励鼓励嘛！");
        messageList2.add("这个月完成不了任务了，大侠可否帮忙点个钟？");
        messageList2.add("不好意思，现在暂时回答不了您的问题，稍后回复您～");
        messageList2.add("方便的话麻烦留一个联系方式，以后常联系～");
        messageList2.add("多谢客官打赏");
        SubmenuFastReplyFragment fragment1 = new SubmenuFastReplyFragment();
        fragment1.setData(remoteUser.getChatId(), messageList1);
        fragmentList.add(fragment1);
        SubmenuFastReplyFragment fragment2 = new SubmenuFastReplyFragment();
        fragment2.setData(remoteUser.getChatId(), messageList2);
        fragmentList.add(fragment2);
        menus.add(new ChatMenu(R.drawable.chat_menu_fast_reply, null, fragmentList));
    }

    //创建发券菜单
    public void createCouponMenu(final Activity activity, final User remoteUser) {
        menus.add(new ChatMenu(R.drawable.chat_menu_coupon, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowDeliverCouponView(activity, remoteUser);
            }
        }, null));
    }

    //显示发券界面，因为当前两个app都已实现
    public void onShowDeliverCouponView(Activity activity, User remoteUser) {

    }

    //创建预约菜单
    public void createAppointmentMenu(final User remoteUser) {
        menus.add(new ChatMenu(R.drawable.chat_menu_appointment, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInSubmitAppointment) {
                    XToast.show("正在处理，请稍后");
                    return;
                }
                AppointmentData data = new AppointmentData();
                data.setCustomerChatId(remoteUser.getChatId());
                data.setCustomerId(remoteUser.getId());
                data.setCustomerName(remoteUser.getName());
//                boolean fixTech = technician.getRoles() != null && !technician.getRoles().contains(User.ROLE_FLOOR);
//                if (fixTech) {
//                    Technician tech = new Technician();
//                    tech.setId(technician.getUserId());
//                    tech.setAvatarUrl(technician.getAvatarUrl());
//                    tech.setName(technician.getNickName());
//                    data.setTechnician(tech);
//                    data.setFixTechnician(true);
//                }
                EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_SHOW, TAG, data));
            }
        }, null));
    }

    //处理预约菜单事件
    public void processAppointmentEvent(AppointmentEvent event) {
        if (!TAG.equals(event.getTag())) {
            return;
        }
        if (event.getCmd() == AppointmentEvent.CMD_HIDE) {
            if (event.getData() != null) {
                if (OrderChatManager.isFreeAppointment(event.getData(), null)) {
                    //免费预约，发送确认消息
                    isInSubmitAppointment = false;
                    MessageManager.getInstance().sendMessage(
                            OrderChatManager.createMessage(
                                    event.getData().getCustomerChatId(),
                                    ChatMessage.MSG_TYPE_ORDER_CONFIRM,
                                    event.getData()));
                } else {
                    //付费预约，先生成订单，然后发送确认消息
                    EventBusSafeRegister.register(this);
                    EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_SUBMIT, TAG, event.getData()));
                }
            } else {
                isInSubmitAppointment = false;
            }
        } else if (event.getCmd() == AppointmentEvent.CMD_SUBMIT_RESULT) {
            isInSubmitAppointment = false;
            if (event.getData().isSubmitSuccess()) {
                //生成订单成功，发送确认消息
                MessageManager.getInstance().sendMessage(
                        OrderChatManager.createMessage(
                                event.getData().getCustomerChatId(),
                                ChatMessage.MSG_TYPE_ORDER_CONFIRM,
                                event.getData()));
            } else {
                XToast.show("生成订单失败：" + event.getData().getSubmitErrorString());
            }
        }
    }

    //创建更多菜单
    public void createMoreMenu() {
        if (moreMenus.size() == 0) {
            return;
        }
        List<Fragment> fragmentList = new ArrayList<>();
        SubmenuMoreFragment fragment = new SubmenuMoreFragment();
        fragment.setData(moreMenus);
        fragmentList.add(fragment);
        menus.add(new ChatMenu(R.drawable.chat_menu_more, null, fragmentList));
    }

    //创建更多-位置菜单
    public void createMoreLocationMenu(final User remoteUser) {
        moreMenus.add(new ChatMenu("会所位置", R.drawable.chat_menu_location, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmdChat.getInstance().getClubLocation(new Callback<Location>() {
                    @Override
                    public void onResponse(Location result, Throwable error) {
                        if (result != null) {
                            MessageManager.getInstance()
                                    .sendLocationMessage(remoteUser, result);
                        }
                    }
                });
            }
        }, null));
    }

    //创建更多-求预约菜单
    public void createMoreRequestOrderMenu(final Context context, final User remoteUser) {
        moreMenus.add(new ChatMenu("求预约", R.drawable.chat_menu_order_request, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context, R.style.AppTheme_AlertDialog)
                        .setMessage("确定发送求预约消息?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ChatMessage chatMessage = OrderChatMessage.createRequestOrderMessage(remoteUser.getChatId());
                                MessageManager.getInstance().sendMessage(chatMessage);
                            }
                        })
                        .create()
                        .show();
            }
        }, null));
    }

    //创建更多-电子期刊菜单
    public void createMoreJournalMenu(final Activity activity, final User remoteUser) {
        moreMenus.add(new ChatMenu("电子期刊", R.drawable.chat_menu_location, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDataManager.getInstance().loadJournalList(new NetworkSubscriber<Void>() {
                    @Override
                    public void onCallbackSuccess(Void v) {
                        Intent intent = new Intent(activity, ShareListActivity.class);
                        intent.putExtra(Constants.EXTRA_CHAT_ID, remoteUser.getChatId());
                        ArrayList<String> dataTypeList = new ArrayList<>();
                        dataTypeList.add(ShareDataManager.DATA_TYPE_JOURNAL);
                        intent.putStringArrayListExtra(ShareListActivity.EXTRA_DATA_TYPE_LIST, dataTypeList);
                        activity.startActivity(intent);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        XToast.show("加载数据失败：" + e.getMessage());
                    }
                });
            }
        }, null));
    }

    //创建更多-商城菜单
    public void createMoreMallMenu(final Activity activity, final User remoteUser) {
        moreMenus.add(new ChatMenu("特惠商城", R.drawable.chat_menu_location, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDataManager.getInstance().loadOnceCardList(new NetworkSubscriber<Void>() {
                    @Override
                    public void onCallbackSuccess(Void v) {
                        Intent intent = new Intent(activity, ShareListActivity.class);
                        intent.putExtra(Constants.EXTRA_CHAT_ID, remoteUser.getChatId());
                        ArrayList<String> dataTypeList = new ArrayList<>();
                        dataTypeList.add(ShareDataManager.DATA_TYPE_ONCE_CARD_SINGLE);
                        dataTypeList.add(ShareDataManager.DATA_TYPE_ONCE_CARD_MIX);
                        dataTypeList.add(ShareDataManager.DATA_TYPE_ONCE_CARD_CREDIT);
                        intent.putStringArrayListExtra(ShareListActivity.EXTRA_DATA_TYPE_LIST, dataTypeList);
                        activity.startActivity(intent);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        XToast.show("加载数据失败：" + e.getMessage());
                    }
                });
            }
        }, null));
    }
}
