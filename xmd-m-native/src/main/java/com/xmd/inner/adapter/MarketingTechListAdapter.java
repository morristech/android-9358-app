package com.xmd.inner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.utils.Utils;
import com.xmd.inner.ConstantResource;
import com.xmd.inner.R;
import com.xmd.inner.SeatBillDataManager;
import com.xmd.inner.bean.NativeEmployeeBean;
import com.xmd.inner.bean.OrderBillBean;

import java.util.List;

/**
 * Created by Lhj on 17-12-1.
 */

public class MarketingTechListAdapter extends RecyclerView.Adapter {

    private List<NativeEmployeeBean> mEmployeeList;
    private ItemHandleCallBack mCallBack;
    private Context mContext;

    public MarketingTechListAdapter(Context context, List<NativeEmployeeBean> data, ItemHandleCallBack callBack) {
        this.mContext = context;
        this.mEmployeeList = data;
        this.mCallBack = callBack;
    }

    public void setData(List<NativeEmployeeBean> data) {
        this.mEmployeeList = data;
        notifyDataSetChanged();
    }

    public static final int EMPLOYEE_SPA_TYPE = 1;
    public static final int EMPLOYEE_GOODS_TYPE = 2;

    public interface ItemHandleCallBack {

        void addTech();

        void deleteTech(int position);

        void techChoice(int position);

        void timeType(int position, int type);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case EMPLOYEE_SPA_TYPE:
                View viewSpa = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_native_employee_spa_item, parent, false);
                return new NativeEmployeeSpaViewHolder(viewSpa);
            case EMPLOYEE_GOODS_TYPE:
                View viewGoods = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_native_employee_goods_item, parent, false);
                return new NativeEmployeeGoodsViewHolder(viewGoods);
            default:
                View viewDef = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_native_employee_spa_item, parent, false);
                return new NativeEmployeeSpaViewHolder(viewDef);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NativeEmployeeSpaViewHolder) {
            NativeEmployeeSpaViewHolder spaViewHolder = (NativeEmployeeSpaViewHolder) holder;
            NativeEmployeeBean bean = mEmployeeList.get(position);
            spaViewHolder.techPosition.setText(String.format("技师%s*", String.valueOf(position + 1)));
            spaViewHolder.tvTechChoice.setText(TextUtils.isEmpty(bean.getEmployeeName()) ? ResourceUtils.getString(R.string.tech_number_choice) : bean.getEmployeeName());
            spaViewHolder.tvDeleteTech.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            spaViewHolder.tvAddTech.setVisibility((position == (mEmployeeList.size() - 1)) ? View.VISIBLE : View.INVISIBLE);
            addView(spaViewHolder.rgTimeRadio, bean.getBellId());
            spaViewHolder.rgTimeRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    mCallBack.timeType(position, checkedId);
                }
            });
            spaViewHolder.tvDeleteTech.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.deleteTech(position);
                }
            });

            spaViewHolder.tvAddTech.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.addTech();
                }
            });

            spaViewHolder.tvTechChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.techChoice(position);
                }
            });

        } else {
            final NativeEmployeeGoodsViewHolder goodsViewHolder = (NativeEmployeeGoodsViewHolder) holder;
            NativeEmployeeBean bean = mEmployeeList.get(position);
            goodsViewHolder.tvTechChoice.setText(TextUtils.isEmpty(bean.getEmployeeName()) ? ResourceUtils.getString(R.string.tech_number_choice) : bean.getEmployeeName());
            goodsViewHolder.tvDeleteTech.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            goodsViewHolder.rlHandleItem.setVisibility((position == (mEmployeeList.size() - 1)) ? View.VISIBLE : View.GONE);
            goodsViewHolder.techPositionNum.setText(String.format("营销人员%s*", String.valueOf((position + 1))));
            goodsViewHolder.tvAddTech.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.addTech();
                }
            });

            goodsViewHolder.tvDeleteTech.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.deleteTech(position);
                }
            });

            goodsViewHolder.tvTechChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.techChoice(position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mEmployeeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mEmployeeList.get(position).getServiceType().equals(ConstantResource.BILL_SPA_TYPE)) {
            return EMPLOYEE_SPA_TYPE;
        } else {
            return EMPLOYEE_GOODS_TYPE;
        }
    }

    static class NativeEmployeeSpaViewHolder extends RecyclerView.ViewHolder {

        TextView techPosition;
        TextView tvTechChoice;
        RadioGroup rgTimeRadio;
        TextView tvAddTech;
        TextView tvDeleteTech;
        RelativeLayout rlHandleItem;

        public NativeEmployeeSpaViewHolder(View itemView) {
            super(itemView);
            techPosition = (TextView) itemView.findViewById(R.id.tech_position);
            tvTechChoice = (TextView) itemView.findViewById(R.id.tv_tech_choice);
            rgTimeRadio = (RadioGroup) itemView.findViewById(R.id.rg_time_radio);
            tvAddTech = (TextView) itemView.findViewById(R.id.tv_add_tech);
            tvDeleteTech = (TextView) itemView.findViewById(R.id.tv_delete_tech);
            rlHandleItem = (RelativeLayout) itemView.findViewById(R.id.rl_handle_item);
        }
    }

    static class NativeEmployeeGoodsViewHolder extends RecyclerView.ViewHolder {

        TextView techPositionNum;
        TextView tvTechChoice;
        TextView tvAddTech;
        TextView tvDeleteTech;
        RelativeLayout rlHandleItem;

        public NativeEmployeeGoodsViewHolder(View itemView) {
            super(itemView);
            techPositionNum = (TextView) itemView.findViewById(R.id.tech_position_num);
            tvTechChoice = (TextView) itemView.findViewById(R.id.tv_tech_choice);
            tvAddTech = (TextView) itemView.findViewById(R.id.tv_add_tech);
            tvDeleteTech = (TextView) itemView.findViewById(R.id.tv_delete_tech);
            rlHandleItem = (RelativeLayout) itemView.findViewById(R.id.rl_handle_item);
        }
    }

    private void addView(RadioGroup radioGroup, Integer billId) {
        for (OrderBillBean bean : SeatBillDataManager.getManagerInstance().getOrderBellList()) {
            RadioButton btn = new RadioButton(mContext);
            if (billId != null && bean.id == billId) {
                btn.setChecked(true);
            } else {
                btn.setChecked(false);
            }
            setRadioBtnAttribute(btn, bean.name, bean.id);
            radioGroup.addView(btn);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
            layoutParams.setMargins(0, 0, Utils.dip2px(mContext, 10), 0);
            btn.setLayoutParams(layoutParams);
        }

    }

    private void setRadioBtnAttribute(final RadioButton codeBtn, String btnContent, Integer id) {
        if (btnContent == null) {
            return;
        }
        codeBtn.setBackgroundResource(R.drawable.checked_time_type_bg);
        codeBtn.setTextColor(ResourceUtils.getColor(R.color.radio_button_bill_text_color_selector));
        codeBtn.setId(id);
        codeBtn.setButtonDrawable(null);
        codeBtn.setText(btnContent);
        codeBtn.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Utils.dip2px(mContext, 25));
        codeBtn.setPadding(Utils.dip2px(mContext, 12), 0, Utils.dip2px(mContext, 12), 0);
        codeBtn.setLayoutParams(llp);
    }


}
