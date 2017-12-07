package com.xmd.inner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.Constants;
import com.xmd.app.utils.Utils;
import com.xmd.inner.adapter.OrderAdapter;
import com.xmd.inner.adapter.SeatAdapter;
import com.xmd.inner.bean.OrderInfo;
import com.xmd.inner.bean.RoomInfo;
import com.xmd.inner.bean.SeatInfo;
import com.xmd.inner.event.UpdateRoomEvent;
import com.xmd.inner.event.UpdateSeatEvent;
import com.xmd.inner.httprequest.DataManager;
import com.xmd.inner.httprequest.response.RoomOrderInfoResult;
import com.xmd.inner.widget.CustomRecycleViewDecoration;
import com.xmd.inner.widget.FullyGridLayoutManager;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by zr on 17-12-1.
 * 会所房间中座位管理
 */

public class NativeSeatActivity extends BaseActivity {
    public static final String EXTRA_ROOM_ID = "room_id";
    public static final String EXTRA_ROOM_TITLE = "room_title";

    @BindView(R2.id.rv_seat_list)
    RecyclerView mSeatList;
    @BindView(R2.id.rv_order_list)
    RecyclerView mOrderList;

    @BindView(R2.id.layout_amount)
    RelativeLayout mAmountLayout;
    @BindView(R2.id.tv_amount_title)
    TextView mAmountTitle;
    @BindView(R2.id.tv_amount_value)
    TextView mAmountValue;

    @BindView(R2.id.layout_edit)
    LinearLayout mEditLayout;

    @BindView(R2.id.layout_seat_operate)
    LinearLayout mSeatOperateLayout;
    @BindView(R2.id.btn_seat_left)
    Button mSeatLeftBtn;
    @BindView(R2.id.btn_seat_right)
    Button mSeatRightBtn;
    @BindView(R2.id.btn_room_operate)
    Button mRoomOperateBtn;

    private String mRoomId;

    private Subscription mGetOrderInfoByRoomSubscription;
    private Subscription mSetRoomStatusSubscription;
    private Subscription mCancelBookRoomSeatSubscription;

    private SeatAdapter mSeatAdapter;
    private OrderAdapter mOrderAdapter;

    private RoomInfo mRoomInfo;
    private List<OrderInfo> mOrderInfoList;
    private SeatInfo mSelectedSeatInfo;
    private OrderInfo mSelectedOrderInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        setContentView(R.layout.activity_native_seat);
        ButterKnife.bind(this);
        setTitle(getIntent().getStringExtra(EXTRA_ROOM_TITLE));

        mSeatAdapter = new SeatAdapter(this);
        mSeatAdapter.setListener(new SeatAdapter.ItemClickListener() {
            @Override
            public void onItemClick(SeatInfo seatInfo) {
                mSelectedSeatInfo = seatInfo;
                initSeatData(mSelectedSeatInfo);
            }

            @Override
            public void onItemClear() {
                initRoomData(mRoomInfo);
            }
        });
        mSeatList.setHasFixedSize(true);
        mSeatList.setNestedScrollingEnabled(false);
        mSeatList.setLayoutManager(new FullyGridLayoutManager(this, 3));
        mSeatList.setItemAnimator(new DefaultItemAnimator());
        mSeatList.setAdapter(mSeatAdapter);

        mOrderAdapter = new OrderAdapter(this);
        mOrderList.setHasFixedSize(true);
        mOrderList.setNestedScrollingEnabled(false);
        mOrderList.setLayoutManager(new FullyGridLayoutManager(this, 1));
        mOrderList.addItemDecoration(new CustomRecycleViewDecoration(2));
        mOrderList.setItemAnimator(new DefaultItemAnimator());
        mOrderList.setAdapter(mOrderAdapter);

