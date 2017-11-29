package com.xmd.manager.adapter;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.manager.R;
import com.xmd.manager.beans.CashierClubDetailInfo;
import com.xmd.manager.beans.CommissionTechInfo;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zr on 17-11-25.
 * 技师工资报表:某技师某天提成明细
 */

public class ReportDetailAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String CASHIER_TYPE_SPA = "spa";
    private static final String CASHIER_TYPE_GOODS = "goods";

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    public static final int FOOTER_STATUS_SUCCESS = 0;
    public static final int FOOTER_STATUS_ERROR = 1;
    public static final int FOOTER_STATUS_NO_NETWORK = 2;
    public static final int FOOTER_STATUS_NONE = 3;
    public static final int FOOTER_STATUS_LOADING = 4;

    private List<T> mData = new ArrayList<>();
    private Context mContext;
    private CallBack mCallBack;

    private int mFooterStatus;

    public interface CallBack {
        void onLoadMore();

        void onItemClick(Object info);
    }

    @IntDef({FOOTER_STATUS_SUCCESS, FOOTER_STATUS_ERROR, FOOTER_STATUS_NO_NETWORK, FOOTER_STATUS_NONE, FOOTER_STATUS_LOADING})
    public @interface Flavour {
    }

    public ReportDetailAdapter(Context context) {
        this.mContext = context;
    }

    public void clearData() {
        this.mData.clear();
    }

    public void setData(List<T> data) {
        this.mData.addAll(data);
    }

    public void setCallBack(CallBack callBack) {
        this.mCallBack = callBack;
    }

    public void setStatus(@Flavour int status) {
        this.mFooterStatus = status;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_report_info, parent, false));
            case TYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout_footer, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.mMoreMsg.setText(getDescStr(mFooterStatus));
            switch (mFooterStatus) {
                case FOOTER_STATUS_SUCCESS:
                case FOOTER_STATUS_ERROR:
                case FOOTER_STATUS_NO_NETWORK:
                    footerHolder.itemView.setOnClickListener(v -> mCallBack.onLoadMore());
                    footerHolder.mMoreProgress.setVisibility(View.GONE);
                    break;
                case FOOTER_STATUS_LOADING:
                    footerHolder.itemView.setOnClickListener(null);
                    footerHolder.mMoreProgress.setVisibility(View.VISIBLE);
                    break;
                case FOOTER_STATUS_NONE:
                    footerHolder.itemView.setOnClickListener(null);
                    footerHolder.mMoreProgress.setVisibility(View.GONE);
                    break;
                default:
                    footerHolder.itemView.setOnClickListener(null);
                    footerHolder.mMoreLayout.setVisibility(View.INVISIBLE);
                    break;
            }
        } else {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            if (mData.get(position) instanceof CommissionTechInfo) {
                CommissionTechInfo info = (CommissionTechInfo) mData.get(position);
                Glide.with(mContext).load(info.typeImageUrl).dontAnimate().placeholder(R.drawable.black_money).into(itemHolder.mTypeImage);
                itemHolder.mTitleText.setText(info.description);
                itemHolder.mAmountText.setText("+" + Utils.moneyToStringEx(info.totalCommission));
                itemHolder.mSubTitleText.setText(info.payChannelName);
                itemHolder.mTimeText.setText(DateUtil.long2Date(info.createTime, "HH:mm:ss"));
                itemHolder.itemView.setOnClickListener(v -> mCallBack.onItemClick(info));
            } else if (mData.get(position) instanceof CashierClubDetailInfo) {
                CashierClubDetailInfo info = (CashierClubDetailInfo) mData.get(position);
                switch (info.scope) {
                    case CASHIER_TYPE_GOODS:
                        itemHolder.mTypeImage.setImageResource(R.drawable.ic_type_goods);
                        itemHolder.mTitleText.setText(info.itemName + " * " + info.count);
                        itemHolder.mAmountText.setText("+" + Utils.moneyToStringEx(info.amount * info.count));
                        break;
                    case CASHIER_TYPE_SPA:
                        itemHolder.mTitleText.setText(info.itemName);
                        itemHolder.mTypeImage.setImageResource(R.drawable.ic_type_spa);
                        itemHolder.mAmountText.setText("+" + Utils.moneyToStringEx(info.amount));
                        break;
                    default:
                        itemHolder.mTitleText.setText("未知");
                        itemHolder.mTypeImage.setImageResource(R.drawable.black_money);
                        break;
                }
                itemHolder.mSubTitleText.setText(info.payChannel);
                itemHolder.mTimeText.setText(info.orderTime);
                itemHolder.itemView.setOnClickListener(v -> mCallBack.onItemClick(info));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (!mData.isEmpty()) {
            return mData.size() + 1;
        }
        return 0;
    }

    private String getDescStr(int status) {
        String desc = "点击或上拉，加载更多";
        switch (status) {
            case FOOTER_STATUS_ERROR:
                desc = "加载失败，点击重试...";
                break;
            case FOOTER_STATUS_NO_NETWORK:
                desc = "网络异常，请稍后重试...";
                break;
            case FOOTER_STATUS_NONE:
                desc = "---数据加载完成---";
                break;
            case FOOTER_STATUS_LOADING:
                desc = "正在加载...";
                break;
            case FOOTER_STATUS_SUCCESS:
            default:
                break;
        }
        return desc;
    }

    public class ItemViewHolder<T> extends RecyclerView.ViewHolder {
        @BindView(R.id.img_type)
        ImageView mTypeImage;
        @BindView(R.id.tv_info_title)
        TextView mTitleText;
        @BindView(R.id.tv_info_amount)
        TextView mAmountText;
        @BindView(R.id.tv_info_sub_title)
        TextView mSubTitleText;
        @BindView(R.id.tv_info_time)
        TextView mTimeText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class FooterViewHolder<T> extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_footer_layout)
        LinearLayout mMoreLayout;
        @BindView(R.id.pb_more_data)
        ProgressBar mMoreProgress;
        @BindView(R.id.tv_more_data)
        TextView mMoreMsg;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
