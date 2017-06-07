package com.xmd.technician.window;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crazyman.library.PermissionTool;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EasyUtils;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.AcceptOrRejectGame;
import com.xmd.technician.bean.ClubJournalBean;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.bean.CreditAccountResult;
import com.xmd.technician.bean.CreditGift;
import com.xmd.technician.bean.CreditStatusResult;
import com.xmd.technician.bean.GameResult;
import com.xmd.technician.bean.GiftListResult;
import com.xmd.technician.bean.MarkChatToUserBean;
import com.xmd.technician.bean.MarketingChatShareBean;
import com.xmd.technician.bean.OnceCardItemBean;
import com.xmd.technician.bean.PlayDiceGame;
import com.xmd.technician.bean.SendGameResult;
import com.xmd.technician.bean.UserGetCouponResult;
import com.xmd.technician.bean.UserWin;
import com.xmd.technician.chat.ChatCategoryManager;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatHelper;
import com.xmd.technician.chat.ChatSentMessageHelper;
import com.xmd.technician.chat.ChatUser;

import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.chat.event.CancelGame;
import com.xmd.technician.chat.chatrow.EaseCustomChatRowProvider;
import com.xmd.technician.chat.chatview.BaseEaseChatView;
import com.xmd.technician.chat.chatview.EMessageListItemClickListener;
import com.xmd.technician.chat.chatview.EaseChatMessageList;
import com.xmd.technician.chat.controller.ChatUI;
import com.xmd.technician.chat.runtimepermissions.PermissionsManager;
import com.xmd.technician.chat.utils.EaseCommonUtils;
import com.xmd.technician.chat.utils.UserUtils;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Util;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.CategoryListResult;
import com.xmd.technician.http.gson.CouponListResult;
import com.xmd.technician.http.gson.InUserBlacklistResult;
import com.xmd.technician.http.gson.MarkChatToUserResult;
import com.xmd.technician.http.gson.OrderManageResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.permission.CheckBusinessPermission;
import com.xmd.technician.permission.PermissionConstants;
import com.xmd.technician.widget.FlowerAnimation;
import com.xmd.technician.widget.GameSettingDialog;
import com.xmd.technician.widget.RewardConfirmDialog;
import com.xmd.technician.widget.chatview.ChatOperationDialog;
import com.xmd.technician.widget.chatview.EaseChatInputMenu;
import com.xmd.technician.widget.chatview.EaseChatInputMenu.ChatInputMenuListener;
import com.xmd.technician.widget.chatview.EaseChatInputMenu.ChatSentSpecialListener;
import com.xmd.technician.widget.chatview.EaseVoiceRecorderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-3-31.
 */

public class TechChatActivity extends BaseActivity implements EMMessageListener {
    @Bind(R.id.message_list_view)
    EaseChatMessageList messageList;
    @Bind(R.id.voice_recorder)
    EaseVoiceRecorderView voiceRecorderView;
    @Bind(R.id.input_menu)
    EaseChatInputMenu inputMenu;
    @Bind(R.id.linear_chat)
    RelativeLayout linearChat;

    private static final int GET_COUPON_CODE = 0x001;
    private static final int GET_MARKETING_CODE = 0x002;
    private static final int GET_PREFERENTIAL_CODE = 0x003;
    private static final int GET_JOURNAL_CODE = 0x004;
    private static final int REQUEST_CODE_LOCAL = 0x005;
    private static final int REQUEST_RECORD_AUDIO = 0x006;
    public static final String REQUEST_COUPON_TYPE = "coupon";
    public static final String REQUEST_MARKETING_TYPE = "marketing";
    public static final String REQUEST_PREFERENTIAL_TYPE = "preferential";
    public static final String REQUEST_JOURNAL_TYPE = "journal";


    private int chatType = 1; //单聊，群聊，聊天室
    private int chatUserType = 3;//1管理者，2技师，3普通客户
    private String toChatUserId;//对方环信ID
    private EMConversation mConversation;
    private InputMethodManager inputManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private ChatInputMenuListener inputMenuListener;
    private ChatSentSpecialListener specialInputListener;
    private boolean isLoading;
    private boolean haveMoreData = true;
    private boolean isMessageListInited;
    private int pagesSize = 20;
    private String mTechCode;

