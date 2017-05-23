package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.ItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 16-8-18.
 */

public class StatisticsDataRecycleViewAdapter extends RecyclerView.Adapter<StatisticsDataRecycleViewAdapter.ViewHolder> {

    private CallbackItem mOnItemClick;
    private boolean isShowArrowRight;

    public interface CallbackItem<T> {

        void onItemClicked(T bean);

    }

    public StatisticsDataRecycleViewAdapter(boolean showArrowRight) {
        this.isShowArrowRight = showArrowRight;
    }


    public static class Item {
        public String time;
        public String number;

        public Item() {

        }

        public Item(String time, String number) {
            this.time = time;
            this.number = number;
        }
    }

    private List<Item> statisticsData = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new ViewHolder(layoutInflater.inflate(R.layout.recycleview_statistics_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(statisticsData.get(position));
        ItemBean itemBean = new ItemBean(statisticsData.get(position).time, statisticsData.get(position).number);
        if (isShowArrowRight) {
            holder.itemView.setClickable(true);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClick.onItemClicked(itemBean);
                }
            });
        } else {
            holder.itemView.setClickable(false);
        }

    }

    @Override
    public int getItemCount() {
        return statisticsData.size();
    }

    public void setData(List<Item> data, CallbackItem callbackItem) {
        this.mOnItemClick = callbackItem;
        if (data != null) {
            statisticsData = data;
        }
    }

    public void setData(List<Item> data) {
        if (data != null) {
            statisticsData = data;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv1;
        private TextView tv2;
        private ImageView right;

        public ViewHolder(View itemView) {
            super(itemView);
            tv1 = (TextView) itemView.findViewById(R.id.tv_c1);
            tv2 = (TextView) itemView.findViewById(R.id.tv_c2);
            right = (ImageView) itemView.findViewById(R.id.right_arrow);
        }

        public void bind(Item item) {

            tv1.setText(item.time);
            tv2.setText(item.number);
            if (isShowArrowRight) {
                right.setVisibility(View.VISIBLE);
            } else {
                right.setVisibility(View.GONE);
            }

        }
    }
}
