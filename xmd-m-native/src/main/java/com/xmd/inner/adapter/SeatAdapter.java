package com.xmd.inner.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.inner.ConstantResource;
import com.xmd.inner.NativeManager;
import com.xmd.inner.R;
import com.xmd.inner.R2;
import com.xmd.inner.bean.SeatInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zr on 17-12-2.
 */

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.ViewHolder> {
    private Context mContext;
    private List<SeatInfo> mData = new ArrayList<>();
    private ItemClickListener mListener;
    private int selectedPosition;

    public SeatAdapter(Context context) {
        mContext = context;
        selectedPosition = -1;
    }

    public void setData(List<SeatInfo> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void clearData() {
        selectedPosition = -1;
        mData.clear();
    }

    public void setListener(ItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_seat, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final SeatInfo info = mData.get(position);
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.bg_circle_line_pink);
        } else {
            holder.itemView.setBackgroundResource(R.color.colorWhite);
        }

        switch (info.type) {
            case ConstantResource.SEAT_TYPE_BATH:
                holder.mSeatType.setImageResource(R.drawable.ic_seat_bath);
                break;
            case ConstantResource.SEAT_TYPE_BED:
                holder.mSeatType.setImageResource(R.drawable.ic_seat_bed);
                break;
            case ConstantResource.SEAT_TYPE_SOFA:
                holder.mSeatType.setImageResource(R.drawable.ic_seat_sofa);
                break;
            default:
                break;
        }

        switch (info.status) {
            case ConstantResource.STATUS_FREE:
            case ConstantResource.STATUS_BOOKED:
            case ConstantResource.STATUS_CLEAN:
            case ConstantResource.STATUS_DISABLED:
                holder.mSeatStatus.setText(info.name);
                break;
            case ConstantResource.STATUS_USING:
                holder.mSeatStatus.setText(info.userIdentify);
                break;
            default:
                break;
        }
        GradientDrawable mGrad = (GradientDrawable) holder.mSeatType.getBackground();
        mGrad.setColor(NativeManager.getInstance().getStatusColor(info.status));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition == position) {
                    // 选中反选
                    int temp = selectedPosition;
                    selectedPosition = -1;
                    notifyItemChanged(temp);
                    mListener.onItemClear();
                } else {
                    // 切换
                    int temp = selectedPosition;
                    selectedPosition = position;
                    notifyItemChanged(selectedPosition);
                    notifyItemChanged(temp);
                    mListener.onItemClick(info);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.img_seat_type)
        ImageView mSeatType;
        @BindView(R2.id.tv_seat_status)
        TextView mSeatStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemClickListener {
        void onItemClick(SeatInfo seatInfo);

        void onItemClear();
    }
}
