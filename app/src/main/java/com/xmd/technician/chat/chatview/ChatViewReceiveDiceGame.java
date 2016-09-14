package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.bean.RefreshView;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Util;
import com.xmd.technician.common.Utils;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.GameSettingDialog;

/**
 * Created by Administrator on 2016/8/22.
 */
public class ChatViewReceiveDiceGame extends BaseChatView {
    private GameManagerListener mGameManagerListener;
    private TextView mGameDetail;
    private TextView acceptOrRefuse;
    private Button mBtnAccept, mBtnRefuse;
    String gameId, gameStatus;
    long gameStartTime, currentTime;
    private EMConversation mEmConversation;

    public ChatViewReceiveDiceGame(Context context, EMMessage.Direct direct, EMConversation emConversation) {
        super(context, direct);
        this.mEmConversation = emConversation;

    }

    @Override
    protected void onFindViewById() {
        findViewById(R.id.game_container).setVisibility(View.VISIBLE);
        mGameDetail = (TextView) findViewById(R.id.game_need_integral);
        acceptOrRefuse = (TextView) findViewById(R.id.accept_or_refuse_invite);
        mBtnAccept = (Button) findViewById(R.id.game_accept);
        mBtnRefuse = (Button) findViewById(R.id.game_refuse);
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String content = body.getMessage();
        mGameDetail.setText(String.format(ResourceUtils.getString(R.string.dice_game_integral), content));
        try {
            gameStartTime = message.getMsgTime();
            currentTime = System.currentTimeMillis();
            gameId = message.getStringAttribute(ChatConstant.KEY_GAME_ID);
            gameStatus = message.getStringAttribute(ChatConstant.KEY_GAME_STATUS);
            if ((currentTime - gameStartTime > SharedPreferenceHelper.getGameTimeout())&&SharedPreferenceHelper.getGameStatus(gameId).equals(ChatConstant.KEY_REQUEST_GAME)) {
                acceptOrRefuse.setText(String.format("(%s)", "已超时"));
                acceptOrRefuse.setVisibility(View.VISIBLE);
                mBtnRefuse.setEnabled(false);
                mBtnAccept.setEnabled(false);
                return;
            } else if (gameStatus.equals(ChatConstant.KEY_CANCEL_GAME_TYPE)) {
                mEmConversation.removeMessage(SharedPreferenceHelper.getGameMessageId(gameId));
                acceptOrRefuse.setText(String.format("(%s)", "已取消"));
                acceptOrRefuse.setVisibility(View.VISIBLE);
                mBtnRefuse.setEnabled(false);
                mBtnAccept.setEnabled(false);
            } else if (Utils.isNotEmpty(SharedPreferenceHelper.getGameStatus(gameId)) && SharedPreferenceHelper.getGameStatus(gameId).equals(ChatConstant.KEY_GAME_REJECT)) {
                acceptOrRefuse.setText(String.format("(%s)", "已拒绝"));
                acceptOrRefuse.setVisibility(View.VISIBLE);
                mBtnRefuse.setEnabled(false);
                mBtnAccept.setEnabled(false);
                return;
            } else if (Utils.isNotEmpty(SharedPreferenceHelper.getGameStatus(gameId)) && (SharedPreferenceHelper.getGameStatus(gameId).equals(ChatConstant.KEY_OVER_GAME_TYPE)
                    || SharedPreferenceHelper.getGameStatus(gameId).equals(ChatConstant.KEY_ACCEPT_GAME))) {
                acceptOrRefuse.setText(String.format("(%s)", "已接受"));
                 SharedPreferenceHelper.setGameStatus(gameId,ChatConstant.KEY_ACCEPT_GAME);
                acceptOrRefuse.setVisibility(View.VISIBLE);
                mBtnRefuse.setEnabled(false);
                mBtnAccept.setEnabled(false);
            } else {
                acceptOrRefuse.setVisibility(View.GONE);
                mBtnRefuse.setEnabled(true);
                mBtnAccept.setEnabled(true);
                SharedPreferenceHelper.setGameStatus(gameId,ChatConstant.KEY_REQUEST_GAME);
                SharedPreferenceHelper.setGameMessageId(gameId,message.getMsgId());
            }

        } catch (HyphenateException e) {
            e.printStackTrace();
        }

        mBtnAccept.setOnClickListener(v -> {
            if (mGameManagerListener != null) {
                currentTime = System.currentTimeMillis();
                if((currentTime - gameStartTime > SharedPreferenceHelper.getGameTimeout())){
                    acceptOrRefuse.setText(String.format("(%s)", "已超时"));
                    acceptOrRefuse.setVisibility(View.VISIBLE);
                    mBtnRefuse.setEnabled(false);
                    mBtnAccept.setEnabled(false);
                }else{
                    mBtnRefuse.setEnabled(false);
                    mBtnAccept.setEnabled(false);
                    mGameManagerListener.onAccept(message);
                }

            }
        });
        mBtnRefuse.setOnClickListener(v -> {
            currentTime = System.currentTimeMillis();
            if((currentTime - gameStartTime > SharedPreferenceHelper.getGameTimeout())){
                acceptOrRefuse.setText(String.format("(%s)", "已超时"));
                acceptOrRefuse.setVisibility(View.VISIBLE);
                mBtnRefuse.setEnabled(false);
                mBtnAccept.setEnabled(false);
            }else{
                if (mGameManagerListener != null) {
                    mGameManagerListener.onRefuse(message);
                    mBtnRefuse.setEnabled(false);
                    mBtnAccept.setEnabled(false);
                    SharedPreferenceHelper.setGameStatus(gameId, ChatConstant.KEY_GAME_REJECT);
                    SharedPreferenceHelper.setGameMessageId(gameId,message.getMsgId());
                }
            }

        });
    }

    public void setGameManagerListener(GameManagerListener listener) {
        this.mGameManagerListener = listener;
    }

    public interface GameManagerListener {
        void onAccept(EMMessage message);

        void onRefuse(EMMessage message);
    }
}
