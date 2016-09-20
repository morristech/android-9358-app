package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.DateUtils;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.UserWin;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.UserUtils;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.msgctrl.RxBus;
import java.util.Date;


/**
 * Created by Administrator on 2016/8/24.
 */
public class ChatViewPlayDiceGame extends BaseChatView {

    private TextView mAdverseName, mUserName, mGameResult, mLoseOrWin, mPlayAgain;
    private ImageView mAdverseDice, mUserDice, mAdverseWin, mUserWin, mUserResult;
    private LinearLayout diceResult;
    private int adverseDice, userDice;
    private EMConversation emConversation;
    private GameManagerListener gameManagerListener;

    @Override
    protected void onInflateView() {
        LayoutInflater.from(context).inflate(R.layout.chat_received_game_respond, this);
    }

    public ChatViewPlayDiceGame(Context context, EMMessage.Direct direct, EMConversation emConversation) {
        super(context, direct);
        this.emConversation = emConversation;
    }

    @Override
    protected void onFindViewById() {
        mAdverseName = (TextView) findViewById(R.id.adverse_name);
        mUserName = (TextView) findViewById(R.id.user_name);
        mGameResult = (TextView) findViewById(R.id.dice_game_amount);
        mPlayAgain = (TextView) findViewById(R.id.dice_game_again);
        mAdverseDice = (ImageView) findViewById(R.id.adverse_dice);
        mUserDice = (ImageView) findViewById(R.id.user_dice);
        mAdverseWin = (ImageView) findViewById(R.id.img_adverse_win);
        mUserWin = (ImageView) findViewById(R.id.img_user_win);
        mUserResult = (ImageView) findViewById(R.id.user_win_or_lose);
        diceResult = (LinearLayout) findViewById(R.id.dice_game_result);
    }

    @Override
    protected void onSetUpView() {
        String messageId = message.getMsgId();
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String content = body.getMessage();
        try {
            String result = message.getStringAttribute(ChatConstant.KEY_GAME_RESULT);
            String adverseName = message.getStringAttribute(ChatConstant.KEY_NAME);
            mUserName.setText(Utils.StrSubstring(3, SharedPreferenceHelper.getUserName(), true));
            if (message.direct() == EMMessage.Direct.RECEIVE) {
                userDice = getSendDiceResult(result);
                adverseDice = getRecvDiceResult(result);
                mAdverseName.setText(Utils.StrSubstring(3, adverseName, true));
            } else {
                userDice = getRecvDiceResult(result);
                adverseDice = getSendDiceResult(result);
                mAdverseName.setText(Utils.StrSubstring(3, message.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME), true));
            }
            emConversation.removeMessage(SharedPreferenceHelper.getGameMessageId(message.getStringAttribute(ChatConstant.KEY_GAME_ID)));
            if (Utils.isNotEmpty(SharedPreferenceHelper.getGameStatus(messageId))&&SharedPreferenceHelper.getGameStatus(messageId).equals(ChatConstant.KEY_OVER_GAME_TYPE)) {
                synchronized (this){
                    initResultView(content,messageId);
                }

            } else {
                mUserResult.setVisibility(View.GONE);
                diceResult.setVisibility(View.GONE);
                mGameResult.setVisibility(View.GONE);
                mUserWin.setVisibility(View.GONE);
                mAdverseWin.setVisibility(View.GONE);
                Glide.with(context).load(R.drawable.dice_gif).asGif().into(mUserDice);
                Glide.with(context).load(R.drawable.dice_gif).asGif().into(mAdverseDice);

                ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {

                    @Override
                    public void run() {
                        synchronized (this){
                            initResultView(content,messageId);
                        }
                    }
                }, 2000);
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        mPlayAgain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gameManagerListener.playAgain(message);
            }
        });
    }

    @Override
    public void setUpBaseView() {
        TextView timestamp = (TextView) findViewById(R.id.time);
        if (timestamp != null) {
            timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
            timestamp.setVisibility(View.VISIBLE);
        }
        UserUtils.setUserAvatar(context, SharedPreferenceHelper.getEmchatId(), userAvatarView);
    }

    private int getSendDiceResult(String result) {
        return Integer.parseInt(result.substring(0, 1));
    }

    private int getRecvDiceResult(String result) {
        return Integer.parseInt(result.substring(2, 3));
    }

    private void initImageDice(int adverseDice, ImageView image) {
        switch (adverseDice) {
            case 1:
                Glide.with(context).load(R.drawable.dice_one).error(R.drawable.dice_one).into(image);
                break;
            case 2:
                Glide.with(context).load(R.drawable.dice_two).error(R.drawable.dice_two).into(image);
                break;
            case 3:
                Glide.with(context).load(R.drawable.dice_three).error(R.drawable.dice_three).into(image);
                break;
            case 4:
                Glide.with(context).load(R.drawable.dice_four).error(R.drawable.dice_four).into(image);
                break;
            case 5:
                Glide.with(context).load(R.drawable.dice_five).error(R.drawable.dice_five).into(image);
                break;
            case 6:
                Glide.with(context).load(R.drawable.dice_six).error(R.drawable.dice_six).into(image);
                break;
            case 0:
                Glide.with(context).load(R.drawable.dice_six).error(R.drawable.dice_six).into(image);
                break;
        }
    }
    private void initResultView(String content,String messageId) {
        initImageDice(userDice, mUserDice);
        initImageDice(adverseDice, mAdverseDice);
        mUserResult.setVisibility(View.VISIBLE);
        diceResult.setVisibility(View.VISIBLE);
        mGameResult.setVisibility(View.VISIBLE);
        SpannableString msp = null;
        if (userDice > adverseDice ) {
            Glide.with(context).load(R.drawable.dice_win).asBitmap().into(mUserResult);
            mGameResult.setText(String.format("+%s", content));
            msp = new SpannableString(String.format(ResourceUtils.getString(R.string.win_and_play_again), content));
            mUserWin.setVisibility(View.VISIBLE);
            mAdverseWin.setVisibility(View.GONE);
            RxBus.getInstance().post(new UserWin(message.getMsgId()));
        } else {
            Glide.with(context).load(R.drawable.dice_lose).asBitmap().into(mUserResult);
            mGameResult.setText(String.format("-%s", content));
            msp = new SpannableString(String.format(ResourceUtils.getString(R.string.failed_and_play_again), content));
            mAdverseWin.setVisibility(View.VISIBLE);
            mUserWin.setVisibility(View.GONE);
         SharedPreferenceHelper.setGameStatus(messageId,ChatConstant.KEY_OVER_GAME_TYPE);
        }
        msp.setSpan(new UnderlineSpan(), msp.length() - 4, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new ForegroundColorSpan(ResourceUtils.getColor(R.color.play_game_again)), msp.length() - 4, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mPlayAgain.setText(msp);
        mPlayAgain.setMovementMethod(LinkMovementMethod.getInstance());
        mPlayAgain.setClickable(true);
    }

    public void setGameManagerListener(GameManagerListener listener) {
        this.gameManagerListener = listener;
    }

    public interface GameManagerListener {
        void playAgain(EMMessage message);

    }

}
