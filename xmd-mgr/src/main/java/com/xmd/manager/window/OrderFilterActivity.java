package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.adapter.OrderFilterAdapter;
import com.xmd.manager.beans.OrderProjectBean;
import com.xmd.manager.beans.OrderStatusBean;
import com.xmd.manager.beans.OrderTechNoBean;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.widget.DateTimePickDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-9-20.
 */

public class OrderFilterActivity extends BaseActivity {

    @BindView(R.id.tv_filter_start_time)
    TextView tvFilterStartTime;
    @BindView(R.id.tv_filter_end_time)
    TextView tvFilterEndTime;
    @BindView(R.id.img_order_filter_status)
    ImageView imgOrderFilterStatus;
    @BindView(R.id.tv_order_status_all)
    TextView tvOrderStatusAll;
    @BindView(R.id.rv_order_status)
    RecyclerView rvOrderStatus;
    @BindView(R.id.ll_order_status_item)
    LinearLayout llOrderStatusItem;
    @BindView(R.id.img_order_filter_project)
    ImageView imgOrderFilterProject;
    @BindView(R.id.tv_order_project_all)
    TextView tvOrderProjectAll;
    @BindView(R.id.rv_order_project)
    RecyclerView rvOrderProject;
    @BindView(R.id.ll_order_project_item)
    LinearLayout llOrderProjectItem;
    @BindView(R.id.img_order_filter_staff)
    ImageView imgOrderFilterStaff;
    @BindView(R.id.tv_order_staff_all)
    TextView tvOrderStaffAll;
    @BindView(R.id.ll_order_staff_item)
    LinearLayout llOrderStaffItem;
    @BindView(R.id.rv_order_staff)
    RecyclerView rvOrderStaff;

    private OrderFilterAdapter mOrderStatusAdapter;
    private OrderFilterAdapter mOrderProjectAdapter;
    private OrderFilterAdapter mOrderTechAdapter;
    private LinearLayoutManager mLayoutManager;

