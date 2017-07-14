package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.manager.R;
import com.xmd.manager.beans.GroupMemberBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.widget.CircularBeadImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 2016/12/30.
 */

public class SelectedMemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public interface ItemClickedInterface {
        void onItemClicked(GroupMemberBean bean, Integer position, boolean isChecked);
    }

    private static final int TYPE_MEMBER_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_DADA_IS_EMPTY = 2;

    private boolean mIsNoMore = false;
    private boolean mIsEmpty = false;
    private List<GroupMemberBean> mData = null;
    private Context mContext;
    private ItemClickedInterface mInterface;


    public SelectedMemberAdapter(Context context, List<GroupMemberBean> data, ItemClickedInterface itemInterface) {
        mContext = context;
        mData = data;
        mInterface = itemInterface;

    }

    public void setData(List<GroupMemberBean> data) {
        mData = data;
        mIsEmpty = data.isEmpty();
        notifyDataSetChanged();
    }

    public void setIsNoMore(boolean isNoMore) {
        mIsNoMore = isNoMore;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_MEMBER_ITEM:
                return new MemberItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_member_list_item, parent, false));
            case TYPE_FOOTER:
                return new ListFooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false));
            default:
                return null;

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ListFooterHolder) {
            ListFooterHolder footerHolder = (ListFooterHolder) holder;
            String desc = ResourceUtils.getString(R.string.order_list_item_loading);
            if (mIsEmpty) {
                desc = ResourceUtils.getString(R.string.order_list_item_empty);
                footerHolder.itemFooter.setOnClickListener(null);
            } else if (mIsNoMore) {
                desc = ResourceUtils.getString(R.string.order_list_item_no_more);
                footerHolder.itemFooter.setOnClickListener(null);
            }
            footerHolder.itemFooter.setText(desc);
        } else if (holder instanceof MemberItemHolder) {
            Object obj = mData.get(position);
            if (obj instanceof GroupMemberBean) {
                bindMemberItemViewHolder(holder, obj, position);
            }

        }
    }

    private void bindMemberItemViewHolder(RecyclerView.ViewHolder holder, Object obj, Integer position) {
        MemberItemHolder viewHolder = (MemberItemHolder) holder;
        GroupMemberBean member = (GroupMemberBean) obj;
        viewHolder.customerName.setText(Utils.StrSubstring(10, member.name, true));
        Glide.with(mContext).load(member.avatarUrl).error(R.drawable.default_rectangular_avatar).into(viewHolder.customerHead);
        viewHolder.customerPhone.setText(member.telephone);
        viewHolder.customerBadCommentCount.setText(String.format("差评 %s", member.badCommentCount));
        if (member.userType.equals("user")) {
            viewHolder.customerType.setText("粉丝");
        } else {
            viewHolder.customerType.setText(member.userType);
        }
        //isSelected; 1,已包含，2，已被选中，3，未被选中
        if (member.isSelected == 1) {
            viewHolder.customerSelectState.setBackgroundResource(R.drawable.had_selected);
            viewHolder.itemView.setClickable(false);
        } else if (member.isSelected == 2) {
            viewHolder.itemView.setClickable(true);
            viewHolder.customerSelectState.setBackgroundResource(R.drawable.is_selected);
            viewHolder.itemView.setOnClickListener(v -> mInterface.onItemClicked(member, position, true));
        } else {
            viewHolder.itemView.setClickable(true);
            viewHolder.customerSelectState.setBackgroundResource(R.drawable.can_selected);
            viewHolder.itemView.setOnClickListener(v -> mInterface.onItemClicked(member, position, false));
        }

    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else if (!mIsEmpty) {
            return TYPE_MEMBER_ITEM;
        } else {
            return TYPE_DADA_IS_EMPTY;
        }
    }

    static class MemberItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.customer_select_state)
        ImageView customerSelectState;
        @BindView(R.id.customer_head)
        CircularBeadImageView customerHead;
        @BindView(R.id.customer_name)
        TextView customerName;
        @BindView(R.id.customer_type)
        TextView customerType;
        @BindView(R.id.customer_phone)
        TextView customerPhone;
        @BindView(R.id.customer_bad_comment_count)
        TextView customerBadCommentCount;

        public MemberItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ListFooterHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_footer)
        TextView itemFooter;

        public ListFooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
