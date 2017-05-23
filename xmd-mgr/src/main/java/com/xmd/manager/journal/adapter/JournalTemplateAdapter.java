package com.xmd.manager.journal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.manager.R;
import com.xmd.manager.journal.contract.JournalTemplateChoiceContract;
import com.xmd.manager.journal.model.Journal;
import com.xmd.manager.journal.model.JournalTemplate;

import java.util.List;

/**
 * Created by heyangya on 16-11-1.
 */

public class JournalTemplateAdapter extends RecyclerView.Adapter<JournalTemplateAdapter.ViewHolder> {
    private JournalTemplateChoiceContract.Presenter mPresenter;
    private List<JournalTemplate> mTemplates;
    private Journal mJournal;

    public JournalTemplateAdapter(JournalTemplateChoiceContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public void setData(List<JournalTemplate> templates, Journal journal) {
        mTemplates = templates;
        mJournal = journal;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_journal_template, parent, false);
        return new ViewHolder(view, mPresenter);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mTemplates != null) {
            holder.bind(mTemplates.get(position), mJournal);
        }
    }

    @Override
    public int getItemCount() {
        if (mTemplates != null) {
            return mTemplates.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;
        private View mSelectedView;
        private Button mViewButton;
        private ImageView mImageView;
        private JournalTemplateChoiceContract.Presenter mPresenter;

        private JournalTemplate mTemplate;

        public ViewHolder(View itemView, JournalTemplateChoiceContract.Presenter presenter) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_title);
            mSelectedView = itemView.findViewById(R.id.tv_selected);
            mImageView = (ImageView) itemView.findViewById(R.id.img_icon);
            mViewButton = (Button) itemView.findViewById(R.id.btn_view);
            mViewButton.setOnClickListener(mOnClickView);
            mImageView.setOnClickListener(mOnClickImageView);
            mPresenter = presenter;
        }

        public void bind(JournalTemplate template, Journal journal) {
            mTemplate = template;
            mTitleTextView.setText(template.getName());
            if (template.getId() == journal.getTemplate().getId()) {
                mSelectedView.setVisibility(View.VISIBLE);
            } else {
                mSelectedView.setVisibility(View.GONE);
            }
            Glide.with(itemView.getContext()).load(template.getImageUrl()).into(mImageView);
        }

        private View.OnClickListener mOnClickView = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onViewTemplate(mTemplate);
            }
        };

        private View.OnClickListener mOnClickImageView = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onSelectTemplate(mTemplate);
            }
        };
    }
}
