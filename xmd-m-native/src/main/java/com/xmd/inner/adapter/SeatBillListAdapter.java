package com.xmd.inner.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.inner.ConstantResource;
import com.xmd.inner.R;
import com.xmd.inner.bean.NativeItemBean;

import java.util.List;

/**
 * Created by Lhj on 17-12-1.
 */

public class SeatBillListAdapter extends RecyclerView.Adapter {

    private List<NativeItemBean> mNativeItemList;
    private BillCallBack mCallBack;
    private Context mContext;
    private int mHandleBillType;

    public static final int HANDLE_BILL_TYPE_CREATE = 1; //创建座位订单
    public static final int HANDLE_BILL_TYPE_ADD = 2; //座位订单添加消费项
    public static final int HANDLE_BILL_TYPE_MODIFY = 3; //修改座位订单消费项

    public static final int NORMAL_ITEM_TYPE = 1;
    public static final int ADD_ITEM_TYPE = 2;

    public SeatBillListAdapter(Context context, List<NativeItemBean> data, int handleBillType) {
        this.mContext = context;
        this.mNativeItemList = data;
        this.mHandleBillType = handleBillType;
        notifyDataSetChanged();
    }

    public void setBillCallBack(BillCallBack callBack) {
        this.mCallBack = callBack;
    }

    public void setNativeData(List<NativeItemBean> data) {
        this.mNativeItemList = data;
        notifyDataSetChanged();
    }

    public interface BillCallBack {
        void consumeItemChoice(int position);

        void serviceItemChoice(int position);

        void newAddItem();

        void deleteItem(int position);

        void addTechItem(int position);

        void removeTechItem(int parentPosition, int position);

        void techChoice(int parentPosition, int position);

        void billSellTotal(int parentPosition, int total);

