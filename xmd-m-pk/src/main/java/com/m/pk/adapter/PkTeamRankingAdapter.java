package com.m.pk.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.m.pk.ConstantResource;
import com.m.pk.R;
import com.m.pk.bean.RankingListBean;

import java.util.List;

/**
 * Created by Lhj on 18-1-4.
 */

public class PkTeamRankingAdapter extends RecyclerView.Adapter<BindingViewHolder> {

    private static final byte REGISTER_TYPE = 1; //拓客锁客
    private static final byte SALE_TYPE = 2; //商城销售
    private static final byte SERVICE_TYPE = 3; //服务之星
    private static final byte PAID_TYPE = 4; //点钟券
    private static final byte PANIC_TYPE = 5; //限时抢

    private Context mContext;
    private List<RankingListBean> mData;
    private String mCategoryId;
    private LayoutInflater mInflater;


    public PkTeamRankingAdapter(Context context, List<RankingListBean> data, String categoryId) {
        this.mContext = context;
        this.mData = data;
        this.mCategoryId = categoryId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        notifyDataSetChanged();
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        switch (viewType) {
            case REGISTER_TYPE:
                binding = DataBindingUtil.inflate(mInflater, R.layout.item_pk_ranking_register, parent, false);
                break;
            case SALE_TYPE:
                binding = DataBindingUtil.inflate(mInflater, R.layout.item_pk_ranking_sale, parent, false);
                break;
            case SERVICE_TYPE:
                binding = DataBindingUtil.inflate(mInflater, R.layout.item_pk_ranking_service, parent, false);
                break;
            case PAID_TYPE:
                binding = DataBindingUtil.inflate(mInflater, R.layout.item_pk_ranking_paid, parent, false);
                break;
            case PANIC_TYPE:
                binding = DataBindingUtil.inflate(mInflater, R.layout.item_pk_ranking_panic, parent, false);
                break;
            default:
                binding = DataBindingUtil.inflate(mInflater, R.layout.item_pk_ranking_register, parent, false);
                break;
        }
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        final RankingListBean rankingBean = mData.get(position);
        holder.getBinding().setVariable(com.m.pk.BR.item, rankingBean);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        RankingListBean bean = mData.get(position);
        String itemCategoryId;
        itemCategoryId = TextUtils.isEmpty(bean.getCategoryId()) ? mCategoryId : bean.getCategoryId();
        switch (itemCategoryId) {
            case ConstantResource.KEY_CATEGORY_CUSTOMER_TYPE:
                return REGISTER_TYPE;
            case ConstantResource.KEY_CATEGORY_SAIL_TYPE:
                return SALE_TYPE;
            case ConstantResource.KEY_CATEGORY_COMMENT_TYPE:
                return SERVICE_TYPE;
            case ConstantResource.KEY_CATEGORY_PAID_TYPE:
                return PAID_TYPE;
            case ConstantResource.KEY_CATEGORY_PANIC_BUY_TYPE:
                return PANIC_TYPE;
            default:
                return SERVICE_TYPE;
        }

    }
}
