package com.xmd.manager.journal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.journal.contract.JournalContentEditContract;
import com.xmd.manager.journal.model.Journal;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalContentType;

import java.util.List;

/**
 * Created by heyangya on 16-11-1.
 */

public class JournalContentEditAdapter extends RecyclerView.Adapter<JournalContentEditAdapter.ViewHolder> {
    private JournalContentEditContract.Presenter mPresenter;
    private Journal mJournal;
    private List<JournalContent> mDeletedContents;

    public static final int ITEM_VIEW_TYPE_HEADER = 0;
    public static final int ITEM_VIEW_TYPE_NORMAL = 1;
    public static final int ITEM_VIEW_TYPE_DELETED = 2;

    public JournalContentEditAdapter(JournalContentEditContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public void setData(Journal journal, List<JournalContent> deletedContents) {
        mJournal = journal;
        mDeletedContents = deletedContents;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view;
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(context).inflate(R.layout.list_header_journal_content, parent, false);
        } else if (viewType == ITEM_VIEW_TYPE_NORMAL) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_journal_content_common, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_journal_content_deleted_type, parent, false);
        }
        return new ViewHolder(viewType, view, mPresenter);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mJournal != null) {
            int viewType = getItemViewType(position);
            if (viewType == ITEM_VIEW_TYPE_HEADER) {
                holder.bind(mJournal.getTitle(), mJournal.getSubTitle());
            } else if (viewType == ITEM_VIEW_TYPE_NORMAL) {
                int realPos = position - 1;
                holder.bind(mJournal.getContent(realPos), realPos != 0, position);
            } else if (viewType == ITEM_VIEW_TYPE_DELETED) {
                int realPos = position - mJournal.getContentSize() - 1;
                holder.bind(mDeletedContents.get(realPos), realPos == 0);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mJournal != null) {
            return 1 + mJournal.getContentSize() + mDeletedContents.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_VIEW_TYPE_HEADER;
        }
        if (mJournal != null && mDeletedContents != null) {
            if (position >= mJournal.getContentSize() + 1) {
                return ITEM_VIEW_TYPE_DELETED;
            }
        }
        return ITEM_VIEW_TYPE_NORMAL;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private EditText mTitleEditText;
        private EditText mSubTitleEditText;

        private TextView mTitleTextView;
        private TextView mNameTextView;
        private EditText mContentTitleEditText;
        private View mCopyButton;
        private View mDeleteButton;
        private View mMoveUpButton;
        private View mRecoverButton;
        private FrameLayout mContentViewLayout;
        private View mDividerCopyDelete;
        private View mDividerDeleteMoveUp;

        private JournalContentEditContract.Presenter mPresenter;
        private JournalContent mContent;

        public ViewHolder(int viewType, View itemView, JournalContentEditContract.Presenter presenter) {
            super(itemView);

            if (viewType == ITEM_VIEW_TYPE_HEADER) {
                mTitleEditText = (EditText) itemView.findViewById(R.id.edt_title);
                mSubTitleEditText = (EditText) itemView.findViewById(R.id.edt_sub_title);
                mTitleEditText.addTextChangedListener(mTitleTextWatcher);
                mSubTitleEditText.addTextChangedListener(mSubTitleTextWatcher);
            } else if (viewType == ITEM_VIEW_TYPE_NORMAL) {
                mTitleTextView = (TextView) itemView.findViewById(R.id.tv_title);
                mContentTitleEditText = (EditText) itemView.findViewById(R.id.edt_content_title);
                mCopyButton = itemView.findViewById(R.id.btn_copy);
                mDeleteButton = itemView.findViewById(R.id.btn_delete);
                mMoveUpButton = itemView.findViewById(R.id.btn_move_up);
                mContentViewLayout = (FrameLayout) itemView.findViewById(R.id.content_view_layout);
                mDividerCopyDelete = itemView.findViewById(R.id.divider_copy_delete);
                mDividerDeleteMoveUp = itemView.findViewById(R.id.divider_delete_move_up);

                mCopyButton.setOnClickListener(mOnClickCopy);
                mDeleteButton.setOnClickListener(mOnClickDelete);
                mMoveUpButton.setOnClickListener(mOnClickMoveUp);
                mContentTitleEditText.addTextChangedListener(mContentTitleTextWatcher);
            } else if (viewType == ITEM_VIEW_TYPE_DELETED) {
                mTitleTextView = (TextView) itemView.findViewById(R.id.tv_title);
                mNameTextView = (TextView) itemView.findViewById(R.id.tv_name);
                mRecoverButton = (Button) itemView.findViewById(R.id.btn_recover);
                mRecoverButton.setOnClickListener(mOnClickRecover);
            }

            mPresenter = presenter;
        }

        public void bind(JournalContent content, boolean showMoveUp, int position) {
            mContent = content;
            mTitleTextView.setText(content.getType().getName());
            if (showMoveUp) {
                mMoveUpButton.setVisibility(View.VISIBLE);
                mDividerDeleteMoveUp.setVisibility(View.VISIBLE);
            } else {
                mMoveUpButton.setVisibility(View.GONE);
                mDividerDeleteMoveUp.setVisibility(View.GONE);
            }
            mContentViewLayout.removeAllViews();

            mContentTitleEditText.setVisibility(View.VISIBLE);
            mContentTitleEditText.setText(mContent.getTitle());
            mCopyButton.setVisibility(View.VISIBLE);
            mDividerCopyDelete.setVisibility(View.VISIBLE);
            switch (mContent.getType().getKey()) {
                case JournalContentType.CONTENT_KEY_TECHNICIAN_RANKING:
                    mCopyButton.setVisibility(View.GONE);
                    mDividerCopyDelete.setVisibility(View.GONE);
                case JournalContentType.CONTENT_KEY_IMAGE_ARTICLE:
                    mContentTitleEditText.setVisibility(View.GONE);
                    break;
                case JournalContentType.CONTENT_KEY_VIDEO:
                    mCopyButton.setVisibility(View.GONE);
                    mDividerCopyDelete.setVisibility(View.GONE);
                    break;
            }
            JournalContentEditViewHelper.setupContentView(itemView.getContext(), mContentViewLayout, mContent, mPresenter, position);
        }

        public void bind(JournalContent content, boolean showTitle) {
            mContent = content;
            if (showTitle) {
                mTitleTextView.setVisibility(View.VISIBLE);
            } else {
                mTitleTextView.setVisibility(View.GONE);
            }
            mNameTextView.setText(mContent.getType().getName());
        }

        public void bind(String title, String subTitle) {
            mTitleEditText.setText(title);
            mSubTitleEditText.setText(subTitle);
        }

        private View.OnClickListener mOnClickMoveUp = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onMoveUpContent(mContent);
            }
        };

        private View.OnClickListener mOnClickCopy = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCopyContent(mContent);
            }
        };

        private View.OnClickListener mOnClickDelete = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onDeleteContent(mContent);
            }
        };

        private View.OnClickListener mOnClickRecover = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onRecoverContent(mContent);
            }
        };

        private TextWatcher mTitleTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.onSetTitle(mTitleEditText.getText().toString());
            }
        };

        private TextWatcher mSubTitleTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.onSetSubTitle(mSubTitleEditText.getText().toString());
            }
        };

        private TextWatcher mContentTitleTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.onContentTitleChange(mContent, mContentTitleEditText.getText().toString());
            }
        };


    }
}
