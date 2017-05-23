package com.xmd.technician.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.ClubJournalBean;
import com.xmd.technician.bean.OnceCardItemBean;
import com.xmd.technician.common.Utils;
import com.xmd.technician.widget.CircleImageView;
import com.xmd.technician.widget.RoundImageView;
import com.xmd.technician.widget.StartCustomTextView;

import java.util.List;

import butterknife.Bind;
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
            case ONCE_CARD_TYPE:
                View viewOnceCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_share_once_card_item, parent, false);
                return new OnceCardItemViewHolder(viewOnceCard);
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
        if (holder instanceof OnceCardItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof OnceCardItemBean)) {
                return;
            }
            final OnceCardItemBean onceCard = (OnceCardItemBean) obj;
            OnceCardItemViewHolder cardItemViewHolder = (OnceCardItemViewHolder) holder;
            Glide.with(mContext).load(onceCard.imageUrl).into(cardItemViewHolder.onceCardHead);
            cardItemViewHolder.onceCardTitle.setText(Utils.StrSubstring(6,onceCard.name,true));
            cardItemViewHolder.onceCardCredit.setMText(Utils.StrSubstring(12, onceCard.comboDescription, true).trim());
            cardItemViewHolder.onceCardCredit.setTextColor(Color.parseColor("#666666"));
            cardItemViewHolder.onceCardMoney.setMText(onceCard.techRoyalty);
            cardItemViewHolder.onceCardMoney.setTextColor(Color.parseColor("#fb5549"));
            if (onceCard.selectedStatus == 1) {
                cardItemViewHolder.onceCardSelect.setSelected(false);
                cardItemViewHolder.itemView.setOnClickListener(v -> mOnItemClick.onItemCheck(onceCard, position, false));
            } else {
                cardItemViewHolder.onceCardSelect.setSelected(true);
                cardItemViewHolder.itemView.setOnClickListener(v -> mOnItemClick.onItemCheck(onceCard, position, true));
            }
//            if (position == 0) {
//                cardItemViewHolder.onceCardMarkNew.setVisibility(View.VISIBLE);
//                cardItemViewHolder.onceCardMarkFavorable.setVisibility(View.GONE);
//            } else {
//                cardItemViewHolder.onceCardMarkNew.setVisibility(View.GONE);
//                if (onceCard.isPreferential) {
//                    cardItemViewHolder.onceCardMarkFavorable.setVisibility(View.VISIBLE);
//                } else {
//                    cardItemViewHolder.onceCardMarkFavorable.setVisibility(View.GONE);
//                }
//            }
            return;
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ClubJournalItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.journal_head)
        CircleImageView journalHead;
        @Bind(R.id.journal_name)
        TextView journalName;
        @Bind(R.id.journal_mark)
        TextView journalMark;
        @Bind(R.id.journal_release_time)
        TextView journalReleaseTime;
        @Bind(R.id.journal_select)
        TextView journalSelect;

        ClubJournalItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class ListFooterHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_footer)
        TextView itemFooter;

        public ListFooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OnceCardItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.once_card_head)
        RoundImageView onceCardHead;
        @Bind(R.id.once_card_mark_new)
        TextView onceCardMarkNew;
        @Bind(R.id.once_card_mark_favorable)
        TextView onceCardMarkFavorable;
        @Bind(R.id.once_card_title)
        TextView onceCardTitle;
        @Bind(R.id.once_card_credit)
        StartCustomTextView onceCardCredit;
        @Bind(R.id.once_card_money)
        StartCustomTextView onceCardMoney;
        @Bind(R.id.once_card_select)
        TextView onceCardSelect;

        OnceCardItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}


