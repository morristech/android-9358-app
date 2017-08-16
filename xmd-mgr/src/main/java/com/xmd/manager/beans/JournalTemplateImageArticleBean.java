package com.xmd.manager.beans;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-8-15.
 */

public class JournalTemplateImageArticleBean {


    public String id;
    public List<Article> articles = new ArrayList<>();
    public int imageCount;
    public String coverUrl;

    public static class Article {
        public int wordsLimit;
        public String content;
    }

    @Override
    public String toString() {
        return "JournalTemplateImageArticleBean{" +
                "id='" + id + '\'' +
                ", articles=" + articles +
                ", imageCount=" + imageCount +
                ", coverUrl='" + coverUrl + '\'' +
                '}';
    }
}
