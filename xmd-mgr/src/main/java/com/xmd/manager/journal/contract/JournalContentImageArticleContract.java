package com.xmd.manager.journal.contract;

import com.xmd.manager.journal.BasePresenter;
import com.xmd.manager.journal.model.ImageArticleTemplate;
import com.xmd.manager.journal.model.JournalItemImageArticle;

import java.util.List;

/**
 * Created by heyangya on 17-1-3.
 */

public class JournalContentImageArticleContract {
    public interface Presenter extends BasePresenter {
        void onClickSelectTemplate();

        void onTemplateViewCreate();

        void onSelectTemplate(ImageArticleTemplate template);

        void onDataSetChanged();

        void clearData();

        void showData(int index);

        void onClickUploadButton();

        void onTextChanged(int index, String text);

        void onClickChangeTemplate();
    }

    public interface View {
        void createView(ImageArticleTemplate template);

        void showData(JournalItemImageArticle data);

        void updateImageView(JournalItemImageArticle data, int index);

        void setUploadButtonVisible(boolean visible);

        void setUploadButtonText(String text);

        void onImageUploadWait(int index);

        void onImageUploadStart(int index);

        void onImageUploadProgress(int index, int progress);

        void onImageUploadFinished(int index, String error);

        void onImageUploadCanceled(int index);
    }

    public interface TemplateView {
        void setPresenter(Presenter presenter);

        void showLoadTemplateSuccess(List<ImageArticleTemplate> data, ImageArticleTemplate selected);
    }
}
