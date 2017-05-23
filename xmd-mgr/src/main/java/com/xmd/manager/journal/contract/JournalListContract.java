package com.xmd.manager.journal.contract;

import com.xmd.manager.journal.BasePresenter;
import com.xmd.manager.journal.BaseView;
import com.xmd.manager.journal.model.Journal;

import java.util.List;

/**
 * Created by heyangya on 16-10-31.
 */

public interface JournalListContract {
    interface Presenter extends BasePresenter {
        void publishJournal(Journal journal, int viewPosition);

        void offlineJournal(Journal journal, int viewPosition);

        void deleteJournal(Journal journal);

        void editJournal(Journal journal);

        void viewJournal(Journal journal);

        void newJournal();

        void refreshJournal();
    }

    interface View extends BaseView<Presenter> {
        void showLoadingJournalList();

        void showJournalList(List<Journal> journals);

        void showLoadJournalListError(String error);

        void updateJournalList();

        void updateJournalListItem(int index);

        void setResult(int resultCode);
    }
}
