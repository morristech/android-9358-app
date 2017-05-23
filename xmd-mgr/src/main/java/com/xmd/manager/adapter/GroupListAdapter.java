package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.GroupBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lhj on 2016/12/10.
 */

public class GroupListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_GROUP = 0;
    private static final int TYPE_ADD_GROUP = 1;

    private OnItemClickedListener mItemClickedListener;
    private List<GroupBean> mData;
    private Context mContext;


    public interface OnItemClickedListener {
        void onGroupItemClicked(GroupBean ben, int position);
    }


    public GroupListAdapter(Context context) {

        this.mContext = context;

    }

    public void setGroupData(List<GroupBean> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public void setItemClickedListener(OnItemClickedListener listener) {
        this.mItemClickedListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_GROUP:
                View view = LayoutInflater.from(mContext).inflate(R.layout.message_group_list_item, parent, false);
                return new GroupViewHolder(view);
            case TYPE_ADD_GROUP:
                View viewAdd = LayoutInflater.from(mContext).inflate(R.layout.message_group_list_add_item, parent, false);
                return new GroupAddViewHolder(viewAdd);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupViewHolder) {
            GroupViewHolder groupHolder = (GroupViewHolder) holder;
            GroupBean groupBean = mData.get(position);
            groupHolder.groupName.setText(String.format(groupBean.name + "(%s)", String.valueOf(groupBean.totalCount)));
            groupHolder.groupDescription.setText(groupBean.description);
            if (null != groupBean.isChecked && groupBean.isChecked) {
                groupHolder.groupName.setSelected(true);
            } else {
                groupHolder.groupName.setSelected(false);
            }
            groupHolder.groupName.setOnClickListener(v -> {
                mItemClickedListener.onGroupItemClicked(groupBean, position);
            });
        } else {
            GroupAddViewHolder groupHolder = (GroupAddViewHolder) holder;
            groupHolder.itemView.setOnClickListener(v -> {
                mItemClickedListener.onGroupItemClicked(null, position);
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position < mData.size()) {
            return TYPE_GROUP;
        } else {
            return TYPE_ADD_GROUP;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }


    static class GroupViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.group_name)
        TextView groupName;
        @Bind(R.id.group_description)
        TextView groupDescription;

        public GroupViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class GroupAddViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.group_new_add)
        TextView groupAdd;

        public GroupAddViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
