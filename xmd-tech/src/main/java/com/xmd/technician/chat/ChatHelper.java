package com.xmd.technician.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.EMLog;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.ChatMessage;
import com.xmd.technician.R;
import com.xmd.technician.chat.controller.ChatUI;
import com.xmd.technician.chat.db.ChatDBManager;
import com.xmd.technician.chat.db.UserDao;
import com.xmd.technician.chat.event.EventUnreadMessageCount;
import com.xmd.technician.chat.event.ReceiveMessage;
import com.xmd.technician.chat.model.ChatModel;
import com.xmd.technician.chat.model.EaseNotifier;
import com.xmd.technician.chat.receiver.CallReceiver;
import com.xmd.technician.chat.utils.EaseCommonUtils;
import com.xmd.technician.chat.utils.PreferenceManager;
import com.xmd.technician.chat.utils.UserUtils;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.Utils;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.window.MainActivity;
import com.xmd.technician.window.TechChatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Lhj on 17-4-6.
 */

public class ChatHelper {
    /**
     * data sync listener
     */
    public interface DataSyncListener {
        void onSyncComplete(boolean success);
    }

    protected static final String TAG = "ChatHelper";

    private ChatUI easeUI;

    /**
     * EMEventListener
     */
    protected EMMessageListener messageListener = null;

    private Map<String, ChatUser> contactList;

    private static ChatHelper instance = null;

    private ChatModel ChatModel = null;
    /**
     * sync blacklist status listener
     */
    private List<DataSyncListener> syncBlackListListeners;

    private String username;
    private Context appContext;
    private CallReceiver callReceiver;
    private UserDao userDao;
    private LocalBroadcastManager broadcastManager;

    private boolean isGroupAndContactListenerRegisted;

    private ChatHelper() {
    }

    public synchronized static ChatHelper getInstance() {
        if (instance == null) {
            instance = new ChatHelper();
        }
        return instance;
    }

