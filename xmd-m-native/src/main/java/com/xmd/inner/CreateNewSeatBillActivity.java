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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.inner.adapter.SeatBillListAdapter;
import com.xmd.inner.bean.NativeCreateBill;
import com.xmd.inner.bean.NativeEmployeeBean;
import com.xmd.inner.bean.NativeItemBean;
import com.xmd.inner.event.CategoryChangedEvent;
import com.xmd.inner.event.EmployeeChangedEvent;
import com.xmd.inner.event.ServiceItemChangedEvent;
import com.xmd.inner.event.UpdateRoomEvent;
import com.xmd.inner.event.UpdateSeatEvent;
import com.xmd.inner.event.UserIdentifyChangedEvent;
import com.xmd.inner.httprequest.DataManager;
import com.xmd.inner.httprequest.response.CreateSeatOrderResult;
import com.xmd.inner.httprequest.response.UserIdentifyListResult;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-12-1.
 * 创建新订单
 */

public class CreateNewSeatBillActivity extends BaseActivity implements SeatBillListAdapter.BillCallBack {

    @BindView(R2.id.tv_seat_num)
    TextView tvSeatNum;
    @BindView(R2.id.et_tech_hand)
    TextView etTechHand;
    @BindView(R2.id.rv_bill)
    RecyclerView rvBill;
    @BindView(R2.id.btn_save)
    Button btnSave;
    @BindView(R2.id.ll_identify_view)
    LinearLayout llIdentifyView;

    public static final String INTENT_KEY_ROOM_NAME = "roomName";
    public static final String INTENT_KEY_SEAT_NAME = "seatName";
    public static final String INTENT_KEY_SEAT_ID = "seatId";
    public static final String INTENT_KEY_ROOM_ID = "roomId";

    private String roomTitle;
    private String seatName;
    private long roomId;
    private long seatId;
    private List<NativeItemBean> mItemsList;
    private List<NativeEmployeeBean> employeeList;
    private SeatBillListAdapter mSeatBillListAdapter;
    private SeatBillDataManager mSeatBillDataManager;
    private FragmentManager fm;
    private SeatBillCategorySelectFragment categoryFragment;
    private SeatBillTechSelectFragment techFragment;
    private SeatBillServiceSelectFragment serviceFragment;
    private UserIdentifySelectFragment identifyFragment;
    private NativeCreateBill nativeBill;

    private FragmentTransaction ft;

    public static void startCreateNewSeatBillActivity(Activity activity, String roomName, long roomId, String seatName, long seatId) {
        Intent intent = new Intent(activity, CreateNewSeatBillActivity.class);
        intent.putExtra(INTENT_KEY_ROOM_NAME, roomName);
        intent.putExtra(INTENT_KEY_SEAT_NAME, seatName);
        intent.putExtra(INTENT_KEY_ROOM_ID, roomId);
        intent.putExtra(INTENT_KEY_SEAT_ID, seatId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_seat_bill);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        roomTitle = getIntent().getStringExtra(INTENT_KEY_ROOM_NAME);
        seatName = getIntent().getStringExtra(INTENT_KEY_SEAT_NAME);
        roomId = getIntent().getLongExtra(INTENT_KEY_ROOM_ID, 0);
        seatId = getIntent().getLongExtra(INTENT_KEY_SEAT_ID, 0);
        setTitle(roomTitle);
        tvSeatNum.setText(seatName);
        setBackVisible(true);
        mSeatBillDataManager = SeatBillDataManager.getManagerInstance();
        nativeBill = new NativeCreateBill();
        initListData();
        getIdentifyList();
    }

    private void initListData() {
        mItemsList = new ArrayList<>();
        addFirstNativeItem();
        mSeatBillListAdapter = new SeatBillListAdapter(this, mItemsList, SeatBillListAdapter.HANDLE_BILL_TYPE_CREATE);
        mSeatBillListAdapter.setBillCallBack(this);
        rvBill.setHasFixedSize(true);
        rvBill.setLayoutManager(new LinearLayoutManager(this));
        rvBill.setAdapter(mSeatBillListAdapter);

    }

