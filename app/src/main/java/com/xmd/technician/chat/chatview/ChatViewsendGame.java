package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.DateUtils;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.chat.UserUtils;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.widget.RoundImageView;


import java.util.Date;

/**
 * Created by Administrator on 2016/8/22.
 */
public class ChatViewSendGame extends BaseChatView {
    private TextView mGameAmount, mAdverseName, mUserName, mWaitGame, mGameIntroduce, mRefuseGame,mCancel;
    private RoundImageView mAdverseHead, mUserHead;
    private String mDiceGameAmount, mDiceAdverseName, mDiceUserName, mDiceWaitGame;
    private EMConversation emConversation;
    private GameCancelListener mGameCancelListener;
    long gameStartTime,currentTime;

    public ChatViewSendGame(Context context, EMMessage.Direct direct, EMConversation emConversation) {
        super(context, direct);
        this.emConversation = emConversation;
    }

    @Override
    protected void onFindViewById() {
        findViewById(R.id.dice_game_sent).setVisibility(View.VISIBLE);
        mGameIntroduce = (TextView) findViewById(R.id.invite_game_top);
        mGameAmount = (TextView) findViewById(R.id.dice_game_amount);
        mAdverseName = (TextView) findViewById(R.id.adverse_name);
        mUserName = (TextView) findViewById(R.id.user_name);
        mWaitGame = (TextView) findViewById(R.id.wait_game);
        mAdverseHead = (RoundImageView) findViewById(R.id.adverse_head);
        mUserHead = (RoundImageView) findViewById(R.id.user_head);
        mRefuseGame = (TextView) findViewById(R.id.refuse_game);
        mCancel = (TextView) findViewById(R.id.game_cancel);
    }