    private List<OrderStatusBean> mOrderStatusList;
    private List<OrderProjectBean> mOrderProjectList;
    private List<OrderTechNoBean> mOrderTechNoList;
    private String mStartTime;
    private String mEndTime;
    private boolean orderStatusIsOpen, orderProjectIsOpen, orderTechIsOpen; //一开始均处于打开状态


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_filter);
        ButterKnife.bind(this);
        getIntentData();
        initView();
    }

    public void getIntentData() {
        Intent intentData = getIntent();
        mStartTime = intentData.getStringExtra(OnlineOrderActivity.INTENT_ORDER_START_TIME);
        mEndTime = intentData.getStringExtra(OnlineOrderActivity.INTENT_ORDER_END_TIME);
        mOrderStatusList = intentData.getParcelableArrayListExtra(OnlineOrderActivity.INTENT_ORDER_STATUS_LIST);
        mOrderProjectList = intentData.getParcelableArrayListExtra(OnlineOrderActivity.INTENT_ORDER_PROJECT_LIST);
        mOrderTechNoList = intentData.getParcelableArrayListExtra(OnlineOrderActivity.INTENT_ORDER_TECH_NO_LIST);
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.order_filter_activity_title));
        setRightVisible(true, "清空", clearDataListener);
        mOrderStatusAdapter = new OrderFilterAdapter(mOrderStatusList);
        mOrderProjectAdapter = new OrderFilterAdapter(mOrderProjectList);
        mOrderTechAdapter = new OrderFilterAdapter(mOrderTechNoList);
        mLayoutManager = new GridLayoutManager(OrderFilterActivity.this, 3);
        rvOrderStatus.setLayoutManager(mLayoutManager);
        rvOrderProject.setLayoutManager(new GridLayoutManager(OrderFilterActivity.this, 3));
        rvOrderStaff.setLayoutManager(new GridLayoutManager(OrderFilterActivity.this, 3));
        rvOrderStatus.setHasFixedSize(true);
        rvOrderProject.setHasFixedSize(true);
        rvOrderStaff.setHasFixedSize(true);
        rvOrderStatus.setAdapter(mOrderStatusAdapter);
        rvOrderProject.setAdapter(mOrderProjectAdapter);
        rvOrderStaff.setAdapter(mOrderTechAdapter);
        tvFilterStartTime.setText(Utils.isEmpty(mStartTime) ? DateUtil.getFirstDayOfMonth() : mStartTime);
        tvFilterEndTime.setText(Utils.isEmpty(mEndTime) ? DateUtil.getCurrentDate() : mEndTime);
        mOrderStatusAdapter.setItemClickListener(orderStatusBeanListener);
        mOrderProjectAdapter.setItemClickListener(orderProjectListener);
        mOrderTechAdapter.setItemClickListener(orderTechNoListener);
        orderStatusIsOpen = true;
        orderProjectIsOpen = true;
        orderTechIsOpen = true;
        judgeProjectData();
        judgeStatusData();
        judgeTechData();

    }

    View.OnClickListener clearDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearDataStatus();
            clearDataProject();
            clearDataTech();
            tvFilterStartTime.setText(Utils.isEmpty(mStartTime) ? DateUtil.getFirstDayOfMonth() : mStartTime);
            tvFilterEndTime.setText(Utils.isEmpty(mEndTime) ? DateUtil.getCurrentDate() : mEndTime);
        }
    };

    @OnClick({R.id.tv_filter_start_time, R.id.tv_filter_end_time, R.id.ll_order_status_filter, R.id.tv_order_status_all, R.id.ll_order_project_filter,
            R.id.tv_order_project_all, R.id.ll_order_staff_filter, R.id.tv_order_staff_all,R.id.btn_filter_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_filter_start_time:
                DateTimePickDialog dataPickDialogStr = new DateTimePickDialog(OrderFilterActivity.this, tvFilterStartTime.getText().toString());
                dataPickDialogStr.dateTimePicKDialog(tvFilterStartTime);
                break;
            case R.id.tv_filter_end_time:
                DateTimePickDialog dataPickDialogEnd = new DateTimePickDialog(OrderFilterActivity.this, tvFilterEndTime.getText().toString());
                dataPickDialogEnd.dateTimePicKDialog(tvFilterEndTime);
                break;
            case R.id.ll_order_status_filter:
                orderStatusIsOpen = !orderStatusIsOpen;
                llOrderStatusItem.setVisibility(orderStatusIsOpen ? View.VISIBLE : View.GONE);
                imgOrderFilterStatus.setImageResource(orderStatusIsOpen ? R.drawable.arrow_up : R.drawable.arrow_down);
                break;
            case R.id.tv_order_status_all:
                clearDataStatus();
                break;
            case R.id.ll_order_project_filter:
                orderProjectIsOpen = !orderProjectIsOpen;
                llOrderProjectItem.setVisibility(orderProjectIsOpen ? View.VISIBLE : View.GONE);
                imgOrderFilterProject.setImageResource(orderProjectIsOpen ? R.drawable.arrow_up : R.drawable.arrow_down);
                break;
            case R.id.tv_order_project_all:
                clearDataProject();
                break;
            case R.id.ll_order_staff_filter:
                orderTechIsOpen = !orderTechIsOpen;
                llOrderStaffItem.setVisibility(orderTechIsOpen ? View.VISIBLE : View.GONE);
                imgOrderFilterStaff.setImageResource(orderTechIsOpen ? R.drawable.arrow_up : R.drawable.arrow_down);
                break;
            case R.id.tv_order_staff_all:
                clearDataTech();
                break;
            case R.id.btn_filter_commit:
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(OnlineOrderActivity.INTENT_ORDER_STATUS_LIST, (ArrayList<? extends Parcelable>) mOrderStatusList);
                intent.putParcelableArrayListExtra(OnlineOrderActivity.INTENT_ORDER_PROJECT_LIST, (ArrayList<? extends Parcelable>) mOrderProjectList);
                intent.putParcelableArrayListExtra(OnlineOrderActivity.INTENT_ORDER_TECH_NO_LIST, (ArrayList<? extends Parcelable>) mOrderTechNoList);
                intent.putExtra(OnlineOrderActivity.INTENT_ORDER_START_TIME, tvFilterStartTime.getText().toString());
                intent.putExtra(OnlineOrderActivity.INTENT_ORDER_END_TIME, tvFilterEndTime.getText().toString());
                setResult(RESULT_OK,intent);
                this.finish();
                break;
        }
    }

    OrderFilterAdapter.OnItemClickedListener<OrderStatusBean> orderStatusBeanListener = new OrderFilterAdapter.OnItemClickedListener<OrderStatusBean>() {
        @Override
        public void onItemViewCLicked(OrderStatusBean bean, int position) {
            mOrderStatusList.get(position).isSelected = (bean.isSelected == 0 ? 1 : 0);
            mOrderStatusAdapter.notifyItemChanged(position);
            judgeStatusData();
        }
    };

    OrderFilterAdapter.OnItemClickedListener<OrderProjectBean> orderProjectListener = new OrderFilterAdapter.OnItemClickedListener<OrderProjectBean>() {
        @Override
        public void onItemViewCLicked(OrderProjectBean bean, int position) {
            mOrderProjectList.get(position).isSelect = (bean.isSelect == 0 ? 1 : 0);
            mOrderProjectAdapter.notifyItemChanged(position);
            judgeProjectData();
        }
    };

    OrderFilterAdapter.OnItemClickedListener<OrderTechNoBean> orderTechNoListener = new OrderFilterAdapter.OnItemClickedListener<OrderTechNoBean>() {
        @Override
        public void onItemViewCLicked(OrderTechNoBean bean, int position) {
            mOrderTechNoList.get(position).isSelect = (bean.isSelect == 0 ? 1 : 0);
            mOrderTechAdapter.notifyItemChanged(position);
            judgeTechData();
        }
    };

    private void clearDataStatus() {
        tvOrderStatusAll.setSelected(true);
        for (OrderStatusBean bean : mOrderStatusList) {
            bean.isSelected = 0;
        }
        mOrderStatusAdapter.setData(mOrderStatusList);
    }

    private void clearDataProject() {
        tvOrderProjectAll.setSelected(true);
        for (OrderProjectBean bean : mOrderProjectList) {
            bean.isSelect = 0;
        }
        mOrderProjectAdapter.setData(mOrderProjectList);
    }

    private void clearDataTech() {
        tvOrderStaffAll.setSelected(true);
        for (OrderTechNoBean bean : mOrderTechNoList) {
            bean.isSelect = 0;
        }
        mOrderTechAdapter.setData(mOrderTechNoList);
    }

    private void judgeStatusData() {
        tvOrderStatusAll.setSelected(true);
        for (OrderStatusBean bean : mOrderStatusList) {
            if (bean.isSelected == 1) {
                tvOrderStatusAll.setSelected(false);
                break;
            }
        }
    }

    private void judgeProjectData() {
        tvOrderProjectAll.setSelected(true);
        for (OrderProjectBean bean : mOrderProjectList) {
            if (bean.isSelect == 1) {
                tvOrderProjectAll.setSelected(false);
                break;
            }
        }
    }

    private void judgeTechData() {
        tvOrderStaffAll.setSelected(true);
        for (OrderTechNoBean bean : mOrderTechNoList) {
            if (bean.isSelect == 1) {
                tvOrderStaffAll.setSelected(false);
            }
        }
    }


}
