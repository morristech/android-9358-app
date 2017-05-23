package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CheckInfo;
import com.xmd.manager.beans.CouponInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 15-12-11.
 */
public class VerificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public interface CallBackListener {
        void onCouponItemClicked(CouponInfo couponInfo);

        void onSelectedItemClicked(CouponInfo couponInfo, Integer position, boolean isSelected);
    }

    private List<CheckInfo> mVerificationList;
    private CallBackListener mCallBackListener;
    private Context mContext;

    private final static int ITEM_TYPE_UNKNOWN = 0;
    private final static int ITEM_TYPE_COUPON = 1;
    private final static int ITEM_TYPE_ORDER = 2;

    public VerificationListAdapter(Context context, List<CheckInfo> verificationList, CallBackListener onCallClickListener) {
        mContext = context;
        mVerificationList = verificationList;
        mCallBackListener = onCallClickListener;
        notifyDataSetChanged();
    }

    public void setData(List<CheckInfo> verificationList) {
        mVerificationList = verificationList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;
        switch (viewType) {
            case ITEM_TYPE_COUPON:
                layoutId = R.layout.customer_coupon_list_item;
                break;
            case ITEM_TYPE_ORDER:
                layoutId = R.layout.customer_coupon_list_item; //FIXME
                break;
            default:
                throw new RuntimeException("CommonVerificationListAdapter: 不应该到这里！");
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new CouponItemViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        CheckInfo checkInfo = mVerificationList.get(position);
        switch (checkInfo.getType()) {
            case Constant.VERIFICATION_COUPON:
            case Constant.VERIFICATION_SERVICE_ITEM:
                return ITEM_TYPE_COUPON;
            case Constant.VERIFICATION_ORDER:
                return ITEM_TYPE_ORDER;
            default:
                return ITEM_TYPE_UNKNOWN;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CouponItemViewHolder) {
            ((CouponItemViewHolder) holder).bind(mVerificationList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (int i = 0; i < mVerificationList.size(); i++) {
            if (getItemViewType(i) != ITEM_TYPE_UNKNOWN) {
                count++;
            }
        }
        return count;
    }

    static class CouponItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ll_img_layout)
        LinearLayout llImageLayout;
        @Bind(R.id.img_coupon_selected)
        ImageView imgCouponSelected;
        @Bind(R.id.coupon_top_view)
        View couponTopView;
        @Bind(R.id.coupon_name)
        TextView couponName;
        @Bind(R.id.coupon_type)
        TextView couponType;
        @Bind(R.id.coupon_desc)
        TextView couponDesc;
        @Bind(R.id.coupon_status)
        TextView couponStatus;
        @Bind(R.id.activity_duration)
        TextView activityDuration;
        @Bind(R.id.coupon_duration)
        TextView couponDuration;
        @Bind(R.id.coupon_use_duration_label)
        TextView couponUseDurationLabel;
        @Bind(R.id.coupon_use_duration)
        TextView couponUseDuration;
        @Bind(R.id.ll_deliver)
        LinearLayout llDeliver;
        @Bind(R.id.alertTitleN0Data)
        TextView alertTitleN0Data;

        CouponItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(CheckInfo checkInfo) {
//            final CouponInfo couponInfo = ;
//            CouponItemViewHolder itemHolder = (CouponItemViewHolder) holder;
//            itemHolder.couponName.setText(Utils.StrSubstring(10, couponInfo.actTitle, true));
//            itemHolder.couponUseDuration.setText(DescribeMesaageUtil.getTimePeriodDes(couponInfo.useTimePeriod));
//
//            itemHolder.activityDuration.setText(couponInfo.actPeriod);
//            itemHolder.couponDuration.setText(couponInfo.couponPeriod);
//            if (Utils.isNotEmpty(couponInfo.useType)) {
//                if (couponInfo.useType.equals(RequestConstant.KEY_MESSAGE_TYPE_LUCKY_WHEEL) || couponInfo.useType.equals(RequestConstant.KEY_ONE_YUAN)
//                        || couponInfo.useType.equals(RequestConstant.KEY_CODE_PAID_SERVICE_TYPE) || couponInfo.useType.equals(RequestConstant.KEY_TIME_CARD)) {
//                    itemHolder.couponType.setText(couponInfo.couponTypeName);
//                    itemHolder.couponDesc.setText(couponInfo.useTypeName);
//                } else {
//                    itemHolder.couponType.setText(couponInfo.useTypeName);
//                    itemHolder.couponDesc.setText(couponInfo.consumeMoneyDescription);
//                }
//            } else {
//                itemHolder.couponType.setText(couponInfo.couponTypeName);
//                itemHolder.couponDesc.setText(couponInfo.consumeMoneyDescription);
//            }
//
//
//            if (couponInfo.isSelected == 1) {
//                itemHolder.couponStatus.setTextColor(ResourceUtils.getColor(R.color.colorClubItemBody));
//                itemHolder.couponStatus.setText(ResourceUtils.getString(R.string.coupon_usable));
//                itemHolder.imgCouponSelected.setSelected(false);
//                itemHolder.llImageLayout.setOnClickListener(v -> {
//                    v.setSelected(true);
//                    mCallBackListener.onSelectedItemClicked(couponInfo, position, false);
//                });
//            } else if (couponInfo.isSelected == 2) {
//                itemHolder.couponStatus.setTextColor(ResourceUtils.getColor(R.color.colorClubItemBody));
//                itemHolder.couponStatus.setText(ResourceUtils.getString(R.string.coupon_usable));
//                itemHolder.imgCouponSelected.setSelected(true);
//                itemHolder.llImageLayout.setOnClickListener(v -> {
//                    v.setSelected(false);
//                    mCallBackListener.onSelectedItemClicked(couponInfo, position, true);
//                });
//            } else {
//                itemHolder.couponStatus.setTextColor(ResourceUtils.getColor(R.color.amount_color));
//                itemHolder.couponStatus.setText(ResourceUtils.getString(R.string.coupon_unusable));
//                itemHolder.imgCouponSelected.setSelected(false);
//                itemHolder.llImageLayout.setOnClickListener(v -> {
//                    v.setSelected(false);
//                    ToastUtils.showToastLong(mContext, "不在可用的使用时段内");
//                });
//            }
//
//            itemHolder.itemView.setOnClickListener(v -> mCallBackListener.onCouponItemClicked(couponInfo));
        }
    }

}
