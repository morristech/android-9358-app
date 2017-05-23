package com.xmd.manager.journal.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.ServiceItem;
import com.xmd.manager.journal.contract.ClubServiceChoiceContract;

import java.util.List;

/**
 * Created by Administrator on 2016/11/14.
 */
public class ClubServiceItemAdapter extends RecyclerView.Adapter<ClubServiceItemAdapter.ItemViewHolder> {
    private List<ServiceItem> mListData;
    private ClubServiceChoiceContract.Presenter mPresenter;

    public ClubServiceItemAdapter(ClubServiceChoiceContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public void setData(List<ServiceItem> data) {
        mListData = data;
    }


    @Override
    public ClubServiceItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_service_items_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClubServiceItemAdapter.ItemViewHolder holder, int position) {
        holder.bind(mListData.get(position));
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mItemTest;
        private ServiceItem mServiceItem;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mItemTest = (TextView) itemView.findViewById(R.id.item_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.onClickServiceItem(mServiceItem);
                }
            });
        }

        public void bind(ServiceItem item) {
            mServiceItem = item;
            mItemTest.setText(item.name);
            itemView.setSelected(mPresenter.isServiceItemSelected(item));
        }
    }
}
