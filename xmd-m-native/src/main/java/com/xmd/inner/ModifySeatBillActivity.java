package com.xmd.inner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.inner.adapter.SeatBillListAdapter;
import com.xmd.inner.bean.ConsumeInfo;
import com.xmd.inner.bean.EmployeeInfo;
import com.xmd.inner.bean.NativeEmployeeBean;
import com.xmd.inner.bean.NativeItemBean;
import com.xmd.inner.bean.NativeUpdateBill;
import com.xmd.inner.bean.OrderInfo;
import com.xmd.inner.event.CategoryChangedEvent;
import com.xmd.inner.event.EmployeeChangedEvent;
import com.xmd.inner.event.ServiceItemChangedEvent;
import com.xmd.inner.event.UpdateRoomEvent;
import com.xmd.inner.event.UpdateSeatEvent;
import com.xmd.inner.httprequest.DataManager;
import com.xmd.inner.httprequest.response.OrderItemUpdateResult;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-12-5.
 * 修改消费项
 */

public class ModifySeatBillActivity extends BaseActivity implements SeatBillListAdapter.BillCallBack {

    @BindView(R2.id.tv_seat_num)
    TextView tvSeatNum;
    @BindView(R2.id.et_tech_hand)
    TextView etTechHand;
    @BindView(R2.id.rv_bill)
    RecyclerView rvBill;
    @BindView(R2.id.btn_save)
    Button btnSave;

    public static final String INTENT_KEY_ROOM_NAME = "roomName";
    public static final String INTENT_KEY_ORDER_INFO = "orderInfo";


    private List<NativeItemBean> mItemsList;
    private SeatBillListAdapter mSeatBillListAdapter;
    private SeatBillDataManager mSeatBillDataManager;
    private SeatBillCategorySelectFragment categoryFragment;
    private SeatBillTechSelectFragment techFragment;
    private SeatBillServiceSelectFragment serviceFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;

    private NativeUpdateBill addBill;
    private OrderInfo mOrderInfo;
    private String roomTitle;


