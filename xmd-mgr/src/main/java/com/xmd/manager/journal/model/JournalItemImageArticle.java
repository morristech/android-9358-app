package com.xmd.manager.journal.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.xmd.manager.service.response.JournalImageArticleJson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 17-1-3.
 */

public class JournalItemImageArticle extends JournalItemBase {
    public String templateId;
    public List<AlbumPhoto> imageList = new ArrayList<>();
    public List<String> articleList = new ArrayList<>();

    public JournalItemImageArticle(String data) {
        super(data);
        if (!TextUtils.isEmpty(data)) {
            try {
                JournalImageArticleJson result = new Gson().fromJson(data, JournalImageArticleJson.class);
                templateId = result.templateId;
                if (result.imageIds != null) {
                    for (String imageId : result.imageIds) {
                        AlbumPhoto image = new AlbumPhoto();
                        image.setImageId(imageId);
                        image.setNeedUpload(false);
                        imageList.add(image);
                    }
                }
                if (result.articles != null && result.articles.size() > 0) {
                    articleList.addAll(result.articles);
                }
            } catch (Exception ignore) {

            }
        }
    }

    @Override
    public String contentToString() {
        if (!TextUtils.isEmpty(templateId)) {
            JournalImageArticleJson result = new JournalImageArticleJson();
            result.templateId = templateId;
            if (imageList.size() > 0) {
                result.imageIds = new ArrayList<>();
                for (AlbumPhoto image : imageList) {
                    result.imageIds.add(image.getImageId());
                }
            }
            if (articleList.size() > 0) {
                result.articles = articleList;
            }
            try {
                return new Gson().toJson(result);
            } catch (Exception ignore) {

            }
        }
        return null;
    }

    @Override
    public String isDataReady() {
        if (imageList.size() > 0) {
            for (AlbumPhoto image : imageList) {
                if (TextUtils.isEmpty(image.getLocalPath()) && TextUtils.isEmpty(image.getImageId())) {
                    return "请选择图片";
                }
                if (image.isNeedUpload()) {
                    return "请上传图片";
                }
            }
        }
        if (articleList.size() > 0) {
            for (String article : articleList) {
                if (TextUtils.isEmpty(article)) {
                    return "文字不能为空";
                }
            }
        }
        return "true";
    }

    @Override
    public JournalItemBase clone() throws CloneNotSupportedException {
        JournalItemImageArticle copy = (JournalItemImageArticle) super.clone();
        copy.imageList = new ArrayList<>();
        for (AlbumPhoto image : imageList) {
            copy.imageList.add(image.clone());
        }
        copy.articleList = new ArrayList<>();
        if (articleList.size() > 0) {
            copy.articleList.addAll(articleList);
        }
        return copy;
    }
}