    private EaseCustomChatRowProvider chatRowProvider;
    private ChatSentMessageHelper chatSentMessageHelper;
    public static TechChatActivity activityInstance;
    private int mGameIntegral;     //游戏所需积分
    private int mAvailableCredit; //可用积分
    private String adverseName;
    private GameSettingDialog mGameSettingDialog;
    private FlowerAnimation animation;
    private MarkChatToUserBean locationBean;
    private ChatCategoryManager categoryManager;

    private List<CouponInfo> mCouponList; //
    private List<CouponInfo> mSelectedCouponInfo;//选中的优惠券列表
    private List<ClubJournalBean> mSelectedJournal;//选中的期刊列表
    private List<OnceCardItemBean> mSelectedOnceCard;//选中的次卡列表
    private List<MarketingChatShareBean> mChatShareBeans;//选中的活动列表


    private Subscription mManagerOrderSubscription;//订单
    private Subscription mGetRedpacklistSubscription;//查询可用优惠券及点钟券
    private Subscription mSendDiceGameSubscription;//发送筛子游戏
    private Subscription mAcceptOrRejectGameSubscription;//接受或拒绝游戏
    private Subscription mUserAvailableCreditSubscription;//可用积分
    private Subscription mUserWinSubscription;//游戏结果
    private Subscription mAcceptGameResultSubscription;//接受游戏
    private Subscription mPlayGameAgainSubscription;//筛子游戏再玩一次
    private Subscription mCancelGameSubscription;//取消筛子游戏
    private Subscription mCreditStatusSubscription;//积分开关状态
    private Subscription mGiftResultSubscription;//礼物列表
    private Subscription mClubUserGetCouponSubscription;//优惠券自动领取
    private Subscription mClubPositionSubscription;//会所位置
    private Subscription mInUserBlacklistSubscription;//黑名单
    private Subscription mChatCategorySubscription;//显示的列表
    //技师在用户黑名单中
    private boolean mInUserBlacklist = false;
    private String[] permission = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_chat);
        ButterKnife.bind(this);

        initView();
        initSendCoupon();
        initOrderManager();
        initPlayCreditGame();
        initGoldAnimationView();
    }

    private void initView() {
        setBackVisible(true);
        mListView = messageList.getListView();
        chatType = ChatConstant.CHAT_TYPE_SINGLE;
        toChatUserId = getIntent().getStringExtra(ChatConstant.TO_CHAT_USER_ID);
        chatUserType = UserProfileProvider.getInstance().getChatUserInfo(toChatUserId).getUserChatType();
        mSwipeRefreshLayout = messageList.getSwipeRefreshLayout();
        mSwipeRefreshLayout.setColorSchemeColors(ResourceUtils.getColor(R.color.primary_button_color_normal));
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        inputMenu.setFragmentManager(getSupportFragmentManager());

        setUpView();
    }

    private void setUpView() {
        setTitle(toChatUserId);
        initListener();
        initProvider();
        categoryManager = ChatCategoryManager.getInstance();
        if (UserUtils.getUserInfo(toChatUserId) != null) {
            ChatUser user = UserUtils.getUserInfo(toChatUserId);
            if (user != null) {
                setTitle(user.getNick());
            }
        }
        adverseName = mAppTitle.getText().toString();
        if (chatType != ChatConstant.CHAT_TYPE_CHATROOM) {
            onConversationInit();
            onMessageListInit();
        }
        setRefreshLayoutListener();
        mClubUserGetCouponSubscription = RxBus.getInstance().toObservable(UserGetCouponResult.class).subscribe(
                couponResult -> handleUserGetCoupon(couponResult)
        );

        mGiftResultSubscription = RxBus.getInstance().toObservable(GiftListResult.class).subscribe(
                result -> {
                    for (CreditGift gift : result.respData) {
                        SharedPreferenceHelper.setGiftImageById(gift.id, gift.gifUrl);
                    }
                }
        );
        mClubPositionSubscription = RxBus.getInstance().toObservable(MarkChatToUserResult.class).subscribe(
                result -> {
                    locationBean = result.respData;
                }
        );
        mInUserBlacklistSubscription = RxBus.getInstance().toObservable(InUserBlacklistResult.class).subscribe(
                result -> handlerUserInBlacklist(result));
        mChatCategorySubscription = RxBus.getInstance().toObservable(CategoryListResult.class).subscribe(
                result -> handlerCategoryList(result)
        );

        categoryManager.getChatManagerData();
        chatSentMessageHelper = new ChatSentMessageHelper(TechChatActivity.this, toChatUserId, messageList, true);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_IN_USER_BLACKLIST, toChatUserId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MARK_CHAT_TO_USER);
    }

    private void initListener() {
        inputMenuListener = new EaseChatInputMenu.ChatInputMenuListener() {
            @Override
            public void onSendMessage(String content) {
                chatSentMessageHelper.sendTextMessage(content);
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                if (PermissionTool.hasPermissions(TechChatActivity.this, new String[]{Manifest.permission.RECORD_AUDIO})) {
                    return voiceRecorderView.onPressToSpeakBtnTouch(v, event, (voiceFilePath, voiceTimeLength) -> {
                        chatSentMessageHelper.sendVoiceMessage(voiceFilePath, voiceTimeLength);
                    });
                } else {
                    PermissionTool.requestPermission(TechChatActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, new String[]{"录音"}, REQUEST_RECORD_AUDIO);
                    return false;
                }
            }
        };
        specialInputListener = new EaseChatInputMenu.ChatSentSpecialListener() {
            @Override
            public void onPickPictureClicked() {
                hideKeyboard();
                Util.selectPicFromLocal(TechChatActivity.this, REQUEST_CODE_LOCAL);
            }

            @Override
            public void onCommonMessageClicked(String s) {
                chatSentMessageHelper.sendTextMessage(s);
            }

            @Override
            public void onBegRewordClicked() {
                new RewardConfirmDialog(TechChatActivity.this, getString(R.string.beg_reward), getString(R.string.send_request_user_reward), "") {
                    @Override
                    public void onConfirmClick() {
                        super.onConfirmClick();
                        chatSentMessageHelper.sendBegRewardMessage("<i></i>万水千山总是情<br/>打赏两个行不行~");
                    }
                }.show();
            }

            @Override
            public void onMarketClicked() {
                Intent marketIntent = new Intent(TechChatActivity.this, MarketingChatShareActivity.class);
                startActivityForResult(marketIntent, GET_MARKETING_CODE);
            }

            @Override
            public void periodicalClicked() {
                Intent journalIntent = new Intent(TechChatActivity.this, ClubJournalChatShareActivity.class);
                startActivityForResult(journalIntent, GET_JOURNAL_CODE);
            }

            @Override
            public void onCardClicked() {

            }

            @Override
            public void onPreferenceClicked() {
                Intent journalIntent = new Intent(TechChatActivity.this, MallDiscountChatShareActivity.class);
                startActivityForResult(journalIntent, GET_PREFERENTIAL_CODE);
            }

            @Override
            public void onCouponClicked() {
                Intent intent = new Intent(TechChatActivity.this, AvailableCouponListActivity.class);
                startActivityForResult(intent, GET_COUPON_CODE);
            }

            @Override
            public void onGameClicked() {
                mGameIntegral = 1;
                mGameSettingDialog = new GameSettingDialog(TechChatActivity.this);
                mGameSettingDialog.setConfirmClickInterface(new GameSettingDialog.ConfirmClickInterface() {
                    @Override
                    public void ConfirmClicked(int integralNum) {
                        mGameIntegral = integralNum;
                        if (mAvailableCredit > mGameIntegral) {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(RequestConstant.KEY_USER_CLUB_ID, SharedPreferenceHelper.getUserClubId());
                            params.put(RequestConstant.KEY_UER_CREDIT_AMOUNT, String.valueOf(mGameIntegral));
                            params.put(RequestConstant.KEY_GAME_USER_EMCHAT_ID, toChatUserId);
                            params.put(RequestConstant.KEY_DICE_GAME_TIME, String.valueOf(System.currentTimeMillis()));
                            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_INITIATE_GAME, params);
                        } else {
                            showCreditInsufficientDialog(String.valueOf(mGameIntegral));
                        }
                    }
                });
                mGameSettingDialog.show();
            }

            @Override
            public void onLocationClicked() {
                if (null != locationBean) {
                    chatSentMessageHelper.sendLocationMessage(locationBean.lat, locationBean.lng, locationBean.address, locationBean.staticMap);
                }

            }

        };
        inputMenu.setChatInputMenuListener(inputMenuListener);
        inputMenu.setChatSentSpecialListener(specialInputListener);
    }

    private void initProvider() {
        chatRowProvider = new EaseCustomChatRowProvider() {
            @Override
            public int getCustomChatRowTypeCount() {
                return 40;
            }

            @Override
            public int getCustomChatRowType(EMMessage message) {
                return EaseCommonUtils.getCustomChatType(message);
            }

            @Override
            public BaseEaseChatView getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
                return EaseCommonUtils.getCustomChatView(TechChatActivity.this, message, position, adapter);
            }
        };
    }

    private void onConversationInit() {
        mConversation = EMClient.getInstance().chatManager().getConversation(toChatUserId, EaseCommonUtils.getConversationType(chatType), true);
        mConversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = mConversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < mConversation.getAllMsgCount() && msgCount < pagesSize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            mConversation.loadMoreMsgFromDB(msgId, pagesSize - msgCount);
        }
    }

    private void onMessageListInit() {
        messageList.init(toChatUserId, chatType, chatRowProvider);
        setListItemClickListener();

        messageList.getListView().setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                inputMenu.initIconImageView(null, null);
                return false;
            }
        });

        isMessageListInited = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMessageListInited) {
            messageList.refresh();
            ChatUI.getInstance().popActivity(TechChatActivity.this);
            EMClient.getInstance().chatManager().addMessageListener(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().chatManager().removeMessageListener(this);
        ChatUI.getInstance().popActivity(TechChatActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
        RxBus.getInstance().unsubscribe(mGetRedpacklistSubscription, mManagerOrderSubscription,
                mSendDiceGameSubscription, mAcceptGameResultSubscription, mAcceptOrRejectGameSubscription, mUserAvailableCreditSubscription
                , mUserWinSubscription, mCancelGameSubscription, mPlayGameAgainSubscription, mCreditStatusSubscription,
                mGiftResultSubscription, mClubUserGetCouponSubscription, mClubPositionSubscription, mInUserBlacklistSubscription, mChatCategorySubscription);
    }


    private void handlerCategoryList(CategoryListResult result) {
        if (result.statusCode == 200) {
            categoryManager.initial();
            categoryManager.setCategoryList(result.respData);
            inputMenu.setCommentMenuView(categoryManager.getCommentMenu(chatUserType));
            inputMenu.setMoreMenuView(categoryManager.getMoreMenu(chatUserType));
        }
    }


    private void initGoldAnimationView() {
        animation = new FlowerAnimation(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        animation.setLayoutParams(params);
        linearChat.addView(animation);
    }

    @CheckBusinessPermission(PermissionConstants.MESSAGE_PLAY_CREDIT_GAME)
    public void initPlayCreditGame() {
        //积分状态
        mCreditStatusSubscription = RxBus.getInstance().toObservable(CreditStatusResult.class).subscribe(
                statusResult -> {
                    if (statusResult.statusCode == 200) {
                        SharedPreferenceHelper.setGameTimeout(statusResult.respData.gameTimeoutSeconds);
                    }
                }
        );
        //发送游戏邀请
        mSendDiceGameSubscription = RxBus.getInstance().toObservable(SendGameResult.class).subscribe(
                sendGameResult -> {
                    if (sendGameResult.statusCode == 200 && Utils.isNotEmpty(sendGameResult.respData.gameId)) {
                        chatSentMessageHelper.sendDiceGameMessage(String.valueOf(mGameIntegral), sendGameResult.respData.gameId, ChatConstant.KEY_REQUEST_GAME, "0:0", SharedPreferenceHelper.getEmchatId(), adverseName);
                    }
                }
        );
        //用户可用积分判定
        mUserAvailableCreditSubscription = RxBus.getInstance().toObservable(CreditAccountResult.class).subscribe(
                result -> {
                    if (result.statusCode == 200) {
                        if (result.respData.size() > 0) {
                            mAvailableCredit = result.respData.get(0).amount;
                        }

                    }
                }
        );
        //游戏胜负结果
        mUserWinSubscription = RxBus.getInstance().toObservable(UserWin.class).subscribe(
                result -> {
                    if (!SharedPreferenceHelper.getGameStatus(result.messageId).equals(ChatConstant.KEY_OVER_GAME_TYPE)) {
                        animation.startAnimation();
                        SharedPreferenceHelper.setGameStatus(result.messageId, ChatConstant.KEY_OVER_GAME_TYPE);
                    }
                }
        );
        //再次发起游戏
        mPlayGameAgainSubscription = RxBus.getInstance().toObservable(PlayDiceGame.class).subscribe(
                result -> {
                    if (mAvailableCredit > Integer.parseInt(result.content)) {
                        playGameAgain(result.content);
                        mGameIntegral = Integer.parseInt(result.content);
                    } else {
                        showCreditInsufficientDialog(result.content);
                    }

                }

        );
        //取消游戏
        mCancelGameSubscription = RxBus.getInstance().toObservable(CancelGame.class).subscribe(
                result -> {
                    EMMessage message = result.message;
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(RequestConstant.KEY_DICE_GAME_STATUS, ChatConstant.KEY_CANCEL_GAME_TYPE);
                        params.put(RequestConstant.KEY_DICE_GAME_ID, message.getStringAttribute(RequestConstant.KEY_DICE_GAME_ID).substring(5));
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_GAME_ACCEPT_OR_REJECT, params);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }

                }
        );

        //接受或拒绝游戏
        mAcceptOrRejectGameSubscription = RxBus.getInstance().toObservable(AcceptOrRejectGame.class).subscribe(
                acceptOrRejectGame -> handlerAcceptOrRejectGame(acceptOrRejectGame)
        );
        //游戏结果
        mAcceptGameResultSubscription = RxBus.getInstance().toObservable(GameResult.class).subscribe(
                gameResult -> handlerGameInvite(gameResult)
        );
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CREDIT_STATUS);//获取积分状态
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CREDIT_ACCOUNT);//获取积分数目
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CREDIT_GIFT_LIST);//积分礼物列表

    }

    private void handlerAcceptOrRejectGame(AcceptOrRejectGame acceptOrRejectGame) {
        if (mAvailableCredit > Integer.parseInt(acceptOrRejectGame.gameContent)) {
            Map<String, String> params = new HashMap<>();
            String gameId = acceptOrRejectGame.gameId.substring((5));
            params.put(RequestConstant.KEY_DICE_GAME_ID, gameId);
            params.put(RequestConstant.KEY_STATUS, ChatConstant.KEY_GAME_ACCEPT);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_GAME_ACCEPT_OR_REJECT, params);
        } else {
            showCreditInsufficientDialog(acceptOrRejectGame.gameContent);
        }
    }

    private void handlerGameInvite(GameResult result) {
        if (result.statusCode == 200) {

            if (result.respData.status.equals(ChatConstant.KEY_ACCEPT_GAME)) {
                chatSentMessageHelper.sendDiceGameMessage(result.respData.belongingsAmount, result.respData.id, ChatConstant.KEY_ACCEPT_GAME, "0:0", toChatUserId, adverseName);
                ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                    @Override
                    public void run() {
                        chatSentMessageHelper.sendDiceGameMessage(result.respData.belongingsAmount, result.respData.id, ChatConstant.KEY_OVER_GAME_TYPE, result.respData.srcPoint + ":" + result.respData.dstPoint, toChatUserId, adverseName);
                    }
                }, 250);
            } else if (result.respData.status.equals(ChatConstant.KEY_REFUSED_GAME_STATUS)) {

                chatSentMessageHelper.sendDiceGameMessage(result.respData.belongingsAmount, result.respData.id, ChatConstant.KEY_GAME_REJECT, result.respData.srcPoint + ":" + result.respData.dstPoint, toChatUserId, adverseName);
            } else if (result.respData.status.equals(ChatConstant.KEY_OVERTIME_GAME)) {
                chatSentMessageHelper.sendDiceGameMessage(result.respData.belongingsAmount, result.respData.id, ChatConstant.KEY_OVERTIME_GAME, result.respData.srcPoint + ":" + result.respData.dstPoint, toChatUserId, adverseName);
            } else if (result.respData.status.equals(ChatConstant.KEY_CANCEL_GAME_TYPE)) {

                chatSentMessageHelper.sendDiceGameMessage(result.respData.belongingsAmount, result.respData.id, ChatConstant.KEY_CANCEL_GAME_TYPE, result.respData.srcPoint + ":" + result.respData.dstPoint, toChatUserId, adverseName);
            }

        } else {

            SharedPreferenceHelper.setGameStatus(result.respData.id, ChatConstant.KEY_GAME_DISABLE);
            mConversation.removeMessage(result.respData.id);
        }

    }

    private void playGameAgain(String content) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestConstant.KEY_USER_CLUB_ID, SharedPreferenceHelper.getUserClubId());
        params.put(RequestConstant.KEY_UER_CREDIT_AMOUNT, content);
        params.put(RequestConstant.KEY_GAME_USER_EMCHAT_ID, toChatUserId);
        params.put(RequestConstant.KEY_DICE_GAME_TIME, String.valueOf(System.currentTimeMillis()));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_INITIATE_GAME, params);
    }

    private void initOrderManager() {
        mManagerOrderSubscription = RxBus.getInstance().toObservable(OrderManageResult.class).subscribe(
                result -> chatSentMessageHelper.sendOrderMessage(result.content, result.orderId));
    }


    private void handleUserGetCoupon(UserGetCouponResult couponResult) {
        String userActId = "";
        if (couponResult.statusCode == 200) {
            if (couponResult.respData == null) {
                return;
            }
            if (Utils.isNotEmpty(couponResult.respData.userActId)) {
                userActId = couponResult.respData.userActId;
            }
        }
        chatSentMessageHelper.sendCouponMessage(couponResult.content, couponResult.actId, couponResult.techCode, userActId);
    }

    private void handlerCheckedCoupon(CouponInfo result) {
        if (!(ChatConstant.KEY_COUPON_PAID_TYPE).equals(result.couponType)) {
            String content = String.format("<i>%s</i><span>%d</span>元<b>%s</b>", result.useTypeName, result.actValue, result.couponPeriod);
            EaseCommonUtils.userGetCoupon(content, result.actId, "tech", toChatUserId);
        } else {
            chatSentMessageHelper.sendPaidCouponMessage(String.format("<i>求点钟</i>立减<span>%1$d</span>元<b>%2$s</b>", result.actValue, result.couponPeriod), result.actId, mTechCode);
        }
    }

    //特惠商城,电子期刊
    private void handlerCheckedActivity(String actId, String subType, String jounrlId) {
        chatSentMessageHelper.sendActivityMessage(actId, subType, jounrlId);
    }

    //营销活动
    private void handlerCheckedMarketing(String actId, String subType) {
        chatSentMessageHelper.sendActivityMessage(actId, subType, "");
    }


    @CheckBusinessPermission(PermissionConstants.MESSAGE_SEND_COUPON)
    public void initSendCoupon() {
        mGetRedpacklistSubscription = RxBus.getInstance().toObservable(CouponListResult.class).subscribe(
                redpackResult -> getAvailableCouponList(redpackResult));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_LIST);
    }

    private void getAvailableCouponList(CouponListResult result) {
        if (result.statusCode == 200) {
            mTechCode = result.respData.techCode;
            if (result.respData.coupons != null) {
                if (mCouponList == null) {
                    mCouponList = new ArrayList<>();
                } else {
                    mCouponList.clear();
                }

                for (CouponInfo info : result.respData.coupons) {
                    info.selectedStatus = 1;
                    mCouponList.add(info);
                }
            }
        }
    }


    private void setListItemClickListener() {
        messageList.setItemClickListener(new EMessageListItemClickListener() {
            @Override
            public void onResendClick(EMMessage message) {
                chatSentMessageHelper.resendMessage(message);
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                return false;
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                String[] strings = null;
                if (message.direct() == EMMessage.Direct.RECEIVE) {
                    strings = new String[]{"删除"};
                } else {
                    strings = new String[]{"删除", "撤回"};
                }
                //如果长按了撤回消息，不做操作
                if (Utils.isNotEmpty(message.getStringAttribute(ChatConstant.KEY_REVOKE_TYPE, ""))) {
                    return;
                }
                ChatOperationDialog dialog = new ChatOperationDialog(TechChatActivity.this, strings);
                dialog.show();
                dialog.setOperationMenuItemClickedListener(new ChatOperationDialog.OperationMenuItemClickedListener() {
                    @Override
                    public void itemClicked(int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0://删除
                                new RewardConfirmDialog(TechChatActivity.this, "温馨提示", "确认删除此条消息码？", "确认") {
                                    @Override
                                    public void onConfirmClick() {
                                        this.dismiss();
                                        mConversation.removeMessage(message.getMsgId());
                                        messageList.refreshSelectLast();
                                    }
                                }.show();
                                break;
                            case 1://撤回
                                long currentTime = System.currentTimeMillis();
                                long messageTime = message.getMsgTime();
                                //发送时间小于2分钟的话可撤回
                                if ((currentTime - messageTime) < 2 * 60 * 1000) {
                                    mConversation.removeMessage(message.getMsgId());
                                    mConversation.insertMessage(chatSentMessageHelper.createInsertMessage(SharedPreferenceHelper.getEmchatId(), messageTime));
                                    messageList.refreshSelectLast();
                                    chatSentMessageHelper.sendCmdMessage(message.getMsgId(), messageTime);

                                } else {
                                    new RewardConfirmDialog(TechChatActivity.this, "", "发送时间超过2分钟的消息,不能被撤回", "确认", false) {
                                        @Override
                                        public void onConfirmClick() {
                                            this.dismiss();
                                        }
                                    }.show();
                                }


                                break;
                        }
                    }
                });

            }

            @Override
            public void onUserAvatarClick(String username) {

            }

            @Override
            public void onUserAvatarLongClick(String username) {

            }
        });
    }


    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void setRefreshLayoutListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (mListView.getFirstVisiblePosition() == 0 && !isLoading && haveMoreData) {
                            List<EMMessage> messages;
                            try {
                                if (chatType == ChatConstant.CHAT_TYPE_SINGLE) {
                                    messages = mConversation.loadMoreMsgFromDB(messageList.getItem(0).getMsgId(),
                                            pagesSize);
                                } else {
                                    messages = mConversation.loadMoreMsgFromDB(messageList.getItem(0).getMsgId(),
                                            pagesSize);
                                }
                            } catch (Exception e1) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            if (messages.size() > 0) {
                                messageList.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != pagesSize) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }

                            isLoading = false;

                        } else {
                            Toast.makeText(TechChatActivity.this, getResources().getString(R.string.no_more_messages),
                                    Toast.LENGTH_SHORT).show();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    @OnClick({R.id.toolbar_back, R.id.toolbar_right_img})
    public void onToolbarClicked(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back:
                if (EasyUtils.isSingleActivity(TechChatActivity.this)) {
                    Intent intent = new Intent(TechChatActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                ChatHelper.getInstance().postUnReadMessageCount();
                this.finish();
                break;
            case R.id.toolbar_right:
                emptyHistory();
                break;
        }
    }

    private void emptyHistory() {
        new RewardConfirmDialog(TechChatActivity.this, "", getString(R.string.clean_all_message), "") {
            @Override
            public void onConfirmClick() {

                if (mConversation != null) {
                    mConversation.clearAllMessages();
                }
                messageList.refresh();
            }
        }.show();

    }


    private void showCreditInsufficientDialog(String content) {
        new RewardConfirmDialog(TechChatActivity.this, getString(R.string.credit_insufficient), String.format(getString(R.string.credit_insufficient_alert), content),
                getString(R.string.how_to_get_credit)) {
            @Override
            public void onConfirmClick() {
                Intent intent = new Intent(TechChatActivity.this, CreditRuleExplainActivity.class);
                startActivity(intent);
                super.onConfirmClick();
            }
        }.show();
    }

    private void handlerUserInBlacklist(InUserBlacklistResult result) {
        if (result.statusCode == 200) {
            mInUserBlacklist = result.respData;
        }
        if(null != chatSentMessageHelper){
            chatSentMessageHelper.setInUserBlackList(result.respData);
        }

    }


    @Override
    protected void onNewIntent(Intent intent) {
        String userName = intent.getStringExtra(ChatConstant.TO_CHAT_USER_ID);
        if (toChatUserId.equals(userName)) {
            super.onNewIntent(intent);
        } else {
            finish();
            startActivity(intent);
        }

    }

    public String getTochatUserId() {
        return toChatUserId;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {
        for (EMMessage message : list) {
            String username = null;
            // group message
            if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                // single chat message
                username = message.getFrom();
            }

            // if the message is for current conversation
            if (username.equals(toChatUserId) || message.getTo().equals(toChatUserId)) {
                messageList.refreshSelectLast();
                ChatUI.getInstance().getNotifier().vibrateAndPlayTone(message);
                mConversation.markMessageAsRead(message.getMsgId());
            } else {
                ChatUI.getInstance().getNotifier().onNewMsg(message);
            }
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {
        for (EMMessage message : list) {
            EMCmdMessageBody messageBody = (EMCmdMessageBody) message.getBody();
            String action = messageBody.action();
            if (action.equals(ChatConstant.KEY_CHAT_CMD_REVOKE_ACTION)) {
                String messageId = message.getStringAttribute(ChatConstant.KEY_MESSAGE_ID, "");
                long messageTime = message.getLongAttribute(ChatConstant.KEY_TIME, System.currentTimeMillis());
                if (Utils.isNotEmpty(messageId)) {
                    mConversation.removeMessage(messageId);
                    mConversation.insertMessage(chatSentMessageHelper.createInsertMessage(toChatUserId, messageTime));
                    messageList.refreshSelectLast();
                }

            }
        }

    }

    @Override
    public void onMessageRead(List<EMMessage> list) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }


    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputMenu.initIconImageView(null, null);
        //发送本地图片
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO:
                if (resultCode == RESULT_OK) {

                } else {
                    Toast.makeText(this, "获取权限失败", Toast.LENGTH_SHORT).show();
                }
                return;

            case GET_COUPON_CODE://优惠券
                if (null == mSelectedCouponInfo) {
                    mSelectedCouponInfo = new ArrayList<>();
                } else {
                    mSelectedCouponInfo.clear();
                }
                mSelectedCouponInfo = (List<CouponInfo>) data.getSerializableExtra(REQUEST_COUPON_TYPE);
                for (int i = 0; i < mSelectedCouponInfo.size(); i++) {
                    handlerCheckedCoupon(mSelectedCouponInfo.get(i));
                }
                break;
            case GET_MARKETING_CODE://营销活动
                if (null == mChatShareBeans) {
                    mChatShareBeans = new ArrayList<>();
                } else {
                    mChatShareBeans.clear();
                }
                mChatShareBeans = (List<MarketingChatShareBean>) data.getSerializableExtra(REQUEST_MARKETING_TYPE);
                for (int i = 0; i < mChatShareBeans.size(); i++) {
                    String actId;
                    String activityType;
                    actId = Utils.isEmpty(mChatShareBeans.get(i).actId) ? mChatShareBeans.get(i).id : mChatShareBeans.get(i).actId;
                    if (Utils.isNotEmpty(mChatShareBeans.get(i).itemName)) {
                        activityType = ChatConstant.KEY_SUB_TYPE_INDIANA;
                    } else if (Utils.isNotEmpty(mChatShareBeans.get(i).startTime)) {
                        activityType = ChatConstant.KEY_SUB_TYPE_TURNTABLE;
                    } else {
                        activityType = ChatConstant.KEY_SUB_TYPE_SECKILL;
                    }
                    handlerCheckedMarketing(actId, activityType);
                }
                break;
            case GET_JOURNAL_CODE://电子期刊
                if (null == mSelectedJournal) {
                    mSelectedJournal = new ArrayList<>();
                } else {
                    mSelectedJournal.clear();
                }
                mSelectedJournal = (List<ClubJournalBean>) data.getSerializableExtra(REQUEST_JOURNAL_TYPE);
                for (int i = 0; i < mSelectedJournal.size(); i++) {
                    handlerCheckedActivity(mSelectedJournal.get(i).journalId, ChatConstant.KEY_SUB_TYPE_JOURNAL, String.valueOf(mSelectedJournal.get(i).templateId));
                }
                break;
            case GET_PREFERENTIAL_CODE://特惠商城
                if (null == mSelectedOnceCard) {
                    mSelectedOnceCard = new ArrayList<>();
                } else {
                    mSelectedOnceCard.clear();
                }
                mSelectedOnceCard = (List<OnceCardItemBean>) data.getSerializableExtra(REQUEST_PREFERENTIAL_TYPE);
                for (int i = 0; i < mSelectedOnceCard.size(); i++) {
                    if (mSelectedOnceCard.get(i).cardType.equals(Constant.ITEM_CARD_TYPE)) {
                        handlerCheckedActivity(mSelectedOnceCard.get(i).id, ChatConstant.KEY_SUB_TYPE_TIMES_SCARD, "");
                    } else if (mSelectedOnceCard.get(i).cardType.equals(Constant.ITEM_PACKAGE_TYPE)) {
                        handlerCheckedActivity(mSelectedOnceCard.get(i).id, ChatConstant.KEY_SUB_TYPE_PACKAGE, "");
                    } else {
                        handlerCheckedActivity(mSelectedOnceCard.get(i).id, ChatConstant.KEY_SUB_TYPE_GIFT, "");
                    }

                }
                break;
            case REQUEST_CODE_LOCAL://位置
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        chatSentMessageHelper.sendPicByUri(selectedImage);
                    }
                }
                break;
        }


    }

}
