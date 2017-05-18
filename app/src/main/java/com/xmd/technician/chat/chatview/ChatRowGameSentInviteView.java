package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.event.CancelGame;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.RoundImageView;

/**
 * Created by Lhj on 17-4-10.
 */

public class ChatRowGameSentInviteView extends BaseEaseChatView {

    private TextView mGameAmount, mAdverseName, mUserName, mCurrentGameStatus, mGameIntroduce, mCancel,mCancelOrReject;
    private RoundImageView mUserHead;
    long gameStartTime,currentTime;

    private String mGameStatus,mGameId;

    public ChatRowGameSentInviteView(Context context, EMMessage message, int position, BaseAdapter adapter, String gameStaus) {
        super(context, message, position, adapter);
        this.mGameStatus = gameStaus;
    }

    @Override
    protected void onInflateView() {
        mInflater.inflate(R.layout.chat_row_sent_game_invite,this);
    }

    @Override
    protected void onFindViewById() {
        mGameIntroduce = (TextView) findViewById(R.id.invite_game_top);
        mGameAmount = (TextView) findViewById(R.id.dice_game_amount);
        mAdverseName = (TextView) findViewById(R.id.adverse_name);
        mUserName = (TextView) findViewById(R.id.user_name);
        mCurrentGameStatus = (TextView) findViewById(R.id.game_status);
        mUserHead = (RoundImageView) findViewById(R.id.user_head);
        mCancel = (TextView) findViewById(R.id.game_cancel);
        mCancelOrReject = (TextView) findViewById(R.id.cancel_or_reject_alert);

    }

    @Override
    protected void onUpdateView() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody body = (EMTextMessageBody) mEMMessage.getBody();
        String content = body.getMessage();
        try {
            mGameId = mEMMessage.getStringAttribute(ChatConstant.KEY_GAME_ID);
            mGameIntroduce.setText(String.format(ResourceUtils.getString(R.string.invite_game_format), Utils.StrSubstring(3, mEMMessage.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME), true)));
            mAdverseName.setText(Utils.StrSubstring(3, mEMMessage.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME), true));
            mUserName.setText(Utils.StrSubstring(3, mEMMessage.getStringAttribute(ChatConstant.KEY_NAME), true));
            mGameAmount.setText(String.format(ResourceUtils.getString(R.string.dice_amount), content));
            Glide.with(mContext).load(SharedPreferenceHelper.getUserAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.icon22).into(mUserHead);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }

        if(Utils.isEmpty(SharedPreferenceHelper.getGameStatus(mGameId))){
            initGameStatusView(ChatConstant.KEY_REQUEST_GAME_STATUS,content);
        }else{
            initGameStatusView(SharedPreferenceHelper.getGameStatus(mGameId),content);
        }

        mCancel.setOnClickListener(v -> {
            gameStartTime = mEMMessage.getMsgTime();
            currentTime = System.currentTimeMillis();
            if((currentTime-gameStartTime)> SharedPreferenceHelper.getGameTimeout()){
               SharedPreferenceHelper.setGameStatus(mGameId, ChatConstant.KEY_OVER_TIME_GAME_STATUS);
               mAdapter.notifyDataSetChanged();
            }else{
                RxBus.getInstance().post(new CancelGame(mEMMessage));
            }

        });
      //  handleTextMessage();
    }
    protected void handleTextMessage() {
        if (mEMMessage.direct() == EMMessage.Direct.SEND) {
            setMessageSendCallback();
            switch (mEMMessage.status()) {
                case CREATE:
                    mProgressBar.setVisibility(View.GONE);
                    mStatusView.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    mProgressBar.setVisibility(View.GONE);
                    mStatusView.setVisibility(View.GONE);
                    break;
                case FAIL:
                    String errorCode = mEMMessage.getStringAttribute(ChatConstant.KEY_ERROR_CODE, ChatConstant.ERROR_SERVER_NOT_REACHABLE);
                    if(ChatConstant.ERROR_IN_BLACKLIST.equals(errorCode)){
                        mProgressBar.setVisibility(View.GONE);
                        mStatusView.setVisibility(View.GONE);
                    }else {
                        mProgressBar.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    break;
                case INPROGRESS:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mStatusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        } else {
            if (!mEMMessage.isAcked() && mEMMessage.getChatType() == EMMessage.ChatType.Chat) {
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(mEMMessage.getFrom(), mEMMessage.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void initGameStatusView(String gameStatus,String body) {

        if(gameStatus.equals(ChatConstant.KEY_REQUEST_GAME_STATUS)){
            mCurrentGameStatus.setText("等待接受");
            mCancelOrReject.setVisibility(View.GONE);
            mCancel.setVisibility(View.VISIBLE);
        }else if(gameStatus.equals(ChatConstant.KEY_CANCEL_GAME_STATUS)){
            mCurrentGameStatus.setText("已取消");
            mCancelOrReject.setVisibility(View.VISIBLE);
            mCancel.setVisibility(View.INVISIBLE);
            mCancelOrReject.setText(String.format("取消游戏，返还%s积分",body));
        }else if(gameStatus.equals(ChatConstant.KEY_REFUSED_GAME_STATUS)||gameStatus.equals(ChatConstant.KEY_GAME_REJECT)){
            mCurrentGameStatus.setText("已拒绝");
            mCancelOrReject.setVisibility(View.VISIBLE);
            mCancel.setVisibility(View.INVISIBLE);
            mCancelOrReject.setText(String.format("对方拒绝游戏，返还%s积分",body));
        }else if(gameStatus.equals(ChatConstant.KEY_ACCEPT_GAME_STATUS)||gameStatus.equals(ChatConstant.KEY_OVER_GAME_STATUS)){
            mCurrentGameStatus.setText("已接受");
            mCancelOrReject.setVisibility(View.GONE);
            mCancel.setVisibility(View.INVISIBLE);
        }else if(gameStatus.equals(ChatConstant.KEY_OVER_TIME_GAME_STATUS)){
            mCurrentGameStatus.setText("已超时");
            mCancelOrReject.setVisibility(View.VISIBLE);
            mCancel.setVisibility(View.INVISIBLE);
            mCancelOrReject.setText(String.format("游戏已超时，返还%s积分",body));
        }

    }

    @Override
    protected void onBubbleClick() {

    }
}
