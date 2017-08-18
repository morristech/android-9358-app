package com.xmd.contact.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.contact.R;
import com.xmd.contact.bean.TagBean;

import java.util.List;


/**
 * Created by Lhj on 17-8-9.
 */

public class TagBeanAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<TagBean> mTagLists;
    private TagItemCLickedListener mItemCLickedListener;

    public TagBeanAdapter(Context context, List<TagBean> tagLists) {
        this.mContext = context;
        this.mTagLists = tagLists;
        notifyDataSetChanged();
    }

    public void setOnItemCLickedListener(TagItemCLickedListener listener) {
        this.mItemCLickedListener = listener;
    }

    public interface TagItemCLickedListener {
        void onTagItemClicked(TagBean bean, int position);
    }

    public void setTagBeanData(List<TagBean> list) {
        this.mTagLists = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View tagView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tag_bean_item, parent, false);
        return new TagListViewHolder(tagView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final TagListViewHolder viewHolder = (TagListViewHolder) holder;
        final TagBean bean = mTagLists.get(position);
        viewHolder.tvTagTitle.setText(bean.tagString);
        if (bean.isSelected) {
            viewHolder.itemView.setSelected(true);
        } else {
            viewHolder.itemView.setSelected(false);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemCLickedListener != null) {
                    mItemCLickedListener.onTagItemClicked(bean, position);
                }
                if (viewHolder.itemView.isSelected()) {
                    viewHolder.itemView.setSelected(false);
                } else {
                    viewHolder.itemView.setSelected(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTagLists.size();
    }


    static class TagListViewHolder extends RecyclerView.ViewHolder {
        TextView tvTagTitle;

        public TagListViewHolder(View tagView) {
            super(tagView);
            tvTagTitle = (TextView) tagView.findViewById(R.id.tv_tag);

        }
    }
}