    /**
     * init helper
     *
     * @param context application context
     */
    public void init(Context context) {
        ChatModel = new ChatModel(context);
        EMOptions options = initChatOptions();
        //use default options if options is null
        if (ChatUI.getInstance().init(context, options)) {
            appContext = context;
            //调试模式时为true，release模式时为false
            EMClient.getInstance().setDebugMode(true);
            easeUI = ChatUI.getInstance();
            setEaseUIProviders();
            //initialize preference manager
            PreferenceManager.init(context);
            //initialize profile manager
            //getUserProfileManager().init(context);

            // TODO: set Call options
            // min video kbps
            int minBitRate = PreferenceManager.getInstance().getCallMinVideoKbps();
            if (minBitRate != -1) {
                EMClient.getInstance().callManager().getCallOptions().setMinVideoKbps(minBitRate);
            }

            // max video kbps
            int maxBitRate = PreferenceManager.getInstance().getCallMaxVideoKbps();
            if (maxBitRate != -1) {
                EMClient.getInstance().callManager().getCallOptions().setMaxVideoKbps(maxBitRate);
            }

            // max frame rate
            int maxFrameRate = PreferenceManager.getInstance().getCallMaxFrameRate();
            if (maxFrameRate != -1) {
                EMClient.getInstance().callManager().getCallOptions().setMaxVideoFrameRate(maxFrameRate);
            }

            // audio sample rate
            int audioSampleRate = PreferenceManager.getInstance().getCallAudioSampleRate();
            if (audioSampleRate != -1) {
                EMClient.getInstance().callManager().getCallOptions().setAudioSampleRate(audioSampleRate);
            }

            /**
             * This function is only meaningful when your app need recording
             * If not, remove it.
             * This function need be called before the video stream started, so we set it in onCreate function.
             * This method will set the preferred video record encoding codec.
             * Using default encoding format, recorded file may not be played by mobile player.
             */
            //EMClient.getInstance().callManager().getVideoCallHelper().setPreferMovFormatEnable(true);

            // resolution
            String resolution = PreferenceManager.getInstance().getCallBackCameraResolution();
            if (resolution.equals("")) {
                resolution = PreferenceManager.getInstance().getCallFrontCameraResolution();
            }
            String[] wh = resolution.split("x");
            if (wh.length == 2) {
                try {
                    EMClient.getInstance().callManager().getCallOptions().setVideoResolution(new Integer(wh[0]).intValue(), new Integer(wh[1]).intValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // enabled fixed sample rate
            boolean enableFixSampleRate = PreferenceManager.getInstance().isCallFixedVideoResolution();
            EMClient.getInstance().callManager().getCallOptions().enableFixedVideoResolution(enableFixSampleRate);

            // Offline call push
            EMClient.getInstance().callManager().getCallOptions().setIsSendPushIfOffline(getModel().isPushCall());

            setGlobalListeners();
            broadcastManager = LocalBroadcastManager.getInstance(appContext);
            initDbDao();
            //注册登录/登出事件监听器


            //初始化用户信息,后期版本可以移除 -- FIXME
            EMClient.getInstance().chatManager().loadAllConversations();
            for (EMConversation conversation : getAllConversationList()) {
                EMMessage emMessage = conversation.getLatestMessageFromOthers();
                if (emMessage != null) {
                    ChatMessage chatMessage = new ChatMessage(emMessage);
                    User user = new User();
                    user.setId(chatMessage.getUserId());
                    user.setName(chatMessage.getUserName());
                    user.setAvatar(chatMessage.getUserAvatar());
                    user.setChatId(chatMessage.getEmMessage().getTo());
                    UserInfoServiceImpl.getInstance().saveUser(user);
                }
            }
        }
    }


    private EMOptions initChatOptions() {
        Log.d(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(true);
        // set if you need read ack
        options.setRequireAck(true);
        // set if you need delivery ack
        options.setRequireDeliveryAck(true);

        //you need apply & set your own id if you want to use google cloud messaging.
//        options.setGCMNumber("324169311137");
//        //you need apply & set your own id if you want to use Mi push notification
//        options.setMipushConfig("2882303761517426801", "5381742660801");
//        //you need apply & set your own id if you want to use Huawei push notification
//        options.setHuaweiPushAppId("10492024");

        //set custom servers, commonly used in private deployment
        if (ChatModel.isCustomServerEnable() && ChatModel.getRestServer() != null && ChatModel.getIMServer() != null) {
            options.setRestServer(ChatModel.getRestServer());
            options.setIMServer(ChatModel.getIMServer());
            if (ChatModel.getIMServer().contains(":")) {
                options.setIMServer(ChatModel.getIMServer().split(":")[0]);
                options.setImPort(Integer.valueOf(ChatModel.getIMServer().split(":")[1]));
            }
        }

        if (ChatModel.isCustomAppkeyEnabled() && ChatModel.getCutomAppkey() != null && !ChatModel.getCutomAppkey().isEmpty()) {
            options.setAppKey(ChatModel.getCutomAppkey());
        }

        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
        options.setDeleteMessagesAsExitGroup(getModel().isDeleteMessagesAsExitGroup());
        options.setAutoAcceptGroupInvitation(getModel().isAutoAcceptGroupInvitation());

        return options;
    }

    protected void setEaseUIProviders() {

        easeUI.setUserProfileProvider(new ChatUI.EaseUserProfileProvider() {
            @Override
            public ChatUser getUser(String username) {
                return getUserInfo(username);
            }
        });
        //set options
        easeUI.setSettingsProvider(new ChatUI.EaseSettingsProvider() {

            @Override
            public boolean isSpeakerOpened() {
                return ChatModel.getSettingMsgSpeaker();
            }

            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                return ChatModel.getSettingMsgVibrate();
            }

            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                return ChatModel.getSettingMsgSound();
            }

            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                if (message == null) {
                    return ChatModel.getSettingMsgNotification();
                }
                if (!ChatModel.getSettingMsgNotification()) {
                    return false;
                } else {
                    String chatUsename = null;
                    List<String> notNotifyIds = null;
                    // get user or group id which was blocked to show message notifications
                    if (message.getChatType() == EMMessage.ChatType.Chat) {
                        chatUsename = message.getFrom();
                        notNotifyIds = ChatModel.getDisabledIds();
                    } else {
                        chatUsename = message.getTo();
                        // notNotifyIds = ChatModel.getDisabledGroups();
                    }

                    if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        //set emoji icon provider
        easeUI.setEmojiconInfoProvider(new ChatUI.EaseEmojiconInfoProvider() {

            @Override
            public Emojicon getEmojiconInfo(String emojiconIdentityCode) {
                //动画表情
            /*   ChatEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
                for (Emojicon emojicon : data.getEmojiconList()) {
                    if (emojicon.getIdentityCode().equals(emojiconIdentityCode)) {
                        return emojicon;
                    }
                }*/
                return null;
            }

            @Override
            public Map<String, Object> getTextEmojiconMapping() {
                return null;
            }
        });

        //set notification options, will use default if you don't set it
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //you can update icon here
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // be used on notification bar, different text according the message type.
                String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
                if (message.getType() == EMMessage.Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                ChatUser user = getUserInfo(message.getFrom());
                if (user != null) {
                    return user.getNick() + ": " + ticker;
                } else {
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {

                return null;
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                //点击通知消息跳转
                if (!UserUtils.userExisted(message.getFrom())) {
                    ChatUser user = new ChatUser(message.getFrom());
                    user.setAvatar(message.getStringAttribute(ChatConstant.KEY_NAME, "匿名"));
                    user.setNick(message.getStringAttribute(ChatConstant.KEY_HEADER, ""));
                    if (Utils.isEmpty(message.getStringAttribute(ChatConstant.KEY_TECH_ID, ""))) {
                        user.setUserType(ChatConstant.TO_CHAT_USER_TYPE_CUSTOMER);
                    } else {
                        user.setUserType(ChatConstant.TO_CHAT_USER_TYPE_TECH);
                    }

                    saveContact(user);
                }
                Intent intent = new Intent(appContext, TechChatActivity.class);
                intent.putExtra(ChatConstant.TO_CHAT_USER_ID, message.getFrom());
                return intent;
            }
        });
    }

    EMConnectionListener connectionListener;

    /**
     * set global listener
     */
    protected void setGlobalListeners() {

        syncBlackListListeners = new ArrayList<DataSyncListener>();
        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                EMLog.d("global listener", "onDisconnect" + error);
                if (error == EMError.USER_REMOVED) {
                    onUserException(ChatConstant.ACCOUNT_REMOVED);
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    onUserException(ChatConstant.ACCOUNT_CONFLICT);
                } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                    onUserException(ChatConstant.ACCOUNT_FORBIDDEN);
                }
            }

            @Override
            public void onConnected() {

            }
        };

        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }

        //register incoming call receiver
        appContext.registerReceiver(callReceiver, callFilter);
        //register connection listener
        EMClient.getInstance().addConnectionListener(connectionListener);
        //register group and contact event listener
        registerGroupAndContactListener();
        //register message event listener
        registerMessageListener();

    }

    private void initDbDao() {
        userDao = new UserDao(appContext);
    }

    /**
     * register group and contact listener, you need register when login
     */
    public void registerGroupAndContactListener() {
        if (!isGroupAndContactListenerRegisted) {
            EMClient.getInstance().groupManager().addGroupChangeListener(new MyGroupChangeListener());
            EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
            isGroupAndContactListenerRegisted = true;
        }

    }

    /**
     * group change listener
     */
    class MyGroupChangeListener implements EMGroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {

        }

        @Override
        public void onInvitationAccepted(String groupId, String invitee, String reason) {


        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {


        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            //user is removed from group
            broadcastManager.sendBroadcast(new Intent(ChatConstant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            // group is dismissed,
            broadcastManager.sendBroadcast(new Intent(ChatConstant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applyer, String reason) {

        }

        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {

            String st4 = appContext.getString(R.string.Agreed_to_your_group_chat_application);
            // your application was accepted
            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new EMTextMessageBody(accepter + " " + st4));
            msg.setStatus(EMMessage.Status.SUCCESS);
            // save accept message
            EMClient.getInstance().chatManager().saveMessage(msg);
            // notify the accept message
            getNotifier().vibrateAndPlayTone(msg);

            broadcastManager.sendBroadcast(new Intent(ChatConstant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
            // your application was declined, we do nothing here in demo
        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            // got an invitation
            String st3 = appContext.getString(R.string.Invite_you_to_join_a_group_chat);
            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(inviter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new EMTextMessageBody(inviter + " " + st3));
            msg.setStatus(EMMessage.Status.SUCCESS);
            // save invitation as messages
            EMClient.getInstance().chatManager().saveMessage(msg);
            // notify invitation message
            getNotifier().vibrateAndPlayTone(msg);
            EMLog.d(TAG, "onAutoAcceptInvitationFromGroup groupId:" + groupId);
            broadcastManager.sendBroadcast(new Intent(ChatConstant.ACTION_GROUP_CHANAGED));
        }

        // ============================= group_reform new add api begin
        @Override
        public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
            StringBuilder sb = new StringBuilder();
            for (String member : mutes) {
                sb.append(member).append(",");
            }
            showToast("onMuterListAdded: " + sb.toString());
        }


        @Override
        public void onMuteListRemoved(String groupId, final List<String> mutes) {
            StringBuilder sb = new StringBuilder();
            for (String member : mutes) {
                sb.append(member).append(",");
            }
            showToast("onMuterListRemoved: " + sb.toString());
        }


        @Override
        public void onAdminAdded(String groupId, String administrator) {
            showToast("onAdminAdded: " + administrator);
        }

        @Override
        public void onAdminRemoved(String groupId, String administrator) {
            showToast("onAdminRemoved: " + administrator);
        }

        @Override
        public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
            showToast("onOwnerChanged new:" + newOwner + " old:" + oldOwner);
        }
        // ============================= group_reform new add api end
    }

    void showToast(final String message) {
        Message msg = Message.obtain(handler, 0, message);
        handler.sendMessage(msg);
    }

    protected android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            String str = (String) msg.obj;
            Toast.makeText(appContext, str, Toast.LENGTH_LONG).show();
        }
    };

    /***
     * 好友变化listener
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(String username) {
            // save contact
            Map<String, ChatUser> localUsers = getContactList();
            Map<String, ChatUser> toAddUsers = new HashMap<String, ChatUser>();
            ChatUser user = new ChatUser(username);

            if (!localUsers.containsKey(username)) {
                userDao.saveContact(user);
            }
            toAddUsers.put(username, user);
            localUsers.putAll(toAddUsers);

            broadcastManager.sendBroadcast(new Intent(ChatConstant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactDeleted(String username) {
            Map<String, ChatUser> localUsers = ChatHelper.getInstance().getContactList();
            localUsers.remove(username);
            userDao.deleteContact(username);
            EMClient.getInstance().chatManager().deleteConversation(username, false);
            broadcastManager.sendBroadcast(new Intent(ChatConstant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactInvited(String username, String reason) {

        }

        @Override
        public void onFriendRequestAccepted(String username) {

        }

        @Override
        public void onFriendRequestDeclined(String username) {

        }
    }

    /**
     * user met some exception: conflict, removed or forbidden
     */
    protected void onUserException(String exception) {
        EMLog.e(TAG, "onUserException: " + exception);
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(exception, true);
        appContext.startActivity(intent);
    }

    private ChatUser getUserInfo(String username) {
        ChatUser user = null;
        user = getContactList().get(username);
        if (user == null) {
            user = new ChatUser(username);
            EaseCommonUtils.setUserInitialLetter(user);
        }
        return user;
    }

    /**
     * Global listener
     * If this event already handled by an activity, you don't need handle it again
     * activityList.size() <= 0 means all activities already in background or not in Activity Stack
     */
    protected void registerMessageListener() {
        messageListener = new EMMessageListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
                    Logger.i(">>>", "接收到消息...");
                    //       RxBus.getInstance().post(new EventReceiveMessage(messages));
                    postUnReadMessageCount();

                    if (!easeUI.hasForegroundActivities()) {
                        getNotifier().onNewMsg(message);
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "receive command message");
                }
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                EMLog.d(TAG, "change:");
                EMLog.d(TAG, "change:" + change);
            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * logout
     *
     * @param unbindDeviceToken whether you need unbind your device token
     * @param callback          callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
     //   endCall();
        Log.d(TAG, "logout: " + unbindDeviceToken);
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }

    /**
     * get instance of EaseNotifier
     *
     * @return
     */
    public EaseNotifier getNotifier() {
        return easeUI.getNotifier();
    }

    public com.xmd.technician.chat.model.ChatModel getModel() {
        return (com.xmd.technician.chat.model.ChatModel) ChatModel;
    }

    /**
     * update contact list
     *
     * @param aContactList
     */
    public void setContactList(Map<String, ChatUser> aContactList) {
        if (aContactList == null) {
            if (contactList != null) {
                contactList.clear();
            }
            return;
        }

        contactList = aContactList;
    }

    /**
     * save single contact
     */
    public void saveContact(ChatUser user) {
        contactList.put(user.getUsername(), user);
        ChatModel.saveContact(user);
    }

    /**
     * get contact list
     *
     * @return
     */
    public Map<String, ChatUser> getContactList() {
        if (isLoggedIn() && contactList == null) {
            contactList = ChatModel.getContactList();
        }

        // return a empty non-null object to avoid app crash
        if (contactList == null) {
            return new Hashtable<String, ChatUser>();
        }

        return contactList;
    }

    /**
     * set current username
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        this.username = username;
        ChatModel.setCurrentUserName(username);
    }

    /**
     * get current user's id
     */
    public String getCurrentUserName() {
        if (username == null) {
            username = ChatModel.getCurrentUsernName();
        }
        return username;
    }


    /**
     * update user list to cache and database
     *
     * @param contactInfoList
     */
    public void updateContactList(List<ChatUser> contactInfoList) {
        for (ChatUser u : contactInfoList) {
            contactList.put(u.getUsername(), u);
        }
        ArrayList<ChatUser> mList = new ArrayList<ChatUser>();
        mList.addAll(contactList.values());
        ChatModel.saveContactList(mList);
    }


//    void endCall() {
//        try {
//            EMClient.getInstance().callManager().endCall();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    public void notifyBlackListSyncListener(boolean success) {
        for (DataSyncListener listener : syncBlackListListeners) {
            listener.onSyncComplete(success);
        }
    }

