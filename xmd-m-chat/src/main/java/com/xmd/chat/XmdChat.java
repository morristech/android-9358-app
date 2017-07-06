package com.xmd.chat;

import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.message.ChatMessage;

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
    }

    public MenuFactory getMenuFactory() {
        return menuFactory;
    }

    public void setMenuFactory(MenuFactory menuFactory) {
        this.menuFactory = menuFactory;
    }
}