    @Override
    protected void onSetUpView() {
        try {
            gameStartTime = message.getMsgTime();
            currentTime = System.currentTimeMillis();
            String headUrl = UserProfileProvider.getInstance().getChatUserInfo(message.getTo()).getAvatar();
            if (message.getStringAttribute(ChatConstant.KEY_GAME_STATUS).equals(ChatConstant.KEY_REQUEST_GAME)) {
                if(currentTime-gameStartTime>SharedPreferenceHelper.getGameTimeout()){
                    SharedPreferenceHelper.setGameStatus(message.getStringAttribute(ChatConstant.KEY_GAME_ID), message.getMsgId());
                    initView();
                    EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                    mGameIntroduce.setText(String.format(ResourceUtils.getString(R.string.invite_game_format), Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME), true)));
                    mGameAmount.setText(String.format("%s积分", body.getMessage()));
                    mAdverseName.setText(Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME), true));
                    mUserName.setText(Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_NAME), true));
                    mWaitGame.setVisibility(View.VISIBLE);
                    Glide.with(context).load(SharedPreferenceHelper.getUserAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.icon22).into(mUserHead);
                    mUserHead.setVisibility(View.VISIBLE);
                    mWaitGame.setText(ResourceUtils.getString(R.string.order_status_description_overtime));
                    mCancel.setVisibility(View.GONE);
                    return;
                }else{
                    SharedPreferenceHelper.setGameStatus(message.getStringAttribute(ChatConstant.KEY_GAME_ID), message.getMsgId());
                    initView();
                    EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                    mGameIntroduce.setText(String.format(ResourceUtils.getString(R.string.invite_game_format), Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME), true)));
                    mGameAmount.setText(String.format("%s积分", body.getMessage()));
                    mAdverseName.setText(Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME), true));
                    mUserName.setText(Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_NAME), true));
                    mWaitGame.setVisibility(View.VISIBLE);
                    mWaitGame.setText(ResourceUtils.getString(R.string.order_status_description_waite));
                    Glide.with(context).load(SharedPreferenceHelper.getUserAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.icon22).into(mUserHead);
                    mUserHead.setVisibility(View.VISIBLE);
                    mCancel.setVisibility(View.VISIBLE);
                }

            } else if(message.getStringAttribute(ChatConstant.KEY_GAME_STATUS).equals(ChatConstant.KEY_CANCEL_GAME_TYPE)){
                SharedPreferenceHelper.setGameStatus(message.getStringAttribute(ChatConstant.KEY_GAME_ID), message.getMsgId());
                initView();
                EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                mGameIntroduce.setText(String.format(ResourceUtils.getString(R.string.invite_game_format), Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME), true)));
                mGameAmount.setText(String.format("%s积分", body.getMessage()));
                mAdverseName.setText(Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME), true));
                mUserName.setText(Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_NAME), true));
                mWaitGame.setVisibility(View.VISIBLE);
                Glide.with(context).load(SharedPreferenceHelper.getUserAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.icon22).into(mUserHead);
                mUserHead.setVisibility(View.VISIBLE);
                mCancel.setVisibility(View.INVISIBLE);
                mWaitGame.setText(ResourceUtils.getString(R.string.order_status_description_cancel));

            }else if (message.getStringAttribute(ChatConstant.KEY_GAME_STATUS).equals(ChatConstant.KEY_GAME_REJECT) || message.getStringAttribute(ChatConstant.KEY_GAME_STATUS).equals("refused")) {
                initView();
                EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                if (message.direct() == EMMessage.Direct.RECEIVE) {
                    mGameAmount.setText(String.format("%s积分", body.getMessage()));
                    mGameIntroduce.setText(String.format(ResourceUtils.getString(R.string.invite_game_accept_or_refuse), Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_NAME), true)));
                    mAdverseName.setText(Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_NAME), true));
                    mUserName.setText(Utils.StrSubstring(3, SharedPreferenceHelper.getUserName(), true));
                    Glide.with(context).load(SharedPreferenceHelper.getUserAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.icon22).into(mUserHead);
                    mUserHead.setVisibility(View.VISIBLE);
                    mWaitGame.setVisibility(View.VISIBLE);
                    mWaitGame.setText(ResourceUtils.getString(R.string.order_status_description_reject));
                    emConversation.removeMessage(SharedPreferenceHelper.getGameStatus(message.getStringAttribute(ChatConstant.KEY_GAME_ID)));
                    mCancel.setVisibility(View.INVISIBLE);

                } else {
                    mGameAmount.setText(String.format("%s积分", body.getMessage()));
                    mGameIntroduce.setText(String.format(ResourceUtils.getString(R.string.invite_game_accept_or_refuse), Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME), true)));
                    mAdverseName.setText(Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME), true));
                    mUserName.setText(Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_NAME), true));
                    Glide.with(context).load(headUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.icon22).into(mAdverseHead);
                    mAdverseHead.setVisibility(View.VISIBLE);
                    mRefuseGame.setVisibility(View.VISIBLE);
                    mRefuseGame.setText(ResourceUtils.getString(R.string.order_status_description_reject));
                    mCancel.setVisibility(View.INVISIBLE);
                }

            } else if (message.getStringAttribute(ChatConstant.KEY_GAME_STATUS).equals(ChatConstant.KEY_GAME_ACCEPT)) {
                initView();
                EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                if (message.direct() == EMMessage.Direct.RECEIVE) {
                    mGameAmount.setText(String.format("%s积分", body.getMessage()));
                    if (message.getStringAttribute(ChatConstant.KEY_GAME_INVITE).equals(SharedPreferenceHelper.getEmchatId())) {
                        mGameIntroduce.setText(String.format(ResourceUtils.getString(R.string.invite_game_format), Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_NAME), true)));
                    } else {
                        mGameIntroduce.setText(String.format(ResourceUtils.getString(R.string.invite_game_accept_or_refuse), Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME), true)));
                    }
                    mAdverseName.setText(Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_NAME), true));
                    mUserName.setText(Utils.StrSubstring(3, SharedPreferenceHelper.getUserName(), true));
                    Glide.with(context).load(SharedPreferenceHelper.getUserAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.icon22).into(mUserHead);
                    mUserHead.setVisibility(View.VISIBLE);
                    mWaitGame.setVisibility(View.VISIBLE);
                    mWaitGame.setText(ResourceUtils.getString(R.string.accepted_order));
                    mCancel.setVisibility(View.INVISIBLE);
                    emConversation.removeMessage(SharedPreferenceHelper.getGameStatus(message.getStringAttribute(ChatConstant.KEY_GAME_ID)));
                    SharedPreferenceHelper.setGameStatus(message.getStringAttribute(ChatConstant.KEY_GAME_ID), message.getMsgId());

                } else {
                    mGameAmount.setText(String.format("%s积分", body.getMessage()));
                    mGameIntroduce.setText(String.format(ResourceUtils.getString(R.string.invite_game_accept_or_refuse), Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME), true)));
                    mAdverseName.setText(Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME), true));
                    mUserName.setText(Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_NAME), true));
                    Glide.with(context).load(headUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.icon22).into(mAdverseHead);
                    mAdverseHead.setVisibility(View.VISIBLE);
                    mRefuseGame.setVisibility(View.VISIBLE);
                    mRefuseGame.setText(ResourceUtils.getString(R.string.accepted_order));
                    mCancel.setVisibility(View.INVISIBLE);
                }
            }
            mCancel.setOnClickListener(v ->{
                if(mGameCancelListener !=null){
                    mGameCancelListener.onCancel(message);
                    mWaitGame.setText(ResourceUtils.getString(R.string.order_status_description_cancel));
                    mCancel.setVisibility(View.INVISIBLE);
                }
            });

        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUpBaseView() {
        TextView timestamp = (TextView) findViewById(R.id.time);
        if (timestamp != null) {
            timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
            timestamp.setVisibility(View.VISIBLE);
        }
        UserUtils.setUserAvatar(context, SharedPreferenceHelper.getEmchatId(),userAvatarView);

    }

    private void initView() {
        mWaitGame.setVisibility(View.GONE);
        mAdverseHead.setVisibility(View.GONE);
        mUserHead.setVisibility(View.GONE);
        mRefuseGame.setVisibility(View.GONE);

    }
    public void setGameCancelListener(GameCancelListener listener){
        this.mGameCancelListener = listener;
    }
    public interface GameCancelListener{
        void onCancel(EMMessage message);

    }
}
