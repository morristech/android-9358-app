package com.xmd.manager.journal.widget;

import android.view.View;

/**
 * Created by heyangya on 16-11-9.
 */

public interface IContentView {
    void notifyDataChanged();

    void notifyItemChanged(int index);

    void onDestroy();

    void clearData();

    View getView();
}
