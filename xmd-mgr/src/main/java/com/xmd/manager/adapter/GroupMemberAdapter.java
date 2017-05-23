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
import com.xmd.manager.widget.CircularBeadImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 2016/12/5.
 */

public class GroupMemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onAddMember();

        void onDeleteMember();

        void onDeleteItem(int position);

        void onItemDetail(GroupMemberBean bean);
    }

    private static final int TYPE_MEMBER = 0;
    private static final int TYPE_ADD_MEMBER = 1;
    private static final int TYPE_DELETE_MEMBER = 2;

    private List<GroupMemberBean> mMembers;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private boolean mIsDelete;

    public GroupMemberAdapter(Context context, OnItemClickListener itemClickListener) {
        mContext = context;
        mOnItemClickListener = itemClickListener;
        mMembers = new ArrayList<>();
    }

    public void refreshDataSet(List<GroupMemberBean> GroupMemberBeans) {
        if (GroupMemberBeans != null) {
            mMembers.clear();
            mMembers.addAll(GroupMemberBeans);
            notifyDataSetChanged();
        }
    }

    public void refreshDataDeleterView(boolean isDelete) {
        mIsDelete = isDelete;
        notifyDataSetChanged();
    }

    public void deleteItemRefresh(GroupMemberBean GroupMemberBean) {
        mMembers.remove(GroupMemberBean);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_member_item, parent, false);
        return new GroupMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupMemberViewHolder) {
            GroupMemberViewHolder viewHolder = (GroupMemberViewHolder) holder;

            switch (getItemViewType(position)) {
                case TYPE_MEMBER:
                    GroupMemberBean bean = mMembers.get(position);
                    if (mIsDelete) {
                        viewHolder.groupMemberDelete.setVisibility(View.VISIBLE);
                        viewHolder.itemView.setOnClickListener(v -> {
                            mOnItemClickListener.onDeleteItem(position);
                        });
                    } else {
                        viewHolder.groupMemberDelete.setVisibility(View.GONE);
                        viewHolder.itemView.setOnClickListener(v -> {
                            mOnItemClickListener.onItemDetail(bean);
                        });
                    }
                    if (position <= mMembers.size()) {
                        Glide.with(mContext).load(mMembers.get(position).avatarUrl).error(R.drawable.default_rectangular_avatar).into(viewHolder.groupMemberHead);
                        viewHolder.groupMemberName.setText(mMembers.get(position).name);
                    }

                    break;
                case TYPE_ADD_MEMBER:
                    if (mIsDelete) {
                        viewHolder.groupMemberHead.setVisibility(View.GONE);
                    } else {
                        viewHolder.groupMemberHead.setVisibility(View.VISIBLE);
                        viewHolder.groupMemberHead.setImageResource(R.drawable.icon_add);
                        viewHolder.itemView.setOnClickListener(v -> mOnItemClickListener.onAddMember());
                    }

                    break;
                case TYPE_DELETE_MEMBER:
                    if (mIsDelete) {
                        viewHolder.groupMemberHead.setVisibility(View.GONE);
                    } else {
                        viewHolder.groupMemberHead.setVisibility(View.VISIBLE);
                        viewHolder.groupMemberHead.setImageResource(R.drawable.icon_reduce);
                        viewHolder.itemView.setOnClickListener(v -> mOnItemClickListener.onDeleteMember());
                    }

                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMembers.size() > 0 ? mMembers.size() + 2 : mMembers.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMembers.size() > 0) {
            if (position < getItemCount() - 2) {
                return TYPE_MEMBER;
            } else if (position == getItemCount() - 2) {
                return TYPE_ADD_MEMBER;
            } else if (position == getItemCount() - 1) {
                return TYPE_DELETE_MEMBER;
            } else {
                return TYPE_MEMBER;
            }
        } else {
            return TYPE_ADD_MEMBER;
        }

    }

    static class GroupMemberViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.group_member_head)
        CircularBeadImageView groupMemberHead;
        @Bind(R.id.group_member_name)
        TextView groupMemberName;
        @Bind(R.id.group_member_delete)
        ImageView groupMemberDelete;

        public GroupMemberViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