        getOrderInfo(mRoomId);
    }

    private void getOrderInfo(final String roomId) {
        showLoading();
        mGetOrderInfoByRoomSubscription = DataManager.getInstance().getOrderInfoByName(roomId, new NetworkSubscriber<RoomOrderInfoResult>() {
            @Override
            public void onCallbackSuccess(RoomOrderInfoResult result) {
                hideLoading();
                mRoomInfo = result.getRespData().room;
                mOrderInfoList = result.getRespData().orderList;
                if (mRoomInfo != null) {
                    mSeatList.removeAllViews();
                    mSeatAdapter.clearData();
                    mSeatAdapter.setData(mRoomInfo.seatList);
                    initRoomData(mRoomInfo);
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                hideLoading();
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    private void initRoomData(RoomInfo roomInfo) {
        switch (roomInfo.status) {
            case ConstantResource.STATUS_USING: //使用
                if (mOrderInfoList != null && !mOrderInfoList.isEmpty()) {
                    mAmountLayout.setVisibility(View.VISIBLE);
                    mAmountTitle.setText("房间消费合计");
                    mAmountValue.setText(Constants.MONEY_TAG + Utils.moneyToStringEx(getTotalAmount(mOrderInfoList)));
                    mOrderList.setVisibility(View.VISIBLE);
                    mOrderList.removeAllViews();
                    mOrderAdapter.clearData();
                    mOrderAdapter.setData(mOrderInfoList);
                } else {
                    mAmountLayout.setVisibility(View.GONE);
                    mOrderList.setVisibility(View.GONE);
                }
                mEditLayout.setVisibility(View.GONE);
                mSeatOperateLayout.setVisibility(View.GONE);
                mRoomOperateBtn.setVisibility(View.GONE);
                break;
            case ConstantResource.STATUS_BOOKED: //预订
            case ConstantResource.STATUS_CLEAN: //清洁
                mAmountLayout.setVisibility(View.GONE);
                mOrderList.setVisibility(View.GONE);
                mEditLayout.setVisibility(View.GONE);
                mSeatOperateLayout.setVisibility(View.GONE);
                mRoomOperateBtn.setVisibility(View.GONE);
                break;
            case ConstantResource.STATUS_FREE:  //空闲
                mAmountLayout.setVisibility(View.GONE);
                mOrderList.setVisibility(View.GONE);
                mEditLayout.setVisibility(View.GONE);
                mSeatOperateLayout.setVisibility(View.GONE);
                mRoomOperateBtn.setVisibility(View.VISIBLE);
                mRoomOperateBtn.setText("禁用");
                break;
            case ConstantResource.STATUS_DISABLED:  //禁用
                mAmountLayout.setVisibility(View.GONE);
                mOrderList.setVisibility(View.GONE);
                mEditLayout.setVisibility(View.GONE);
                mSeatOperateLayout.setVisibility(View.GONE);
                mRoomOperateBtn.setVisibility(View.VISIBLE);
                mRoomOperateBtn.setText("取消禁用");
                break;
            default:
                break;
        }
    }

    private void initSeatData(SeatInfo seatInfo) {
        switch (seatInfo.status) {
            case ConstantResource.STATUS_USING: //使用
                mSelectedOrderInfo = getSelectedOrderInfo(mOrderInfoList, seatInfo.id);
                if (mSelectedOrderInfo != null) {
                    mAmountLayout.setVisibility(View.VISIBLE);
                    mAmountTitle.setText("消费合计");
                    mAmountValue.setText(Constants.MONEY_TAG + Utils.moneyToStringEx(mSelectedOrderInfo.amount));
                    mOrderList.setVisibility(View.VISIBLE);
                    mOrderList.removeAllViews();
                    mOrderAdapter.clearData();
                    mOrderAdapter.setData(mSelectedOrderInfo);
                } else {
                    mAmountLayout.setVisibility(View.GONE);
                    mOrderList.setVisibility(View.GONE);
                }
                mEditLayout.setVisibility(View.VISIBLE);
                mSeatOperateLayout.setVisibility(View.VISIBLE);
                mSeatLeftBtn.setVisibility(View.GONE);
                mSeatRightBtn.setVisibility(View.GONE);
                // FIXME 完善结账功能后开通
                mSeatRightBtn.setText("结账");
                mRoomOperateBtn.setVisibility(View.GONE);
                break;
            case ConstantResource.STATUS_BOOKED: //预订
                mSelectedOrderInfo = null;
                mAmountLayout.setVisibility(View.GONE);
                mOrderList.setVisibility(View.GONE);
                mEditLayout.setVisibility(View.GONE);
                mSeatOperateLayout.setVisibility(View.VISIBLE);
                mSeatLeftBtn.setVisibility(View.VISIBLE);
                mSeatLeftBtn.setText("开单");
                mSeatRightBtn.setVisibility(View.VISIBLE);
                mSeatRightBtn.setText("取消预订");
                mRoomOperateBtn.setVisibility(View.GONE);
                break;
            case ConstantResource.STATUS_CLEAN: //清洁
            case ConstantResource.STATUS_DISABLED:  //禁用
                mSelectedOrderInfo = null;
                mAmountLayout.setVisibility(View.GONE);
                mOrderList.setVisibility(View.GONE);
                mEditLayout.setVisibility(View.GONE);
                mSeatOperateLayout.setVisibility(View.GONE);
                mRoomOperateBtn.setVisibility(View.GONE);
                break;
            case ConstantResource.STATUS_FREE:  //空闲
                mSelectedOrderInfo = null;
                mAmountLayout.setVisibility(View.GONE);
                mOrderList.setVisibility(View.GONE);
                mEditLayout.setVisibility(View.GONE);
                mRoomOperateBtn.setVisibility(View.GONE);
                mSeatOperateLayout.setVisibility(View.VISIBLE);
                mSeatLeftBtn.setVisibility(View.VISIBLE);
                mSeatLeftBtn.setText("开单");
                mSeatRightBtn.setVisibility(View.VISIBLE);
                mSeatRightBtn.setText("预订");
                break;
            default:
                break;
        }
    }

    private OrderInfo getSelectedOrderInfo(List<OrderInfo> list, long seatId) {
        for (OrderInfo orderInfo : list) {
            if (seatId == orderInfo.seatId) {
                return orderInfo;
            }
        }
        return null;
    }

    private int getTotalAmount(List<OrderInfo> list) {
        int amount = 0;
        for (OrderInfo orderInfo : list) {
            amount += orderInfo.amount;
        }
        return amount;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGetOrderInfoByRoomSubscription != null) {
            mGetOrderInfoByRoomSubscription.unsubscribe();
        }
        if (mCancelBookRoomSeatSubscription != null) {
            mCancelBookRoomSeatSubscription.unsubscribe();
        }
        if (mSetRoomStatusSubscription != null) {
            mSetRoomStatusSubscription.unsubscribe();
        }
    }

    @OnClick(R2.id.tv_consume_add)
    public void onConsumeAdd() {
        // TODO 跳转 增加消费
    }

    @OnClick(R2.id.tv_consume_modify)
    public void onConsumeModify() {
        // TODO 跳转 修改
    }

    @OnClick(R2.id.btn_seat_left)
    public void onSeatLeftClick() {
        // TODO 跳转 开单
    }

    @OnClick(R2.id.btn_seat_right)
    public void onSeatRightClick() {
        switch (mSelectedSeatInfo.status) {
            case ConstantResource.STATUS_BOOKED:    //预订-->取消预订
                cancelBookSeat();
                break;
            case ConstantResource.STATUS_FREE:      //空闲-->预定
                Intent intent = new Intent(NativeSeatActivity.this, NativeSeatBookActivity.class);
                intent.putExtra(NativeSeatBookActivity.EXTRA_SEAT_INFO, mSelectedSeatInfo);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void cancelBookSeat() {
        showLoading();
        mCancelBookRoomSeatSubscription = DataManager.getInstance().cancelRoomSeatBook(String.valueOf(mSelectedSeatInfo.id), new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                hideLoading();
                XToast.show("已取消预订");
                getOrderInfo(mRoomId);
                EventBus.getDefault().post(new UpdateRoomEvent());
            }

            @Override
            public void onCallbackError(Throwable e) {
                hideLoading();
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    @OnClick(R2.id.btn_room_operate)
    public void onRoomOperateClick() {
        String operate = null;
        switch (mRoomInfo.status) {
            case ConstantResource.STATUS_DISABLED:  // 禁用-->取消禁用
                operate = ConstantResource.STATUS_FREE;
                break;
            case ConstantResource.STATUS_FREE:  // 空闲-->禁用
                operate = ConstantResource.STATUS_DISABLED;
                break;
            default:
                break;
        }
        showLoading();
        mSetRoomStatusSubscription = DataManager.getInstance().setRoomStatus(mRoomId, operate, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                hideLoading();
                XToast.show("操作成功");
                getOrderInfo(mRoomId);
                EventBus.getDefault().post(new UpdateRoomEvent());
            }

            @Override
            public void onCallbackError(Throwable e) {
                hideLoading();
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateSeatEvent event) {
        getOrderInfo(mRoomId);
    }
}
