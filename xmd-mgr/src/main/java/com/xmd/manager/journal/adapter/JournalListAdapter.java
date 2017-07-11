package com.xmd.manager.journal.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.journal.contract.JournalListContract;
import com.xmd.manager.journal.model.Journal;

import java.util.List;

/**
 * Created by heyangya on 16-10-31.
 */

public class JournalListAdapter extends RecyclerView.Adapter<JournalListAdapter.ViewHolder> {
    private JournalListContract.Presenter mPresenter;
    private List<Journal> mJournals;

    public JournalListAdapter(JournalListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public void setData(List<Journal> journals) {
        mJournals = journals;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.chat_share_list_item_wrapper, parent, false);
        return new ViewHolder(view, mPresenter);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mJournals != null) {
            holder.bind(mJournals.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        if (mJournals != null) {
            return mJournals.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private JournalListContract.Presenter mPresenter;

        private Journal mJournal;
        private int mItemPosition;

        private TextView mNoTextView;
        private TextView mStatusTextView;
        private TextView mTitleTextView;
        private TextView mViewCountTextView;
        private TextView mPraisedCountTextView;
        private TextView mSharedCountTextView;
        private TextView mLastModifiedTimeTextView;
        private View mPreviewButton;

        private TextView mEditButton;
        private TextView mPublishButton;
        private TextView mOfflineButton;
        private TextView mDeleteButton;

        private int colorLightGray;
        private int colorLightGreen;
        private int colorLightYellow;

        public ViewHolder(View itemView, JournalListContract.Presenter presenter) {
            super(itemView);
            mPresenter = presenter;
            mNoTextView = (TextView) itemView.findViewById(R.id.tv_no);
            mStatusTextView = (TextView) itemView.findViewById(R.id.tv_status);
            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_title);
            mViewCountTextView = (TextView) itemView.findViewById(R.id.tv_viewCount);
            mPraisedCountTextView = (TextView) itemView.findViewById(R.id.tv_praisedCount);
            mSharedCountTextView = (TextView) itemView.findViewById(R.id.tv_sharedCount);
            mLastModifiedTimeTextView = (TextView) itemView.findViewById(R.id.tv_last_modified_time);
            mPreviewButton = itemView.findViewById(R.id.btn_preview);

            mEditButton = (TextView) itemView.findViewById(R.id.btn_edit);
            mPublishButton = (TextView) itemView.findViewById(R.id.btn_publish);
            mOfflineButton = (TextView) itemView.findViewById(R.id.btn_offline);
            mDeleteButton = (TextView) itemView.findViewById(R.id.btn_delete);

            mEditButton.setOnClickListener(mOnClickEdit);
            mPublishButton.setOnClickListener(mOnClickPublish);
            mOfflineButton.setOnClickListener(mOnClickOffline);
            mDeleteButton.setOnClickListener(mOnClickDelete);
            mPreviewButton.setOnClickListener(mOnClickViewJournal);

            Resources resources = itemView.getContext().getResources();
            colorLightGray = resources.getColor(R.color.light_gray);
            colorLightGreen = resources.getColor(R.color.light_green);
            colorLightYellow = resources.getColor(R.color.light_yellow);
        }

        public void bind(Journal journal, int position) {
            mItemPosition = position;
            mJournal = journal;
            if (journal.getJournalNo() != 0) {
                mNoTextView.setVisibility(View.VISIBLE);
                mNoTextView.setText("NO." + journal.getJournalNo());
            } else {
                mNoTextView.setVisibility(View.GONE);
            }

            mTitleTextView.setText(journal.getTitle());
            mViewCountTextView.setText(journal.getViewCount() + "查看");
            mPraisedCountTextView.setText(journal.getPraisedCount() + "赞");
            mSharedCountTextView.setText(journal.getSharedCount() + "分享");


            switch (journal.getStatus()) {
                case Journal.STATUS_DRAFT:
                    mStatusTextView.setText("草稿");
                    mStatusTextView.setTextColor(colorLightGray);
                    mStatusTextView.setBackgroundResource(R.drawable.stroke_light_gray);
                    mEditButton.setVisibility(View.VISIBLE);
                    mPublishButton.setVisibility(View.VISIBLE);
                    mOfflineButton.setVisibility(View.GONE);
                    mLastModifiedTimeTextView.setText(journal.getLastModifiedTime() + " 编辑");
                    break;
                case Journal.STATUS_PUBLISHED:
                    mStatusTextView.setText("已发布");
                    mStatusTextView.setTextColor(colorLightGreen);
                    mStatusTextView.setBackgroundResource(R.drawable.stroke_light_green);
                    mEditButton.setVisibility(View.GONE);
                    mPublishButton.setVisibility(View.GONE);
                    mOfflineButton.setVisibility(View.VISIBLE);
                    mLastModifiedTimeTextView.setText(journal.getLastModifiedTime() + " 发布");
                    break;
                case Journal.STATUS_OFFLINE:
                    mStatusTextView.setText("已下架");
                    mStatusTextView.setTextColor(colorLightYellow);
                    mStatusTextView.setBackgroundResource(R.drawable.stroke_light_yellow);
                    mEditButton.setVisibility(View.VISIBLE);
                    mPublishButton.setVisibility(View.VISIBLE);
                    mOfflineButton.setVisibility(View.GONE);
                    mLastModifiedTimeTextView.setText(journal.getLastModifiedTime() + " 下架");
                    break;
            }
        }

        private View.OnClickListener mOnClickEdit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.editJournal(mJournal);
            }
        };

        private View.OnClickListener mOnClickPublish = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.publishJournal(mJournal, mItemPosition);
            }
        };

        private View.OnClickListener mOnClickOffline = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.offlineJournal(mJournal, mItemPosition);
            }
        };

        private View.OnClickListener mOnClickDelete = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteJournal(mJournal);
            }
        };

        private View.OnClickListener mOnClickViewJournal = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.viewJournal(mJournal);
            }
        };
    }
}
