package com.xmd.technician.model;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.TechApplication;
import com.xmd.technician.bean.HelloReplyInfo;
import com.xmd.technician.bean.HelloTemplateInfo;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatUser;
import com.xmd.technician.chat.UserUtils;
import com.xmd.technician.common.TechNotifier;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.http.RetrofitServiceFactory;
import com.xmd.technician.http.gson.HelloReplyResult;

import java.io.File;

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
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND, () -> {
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
     *
     * @param userName
     * @param userEmchatId
     */
    public void sendHelloTemplate(String userName, String userEmchatId, String userAvatar, String userType) {
        ChatUser chatUser = new ChatUser(userEmchatId);
        chatUser.setNick(userName);
        chatUser.setAvatar(userAvatar);
        chatUser.setUserType(userType);
        UserUtils.saveUser(chatUser);
        // 招呼文本
        EMMessage txtMessage = EMMessage.createTxtSendMessage(templateContentText.replace(TechApplication.getAppContext().getResources().getString(R.string.hello_setting_content_replace), userName), userEmchatId);
        emSendMessage(txtMessage);
        if (!TextUtils.isEmpty(templateImageCachePath)) {
            // 招呼图片
            EMMessage imgMessage = EMMessage.createImageSendMessage(templateImageCachePath, false, userEmchatId);
            emSendMessage(imgMessage);
        }
    }

    private void emSendMessage(EMMessage message) {
        message.setAttribute(ChatConstant.KEY_TECH_ID, SharedPreferenceHelper.getUserId());
        message.setAttribute(ChatConstant.KEY_NAME, SharedPreferenceHelper.getUserName());
        message.setAttribute(ChatConstant.KEY_HEADER, SharedPreferenceHelper.getUserAvatar());
        message.setAttribute(ChatConstant.KEY_TIME, String.valueOf(System.currentTimeMillis()));
        message.setAttribute(ChatConstant.KEY_SERIAL_NO, SharedPreferenceHelper.getSerialNo());
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    public void checkHelloReply() {
        mHelloReplyCheck = RetrofitServiceFactory.getSpaService().checkHelloReply(SharedPreferenceHelper.getUserToken());
        mHelloReplyCheck.enqueue(new Callback<HelloReplyResult>() {
            @Override
            public void onResponse(Call<HelloReplyResult> call, Response<HelloReplyResult> response) {
                HelloReplyResult result = response.body();
                if (result != null && result.respData != null && result.respData.size() > 0) {
                    HelloReplyInfo info = result.respData.get(0);
                    if (EMClient.getInstance().isConnected())
                        TechApplication.getNotifier().showNotification(TechNotifier.CHAT_TEXT, info.receiverEmChatId, info.receiverName);
                }
            }

            @Override
            public void onFailure(Call<HelloReplyResult> call, Throwable t) {

            }
        });
    }
}
