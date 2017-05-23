package com.xmd.manager.chat;

/**
 * Created by linms@xiaomodo.com on 16-5-23.
 */

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;

import java.util.HashMap;
import java.util.Map;

public class EmchatUserHelper {
    /**
     * 设置用户头像
     *
     * @param username
     */
    public static void setUserAvatarAndNick(Context context, String username, ImageView tvAvatar, TextView tvNickname) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.KEY_ID, username);
        params.put(Constant.KEY_CALLBACK, new EmchatManager.IGetEmchatUserCallback() {
            @Override
            public void onGetUserFromDb(EmchatUser emchatUser) {
                ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, () -> {
                    if (tvAvatar != null) {
                        if (Utils.isNotEmpty(emchatUser.getAvatar())) {
                            try {
                                Glide.with(context).load(emchatUser.getAvatar()).into(tvAvatar);
                            } catch (Exception e) {
                                //正常的string路径
                                Glide.with(context).load(emchatUser.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.icon22).into(tvAvatar);
                            }
                        } else {
                            Glide.with(context).load(R.drawable.icon22).into(tvAvatar);
                        }
                    }
                    if (tvNickname != null) {
                        if (Utils.isNotEmpty(emchatUser.getNick())) {
                            tvNickname.setText(Utils.briefString(emchatUser.getNick(), 10));
                        } else {
                            tvNickname.setText(Utils.briefString("匿名用户", 10));
                        }
                    }
                });
            }
        });
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CHAT_USER, params);
    }

    public static void saveUser(String emchatId, String nickname, String avatar) {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SAVE_CHAT_USER, new EmchatUser(emchatId, nickname, avatar));
    }

    /**
     * @param emchatId
     * @param nickname
     * @param avatar
     */
    public static void startToChat(String emchatId, String nickname, String avatar) {
        startToChat(emchatId, nickname, avatar, null);
    }

    public static void startToChat(String emchatId, String nickname, String avatar, Object object) {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, CommonUtils.wrapChatParams(emchatId, nickname, avatar, object, ""));
    }

    public static void startToChat(String emchatId, String nickname, String avatar, Object object, String userType) {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, CommonUtils.wrapChatParams(emchatId, nickname, avatar, object, userType));
    }

    /**
     * login emchat
     */
    public static void login(Runnable runnable) {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGIN_EMCHAT, runnable);
    }

    /**
     * logout emchat
     */
    public static void logout() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGOUT_EMCHAT);
    }


}

