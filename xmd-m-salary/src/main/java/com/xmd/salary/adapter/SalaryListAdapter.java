package com.xmd.salary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.app.utils.DateUtil;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.salary.R;
import com.xmd.salary.bean.CommissionBean;
import com.xmd.salary.bean.CommissionSumBean;

import java.util.List;

/**
 * Created by Lhj on 17-11-21.
 */

public class SalaryListAdapter<T> extends RecyclerView.Adapter {

    private List<T> mDataList;
    private OnDayItemClickedInterface mOnDayItemClickedListener;
    private Context mContext;

    public static final int COMMISSION_DETAIL_TYPE = 1;
    public static final int COMMISSION_SUM_TYPE = 2;
    public static final int COMMISSION_EMPTY_TYPE = 3;
    public static final int COMMISSION_SERVICE_ITEM_TYPE = 4;

    public SalaryListAdapter(Context context, List<T> commissionList) {
        mContext = context;
        this.mDataList = commissionList;
    }

    public interface OnDayItemClickedInterface<T> {
        void onItemViewClicked(T bean);
    }

    public void setOnDayItemClickedListener(OnDayItemClickedInterface onDayItemClickedListener) {
        this.mOnDayItemClickedListener = onDayItemClickedListener;
    }

    public void setData(List<T> dataList) {
        this.mDataList = dataList;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case COMMISSION_DETAIL_TYPE:
                View detailView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_salary_list_day_item, parent, false);
                return new DetailViewHolder(detailView);
            case COMMISSION_SUM_TYPE:
                View sumView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_salary_list_month_item, parent, false);
                return new SumViewHolder(sumView);
            case COMMISSION_EMPTY_TYPE:
                View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_data_empty_item, parent, false);
                return new EmptyViewHolder(emptyView);

        }
        View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_data_empty_item, parent, false);
        return new EmptyViewHolder(emptyView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DetailViewHolder) {
            DetailViewHolder viewHolder = (DetailViewHolder) holder;
            CommissionBean bean = (CommissionBean) mDataList.get(position);
            Glide.with(mContext).load(bean.typeImageUrl).placeholder(R.drawable.img_commission_default).error(R.drawable.img_commission_default).into(viewHolder.imgSalaryType);
            viewHolder.tvCommissionType.setText(TextUtils.isEmpty(bean.description) ? "提成" : bean.description);
            viewHolder.tvSalaryTime.setText(DateUtil.long2Date(bean.createTime,"HH:mm:ss"));
            viewHolder.tvCommissionPayType.setText(TextUtils.isEmpty(bean.payChannelName) ? "现金支付" : bean.payChannelName);
            viewHolder.tvSalaryTotal.setText(String.format("+%1.2f", bean.totalCommission / 100f));

        } else if (holder instanceof SumViewHolder) {
            SumViewHolder viewHolder = (SumViewHolder) holder;
            final CommissionSumBean bean = (CommissionSumBean) mDataList.get(position);
            viewHolder.tvGetSalaryTime.setText(bean.workDate);
            viewHolder.tvGetSalaryTotal.setText(String.format("+%1.2f", bean.sumCommission / 100f));
            if (null != mOnDayItemClickedListener) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnDayItemClickedListener.onItemViewClicked(bean);
                    }
                });
            }

        } else {
            EmptyViewHolder viewHolder = (EmptyViewHolder) holder;
            if (mDataList.size() > 0) {
                viewHolder.tvEmptyText.setText(ResourceUtils.getString(R.string.all_data_load_finish));
            } else {
                viewHolder.tvEmptyText.setText(ResourceUtils.getString(R.string.search_is_empty));
            }

        }

    }

    @Override
    public int getItemCount() {
        return mDataList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return COMMISSION_EMPTY_TYPE;
        } else {
            if (mDataList.get(position) instanceof CommissionBean) {
                return COMMISSION_DETAIL_TYPE;
            } else if (mDataList.get(position) instanceof CommissionSumBean) {
                return COMMISSION_SUM_TYPE;
            } else {
                return COMMISSION_EMPTY_TYPE;
            }
        }
    }

    static class DetailViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSalaryType;
        TextView tvCommissionType;
        TextView tvCommissionPayType;
        TextView tvSalaryTotal;
        TextView tvSalaryTime;

        DetailViewHolder(View view) {
            super(view);
            imgSalaryType = (ImageView) view.findViewById(R.id.img_salary_type);
            tvCommissionType = (TextView) view.findViewById(R.id.tv_commission_type);
            tvCommissionPayType = (TextView) view.findViewById(R.id.tv_commission_pay_type);
            tvSalaryTotal = (TextView) view.findViewById(R.id.tv_salary_total);
            tvSalaryTime = (TextView) view.findViewById(R.id.tv_salary_time);

        }
    }

    static class SumViewHolder extends RecyclerView.ViewHolder {
        TextView tvGetSalaryTime;
        TextView tvGetSalaryTotal;

        SumViewHolder(View view) {
            super(view);
            tvGetSalaryTime = (TextView) view.findViewById(R.id.tv_get_salary_time);
            tvGetSalaryTotal = (TextView) view.findViewById(R.id.tv_get_salary_total);
        }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmptyText;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            tvEmptyText = (TextView) itemView.findViewById(R.id.tv_empty_text);
        }
    }
}
