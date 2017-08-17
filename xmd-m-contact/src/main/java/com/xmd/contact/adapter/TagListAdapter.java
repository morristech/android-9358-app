package com.xmd.contact.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.contact.R;
import com.xmd.contact.bean.TagBean;
import com.xmd.contact.bean.TreatedTagList;

import java.util.List;


/**
 * Created by Lhj on 17-8-9.
 */

public class TagListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<TreatedTagList> mTagLists;
    private TagChildrenItemClickedListener mClickedListener;

    public TagListAdapter(Context context, List<TreatedTagList> tagLists) {
        this.mContext = context;
        this.mTagLists = tagLists;
    }

    public interface TagChildrenItemClickedListener {
        void itemClicked(TagBean treatedTag, int childrenPosition);
    }

    public void setOnTagChildrenItemClickedListener(TagChildrenItemClickedListener listener) {
        this.mClickedListener = listener;
    }

    public void setData(List<TreatedTagList> data) {
        this.mTagLists = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View tagView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tag_list_item, parent, false);
        return new TagListViewHolder(tagView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        TagListViewHolder viewHolder = (TagListViewHolder) holder;
        final TreatedTagList bean = mTagLists.get(position);
        viewHolder.tvTagTitle.setText(bean.title);
        if (bean.isOpen.equals("1")) {
            viewHolder.imgTagArrow.setImageResource(R.drawable.arrow_up);
            viewHolder.recyclerTagList.setVisibility(View.VISIBLE);
        } else {
            viewHolder.recyclerTagList.setVisibility(View.GONE);
            viewHolder.imgTagArrow.setImageResource(R.drawable.arrow_down);
        }
        viewHolder.llTagTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.isOpen.equals("1")) {
                    bean.isOpen = "0";
                } else {
                    bean.isOpen = "1";
                }
                notifyItemChanged(position);
            }
        });
        if (bean.list.size() > 0) {
            final TagBeanAdapter adapter = new TagBeanAdapter(mContext, bean.list);
            viewHolder.recyclerTagList.setLayoutManager(new GridLayoutManager(mContext, 3));
            viewHolder.recyclerTagList.setAdapter(adapter);
            adapter.setOnItemCLickedListener(new TagBeanAdapter.TagItemCLickedListener() {
                @Override
                public void onTagItemClicked(TagBean tagbean, int position) {
                    if (mClickedListener != null) {
                        mClickedListener.itemClicked(tagbean, position);
                    }
                    if (position == 0) {
                        for (int i = 0; i < bean.list.size(); i++) {
                            bean.list.get(i).isSelected = false;
                        }
                        bean.list.get(0).isSelected = true;
                        adapter.setTagBeanData(bean.list);
                    } else {
                        bean.list.get(0).isSelected = false;
                        if (tagbean.isSelected) {
                            tagbean.isSelected = false;
                        } else {
                            tagbean.isSelected = true;
                        }
                        adapter.notifyItemChanged(0);
                        adapter.notifyItemChanged(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mTagLists.size();
    }


    static class TagListViewHolder extends RecyclerView.ViewHolder {
        TextView tvTagTitle;
        ImageView imgTagArrow;
        LinearLayout llTagTitle;
        RecyclerView recyclerTagList;

        public TagListViewHolder(View tagView) {
            super(tagView);
            tvTagTitle = (TextView) tagView.findViewById(R.id.tv_tag_title);
            imgTagArrow = (ImageView) tagView.findViewById(R.id.img_tag_arrow);
            llTagTitle = (LinearLayout) tagView.findViewById(R.id.ll_tag_title);
            recyclerTagList = (RecyclerView) tagView.findViewById(R.id.recycler_tag_list);
        }
    }
}
