package com.xmd.technician.model;

import android.content.Intent;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.user.User;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.message.ChatMessage;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.TechApplication;
import com.xmd.technician.bean.HelloTemplateInfo;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ThreadPoolManager;
import com.xmd.technician.http.RetrofitServiceFactory;
import com.xmd.technician.http.gson.HelloReplyResult;

import java.io.File;
import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zr on 17-3-18.
 * 用来管理技师打招呼
 */

public class HelloSettingManager {
    private Call<HelloReplyResult> mHelloReplyCheck;

    private Integer templateId;
    private Integer templateParentId;   //为null:代表自定义模版
    private String templateContentText;
    private String templateImageId;
    private String templateImageUrl;
    private String templateImageLink;

    private String templateImageCachePath;

    private static HelloSettingManager ourInstance = new HelloSettingManager();

    public static HelloSettingManager getInstance() {
        return ourInstance;
    }

    private HelloSettingManager() {
    }

    public void resetTemplate() {
        templateId = null;
        templateParentId = null;
        templateContentText = null;
        templateImageId = null;
        templateImageUrl = null;
        templateImageLink = null;
        templateImageCachePath = null;
    }

    public void setTemplate(HelloTemplateInfo info) {
        templateId = info.id;
        templateParentId = info.parentId;
        templateImageId = info.contentImageId;
        templateContentText = info.contentText;
        templateImageUrl = info.contentImageUrl;
        templateImageLink = info.contentImageLink;
    }

    public Integer getTemplateParentId() {
        return templateParentId;
    }

    public String getTemplateImageId() {
        return templateImageId;
    }

    /**
     * 返回自定义模版ID
     *
     * @return
     */
    public Integer getTemplateId() {
        return templateId;
    }

    /**
     * 返回自定义模版内容
     *
     * @return
     */
    public String getTemplateContentText() {
        return templateContentText;
    }

    /**
     * 返回模版图片URL
     *
     * @return
     */
    public String getTemplateImageUrl() {
        return templateImageUrl;
    }

    /**
     * 返回模版图片跳转链接
     *
     * @return
     */
    public String getTemplateImageLink() {
        return templateImageLink;
    }

    /**
     * 缓存图片到本地
     * 利用Glide缓存
     */
    public void getCacheFilePath() {
        ThreadPoolManager.run(() -> {
            FutureTarget<File> futureTarget = Glide.with(TechApplication.getAppContext())
                    .load(templateImageUrl)
                    .downloadOnly(0, 0);
            try {
                File cacheFile = futureTarget.get();
                templateImageCachePath = cacheFile.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                templateImageCachePath = null;
            }
        });
    }

    /**
     * 获取缓存图片的本地路径
     *
     * @return
     */
    public String getTemplateImageCachePath() {
        return templateImageCachePath;
    }

    /**
     * 打招呼
     */
    public void sendHelloTemplate(User user) {
        if (user == null) {
            XToast.show("参数错误");
            return;
        }
        // 招呼文本
        EMMessage txtMessage = EMMessage.createTxtSendMessage(templateContentText.replace(TechApplication.getAppContext().getResources().getString(R.string.hello_setting_content_replace), user.getName()), user.getChatId());
        emSendMessage(txtMessage);
        if (!TextUtils.isEmpty(templateImageCachePath)) {
            // 招呼图片
            EMMessage imgMessage = EMMessage.createImageSendMessage(templateImageCachePath, false, user.getChatId());
            emSendMessage(imgMessage);
        }
    }

    private void emSendMessage(EMMessage msg) {
        ChatMessage message = ChatMessageFactory.get(msg);
        message.addTag(ChatMessage.MSG_TAG_HELLO);
        message.setUserId(SharedPreferenceHelper.getUserId());
        message.setUserName(SharedPreferenceHelper.getUserName());
        message.setUserAvatar(SharedPreferenceHelper.getUserAvatar());
        message.setTime(String.valueOf(System.currentTimeMillis()));

        message.setClubId(SharedPreferenceHelper.getUserClubId());
        message.setClubName(SharedPreferenceHelper.getUserClubName());
        message.setTechNo(SharedPreferenceHelper.getSerialNo());

        //TODO REMOVE
        message.setTechId(SharedPreferenceHelper.getUserId());

        EMClient.getInstance().chatManager().sendMessage(message.getEmMessage());
    }

    public void checkHelloReply() {
        mHelloReplyCheck = RetrofitServiceFactory.getSpaService().checkHelloReply(SharedPreferenceHelper.getUserToken());
        mHelloReplyCheck.enqueue(new Callback<HelloReplyResult>() {
            @Override
            public void onResponse(Call<HelloReplyResult> call, Response<HelloReplyResult> response) {
                // APP内部回复通知
                HelloReplyResult result = response.body();
                if (result != null && result.respData != null && result.respData.size() > 0) {
                    Intent intent = new Intent();
                    intent.setAction(Constant.ACTION_HELLO_REPLY_RECEIVER);
                    intent.putExtra(Constant.EXTRA_HELLO_REPLY_INFO, (Serializable) result.respData);
                    TechApplication.getAppContext().sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<HelloReplyResult> call, Throwable t) {
                Logger.e(t.getLocalizedMessage());
            }
        });
    }
}
