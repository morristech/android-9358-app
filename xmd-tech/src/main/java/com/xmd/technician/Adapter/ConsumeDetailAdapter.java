package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.app.user.User;
import com.xmd.app.widget.CircleAvatarView;
import com.xmd.technician.R;
import com.xmd.technician.bean.ConsumeInfo;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 16-3-31.
 */
public class ConsumeDetailAdapter extends RecyclerView.Adapter {

    private static final byte USER_REWARD = 1;
    private static final byte COUPON_REWARD = 2;
    private static final byte PAID_COUPON = 3;
    private static final byte WITHDRAWAL = 4;
    private static final byte OUT_CLUB = 5;
    private static final byte COMMISSION_TYPE = 6; //分佣
    private static final byte TYPE_FOOTER = 0;

    private List<ConsumeInfo> mConsumeList;
    private Context mContext;
    private boolean mIsNoMore = false;
    private View.OnClickListener mFooterClickListener;

    public ConsumeDetailAdapter(Context context) {
        mContext = context;
        mConsumeList = new ArrayList<>();
    }

    public void setOnFooterClickListener(View.OnClickListener listener) {
        mFooterClickListener = listener;
    }

    public void refreshDataSet(List<ConsumeInfo> infos) {
        if (infos == null) {
            return;
        }
        mConsumeList.clear();
        mConsumeList.addAll(infos);
        notifyDataSetChanged();
    }

    /**
     * There is data, but need to show footer "No More"
     *
     * @param isNoMore
     */
    public void setIsNoMore(boolean isNoMore) {
        mIsNoMore = isNoMore;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        }
        ConsumeInfo consumeInfo = mConsumeList.get(position);
        if (consumeInfo.consumeChannel.equals("tech_fee")) {
            return USER_REWARD;
        } else if (consumeInfo.consumeChannel.equals("red_commission")) {
            return COUPON_REWARD;
        } else if (consumeInfo.consumeChannel.equals("out_club")) {
            return OUT_CLUB;
        } else if (consumeInfo.consumeChannel.equals("commission")) {
            return COMMISSION_TYPE;
        } else {
            return WITHDRAWAL;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false);
            return new FooterViewHolder(view);
        }

        View view;
        switch (viewType) {
            case USER_REWARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.consume_record_item, parent, false);
                break;
            case COUPON_REWARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.consume_record_item, parent, false);
                break;
            case PAID_COUPON:
            case OUT_CLUB:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clock_commission_item, parent, false);
                break;
            case COMMISSION_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commission_record_item, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.consume_record_item, parent, false);
                break;
        }
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DetailViewHolder) {
            DetailViewHolder viewHolder = (DetailViewHolder) holder;
            ConsumeInfo info = mConsumeList.get(position);
            viewHolder.mTitle.setText(info.title);
            if (Utils.isNotEmpty(info.remark)) {
                if (info.remark.contains("##")) {
                    String desStart = info.remark.substring(0, info.remark.lastIndexOf("#") - 1);
                    String desEnd = info.remark.substring(info.remark.lastIndexOf("#") + 1, info.remark.length());
                    viewHolder.mRemark.setText(desStart);
                    viewHolder.mCommissionRemark.setVisibility(View.VISIBLE);
                    viewHolder.mCommissionRemark.setText(desEnd);
                } else {
                    viewHolder.mCommissionRemark.setVisibility(View.GONE);
                    viewHolder.mRemark.setText(info.remark);
                }
            } else {
                viewHolder.mRemark.setVisibility(View.GONE);
                viewHolder.mCommissionRemark.setVisibility(View.GONE);
            }
            viewHolder.mTimeStamp.setText(info.dealDate);

            int viewType = getItemViewType(position);
            if (viewType == WITHDRAWAL) {
                viewHolder.mAmount.setTextColor(mContext.getResources().getColor(R.color.colorTitle));
                viewHolder.mAmount.setText(String.format("%1.2f", info.amount) + "元");
                viewHolder.mTitle.setText(ResourceUtils.getString(R.string.consume));
                viewHolder.mAvatar.setImageResource(R.drawable.icon35);
            } else if (viewType == OUT_CLUB) {
                viewHolder.mAmount.setTextColor(mContext.getResources().getColor(R.color.colorTitle));
                viewHolder.mAmount.setText(String.format("%1.2f", info.amount) + "元");
                viewHolder.mAvatar.setImageResource(R.drawable.icon46);
            } else {
                viewHolder.mAmount.setText(String.format("+%1.2f", info.amount) + "元");
                if (viewType == COUPON_REWARD) {
                    viewHolder.mAvatar.setImageResource(R.drawable.img_default_avatar);
                } else {
                    User user = new User(info.userId);
                    user.setAvatar(info.headImgUrl);
                    viewHolder.mAvatar.setVisibility(View.VISIBLE);
                    viewHolder.mAvatar.setUserInfo(user);
                }
            }
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            String desc = mContext.getResources().getString(R.string.list_item_loading);
            if (mConsumeList.isEmpty()) {
                desc = mContext.getResources().getString(R.string.list_item_empty);
                footerHolder.itemFooter.setOnClickListener(null);
            } else if (mIsNoMore) {
                desc = mContext.getResources().getString(R.string.all_data_load_finish);
                footerHolder.itemFooter.setOnClickListener(null);
            } else {
                footerHolder.itemFooter.setOnClickListener(v -> {
                    if (mFooterClickListener != null) {
                        mFooterClickListener.onClick(v);
                    }
                });
            }
            footerHolder.itemFooter.setText(desc);
        }
    }

    @Override
    public int getItemCount() {
        return mConsumeList.size() + 1;
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        CircleAvatarView mAvatar;
        @BindView(R.id.title)
        TextView mTitle;
        @BindView(R.id.remark)
        TextView mRemark;
        @BindView(R.id.amount)
        TextView mAmount;
        @BindView(R.id.time)
        TextView mTimeStamp;
        @BindView(R.id.commission_remark)
        TextView mCommissionRemark;

        public DetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_footer)
        TextView itemFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
