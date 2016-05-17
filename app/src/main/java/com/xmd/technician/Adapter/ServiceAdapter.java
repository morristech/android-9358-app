package com.xmd.technician.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.bean.ServiceItemInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 16-4-1.
 */
public class ServiceAdapter extends RecyclerView.Adapter {

    private List<ServiceItemInfo> mServiceInfoList;
    private List<String> mSelectedItems;

    public ServiceAdapter() {
        mServiceInfoList = new ArrayList<>();
        mSelectedItems = new ArrayList<>();
    }

    public void refreshDataSet(List<ServiceItemInfo> infoList) {
        if (infoList != null) {
            mServiceInfoList.clear();
            mServiceInfoList.addAll(infoList);
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_item_view, parent, false);
        return new ServiceItemViewHolder(view);
    }

    public String getSelectedIds() {
        StringBuffer ids = new StringBuffer();
        for (ServiceItemInfo info : mServiceInfoList) {
            for (ServiceItemInfo.ItemInfo itemInfo : info.serviceItems) {
                if(itemInfo.isSelected == 1){
                    ids.append(itemInfo.id);
                    ids.append(",");
                }
            }
        }
        if(ids.length() > 0){
            ids.deleteCharAt(ids.length() - 1);
        }
        return ids.toString();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ServiceItemViewHolder) {
            ServiceItemViewHolder viewHolder = (ServiceItemViewHolder) holder;
            ServiceItemInfo itemInfo = mServiceInfoList.get(position);
            ItemAdapter adapter = new ItemAdapter(itemInfo.serviceItems, viewHolder) ;
            viewHolder.mServiceName.setText(itemInfo.name);
            viewHolder.mItemListView.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        return mServiceInfoList.size();
    }

    public class ServiceItemViewHolder extends RecyclerView.ViewHolder implements LinkedSelectedInterface{

        @Bind(R.id.service_name) CheckBox mServiceName;
        @Bind(R.id.item_list) ListView mItemListView;

        public ServiceItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mServiceName.setOnClickListener(v -> {
                if(mServiceName.isChecked()){
                    ((ItemAdapter) mItemListView.getAdapter()).onSelectedAll();
                }else {
                    ((ItemAdapter) mItemListView.getAdapter()).onSelectedNone();
                }});
        }

        @Override
        public void onSelectedAll() {
            mServiceName.setChecked(true);
        }

        @Override
        public void onUnSelectedAll() {
            mServiceName.setChecked(false);
        }

        @Override
        public void onSelectedNone() {

        }
    }

    public class ItemAdapter extends BaseAdapter implements LinkedSelectedInterface{
        private List<ServiceItemInfo.ItemInfo> mmData;
        private int mSelectedCount = 0;
        private LinkedSelectedInterface mServiceNameSelectedInterface;

        public ItemAdapter(List<ServiceItemInfo.ItemInfo> data, LinkedSelectedInterface selectedInterface) {
            mmData = data;
            mServiceNameSelectedInterface = selectedInterface;
            initSelectedCount();
        }

        @Override
        public int getCount() {
            return mmData != null ? mmData.size() : 0;
        }

        @Override
        public ServiceItemInfo.ItemInfo getItem(int position) {
            if (mmData != null && mmData.size() > position) {
                return mmData.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            ServiceItemInfo.ItemInfo itemInfo = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_item_item_view, parent, false);

                holder = new Holder(convertView,itemInfo);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            if (itemInfo != null) {
                holder.mName.setText(itemInfo.name);
                holder.mName.setChecked(itemInfo.isSelected == 1 ? true : false);
                holder.mPrice.setText(itemInfo.price);
                holder.mTime.setText(String.valueOf(itemInfo.duration));
                holder.mName.setOnClickListener(v -> {
                    itemInfo.isSelected = itemInfo.isSelected == 0 ? 1 : 0;
                    onSelectedChange(itemInfo.isSelected == 1 ? 1: -1);
                });
            }

            return convertView;
        }

        @Override
        public void onSelectedAll() {
            for (ServiceItemInfo.ItemInfo item : mmData) {
                item.isSelected = 1;
            }
            mSelectedCount = mmData.size();
            notifyDataSetChanged();
        }

        @Override
        public void onUnSelectedAll() {

        }

        @Override
        public void onSelectedNone() {
            for (ServiceItemInfo.ItemInfo item : mmData) {
                item.isSelected = 0;
            }
            mSelectedCount = 0;
            notifyDataSetChanged();
        }

        public class Holder {
            @Bind(R.id.name) CheckBox mName;
            @Bind(R.id.price) TextView mPrice;
            @Bind(R.id.time) TextView mTime;

            private ServiceItemInfo.ItemInfo mItemInfo;

            public Holder(View itemView,ServiceItemInfo.ItemInfo itemInfo) {
                ButterKnife.bind(this, itemView);
                mItemInfo = itemInfo;
            }

            public void setItemInfo(ServiceItemInfo.ItemInfo itemInfo){
                mItemInfo = itemInfo;
            }
        }

        private void onSelectedChange(int increment ){
            mSelectedCount += increment;
            if(mSelectedCount == mmData.size()){
                mServiceNameSelectedInterface.onSelectedAll();
            }else {
                mServiceNameSelectedInterface.onUnSelectedAll();
            }
        }

        private void initSelectedCount(){
            mSelectedCount = 0;
            for (ServiceItemInfo.ItemInfo itemInfo : mmData){
                mSelectedCount += itemInfo.isSelected;
            }
            if(mSelectedCount == mmData.size()){
                mServiceNameSelectedInterface.onSelectedAll();
            }
        }
    }

    protected interface LinkedSelectedInterface{
        void onSelectedAll();
        void onUnSelectedAll();
        void onSelectedNone();
    }
}
