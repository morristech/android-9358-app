package com.xmd.chat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.View;

import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.Constants;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.appointment.AppointmentData;
import com.xmd.appointment.AppointmentEvent;
import com.xmd.appointment.beans.Technician;
import com.xmd.chat.beans.FastReplySetting;
import com.xmd.chat.beans.Location;
import com.xmd.chat.event.ChatUmengStatisticsEvent;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.OrderChatMessage;
import com.xmd.chat.message.RewardChatMessage;
import com.xmd.chat.view.ChatActivity;
import com.xmd.chat.view.ShareListActivity;
import com.xmd.chat.view.SubmenuEmojiFragment;
import com.xmd.chat.view.SubmenuFastReplyFragment;
import com.xmd.chat.view.SubmenuMoreFragment;
import com.xmd.image_tool.ImageTool;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;
import com.xmd.permission.CheckBusinessPermission;
import com.xmd.permission.PermissionConstants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

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
        createEmojiMenu(activity, editable);
        createFastReplyMenu(activity, remoteUser);
        createCouponMenu(activity, remoteUser);
        createAppointmentMenu(activity, remoteUser);

        //创建更多菜单
        createMoreRequestOrderMenu(activity, remoteUser);
        createMoreRequestRewardMenu(activity, remoteUser);
        createMoreMarketingMenu(activity, remoteUser);
        createMoreJournalMenu(activity, remoteUser);
        createMoreMallMenu(activity, remoteUser);
        createMoreDiceGameMenu(activity, remoteUser);
        createMoreLocationMenu(activity, remoteUser);
        createMoreInvitationMenu(activity, remoteUser);
        createMoreMenu(activity);

        return menus;
    }


    public ChatMenu findMenuByName(String name) {
        for (ChatMenu menu : menus) {
            if (name.equals(menu.getName())) {
                return menu;
            }
        }
        return null;
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
    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_PICTURE)
    public void createPictureMenu(final BaseActivity activity, final User remoteUser) {
        menus.add(new ChatMenu(activity, R.drawable.chat_menu_image, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageTool.onlyPick(true).start(activity, new ImageTool.ResultListener() {
                    @Override
                    public void onResult(String s, Uri uri, Bitmap bitmap) {
                        if (uri != null) {
                            ChatMessageManager.getInstance()
                                    .sendImageMessage(remoteUser.getChatId(), uri.getPath());
                        }
                    }
                });
                EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_PICTURE_CLICK));
            }
        }, null));

    }

    //创建表情菜单
    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_EMOJI)
    public void createEmojiMenu(final BaseActivity activity, Editable editable) {
        List<Fragment> emojiFragmentList = new ArrayList<>();
        SubmenuEmojiFragment submenuEmojiFragment = new SubmenuEmojiFragment();
        submenuEmojiFragment.setOutputView(editable);
        emojiFragmentList.add(submenuEmojiFragment);
        menus.add(new ChatMenu(activity, R.drawable.chat_menu_emoji, null, emojiFragmentList));
    }

    //创建快捷回复菜单
    @CheckBusinessPermission(PermissionConstants.MESSAGE_FAST_REPLY)
    public void createFastReplyMenu(final BaseActivity activity, final User remoteUser) {
        menus.add(new ChatMenu(activity, "快捷回复", R.drawable.chat_menu_fast_reply, null, createFastReplySubMenu(remoteUser.getChatId())));
    }

    public List<Fragment> createFastReplySubMenu(String remoteChatId) {
        FastReplySetting setting = ChatSettingManager.getInstance().getFastReplySetting();
        if (setting == null) {
            ChatSettingManager.getInstance().loadFastReply(new Callback<FastReplySetting>() {
                @Override
                public void onResponse(FastReplySetting setting, Throwable error) {
                    if (error != null || setting == null) {
                        XToast.show("加载快捷回复失败：" + error.getMessage());
                        return;
                    }
                }
            });
            return new ArrayList<>();
        }
        List<Fragment> fragmentList = new ArrayList<>();
        int maxSize = setting.data.size();
        for (int i = 0; i < maxSize; i += 5) {
            SubmenuFastReplyFragment fragment = new SubmenuFastReplyFragment();
            fragment.setData(remoteChatId, setting.data.subList(i, i + 6 > maxSize ? maxSize : i + 6));
            fragmentList.add(fragment);
        }
        return fragmentList;
    }

    //创建发券菜单
    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_COUPON)
    public void createCouponMenu(final BaseActivity activity, final User remoteUser) {
        menus.add(new ChatMenu(activity, R.drawable.chat_menu_coupon, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowDeliverCouponView(activity, remoteUser);
                EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_COUPON_CLICK));
            }

        }, null));

    }

    //显示发券界面，因为当前两个app都已实现
    public void onShowDeliverCouponView(Activity activity, User remoteUser) {

    }

    //创建预约菜单
    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_ORDER)
    public void createAppointmentMenu(final BaseActivity activity, final User remoteUser) {
        menus.add(new ChatMenu(activity, R.drawable.chat_menu_appointment, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInSubmitAppointment) {
                    XToast.show("正在处理，请稍后");
                    return;
                }
                isInSubmitAppointment = true;
                AppointmentData data = new AppointmentData();
                data.setCustomerChatId(remoteUser.getChatId());
                data.setCustomerId(remoteUser.getId());
                data.setCustomerName(remoteUser.getName());
                User user = ChatAccountManager.getInstance().getUser();
                boolean fixTech = user.getUserRoles() != null && !user.getUserRoles().contains(User.ROLE_FLOOR);
                if (fixTech) {
                    Technician tech = new Technician();
                    tech.setId(user.getId());
                    tech.setAvatarUrl(user.getAvatar());
                    tech.setName(user.getName());
                    data.setTechnician(tech);
                    data.setFixTechnician(true);
                }
                EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_SHOW, TAG, data));
                EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_BOOK_CLICK));
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
                if (OrderChatMessage.isFreeAppointment(event.getData(), null)) {
                    //免费预约，发送确认消息
                    isInSubmitAppointment = false;
                    ChatMessageManager.getInstance().sendMessage(
                            OrderChatMessage.create(
                                    event.getData().getCustomerChatId(),
                                    ChatMessage.MSG_TYPE_ORDER_CONFIRM,
                                    event.getData()));
                } else {
                    //付费预约，先生成订单，然后发送确认消息
 //                   EventBusSafeRegister.register(this);
                    EventBus.getDefault().post(new AppointmentEvent(AppointmentEvent.CMD_SUBMIT, TAG, event.getData()));
                }
            } else {
                isInSubmitAppointment = false;
            }
        } else if (event.getCmd() == AppointmentEvent.CMD_SUBMIT_RESULT) {
            XLogger.i(">>>", "生成订单成功，发送预约消息");
            isInSubmitAppointment = false;
            if (event.getData().isSubmitSuccess()) {
                //生成订单成功，发送确认消息
                ChatMessageManager.getInstance().sendMessage(
                        OrderChatMessage.create(
                                event.getData().getCustomerChatId(),
                                ChatMessage.MSG_TYPE_ORDER_CONFIRM,
                                event.getData()));
            } else {
                XToast.show("生成订单失败：" + event.getData().getSubmitErrorString());
            }
        }
    }

    //创建更多菜单
    public void createMoreMenu(final BaseActivity activity) {
        if (moreMenus.size() == 0) {
            XToast.show("暂无更多菜单");
            return;
        }
        List<Fragment> fragmentList = new ArrayList<>();
        SubmenuMoreFragment fragment = new SubmenuMoreFragment();
        fragment.setData(moreMenus);
        fragmentList.add(fragment);
        menus.add(new ChatMenu(activity, R.drawable.chat_menu_more, null, fragmentList));
    }

    //创建更多-位置菜单
    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_LOCATION)
    public void createMoreLocationMenu(final BaseActivity activity, final User remoteUser) {
        moreMenus.add(new ChatMenu(activity, "会所位置", R.drawable.chat_menu_location, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatSettingManager.getInstance().loadClubLocation(false, new Callback<Location>() {
                    @Override
                    public void onResponse(Location result, Throwable error) {
                        if (result != null) {
                            ChatMessageManager.getInstance()
                                    .sendLocationMessage(remoteUser, result);
                        }
                    }
                });
                EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_LOCATION_CLICK));
            }
        }, null));
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE_INVITE_GIFT)
    public void createMoreInvitationMenu(final BaseActivity activity, final User remoteUser) {
        moreMenus.add(new ChatMenu(activity, "邀请有礼", R.drawable.chat_menu_invitation, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<BaseBean> observable = XmdNetwork.getInstance()
                        .getService(NetService.class)
                        .getInviteEnable(UserInfoServiceImpl.getInstance().getCurrentUser().getClubId());
                XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
                    @Override
                    public void onCallbackSuccess(BaseBean result) {
                        if ((Boolean) result.getRespData()) {
                            ChatMessageManager.getInstance().sendInviteGiftMessage(remoteUser.getChatId());
                            EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_INVITATION));
                        } else {
                            XToast.show("当前没有进行中的活动");
                        }

                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        XToast.show(e.getLocalizedMessage());
                    }
                });

            }
        }, null));
    }

    //创建更多-求预约菜单
    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_ORDER_REQUEST)
    public void createMoreRequestOrderMenu(final BaseActivity activity, final User remoteUser) {
        moreMenus.add(new ChatMenu(activity, "求预约", R.drawable.chat_menu_order_request, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity, R.style.AppTheme_AlertDialog)
                        .setMessage("确定发送求预约消息?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ChatMessage chatMessage = OrderChatMessage.createRequestOrderMessage(remoteUser.getChatId());
                                ChatMessageManager.getInstance().sendMessage(chatMessage);
                                EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_BOOK_SEND));
                            }
                        })
                        .create()
                        .show();
                EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_BOOKED_CLICK));
            }
        }, null));
    }

    //创建更多-求打赏菜单
    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_REWARD)
    public void createMoreRequestRewardMenu(final BaseActivity activity, final User remoteUser) {
        moreMenus.add(new ChatMenu(activity, "求打赏", R.drawable.chat_menu_request_reward, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity, R.style.AppTheme_AlertDialog)
                        .setMessage("确定发送求打赏消息?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ChatMessage chatMessage = RewardChatMessage.createRequestRewardMessage(remoteUser.getChatId());
                                ChatMessageManager.getInstance().sendMessage(chatMessage);
                                EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_REWARDED_SEND));
                            }
                        })
                        .create()
                        .show();
                EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_REWARDED_CLICK));
            }
        }, null));
    }

    //创建更多-电子期刊菜单
    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_JOURNAL)
    public void createMoreJournalMenu(final BaseActivity activity, final User remoteUser) {
        moreMenus.add(new ChatMenu(activity, "电子期刊", R.drawable.chat_menu_journal, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showLoading();
                ShareDataManager.getInstance().loadJournalList(new NetworkSubscriber<Void>() {
                    @Override
                    public void onCallbackSuccess(Void v) {
                        activity.hideLoading();
                        Intent intent = new Intent(activity, ShareListActivity.class);
                        intent.putExtra(Constants.EXTRA_CHAT_ID, remoteUser.getChatId());
                        ArrayList<String> dataTypeList = new ArrayList<>();
                        dataTypeList.add(ShareDataManager.DATA_TYPE_JOURNAL);
                        intent.putStringArrayListExtra(ShareListActivity.EXTRA_DATA_TYPE_LIST, dataTypeList);
                        intent.putExtra(ShareListActivity.ACTIVITY_TITLE, "电子期刊");
                        activity.startActivity(intent);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        activity.hideLoading();
                        XToast.show("加载数据失败：" + e.getMessage());
                    }
                });
                EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_JOURNAL_CLICK));
            }
        }, null));
    }

    //创建更多-商城菜单
    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_MALL_INFO)
    public void createMoreMallMenu(final BaseActivity activity, final User remoteUser) {
        moreMenus.add(new ChatMenu(activity, "特惠商城", R.drawable.chat_menu_mall, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showLoading();
                ShareDataManager.getInstance().loadOnceCardList(new NetworkSubscriber<Void>() {
                    @Override
                    public void onCallbackSuccess(Void v) {
                        activity.hideLoading();
                        Intent intent = new Intent(activity, ShareListActivity.class);
                        intent.putExtra(Constants.EXTRA_CHAT_ID, remoteUser.getChatId());
                        ArrayList<String> dataTypeList = new ArrayList<>();
                        dataTypeList.add(ShareDataManager.DATA_TYPE_ONCE_CARD_SINGLE);
                        dataTypeList.add(ShareDataManager.DATA_TYPE_ONCE_CARD_MIX);
                        dataTypeList.add(ShareDataManager.DATA_TYPE_ONCE_CARD_CREDIT);
                        intent.putStringArrayListExtra(ShareListActivity.EXTRA_DATA_TYPE_LIST, dataTypeList);
                        intent.putExtra(ShareListActivity.ACTIVITY_TITLE, "特惠商城");
                        activity.startActivity(intent);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        activity.hideLoading();
                        XToast.show("加载数据失败：" + e.getMessage());
                    }
                });
                EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_MALL_CLICK));
            }
        }, null));
    }

    //创建更多-营销活动菜单
    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_ACTIVITY)
    public void createMoreMarketingMenu(final BaseActivity activity, final User remoteUser) {
        moreMenus.add(new ChatMenu(activity, "营销活动", R.drawable.chat_menu_marketing, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showLoading();
                ShareDataManager.getInstance().loadMarketingList(new NetworkSubscriber<Void>() {
                    @Override
                    public void onCallbackSuccess(Void v) {
                        activity.hideLoading();
                        Intent intent = new Intent(activity, ShareListActivity.class);
                        intent.putExtra(Constants.EXTRA_CHAT_ID, remoteUser.getChatId());
                        intent.putStringArrayListExtra(ShareListActivity.EXTRA_DATA_TYPE_LIST,
                                (ArrayList<String>) ShareDataManager.getInstance().getMarketingDataTypeList());
                        intent.putExtra(ShareListActivity.ACTIVITY_TITLE, "营销活动");
                        activity.startActivity(intent);

                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        activity.hideLoading();
                        XToast.show("加载数据失败：" + e.getMessage());
                    }
                });
                EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_ACTIVITY_CLICK));
            }
        }, null));
    }


    //创建更多-营销活动菜单
    @CheckBusinessPermission(PermissionConstants.MESSAGE_PLAY_CREDIT_GAME)
    public void createMoreDiceGameMenu(final BaseActivity activity, final User remoteUser) {
//        moreMenus.add(new ChatMenu(activity, "积分游戏", R.drawable.chat_menu_game, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playDiceGame(activity.getSupportFragmentManager(), remoteUser.getId());
//            }
//        }, null));
    }

//    public void playDiceGame(FragmentManager fm, String remoteUserId) {
//        FragmentTransaction ft = fm.beginTransaction();
//        Fragment prev = fm.findFragmentByTag("fragment_dice_game_setting");
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        DialogFragment current = DiceGameSettingFragment.newInstance(remoteUserId);
//        current.show(ft, "fragment_dice_game_setting");
//    }
}
