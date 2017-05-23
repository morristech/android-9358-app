package com.xmd.manager.journal.contract;

import com.xmd.manager.journal.BasePresenter;
import com.xmd.manager.journal.BaseView;
import com.xmd.manager.journal.model.Journal;
import com.xmd.manager.journal.model.JournalContentType;
import com.xmd.manager.journal.model.JournalTemplate;

import java.util.List;

/**
 * Created by heyangya on 16-10-31.
 */

public interface JournalTemplateChoiceContract {
    interface Presenter extends BasePresenter {
        void onViewTemplate(JournalTemplate template);

        void onSelectTemplate(JournalTemplate template);

        void onSelectTemplateItem(JournalContentType templateItem, boolean selected);

        void onEditContent();

        void onKeyBack();

        void reloadData();
    }

    interface View extends BaseView<Presenter> {
        void showLoadingTemplates();

        void showTemplates(List<JournalTemplate> templates, Journal journal, List<JournalContentType> unCancelContentTypes);

        void showLoadingTemplatesError();

        void showSelectedTemplate(Journal journal);

        void setNextButtonEnable(boolean enable);

        int getJournalIdFromIntent();
    }
}
