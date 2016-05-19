package com.xmd.technician.chat;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.xmd.technician.R;

public class UserUtils {

    /**
     * 根据username获取相应user
     * @param username
     * @return
     */
    public static ChatUser getUserInfo(String username){
        return UserProfileProvider.getInstance().getChatUserInfo(username);
    }
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
        ChatUser user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            try {
                Glide.with(context).load(user.getAvatar()).into(imageView);
            } catch (Exception e) {
                //正常的string路径
                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.icon22).into(imageView);
            }
        }else{
            Glide.with(context).load(R.drawable.icon22).into(imageView);
        }
    }
    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
        	ChatUser user = getUserInfo(username);
        	if(user != null && user.getNick() != null){
        		textView.setText(user.getNick());
        	}else{
        		textView.setText(username);
        	}
        }
    }

    public static boolean userExisted(String username){
        return UserProfileProvider.getInstance().userExisted(username);
    }

    public static void saveUser(ChatUser user){
        UserProfileProvider.getInstance().saveContactInfo(user);
    }

    public static void updateUser(ChatUser user){
        UserProfileProvider.getInstance().updateContactInfo(user);
    }
}