    public static void startModifySeatBillActivity(Activity activity, String roomName, OrderInfo order) {
        if (order == null) {
            XToast.show("所选座位数据异常");
            return;
        }
        Intent intent = new Intent(activity, ModifySeatBillActivity.class);
        intent.putExtra(INTENT_KEY_ROOM_NAME, roomName);
        intent.putExtra(INTENT_KEY_ORDER_INFO, order);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_seat_bill);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        showLoading("正在加载...");
        roomTitle = getIntent().getStringExtra(INTENT_KEY_ROOM_NAME);
        mOrderInfo = getIntent().getParcelableExtra(INTENT_KEY_ORDER_INFO);
        tvSeatNum.setText(roomTitle);
        etTechHand.setText(mOrderInfo.userIdentify);
        setTitle(mOrderInfo.roomName);
        setBackVisible(true);
        mSeatBillDataManager = SeatBillDataManager.getManagerInstance();
        addBill = new NativeUpdateBill();
        ThreadPoolManager.postToUIDelayed(new Runnable() {
            @Override
            public void run() {
                initListData();
                hideLoading();
            }
        }, 2000);

    }

    private void initListData() {
        mItemsList = new ArrayList<>();
        handleOrderData();
        mSeatBillListAdapter = new SeatBillListAdapter(this, mItemsList, SeatBillListAdapter.HANDLE_BILL_TYPE_MODIFY);
        mSeatBillListAdapter.setBillCallBack(this);
        rvBill.setHasFixedSize(true);
        rvBill.setLayoutManager(new LinearLayoutManager(this));
        rvBill.setAdapter(mSeatBillListAdapter);

    }

    private void handleOrderData() {
        for (ConsumeInfo info : mOrderInfo.itemList) {
            NativeItemBean bean = new NativeItemBean();
            bean.setItemCount(info.itemCount);
            bean.setItemId(info.itemId);
            bean.setItemType(info.itemType);
            bean.setServiceName(info.itemName);
            bean.setConsumeName(mSeatBillDataManager.getConsumeName(info.itemId));
            List<NativeEmployeeBean> employeeList = new ArrayList<>();
            if (info.employeeList.size() > 0) {
                for (EmployeeInfo employeeInfo : info.employeeList) {
                    NativeEmployeeBean employee = new NativeEmployeeBean();
                    employee.setBellId(employeeInfo.bellId);
                    employee.setEmployeeId(employeeInfo.employeeId);
                    employee.setServiceType(info.itemType);
                    employee.setEmployeeName(String.format("[%s]", employeeInfo.employeeNo));
                    employeeList.add(employee);
                }
            } else {
                NativeEmployeeBean employee = new NativeEmployeeBean();
                employee.setServiceType(info.itemType);
                employee.setBellId(0);
                employee.setEmployeeId("");
                employee.setEmployeeName("");
                employeeList.add(employee);
                bean.setEmployeeList(employeeList);
            }

            bean.setEmployeeList(employeeList);
            mItemsList.add(bean);
        }
    }

    @Override
    public void consumeItemChoice(int position) {
        ServiceCategorySelect(position);
    }

    private void ServiceCategorySelect(int position) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("CategorySelectFragment");
        if (prev != null) {
            ft.remove(prev);
        }
        categoryFragment = new SeatBillCategorySelectFragment();
        categoryFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog_Alert);
        categoryFragment.show(ft, "CategorySelectFragment");
        categoryFragment.setCategoryData(position);
    }

    @Override
    public void serviceItemChoice(int parentPosition) {
        serviceItemSelect(parentPosition);
    }

    private void serviceItemSelect(final int position) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("SeatBillServiceSelectFragment");
        if (prev != null) {
            ft.remove(prev);
        }
        serviceFragment = new SeatBillServiceSelectFragment();
        serviceFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog_Alert);
        serviceFragment.show(ft, "SeatBillServiceSelectFragment");
        ThreadPoolManager.postToUIDelayed(new Runnable() {
            @Override
            public void run() {
                serviceFragment.setServiceItemData(position, mSeatBillDataManager.getServiceItemList(mItemsList.get(position).getSelectedPosition()));
            }
        }, 100);

    }

    @Override
    public void newAddItem() {
        newAddNativeItem();
    }

    private void newAddNativeItem() {
        List<NativeEmployeeBean> employeeList = new ArrayList<>();
        NativeItemBean bean = new NativeItemBean(employeeList, "", 1, "", "", "");
        mItemsList.add(bean);
        mSeatBillListAdapter.setNativeData(mItemsList);
    }

    @Override
    public void deleteItem(int position) {
        deleteNativeItem(position);
    }

    private void deleteNativeItem(int position) {
        mItemsList.remove(position);
        mSeatBillListAdapter.setNativeData(mItemsList);
    }

    @Override
    public void addTechItem(int parentPosition) {
        addEmployee(parentPosition);
    }

    private void addEmployee(int parentPosition) {
        NativeItemBean bean = mItemsList.get(parentPosition);
        List<NativeEmployeeBean> employeeList = bean.getEmployeeList();
        NativeEmployeeBean employee = new NativeEmployeeBean();
        employee.setServiceType(bean.getItemType());
        employee.setBellId(null);
        employee.setEmployeeId("");
        employeeList.add(employee);
        bean.setEmployeeList(employeeList);
        mSeatBillListAdapter.notifyItemChanged(parentPosition);
    }

    @Override
    public void removeTechItem(int parentPosition, int position) {
        removeEmployee(parentPosition, position);
    }

    private void removeEmployee(int parentPosition, int position) {
        NativeItemBean bean = mItemsList.get(parentPosition);
        List<NativeEmployeeBean> employeeList = bean.getEmployeeList();
        if (position >= employeeList.size()) {
            return;
        }
        employeeList.remove(position);
        bean.setEmployeeList(employeeList);
        mSeatBillListAdapter.notifyItemChanged(parentPosition);
    }

    @Override
    public void techChoice(int parentPosition, int position) {
        serviceTechSelect(parentPosition, position);
    }

    private void serviceTechSelect(int parentPosition, int position) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("SeatBillTechSelectFragment");
        if (prev != null) {
            ft.remove(prev);
        }
        techFragment = new SeatBillTechSelectFragment();
        techFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog_Alert);
        Bundle bundle = new Bundle();
        bundle.putString(SeatBillTechSelectFragment.KEY_CURRENT_SEAT_STATUS, ConstantResource.HANDLE_SEAT_STATUS_EDIT);
        if (mItemsList.get(parentPosition).getItemType().equals(ConstantResource.BILL_GOODS_TYPE)) {
            bundle.putString(SeatBillTechSelectFragment.KEY_SERVICE_ITEM_TYPE, ConstantResource.BILL_GOODS_TYPE);
            if (mSeatBillDataManager.getAllTechList().size() == 0) {
                XToast.show(ResourceUtils.getString(R.string.has_no_tech));
                return;
            }
        } else {
            bundle.putString(SeatBillTechSelectFragment.KEY_SERVICE_ITEM_TYPE, ConstantResource.BILL_SPA_TYPE);
            if (mSeatBillDataManager.getFreeTechList().size() == 0) {
                XToast.show(ResourceUtils.getString(R.string.has_no_tech));
                return;
            }
        }
        techFragment.setArguments(bundle);
        techFragment.show(ft, "SeatBillTechSelectFragment");
        techFragment.setTechData(parentPosition, position);
    }

    @Override
    public void billSellTotal(int parentPosition, int total) {
        if (parentPosition >= mItemsList.size()) {
            return;
        }
        NativeItemBean bean = mItemsList.get(parentPosition);
        bean.setItemCount(total);
    }

    @Override
    public void billTimeType(int parentPosition, int position, int type) {
        NativeItemBean bean = mItemsList.get(parentPosition);
        NativeEmployeeBean employeeBean = bean.getEmployeeList().get(position);
        employeeBean.setBellId(type);
    }

    @Subscribe
    public void CategoryChangedEvent(CategoryChangedEvent event) {
        int position = event.getPosition();
        NativeItemBean bean = mItemsList.get(position);
        if (bean.getConsumeName().equals(event.getName())) {
            return;
        }
        bean.setConsumeName(event.getName());
        bean.setItemType(event.getType());
        bean.setSelectedPosition(event.getSelectedPosition());
        bean.setServiceName("");
        bean.setItemId("");
        List<NativeEmployeeBean> employeeList = bean.getEmployeeList();
        employeeList.clear();
        if (bean.getEmployeeList().size() == 0) {
            NativeEmployeeBean employee = new NativeEmployeeBean();
            employee.setServiceType(event.getType());
            employee.setBellId(0);
            employee.setEmployeeId("");
            employee.setEmployeeName("");
            employeeList.add(employee);
            bean.setEmployeeList(employeeList);
        }
        mSeatBillListAdapter.notifyItemChanged(position);
    }

    @Subscribe
    public void EmployeeChangedEvent(EmployeeChangedEvent event) {
        int parentPosition = event.getParentPosition();
        int position = event.getPosition();
        String employeeId = event.getTechId();
        String employeeName = "";
        if (TextUtils.isEmpty(event.getTechNo())) {
            employeeName = event.getTechName();
        } else {
            employeeName = String.format("[%s] %s", event.getTechNo(), event.getTechName());
        }
        NativeItemBean bean = mItemsList.get(parentPosition);
        List<NativeEmployeeBean> employeeList = bean.getEmployeeList();
        if (employeeList.contains(employeeList.get(position))) {
            NativeEmployeeBean employeeBean = employeeList.get(position);
            employeeBean.setEmployeeName(employeeName);
            employeeBean.setEmployeeId(employeeId);
        } else {
            NativeEmployeeBean newBean = new NativeEmployeeBean();
            newBean.setEmployeeName(employeeName);
            newBean.setEmployeeId(employeeId);
            employeeList.add(newBean);
        }

        mSeatBillListAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void serviceItemChangedEvent(ServiceItemChangedEvent event) {
        int parentPosition = event.getParentPosition();
        NativeItemBean nativeItemBean = mItemsList.get(parentPosition);
        nativeItemBean.setItemId(event.getItemId());
        nativeItemBean.setServiceName(event.getItemName());
        mSeatBillListAdapter.notifyDataSetChanged();

    }


    @OnClick(R2.id.btn_save)
    public void onViewClicked() {
        addBill.setItemList(mItemsList);
        addBill.setId(mOrderInfo.id);
        if (mSeatBillDataManager.canToAddBill(addBill)) {
            DataManager.getInstance().updateOrderBillItem(addBill, new NetworkSubscriber<OrderItemUpdateResult>() {
                @Override
                public void onCallbackSuccess(OrderItemUpdateResult result) {
                    XToast.show(ResourceUtils.getString(R.string.handle_success_message));
                    EventBus.getDefault().post(new UpdateRoomEvent());
                    EventBus.getDefault().post(new UpdateSeatEvent());
                    ModifySeatBillActivity.this.finish();
                }

                @Override
                public void onCallbackError(Throwable e) {
                    XToast.show(e.getLocalizedMessage());
                }
            });
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mSeatBillDataManager.onDestroyData();
    }
}
