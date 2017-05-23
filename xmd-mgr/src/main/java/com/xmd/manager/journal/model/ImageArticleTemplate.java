package com.xmd.manager.journal.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 17-1-3.
 */

public class ImageArticleTemplate {
    public String id;
    public List<Article> articles = new ArrayList<>();
    public int imageCount;
    public String coverUrl;

    public static class Article {
        public int wordsLimit;
        public String content;
    }
}
