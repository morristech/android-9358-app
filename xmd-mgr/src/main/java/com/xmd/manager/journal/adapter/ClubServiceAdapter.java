package com.xmd.manager.journal.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.ServiceItemInfo;
import com.xmd.manager.journal.contract.ClubServiceChoiceContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/14.
 */
public class ClubServiceAdapter extends RecyclerView.Adapter<ClubServiceAdapter.ServiceItemViewHolder> {
    private List<ServiceItemInfo> mServiceInfoList;
    private ClubServiceChoiceContract.Presenter mPresenter;

    public ClubServiceAdapter(ClubServiceChoiceContract.Presenter presenter) {
        mServiceInfoList = new ArrayList<>();
        mPresenter = presenter;
    }

    public void setData(List<ServiceItemInfo> infoList) {
        mServiceInfoList = infoList;
    }

    @Override
    public ClubServiceAdapter.ServiceItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_service_item_view, parent, false);
        return new ServiceItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClubServiceAdapter.ServiceItemViewHolder holder, int position) {
        holder.bind(mServiceInfoList.get(position));
    }

    @Override
    public int getItemCount() {
        return mServiceInfoList.size();
    }

    public class ServiceItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.service_item_title)
        TextView mItemTitle;
        @Bind(R.id.list_item_view)
        RecyclerView mItemList;
        private ClubServiceItemAdapter mAdapter;

        public ServiceItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mItemList.setLayoutManager(new GridLayoutManager(itemView.getContext(), 4));
            mAdapter = new ClubServiceItemAdapter(mPresenter);
            mItemList.setAdapter(mAdapter);
        }

        public void bind(ServiceItemInfo info) {
            mItemTitle.setText(info.categoryName);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getItemsTotalHeight(info.getServiceList().size()));
            mItemList.setLayoutParams(lp);
            mAdapter.setData(info.getServiceList());
            mAdapter.notifyDataSetChanged();
        }

        int dip2px(float dpValue) {
            final float scale = itemView.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

        private int getItemsTotalHeight(int itemNum) {
            if (itemNum / 4 == 0) {
                return dip2px(40);
            } else {
                if (itemNum / 4 == 1 && itemNum % 4 == 0) {
                    return dip2px(40);
                } else if (itemNum / 4 == 1) {
                    return dip2px(40) * 2;
                } else if (itemNum / 4 > 1) {
                    return dip2px(40) * ((itemNum / 4) + 1);
                }
                return dip2px(40);
            }
        }
    }
}
