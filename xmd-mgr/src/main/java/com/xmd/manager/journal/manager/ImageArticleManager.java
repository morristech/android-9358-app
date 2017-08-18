package com.xmd.manager.journal.manager;

import com.xmd.manager.beans.JournalTemplateImageArticleBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 17-1-3.
 */
public class ImageArticleManager {
    private static ImageArticleManager ourInstance = new ImageArticleManager();

    public static ImageArticleManager getInstance() {
        return ourInstance;
    }

    private ImageArticleManager() {
    }

    List<JournalTemplateImageArticleBean> mTemplates;


    public List<JournalTemplateImageArticleBean> getTemplates(int templateId) {
        if (mTemplates == null) {
            mTemplates = new ArrayList<>();
        } else {
            mTemplates.clear();
        }
        mTemplates.addAll(JournalManager.getInstance().getTemplateById(templateId).getTemplateIamges());
        return mTemplates;
    }

    public JournalTemplateImageArticleBean getTemplateById(String templateId) {
        if (mTemplates != null) {
            for (JournalTemplateImageArticleBean template : mTemplates) {
                if (template.id.equals(templateId)) {
                    return template;
                }
            }
        }

        return null;
    }

    public void clear() {
        mTemplates = null;
    }


}
