package com.xmd.technician.chat.chatview;

import java.util.Date;

import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.Direct;
import com.hyphenate.util.DateUtils;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.CommonUtils;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.chat.UserUtils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseChatView extends LinearLayout {

    protected LayoutInflater inflater;
    protected Context context;
    protected EMMessage message;
    protected EMMessage.Direct mDirect;

    protected TextView timeStampView;
    protected ImageView userAvatarView;

    protected Activity activity;

    public BaseChatView(Context context, EMMessage.Direct direct) {
        super(context);
        this.context = context;
        this.activity = (Activity) context;
        this.mDirect = direct;
        inflater = LayoutInflater.from(context);
        LayoutInflater.from(context).inflate(direct == EMMessage.Direct.RECEIVE ?
                R.layout.chat_received_item : R.layout.chat_sent_item, this);
        initView();
    }

    private void initView() {
//        onInflateView();
        timeStampView = (TextView) findViewById(R.id.time);
        userAvatarView = (ImageView) findViewById(R.id.avatar);

        onFindViewById();
    }

    /**
     * 根据当前message和position设置控件属性等
     *
     * @param message
     */
    public void setUpView(EMMessage message) {
        this.message = message;

        setUpBaseView();
        onSetUpView();
    }

    private void setUpBaseView() {
        // 设置用户昵称头像，bubble背景等
        TextView timestamp = (TextView) findViewById(R.id.time);
        if (timestamp != null) {
            timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
            timestamp.setVisibility(View.VISIBLE);
        }
        //设置头像和nick
        if(message.direct() == Direct.SEND){
            UserUtils.setUserAvatar(context, EMClient.getInstance().getCurrentUser(), userAvatarView);
        }else{
            UserUtils.setUserAvatar(context, message.getFrom(), userAvatarView);
        }
    }


    /**
     * 填充layout
     */
    protected abstract void onInflateView();

    /**
     * 查找chatrow里的控件
     */
    protected abstract void onFindViewById();

    /**
     * 设置更新控件属性
     */
    protected abstract void onSetUpView();
}
