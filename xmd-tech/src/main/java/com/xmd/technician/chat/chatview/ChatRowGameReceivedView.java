package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.AcceptOrRejectGame;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lhj on 17-4-10.
 */

public class ChatRowGameReceivedView extends BaseEaseChatView {

    private TextView acceptOrRefusedMark;
    private TextView gameIntegral;
    private Button acceptGame, refusedGame;
    long gameStartTime, currentTime;
    String gameId, gameStatus;
    private LinearLayout llView;


    public ChatRowGameReceivedView(Context context, EMMessage message, int position, BaseAdapter adapter, String gameStatus) {
        super(context, message, position, adapter);

        this.gameStatus = gameStatus;
    }


    @Override
    protected void onInflateView() {
        mInflater.inflate(R.layout.chat_row_received_game, this);
    }

    @Override
    protected void onFindViewById() {
        acceptOrRefusedMark = (TextView) findViewById(R.id.accept_or_refuse_invite);
        gameIntegral = (TextView) findViewById(R.id.game_need_integral);
        acceptGame = (Button) findViewById(R.id.game_accept);
        refusedGame = (Button) findViewById(R.id.game_refuse);
        llView = (LinearLayout) findViewById(R.id.ll_view);


    }

    @Override
    protected void onUpdateView() {
        mAdapter.notifyDataSetChanged();
    }

    protected void handleTextMessage() {

        if (!mEMMessage.isAcked() && mEMMessage.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(mEMMessage.getFrom(), mEMMessage.getMsgId());
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onSetUpView() {

        try {
            gameId = mEMMessage.getStringAttribute(ChatConstant.KEY_GAME_ID);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        initGameView();
        // handleTextMessage();
    }

    private void initGameView() {
        EMTextMessageBody body = (EMTextMessageBody) mEMMessage.getBody();
        String content = body.getMessage();
        gameIntegral.setText(String.format(ResourceUtils.getString(R.string.dice_game_integral), content));
        gameStartTime = mEMMessage.getMsgTime();
        currentTime = System.currentTimeMillis();
        if (Utils.isEmpty(SharedPreferenceHelper.getGameStatus(gameId))) {
            viewShow(gameStatus);
        } else {
            viewShow(SharedPreferenceHelper.getGameStatus(gameId));
        }

        acceptGame.setOnClickListener(v -> {
            currentTime = System.currentTimeMillis();
            if (currentTime - gameStartTime > SharedPreferenceHelper.getGameTimeout()) {
                SharedPreferenceHelper.setGameStatus(gameId, ChatConstant.KEY_OVER_TIME_GAME_STATUS);
            } else {
                SharedPreferenceHelper.setGameStatus(gameId, ChatConstant.KEY_ACCEPT_GAME_STATUS);
                RxBus.getInstance().post(new AcceptOrRejectGame(body.getMessage(), gameId, ChatConstant.KEY_ACCEPT_GAME, mEMMessage.getFrom()));
            }

        });
        refusedGame.setOnClickListener(v -> {
            currentTime = System.currentTimeMillis();
            if (currentTime - gameStartTime > SharedPreferenceHelper.getGameTimeout()) {
                SharedPreferenceHelper.setGameStatus(gameId, ChatConstant.KEY_OVER_TIME_GAME_STATUS);
            } else {
                SharedPreferenceHelper.setGameStatus(gameId, ChatConstant.KEY_REFUSED_GAME_STATUS);
                Map<String, String> params = new HashMap<>();
                params.put(RequestConstant.KEY_DICE_GAME_ID, gameId.substring(5));
                params.put(RequestConstant.KEY_DICE_GAME_STATUS, ChatConstant.KEY_REFUSED_GAME_STATUS);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_GAME_ACCEPT_OR_REJECT, params);
            }
        });


    }

    private void viewShow(String gameStatus) {
        if (gameStatus.equals(ChatConstant.KEY_OVERTIME_GAME)) {
            acceptOrRefusedMark.setText(String.format("(%s)", ResourceUtils.getString(R.string.order_status_description_overtime)));
            acceptOrRefusedMark.setVisibility(View.VISIBLE);
            refusedGame.setEnabled(false);
            acceptGame.setEnabled(false);

        } else if (gameStatus.equals(ChatConstant.KEY_ACCEPT_GAME) || gameStatus.equals(ChatConstant.KEY_OVER_GAME_TYPE)) {
            acceptOrRefusedMark.setText(String.format("(%s)", ResourceUtils.getString(R.string.accepted_order)));
            acceptOrRefusedMark.setVisibility(View.VISIBLE);
            refusedGame.setEnabled(false);
            acceptGame.setEnabled(false);

        } else if (gameStatus.equals(ChatConstant.KEY_REFUSED_GAME_STATUS) || gameStatus.equals(ChatConstant.KEY_GAME_REJECT)) {
            acceptOrRefusedMark.setText(String.format("(%s)", ResourceUtils.getString(R.string.order_status_description_reject)));
            acceptOrRefusedMark.setVisibility(View.VISIBLE);
            refusedGame.setEnabled(false);
            acceptGame.setEnabled(false);

        } else if (gameStatus.equals(ChatConstant.KEY_CANCEL_GAME_TYPE)) {
            acceptOrRefusedMark.setText(String.format("(%s)", ResourceUtils.getString(R.string.order_status_description_cancel)));
            acceptOrRefusedMark.setVisibility(View.VISIBLE);
            refusedGame.setEnabled(false);
            acceptGame.setEnabled(false);

        } else if (gameStatus.equals(ChatConstant.KEY_REQUEST_GAME)) {
            acceptOrRefusedMark.setVisibility(View.GONE);
            refusedGame.setEnabled(true);
            acceptGame.setEnabled(true);

        } else if (gameStatus.equals(ChatConstant.KEY_GAME_DISABLE)) {
            acceptOrRefusedMark.setText(String.format("(%s)", ResourceUtils.getString(R.string.game_status_description_over)));
            acceptOrRefusedMark.setVisibility(View.VISIBLE);
            refusedGame.setEnabled(false);
            acceptGame.setEnabled(false);

        } else {
            acceptOrRefusedMark.setVisibility(View.GONE);
            refusedGame.setEnabled(false);
            acceptGame.setEnabled(false);

        }

    }

    @Override
    protected void onBubbleClick() {

    }
}
