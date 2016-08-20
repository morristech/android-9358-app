package com.xmd.technician.chat;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.gson.UpdateServiceResult;

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
            if(Utils.isNotEmpty(SharedPreferenceHelper.getUserRemark(username))){
                textView.setText(SharedPreferenceHelper.getUserRemark(username));
            }else{
                textView.setText(getUserNick(username));
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

    public static String getUserNick(String username){
        ChatUser user = UserProfileProvider.getInstance().getChatUserInfo(username);

        if(user != null){
            return user.getNick();
        }
        return ResourceUtils.getString(R.string.default_user_name);
    }

}