    synchronized void reset() {

        ChatModel.setGroupsSynced(false);
        ChatModel.setContactSynced(false);
        ChatModel.setBlacklistSynced(false);
        isGroupAndContactListenerRegisted = false;
        setContactList(null);
        ChatDBManager.getInstance().closeDB();
    }

    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }

    /**
     * 获取会话列表
     *
     * @return
     */
    public List<EMConversation> getAllConversationList() {
        //获取所有的会话消息
        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();

        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMsgCount() > 0) {
                    sortList.add(new Pair<>(conversation.getLastMessage().getMsgTime(), conversation));
                } else {
                    sortList.add(new Pair<>(0L, conversation));
                }
            }
        }
        try {
            // 根据最后一条消息的时间排序
            Collections.sort(sortList, (con1, con2) -> {
                        if (con1.first.equals(con2.first)) {
                            return 0;
                        } else if (con2.first > con1.first) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }


    public void postUnReadMessageCount() {
        RxBus.getInstance().post(new ReceiveMessage());
        RxBus.getInstance().post(new EventUnreadMessageCount(getUnreadMessageCount()));
    }

    public int getUnreadMessageCount() {
        return EMClient.getInstance().chatManager().getUnreadMessageCount();
    }

    public boolean isConnected() {
        return EMClient.getInstance().isConnected();
    }

}