        void billTimeType(int parentPosition, int position, int type);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case NORMAL_ITEM_TYPE:
                View billItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bill_item, parent, false);
                return new BillItemViewHolder(billItemView);
            case ADD_ITEM_TYPE:
                View addItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bill_add_item, parent, false);
                return new AddItemViewHolder(addItem);
            default:
                View defaultView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bill_item, parent, false);
                return new BillItemViewHolder(defaultView);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int parentPosition) {
        if (holder instanceof BillItemViewHolder) {
            final BillItemViewHolder billHolder = (BillItemViewHolder) holder;
            NativeItemBean bean = mNativeItemList.get(parentPosition);
        //    billHolder.consumeItemSelect.setVisibility(mHandleBillType == HANDLE_BILL_TYPE_MODIFY ? View.GONE : View.VISIBLE);
            billHolder.itemOrderNum.setText(String.format("消费%s", String.valueOf(parentPosition + 1)));
            billHolder.tvConsumeChoice.setText(TextUtils.isEmpty(bean.getConsumeName()) ? ResourceUtils.getString(R.string.consume_item_choice) : bean.getConsumeName());
            billHolder.tvConsumeChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.consumeItemChoice(parentPosition);
                }
            });
            billHolder.imgDelete.setVisibility(parentPosition == 0 ? View.GONE : View.VISIBLE);
            billHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.deleteItem(parentPosition);
                }
            });
            if (TextUtils.isEmpty(bean.getItemType())) {//未选择消费项
                billHolder.llServiceItem.setVisibility(View.GONE);
            } else {
                if (bean.getItemType().equals(ConstantResource.BILL_GOODS_TYPE)) {
                    billHolder.llSellTotal.setVisibility(View.VISIBLE);
                    billHolder.serviceItemTitle.setText("商品*");
                    billHolder.tvServiceChoice.setText(TextUtils.isEmpty(bean.getItemId()) ? ResourceUtils.getString(R.string.service_goods_item_choice) : bean.getServiceName());
                } else {
                    billHolder.llSellTotal.setVisibility(View.GONE);
                    billHolder.serviceItemTitle.setText("服务项*");
                    billHolder.tvServiceChoice.setText(TextUtils.isEmpty(bean.getItemId()) ? ResourceUtils.getString(R.string.service_service_item_choice) : bean.getServiceName());
                }

                billHolder.llServiceItem.setVisibility(View.VISIBLE);

                billHolder.tvServiceChoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallBack.serviceItemChoice(parentPosition);
                    }
                });
                billHolder.editGoodsNum.setText(String.valueOf(bean.getItemCount()));
                billHolder.editGoodsNum.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (billHolder.editGoodsNum.getText().toString().length() > 0) {
                            mCallBack.billSellTotal(parentPosition, Integer.parseInt(billHolder.editGoodsNum.getText().toString()));
                        } else {
                            mCallBack.billSellTotal(parentPosition, 0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                MarketingTechListAdapter techListAdapter = new MarketingTechListAdapter(mContext, bean.getEmployeeList(), new MarketingTechListAdapter.ItemHandleCallBack() {
                    @Override
                    public void addTech() {
                        mCallBack.addTechItem(parentPosition);
                    }

                    @Override
                    public void deleteTech(int position) {
                        mCallBack.removeTechItem(parentPosition, position);
                    }

                    @Override
                    public void techChoice(int position) {
                        mCallBack.techChoice(parentPosition, position);
                    }

                    @Override
                    public void timeType(int position, int type) {
                        mCallBack.billTimeType(parentPosition, position, type);
                    }

                });
                billHolder.recyclerEmployee.setLayoutManager(new LinearLayoutManager(mContext));
                billHolder.recyclerEmployee.setHasFixedSize(true);
                billHolder.recyclerEmployee.setAdapter(techListAdapter);
            }
        } else {
            AddItemViewHolder addHolder = (AddItemViewHolder) holder;
            addHolder.llAddItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.newAddItem();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mNativeItemList.size() != 0 && !(TextUtils.isEmpty(mNativeItemList.get(0).getItemType()))&&mHandleBillType != HANDLE_BILL_TYPE_MODIFY) {
            return mNativeItemList.size() + 1;
        } else {
            return mNativeItemList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mNativeItemList.size()) {
            return ADD_ITEM_TYPE;
        } else {
            return NORMAL_ITEM_TYPE;
        }

    }

    static class BillItemViewHolder extends RecyclerView.ViewHolder {

        TextView itemOrderNum;
        ImageView imgDelete;
        TextView tvConsumeChoice;
        TextView tvServiceChoice;
        RecyclerView recyclerEmployee;
        LinearLayout llServiceItem;
        LinearLayout llSellTotal;
        TextView serviceItemTitle;
        EditText editGoodsNum;
        LinearLayout consumeItemSelect;

        public BillItemViewHolder(View itemView) {
            super(itemView);
            itemOrderNum = (TextView) itemView.findViewById(R.id.item_order_num);
            imgDelete = (ImageView) itemView.findViewById(R.id.img_delete);
            tvConsumeChoice = (TextView) itemView.findViewById(R.id.tv_consume_choice);
            tvServiceChoice = (TextView) itemView.findViewById(R.id.tv_service_choice);
            recyclerEmployee = (RecyclerView) itemView.findViewById(R.id.recycler_employee);
            llServiceItem = (LinearLayout) itemView.findViewById(R.id.ll_service_item);
            llSellTotal = (LinearLayout) itemView.findViewById(R.id.ll_sell_total);
            editGoodsNum = (EditText) itemView.findViewById(R.id.et_goods_num);
            serviceItemTitle = (TextView) itemView.findViewById(R.id.tv_service_item_title);
            consumeItemSelect = (LinearLayout) itemView.findViewById(R.id.ll_consume_item_select);

        }
    }

    static class AddItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llAddItem;

        public AddItemViewHolder(View itemView) {
            super(itemView);
            llAddItem = (LinearLayout) itemView.findViewById(R.id.ll_add_item);
        }
    }
}
