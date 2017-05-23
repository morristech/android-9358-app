package com.xmd.manager.journal.model;

import android.text.TextUtils;

import com.xmd.manager.common.Logger;

/**
 * Created by heyangya on 16-12-7.
 */

public class JournalItemArticle extends JournalItemBase {
    private int articleId;
    private String articleTitle;
    private String articleContent;

    public JournalItemArticle(int articleId, String articleTitle, String articleContent) {
        super("");
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.articleContent = articleContent;
    }

    public JournalItemArticle(String data) {
        super(data);
        if (!TextUtils.isEmpty(data)) {
            try {
                articleId = Integer.parseInt(data);
            } catch (Exception ignore) {
                Logger.e("can not covert " + data + " to a article id");
            }
        }
    }

    @Override
    public String contentToString() {
        if (articleId > 0) {
            return String.valueOf(articleId);
        } else {
            return null;
        }
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }
}
