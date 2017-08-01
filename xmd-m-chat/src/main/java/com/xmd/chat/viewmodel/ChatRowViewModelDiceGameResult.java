package com.xmd.chat.viewmodel;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.ChatAccountManager;
import com.xmd.chat.MessageManager;
import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowDiceGameResultBinding;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.DiceGameChatMessage;
import com.xmd.chat.message.TipChatMessage;


/**
 * Created by mo on 17-7-1.
 * 骰子游戏消息 -- 邀请界面, 处理邀请，取消邀请，邀请被接受、被拒绝、超时
 */

public class ChatRowViewModelDiceGameResult extends ChatRowViewModel {
    private DiceGameChatMessage message;
    private String myName;
    private String friendName;

    private ChatRowDiceGameResultBinding binding;
    public ObservableBoolean inProcess = new ObservableBoolean();

    public ChatRowViewModelDiceGameResult(ChatMessage chatMessage) {
        super(chatMessage);
        message = (DiceGameChatMessage) chatMessage;
        myName = ChatAccountManager.getInstance().getUser().getName();
        User friend = UserInfoServiceImpl.getInstance().getUserByChatId(message.getRemoteChatId());
        friendName = friend.getShowName();
    }

    public static View createView(ViewGroup parent) {
        ChatRowDiceGameResultBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.chat_row_dice_game_result, parent, false);
        return binding.getRoot();
    }


    @Override
    public ViewDataBinding onBindView(View view) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
        return binding;
    }

    @Override
    protected boolean contentViewMatchParent() {
        return true;
    }

    @Override
    public Drawable getContentViewBackground(Context context) {
        return null;
    }

    @Override
    public void onUnbindView() {

    }

    public String getMyName() {
        return myName;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getCredit() {
        return message.getCredit();
    }

    public boolean getWin() {
        return message.getMyPoint() > message.getRemotePoint();
    }

    public int getFriendPoint() {
        return message.getRemotePoint();
    }

    public int getMyPoint() {
        return message.getMyPoint();
    }

    @BindingAdapter({"data", "point"})
    public static void bindPoint(final ImageView view, final ChatRowViewModelDiceGameResult data, final int point) {
        if (data.getChatMessage().getInnerProcessed() != null) {
            view.setImageResource(getPointResource(point));
        } else {
            data.inProcess.set(true);
            Glide.with(view.getContext())
                    .load(R.drawable.dice_gif)
                    .asGif()
                    .into(view);
            ThreadPoolManager.postToUIDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setImageResource(getPointResource(point));
                    if (data.getInnerProcessed() == null) {
                        //发送再来一局消息
                        String msg = (data.message.getMyPoint() > data.message.getRemotePoint() ? "获得" : "消费") + data.getCredit() + "积分，再玩一局";
                        TipChatMessage tipChatMessage = TipChatMessage.create(data.message.getRemoteChatId(), msg, TipChatMessage.TIP_TYPE_PLAY_DICE);
                        MessageManager.getInstance().sendTipMessage(tipChatMessage);
                    }
                    data.inProcess.set(false);
                    data.chatMessage.setInnerProcessed("show");
                }
            }, 1500);
        }
    }

    private static int getPointResource(int point) {
        switch (point) {
            case 1:
                return R.drawable.dice_one;
            case 2:
                return R.drawable.dice_two;
            case 3:
                return R.drawable.dice_three;
            case 4:
                return R.drawable.dice_four;
            case 5:
                return R.drawable.dice_five;
            case 6:
                return R.drawable.dice_six;
        }
        return -1;
    }
}
