package com.xmd.technician.model;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.xmd.technician.TechApplication;
import com.xmd.technician.bean.HelloTemplateInfo;

import java.io.File;

/**
 * Created by zr on 17-3-18.
 * 用来管理技师打招呼
 */

public class HelloSettingManager {
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

    public void getCacheFilePath() {
        // 利用Glide缓存
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
    }

    public String getTemplateImageCachePath() {
        return templateImageCachePath;
    }
}
