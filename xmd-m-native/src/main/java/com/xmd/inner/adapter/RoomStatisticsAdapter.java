package com.xmd.inner.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.inner.ConstantResource;
import com.xmd.inner.R;
import com.xmd.inner.R2;
import com.xmd.inner.bean.RoomStatisticInfo;
import com.xmd.inner.event.JumpManagerRoomEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zr on 17-12-5.
 */

public class RoomStatisticsAdapter extends RecyclerView.Adapter<RoomStatisticsAdapter.ViewHolder> {
    public static final String PAGE_MAIN = "main";
    public static final String PAGE_OTHER = "other";
    private Context mContext;
    private List<RoomStatisticInfo> mData = new ArrayList<>();
    private String mPageType;
    private OnItemClickListener mListener;

    public RoomStatisticsAdapter(Context context, String pageType) {
        mContext = context;
        mPageType = pageType;
    }

    public void setData(List<RoomStatisticInfo> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void clearData() {
        mData.clear();
    }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (mPageType) {
            case PAGE_MAIN:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_room_statistics_main, parent, false));
            case PAGE_OTHER:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_room_statistics_other, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final RoomStatisticInfo statisticInfo = mData.get(position);
        GradientDrawable mGrad = (GradientDrawable) holder.mStatusColor.getBackground();
        mGrad.setColor(ConstantResource.STATUS_TYPE_COLOR.get(statisticInfo.color));
        if (PAGE_OTHER.equals(mPageType)) {
            holder.mStatusDesc.setText(statisticInfo.name + ":" + statisticInfo.roomCount);
            if (statisticInfo.filter) {
                holder.mStatusDesc.setTextColor(ResourceUtils.getColor(R.color.colorText3));
                holder.mStatusColor.getBackground().setAlpha(60);
            } else {
                holder.mStatusDesc.setTextColor(ResourceUtils.getColor(R.color.colorText2));
                holder.mStatusColor.getBackground().setAlpha(255);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(statisticInfo, position);
                }
            });
        } else {
            holder.mStatusDesc.setText(statisticInfo.name + ": " + statisticInfo.roomCount);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new JumpManagerRoomEvent());
                }
            });
        }
    }

    public String getFilterStr() {
        StringBuilder filter = new StringBuilder();
        for (RoomStatisticInfo statisticInfo : mData) {
            if (!statisticInfo.filter) {
                filter.append(statisticInfo.code + ",");
            }
        }
        return (filter.length() > 0) ? filter.substring(0, filter.length() - 1) : filter.toString();
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.view_status_color)
        View mStatusColor;
        @BindView(R2.id.tv_status_desc)
        TextView mStatusDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(RoomStatisticInfo statisticInfo, int position);
    }
}
