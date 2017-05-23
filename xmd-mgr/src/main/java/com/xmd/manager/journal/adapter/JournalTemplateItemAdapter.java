package com.xmd.manager.journal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.journal.contract.JournalTemplateChoiceContract;
import com.xmd.manager.journal.model.Journal;
import com.xmd.manager.journal.model.JournalContentType;

import java.util.List;

/**
 * Created by heyangya on 16-11-1.
 */

public class JournalTemplateItemAdapter extends RecyclerView.Adapter<JournalTemplateItemAdapter.ViewHolder> {
    private JournalTemplateChoiceContract.Presenter mPresenter;
    private Journal mJournal;
    private List<JournalContentType> mUnCancelContentTypes;

    public JournalTemplateItemAdapter(JournalTemplateChoiceContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public void setData(Journal journal, List<JournalContentType> unCancelContentTypes) {
        mJournal = journal;
        mUnCancelContentTypes = unCancelContentTypes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_journal_template_item, parent, false);
        return new ViewHolder(view, mPresenter);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mJournal != null) {
            JournalContentType contentType = mJournal.getTemplate().getContentType(position);
            holder.bind(contentType, mJournal, mUnCancelContentTypes != null && mUnCancelContentTypes.contains(contentType));
        }
    }

    @Override
    public int getItemCount() {
        if (mJournal != null) {
            return mJournal.getTemplate().getContentTypeSize();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTextView;
        private CheckBox mCheckBox;
        private JournalTemplateChoiceContract.Presenter mPresenter;
        private JournalContentType mItem;

        public ViewHolder(View itemView, JournalTemplateChoiceContract.Presenter presenter) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.tv_name);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            itemView.setOnClickListener(mOnClickItemView);
            mCheckBox.setOnClickListener(mOnClickCheckBox);
            mPresenter = presenter;
        }

        public void bind(JournalContentType contentType, Journal journal, boolean unCancel) {
            mItem = contentType;
            mNameTextView.setText(contentType.getName());
            if (journal.isContentContainType(contentType)) {
                mCheckBox.setChecked(true);
            } else {
                mCheckBox.setChecked(false);
            }
            mCheckBox.setEnabled(!unCancel);
            itemView.setClickable(!unCancel);
        }

        private View.OnClickListener mOnClickItemView = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckBox.performClick();
            }
        };

        private View.OnClickListener mOnClickCheckBox = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onSelectTemplateItem(mItem, mCheckBox.isChecked());
            }
        };
    }
}
