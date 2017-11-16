package com.xmd.manager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.CouponBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 17-10-18.
 */

public class CouponOperateSelectAdapter extends RecyclerView.Adapter {

    public List<CouponBean> mData;
    public CouponItemClickedListener mClickedListener;

    public CouponOperateSelectAdapter(List<CouponBean> couponOperateBeen) {
        this.mData = couponOperateBeen;
    }

    public interface CouponItemClickedListener {
        void couponItemClicked(CouponBean bean, int position);
    }

    public void setCouponItemClickedListener(CouponItemClickedListener listener) {
        this.mClickedListener = listener;
    }

    public void setData(List<CouponBean> couponOperateBeen) {
        this.mData = couponOperateBeen;
        notifyDataSetChanged();
    }

    public static final int USABLE_COUPON_TYPE = 1;
    public static final int UNUSABLE_COUPON_TYPE = 2;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case USABLE_COUPON_TYPE:
                View usableView = LayoutInflater.from(parent.getContext()).inflate(R.layout.usable_coupon_operate_item, parent, false);
                return new UsableCouponListItemViewHolder(usableView);
            case UNUSABLE_COUPON_TYPE:
                View unUsableView = LayoutInflater.from(parent.getContext()).inflate(R.layout.unusable_coupon_operate_item, parent, false);
                return new UsableCouponListItemViewHolder(unUsableView);
            default:
                View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.usable_coupon_operate_item, parent, false);
                return new UsableCouponListItemViewHolder(viewHolder);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UsableCouponListItemViewHolder) {
            CouponBean bean = mData.get(position);
            UsableCouponListItemViewHolder viewHolder = (UsableCouponListItemViewHolder) holder;
            viewHolder.tvCouponName.setText(bean.actTitle);
            viewHolder.tvCouponNo.setText(String.format("[%s]", bean.businessNo));
            if (bean.isSelected == 0) {
                viewHolder.llCouponView.setSelected(false);
            } else {
                viewHolder.llCouponView.setSelected(true);
            }
            viewHolder.itemView.setOnClickListener(v -> {
                if (mClickedListener != null) {
                    mClickedListener.couponItemClicked(bean, position);
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).isUsable.equals("Y")) {
            return USABLE_COUPON_TYPE;
        } else {
            return UNUSABLE_COUPON_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    static class UsableCouponListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_coupon_name)
        TextView tvCouponName;
        @BindView(R.id.tv_coupon_no)
        TextView tvCouponNo;
        @BindView(R.id.ll_coupon_view)
        LinearLayout llCouponView;

        public UsableCouponListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
