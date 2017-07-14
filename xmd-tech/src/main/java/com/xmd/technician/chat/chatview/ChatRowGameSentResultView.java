package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.PlayDiceGame;
import com.xmd.technician.bean.UserWin;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.utils.UserUtils;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.CircleAvatarView;

/**
 * Created by Lhj on 17-4-11.
 */

public class ChatRowGameSentResultView extends BaseEaseChatView {

    private TextView mAdverseName, mUserName, mGameResult, mLoseOrWin, mPlayAgain;
    private ImageView mAdverseDice, mUserDice, mAdverseWin, mUserWin, mUserResult;
    private LinearLayout diceResult;
    private int adverseDice, userDice;
    private CircleAvatarView avatar;


    public ChatRowGameSentResultView(Context context, EMMessage message, int position, BaseAdapter adapter, String gameStaus) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        mInflater.inflate(R.layout.chat_row_sent_game, this);
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
        avatar = (CircleAvatarView) findViewById(R.id.avatar);
    }

    @Override
    protected void onUpdateView() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSetUpView() {
        String messageId = mEMMessage.getMsgId();
        EMTextMessageBody body = (EMTextMessageBody) mEMMessage.getBody();
        String content = body.getMessage();
        String result = "";
        String adverseName = "";
        try {
            result = mEMMessage.getStringAttribute(ChatConstant.KEY_GAME_RESULT);
            adverseName = mEMMessage.direct() == EMMessage.Direct.RECEIVE ? mEMMessage.getStringAttribute(ChatConstant.KEY_NAME) : mEMMessage.getStringAttribute(ChatConstant.KEY_ADVERSE_NAME);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        UserUtils.setUserAvatar(mContext, EMClient.getInstance().getCurrentUser(), mUserAvatarView);
        mUserName.setText(Utils.StrSubstring(3, SharedPreferenceHelper.getUserName(), true));
        if (mEMMessage.direct() == EMMessage.Direct.RECEIVE) {
            userDice = getSendDiceResult(result);
            adverseDice = getRecvDiceResult(result);
            mAdverseName.setText(Utils.StrSubstring(3, adverseName, true));
        } else {
            userDice = getRecvDiceResult(result);
            adverseDice = getSendDiceResult(result);
            mAdverseName.setText(Utils.StrSubstring(3, adverseName, true));
        }
        if (SharedPreferenceHelper.getGameStatus(messageId).equals(ChatConstant.KEY_OVER_GAME_STATUS)) {
            synchronized (this) {
                initResultView(content, messageId);
            }

        } else {
            mUserResult.setVisibility(View.GONE);
            diceResult.setVisibility(View.GONE);
            mGameResult.setVisibility(View.GONE);
            mUserWin.setVisibility(View.INVISIBLE);
            mAdverseWin.setVisibility(View.INVISIBLE);
            Glide.with(mContext).load(R.drawable.dice_gif).asGif().into(mUserDice);
            Glide.with(mContext).load(R.drawable.dice_gif).asGif().into(mAdverseDice);
            SharedPreferenceHelper.setGameStatus(messageId, ChatConstant.KEY_OVER_TIME_GAME_STATUS);
            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {

                @Override
                public void run() {
                    synchronized (this) {
                        initResultView(content, messageId);
                    }
                }
            }, 2000);
        }
        mPlayAgain.setOnClickListener(v -> {
            RxBus.getInstance().post(new PlayDiceGame(content));
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
                    if (ChatConstant.ERROR_IN_BLACKLIST.equals(errorCode)) {
                        mProgressBar.setVisibility(View.GONE);
                        mStatusView.setVisibility(View.GONE);
                    } else {
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

    private void initResultView(String content, String messageId) {
        initImageDice(userDice, mUserDice);
        initImageDice(adverseDice, mAdverseDice);
        mUserResult.setVisibility(View.VISIBLE);
        diceResult.setVisibility(View.VISIBLE);
        mGameResult.setVisibility(View.VISIBLE);
        SpannableString msp = null;
        if (userDice > adverseDice) {
            Glide.with(mContext).load(R.drawable.dice_win).asBitmap().into(mUserResult);
            mGameResult.setText(String.format("+%s", content));
            msp = new SpannableString(String.format(ResourceUtils.getString(R.string.win_and_play_again), content));
            mUserWin.setVisibility(View.VISIBLE);
            mAdverseWin.setVisibility(View.INVISIBLE);
            RxBus.getInstance().post(new UserWin(mEMMessage.getMsgId()));
        } else {
            Glide.with(mContext).load(R.drawable.dice_lose).asBitmap().into(mUserResult);
            mGameResult.setText(String.format("-%s", content));
            msp = new SpannableString(String.format(ResourceUtils.getString(R.string.failed_and_play_again), content));
            mAdverseWin.setVisibility(View.VISIBLE);
            mUserWin.setVisibility(View.INVISIBLE);
            SharedPreferenceHelper.setGameStatus(messageId, ChatConstant.KEY_OVER_GAME_TYPE);
        }
        msp.setSpan(new UnderlineSpan(), msp.length() - 4, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new ForegroundColorSpan(ResourceUtils.getColor(R.color.play_game_again)), msp.length() - 4, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mPlayAgain.setText(msp);
        mPlayAgain.setMovementMethod(LinkMovementMethod.getInstance());
        mPlayAgain.setClickable(true);
    }

    private void initImageDice(int adverseDice, ImageView image) {
        switch (adverseDice) {
            case 1:
                Glide.with(mContext).load(R.drawable.dice_one).error(R.drawable.dice_one).into(image);
                break;
            case 2:
                Glide.with(mContext).load(R.drawable.dice_two).error(R.drawable.dice_two).into(image);
                break;
            case 3:
                Glide.with(mContext).load(R.drawable.dice_three).error(R.drawable.dice_three).into(image);
                break;
            case 4:
                Glide.with(mContext).load(R.drawable.dice_four).error(R.drawable.dice_four).into(image);
                break;
            case 5:
                Glide.with(mContext).load(R.drawable.dice_five).error(R.drawable.dice_five).into(image);
                break;
            case 6:
                Glide.with(mContext).load(R.drawable.dice_six).error(R.drawable.dice_six).into(image);
                break;
            case 0:
                Glide.with(mContext).load(R.drawable.dice_six).error(R.drawable.dice_six).into(image);
                break;
        }
    }

    private int getSendDiceResult(String result) {
        return Integer.parseInt(result.substring(0, 1));
    }

    private int getRecvDiceResult(String result) {
        return Integer.parseInt(result.substring(2, 3));
    }

    @Override
    protected void onBubbleClick() {

    }
}
