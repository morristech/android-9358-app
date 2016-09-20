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
import com.xmd.technician.common.ResourceUtils;


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

    protected synchronized void onSetUpView() {
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String content = body.getMessage();
        mGameDetail.setText(String.format(ResourceUtils.getString(R.string.dice_game_integral), content));
        try {
            gameStartTime = message.getMsgTime();
            currentTime = System.currentTimeMillis();
            gameId = message.getStringAttribute(ChatConstant.KEY_GAME_ID);
            gameStatus = message.getStringAttribute(ChatConstant.KEY_GAME_STATUS);
            if(gameStatus .equals(ChatConstant.KEY_REQUEST_GAME)){
                if ((currentTime - gameStartTime > SharedPreferenceHelper.getGameTimeout())&&SharedPreferenceHelper.getGameStatus(gameId).equals(ChatConstant.KEY_REQUEST_GAME)) {
                    viewShow(ChatConstant.KEY_OVERTIME_GAME);
                    return;
                }else if(SharedPreferenceHelper.getGameStatus(gameId).equals(ChatConstant.KEY_ACCEPT_GAME)){
                    viewShow(ChatConstant.KEY_ACCEPT_GAME);
                }else if(SharedPreferenceHelper.getGameStatus(gameId).equals(ChatConstant.KEY_GAME_REJECT)){
                    viewShow(ChatConstant.KEY_GAME_REJECT);
                }else if(SharedPreferenceHelper.getGameStatus(gameId).equals(ChatConstant.KEY_GAME_DISABLE)){
                    viewShow(ChatConstant.KEY_GAME_DISABLE);
                }else {
                    viewShow(ChatConstant.KEY_REQUEST_GAME);
                    SharedPreferenceHelper.setGameStatus(gameId,ChatConstant.KEY_REQUEST_GAME);
                    SharedPreferenceHelper.setGameMessageId(gameId,message.getMsgId());
                }
            }else if(gameStatus.equals(ChatConstant.KEY_CANCEL_GAME_TYPE)){
                SharedPreferenceHelper.setGameStatus(gameId,ChatConstant.KEY_CANCEL_GAME_TYPE);
                mEmConversation.removeMessage(SharedPreferenceHelper.getGameMessageId(gameId));
                viewShow(ChatConstant.KEY_CANCEL_GAME_TYPE);
            }

        } catch (HyphenateException e) {
            e.printStackTrace();
        }

        mBtnAccept.setOnClickListener(v -> {
            if (mGameManagerListener != null) {
                currentTime = System.currentTimeMillis();
                if((currentTime - gameStartTime > SharedPreferenceHelper.getGameTimeout())){
                    acceptOrRefuse.setText(String.format("(%s)", ResourceUtils.getString(R.string.order_status_description_overtime)));
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
                acceptOrRefuse.setText(String.format("(%s)", ResourceUtils.getString(R.string.order_status_description_overtime)));
                acceptOrRefuse.setVisibility(View.VISIBLE);
                mBtnRefuse.setEnabled(false);
                mBtnAccept.setEnabled(false);
            }else{
                if (mGameManagerListener != null) {
                    mGameManagerListener.onRefuse(message);
                    mBtnRefuse.setEnabled(false);
                    mBtnAccept.setEnabled(false);
                }
            }

        });
    }
    private void viewShow(String gameStatus){
      if(gameStatus.equals(ChatConstant.KEY_OVERTIME_GAME)){
          acceptOrRefuse.setText(String.format("(%s)", ResourceUtils.getString(R.string.order_status_description_overtime)));
          acceptOrRefuse.setVisibility(View.VISIBLE);
          mBtnRefuse.setEnabled(false);
          mBtnAccept.setEnabled(false);
      }else if(gameStatus.equals(ChatConstant.KEY_ACCEPT_GAME )|| gameStatus.equals(ChatConstant.KEY_OVER_GAME_TYPE)){
          acceptOrRefuse.setText(String.format("(%s)", ResourceUtils.getString(R.string.accepted_order)));
          acceptOrRefuse.setVisibility(View.VISIBLE);
          mBtnRefuse.setEnabled(false);
          mBtnAccept.setEnabled(false);
        }else if(gameStatus.equals(ChatConstant.KEY_GAME_REJECT)){
          acceptOrRefuse.setText(String.format("(%s)", ResourceUtils.getString(R.string.order_status_description_reject)));
          acceptOrRefuse.setVisibility(View.VISIBLE);
          mBtnRefuse.setEnabled(false);
          mBtnAccept.setEnabled(false);
      }else if(gameStatus.equals(ChatConstant.KEY_CANCEL_GAME_TYPE)){
          acceptOrRefuse.setText(String.format("(%s)", ResourceUtils.getString(R.string.order_status_description_cancel)));
          acceptOrRefuse.setVisibility(View.VISIBLE);
          mBtnRefuse.setEnabled(false);
          mBtnAccept.setEnabled(false);
      }else if(gameStatus.equals(ChatConstant.KEY_REQUEST_GAME)){
          acceptOrRefuse.setVisibility(View.GONE);
          mBtnRefuse.setEnabled(true);
          mBtnAccept.setEnabled(true);
      }else if(gameStatus.equals(ChatConstant.KEY_GAME_DISABLE)){
          acceptOrRefuse.setText(String.format("(%s)", ResourceUtils.getString(R.string.game_status_description_over)));
          acceptOrRefuse.setVisibility(View.VISIBLE);
          mBtnRefuse.setEnabled(false);
          mBtnAccept.setEnabled(false);
      }else {
          acceptOrRefuse.setVisibility(View.GONE);
          mBtnRefuse.setEnabled(false);
          mBtnAccept.setEnabled(false);
      }

    }

    public void setGameManagerListener(GameManagerListener listener) {
        this.mGameManagerListener = listener;
    }

    public interface GameManagerListener {
        void onAccept(EMMessage message);

        void onRefuse(EMMessage message);
    }
}