    private void addFirstNativeItem() {
        employeeList = new ArrayList<>();
        NativeItemBean bean = new NativeItemBean(employeeList, "", 1, "", "", "");
        mItemsList.add(bean);
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
        employee.setBellId(0);
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
        bundle.putString(SeatBillTechSelectFragment.KEY_CURRENT_SEAT_STATUS, ConstantResource.HANDLE_SEAT_STATUS_CREATE);
        if (mItemsList.get(parentPosition).getItemType().equals(ConstantResource.BILL_GOODS_TYPE)) {
            bundle.putString(SeatBillTechSelectFragment.KEY_SERVICE_ITEM_TYPE, ConstantResource.BILL_GOODS_TYPE);
            if (mSeatBillDataManager.getAllTechList().size() == 0) {
                XToast.show("无可用营销人员");
                return;
            }
        } else {
            bundle.putString(SeatBillTechSelectFragment.KEY_SERVICE_ITEM_TYPE, ConstantResource.BILL_SPA_TYPE);
            if (mSeatBillDataManager.getFreeTechList().size() == 0) {
                XToast.show("无可用营销人员");
                return;
            }
        }
        techFragment.setArguments(bundle);
        techFragment.show(ft, "SeatBillTechSelectFragment");
        techFragment.setTechData(parentPosition, position);
    }

    @Override
    public void billSellTotal(int parentPosition, int total) {
        if (mItemsList.size() == parentPosition) {
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

    @Subscribe
    public void userIdentifyChangedEvent(UserIdentifyChangedEvent event) {
        nativeBill.setUserIdentify(event.getUserIdentify());
        etTechHand.setText(event.getUserIdentify());
    }

    @OnClick(R2.id.et_tech_hand)
    public void onTechHandClicked() {
        if (mSeatBillDataManager.getUserIdentifyBeenList().size() == 0) {
            XToast.show("会所未提供手牌");
            return;
        }
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("UserIdentifySelectFragment");
        if (prev != null) {
            ft.remove(prev);
        }
        identifyFragment = new UserIdentifySelectFragment();
        identifyFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog_Alert);
        identifyFragment.show(ft, "UserIdentifySelectFragment");
    }

    @OnClick(R2.id.btn_save)
    public void onViewClicked() {
        List<NativeCreateBill> billList = new ArrayList<>();
        nativeBill.setItemList(mItemsList);
        nativeBill.setRoomId(roomId);
        nativeBill.setSeatId(seatId);
        billList.add(nativeBill);
        if (mSeatBillDataManager.canToCreateBill(nativeBill)) {
            DataManager.getInstance().createSeatOrder(billList, new NetworkSubscriber<CreateSeatOrderResult>() {
                @Override
                public void onCallbackSuccess(CreateSeatOrderResult result) {
                    String resultStr = result.getRespData().get(0);
                    if (resultStr.equals("true")) {
                        XToast.show(ResourceUtils.getString(R.string.handle_success_message));
                        EventBus.getDefault().post(new UpdateRoomEvent());
                        EventBus.getDefault().post(new UpdateSeatEvent());
                        CreateNewSeatBillActivity.this.finish();
                    } else {
                        XToast.show("开单失败：" + resultStr);
                    }

                }

                @Override
                public void onCallbackError(Throwable e) {
                    XToast.show(e.getLocalizedMessage());
                }
            });
        }

    }

    private void getIdentifyList() {
        DataManager.getInstance().getIdentifyList(new NetworkSubscriber<UserIdentifyListResult>() {
            @Override
            public void onCallbackSuccess(UserIdentifyListResult result) {
                if (result.getRespData() != null && result.getRespData().size() != 0) {
                    llIdentifyView.setVisibility(View.VISIBLE);
                } else {
                    llIdentifyView.setVisibility(View.GONE);
                }
                mSeatBillDataManager.setUserIdentifyBeenList(result.getRespData());
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSeatBillDataManager.onDestroyData();
    }
}
