package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.ClubJournalBean;
import com.xmd.technician.bean.OnceCardItemBean;
import com.xmd.technician.widget.CircleImageView;

import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Lhj on 2017/05/15.
 */
public class ChatShareAdapter<T> extends RecyclerView.Adapter {
    private List<T> mData;
    private Context mContext;
    private OnItemClickListener mOnItemClick;

    private static final int CLUB_JOURNAL_TYPE = 0x001;
    private static final int ONCE_CARD_TYPE = 0x002;
    private static final int DEFAULT_TYPE = 0x099;


    public ChatShareAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mData = data;
    }

    public void setData(List<T> data) {
        this.mData = data;
        notifyDataSetChanged();
    }


    public interface OnItemClickListener<T> {
        void onItemCheck(T info, int position, boolean idChecked);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mOnItemClick = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position) instanceof ClubJournalBean) {
            return CLUB_JOURNAL_TYPE;
        } else if (mData.get(position) instanceof OnceCardItemBean) {
            return ONCE_CARD_TYPE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case CLUB_JOURNAL_TYPE:
                View viewClubJournal = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_club_journal_chat_share_item, parent, false);
                return new ClubJournalItemViewHolder(viewClubJournal);

            default:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false);
                return new ClubJournalItemViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ClubJournalItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof ClubJournalBean)) {
                return;
            }
            final ClubJournalBean clubJournal = (ClubJournalBean) obj;
            ClubJournalItemViewHolder clubJournalHolder = (ClubJournalItemViewHolder) holder;
            if (position == 0) {
                clubJournalHolder.journalMark.setVisibility(View.VISIBLE);
            } else {
                clubJournalHolder.journalMark.setVisibility(View.GONE);
            }

            if (clubJournal.selectedStatus == 1) {
                clubJournalHolder.journalSelect.setSelected(false);
                clubJournalHolder.itemView.setOnClickListener(v -> mOnItemClick.onItemCheck(clubJournal, position, false));
            } else {
                clubJournalHolder.journalSelect.setSelected(true);
                clubJournalHolder.itemView.setOnClickListener(v -> mOnItemClick.onItemCheck(clubJournal, position, true));
            }
            Glide.with(mContext).load(clubJournal.image).into(clubJournalHolder.journalHead);
            clubJournalHolder.journalName.setText(clubJournal.title);
            clubJournalHolder.journalReleaseTime.setText(String.format("%s发布", clubJournal.modifyDate));
            return;
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ClubJournalItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.journal_head)
        CircleImageView journalHead;
        @BindView(R.id.journal_name)
        TextView journalName;
        @BindView(R.id.journal_mark)
        TextView journalMark;
        @BindView(R.id.journal_release_time)
        TextView journalReleaseTime;
        @BindView(R.id.journal_select)
        TextView journalSelect;

        ClubJournalItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class ListFooterHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_footer)
        TextView itemFooter;

        public ListFooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}


