package com.xmd.chat;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.beans.Location;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.view.ChatActivity;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.Subscribe;

import rx.Observable;

/**
 * Created by mo on 17-6-21.
 * 聊天模块
 */

public class XmdChat {
    public final static String TAG = "XmdChat";
    private static final XmdChat ourInstance = new XmdChat();

    public static XmdChat getInstance() {
        return ourInstance;
    }

    private XmdChat() {
    }


    private Context context;
    private MenuFactory menuFactory;
    private Location location;

    public void init(Context context, boolean debug) {
        XLogger.i("---------聊天系统初始化---------------");
        context = context.getApplicationContext();
        this.context = context;

        EMOptions options = new EMOptions();
        options.setRequireAck(true);
        options.setRequireDeliveryAck(true);
        EMClient.getInstance().init(context, options);
        EMClient.getInstance().setDebugMode(debug);

        EMClient.getInstance().chatManager().loadAllConversations();
        EMClient.getInstance().groupManager().loadAllGroups();

        //初始化用户信息
        EMClient.getInstance().chatManager().loadAllConversations();
        for (EMConversation conversation : EMClient.getInstance().chatManager().getAllConversations().values()) {
            EMMessage emMessage = conversation.getLatestMessageFromOthers();
            if (emMessage != null) {
                ChatMessage chatMessage = ChatMessageFactory.get(emMessage);
                if (chatMessage.getUserId() != null) {
                    User user = new User(chatMessage.getUserId());
                    user.setName(chatMessage.getUserName());
                    user.setAvatar(chatMessage.getUserAvatar());
                    user.setChatId(chatMessage.getEmMessage().getFrom());
                    UserInfoServiceImpl.getInstance().saveUser(user);
                }
            }
            conversation.clear();
        }

        AccountManager.getInstance().init();
        ConversationManager.getInstance().init();
        MessageManager.getInstance().init();
        setMenuFactory(new MenuFactory());

        EventBusSafeRegister.register(this);
    }

    public MenuFactory getMenuFactory() {
        return menuFactory;
    }

    public void setMenuFactory(MenuFactory menuFactory) {
        this.menuFactory = menuFactory;
    }

    public void getClubLocation(final Callback<Location> callback) {
        if (location != null) {
            callback.onResponse(location, null);
            return;
        }
        Observable<BaseBean<Location>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .getClubLocation();
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<Location>>() {
            @Override
            public void onCallbackSuccess(BaseBean<Location> result) {
                location = result.getRespData();
                callback.onResponse(location, null);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onResponse(null, e);
            }
        });
    }

    @Subscribe
    public void onStartChat(EventStartChatActivity event) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_CHAT_ID, event.getRemoteChatId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
