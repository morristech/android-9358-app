package com.xmd.inner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.widget.ClearableEditText;
import com.xmd.inner.adapter.ExRoomAdapter;
import com.xmd.inner.adapter.RoomStatisticsAdapter;
import com.xmd.inner.bean.ExRoomInfo;
import com.xmd.inner.bean.RoomInfo;
import com.xmd.inner.bean.RoomStatisticInfo;
import com.xmd.inner.bean.SeatInfo;
import com.xmd.inner.event.UpdateRoomEvent;
import com.xmd.inner.httprequest.DataManager;
import com.xmd.inner.httprequest.response.RoomSeatListResult;
import com.xmd.inner.httprequest.response.RoomStatisticResult;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by zr on 17-12-1.
 * 会所房间管理
 */

public class NativeRoomActivity extends BaseActivity {
    @BindView(R2.id.edt_search_room)
    ClearableEditText mSearchEdit;
    @BindView(R2.id.rv_room_list)
    RecyclerView mRoomList;
    @BindView(R2.id.rv_room_status)
    RecyclerView mRoomStatus;
    @BindView(R2.id.layout_status)
    LinearLayout mStatusLayout;

    private String mStatus;
    private String mTempRoomName;

    private Subscription mGetRoomSeatListSubscription;
    private Subscription mGetRoomStatisticsSubscription;
    private ExRoomAdapter mExRoomAdapter;
    private RoomStatisticsAdapter mRoomStatisticsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_room);
        ButterKnife.bind(this);
        setTitle(ResourceUtils.getString(R.string.native_room_title));

        mExRoomAdapter = new ExRoomAdapter(NativeRoomActivity.this);
        mRoomList.setLayoutManager(new LinearLayoutManager(NativeRoomActivity.this));
        mRoomList.setHasFixedSize(true);
        mRoomList.setAdapter(mExRoomAdapter);

        getRoomSeatList(mStatus, mTempRoomName);
        getRoomStatistics();
    }

    @OnClick(R2.id.img_search_confirm)
    public void onSearch() {
        mTempRoomName = mSearchEdit.getText().toString().trim();
        getRoomSeatList(mStatus, mTempRoomName);
    }

    private void getRoomStatistics() {
        mGetRoomStatisticsSubscription = DataManager.getInstance().getRoomStatistics(new NetworkSubscriber<RoomStatisticResult>() {
            @Override
            public void onCallbackSuccess(RoomStatisticResult result) {
                if (result.getRespData().statusList != null && !result.getRespData().statusList.isEmpty()) {
                    List<RoomStatisticInfo> tempList = result.getRespData().statusList;
                    Iterator<RoomStatisticInfo> it = tempList.iterator();
                    while (it.hasNext()) {
                        RoomStatisticInfo info = it.next();
                        if (!ConstantResource.RESPONSE_YES.equals(info.status)) {
                            it.remove();
                        }
                    }
                    initStatus(tempList);
                } else {
                    mStatusLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                mStatusLayout.setVisibility(View.GONE);
            }
        });
    }

    private void initStatus(List<RoomStatisticInfo> list) {
        mStatusLayout.setVisibility(View.VISIBLE);
        mRoomStatus.setVisibility(View.VISIBLE);
        mRoomStatisticsAdapter = new RoomStatisticsAdapter(NativeRoomActivity.this, RoomStatisticsAdapter.PAGE_OTHER);
        mRoomStatisticsAdapter.setListener(new RoomStatisticsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RoomStatisticInfo statisticInfo, int position) {
                statisticInfo.filter = !statisticInfo.filter;
                mRoomStatisticsAdapter.notifyItemChanged(position);
                mStatus = mRoomStatisticsAdapter.getFilterStr();
                getRoomSeatList(mStatus, mTempRoomName);
            }
        });
        mRoomStatus.setLayoutManager(new GridLayoutManager(NativeRoomActivity.this, list.size()));
        mRoomStatus.setHasFixedSize(true);
        mRoomStatus.setAdapter(mRoomStatisticsAdapter);
        mRoomStatisticsAdapter.setData(list);
    }

    private void getRoomSeatList(String status, String roomName) {
        showLoading();
        mGetRoomSeatListSubscription = DataManager.getInstance().getRoomSeatInfoList(status, roomName, new NetworkSubscriber<RoomSeatListResult>() {
            @Override
            public void onCallbackSuccess(RoomSeatListResult result) {
                hideLoading();
                mRoomList.removeAllViews();
                mExRoomAdapter.clearData();
                mExRoomAdapter.setData(formatRoomInfo(result.getRespData()));
            }

            @Override
            public void onCallbackError(Throwable e) {
                hideLoading();
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    private List<ExRoomInfo> formatRoomInfo(List<RoomInfo> list) {
        Map<Long, List<RoomInfo>> tempListMap = new HashMap<>();
        Map<Long, String> tempStrMap = new HashMap<>();
        for (RoomInfo roomInfo : list) {
            // 计算使用中座位的数量
            int count = 0;
            for (SeatInfo seatInfo : roomInfo.seatList) {
                if (ConstantResource.STATUS_USING.equals(seatInfo.status)) {
                    count++;
                }
            }
            roomInfo.useCount = count;
            // 按照房间类型分类
            if (tempListMap.containsKey(roomInfo.roomTypeId)) {
                tempListMap.get(roomInfo.roomTypeId).add(roomInfo);
            } else {
                List<RoomInfo> tempList = new ArrayList<>();
                tempList.add(roomInfo);
                tempListMap.put(roomInfo.roomTypeId, tempList);
                tempStrMap.put(roomInfo.roomTypeId, roomInfo.roomTypeName);
            }
        }
        List<ExRoomInfo> resultList = new ArrayList<>();
        Iterator<Map.Entry<Long, List<RoomInfo>>> it = tempListMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, List<RoomInfo>> entry = it.next();
            ExRoomInfo exRoomInfo = new ExRoomInfo();
            exRoomInfo.roomTypeId = entry.getKey();
            exRoomInfo.roomTypeName = tempStrMap.get(entry.getKey());
            exRoomInfo.rooms = entry.getValue();
            resultList.add(exRoomInfo);
        }
        return resultList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGetRoomSeatListSubscription != null) {
            mGetRoomSeatListSubscription.unsubscribe();
        }
        if (mGetRoomStatisticsSubscription != null) {
            mGetRoomStatisticsSubscription.unsubscribe();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RoomInfo info) {
        Intent intent = new Intent(this, NativeSeatActivity.class);
        intent.putExtra(NativeSeatActivity.EXTRA_ROOM_ID, String.valueOf(info.id));
        intent.putExtra(NativeSeatActivity.EXTRA_ROOM_TITLE, info.roomTypeName + " " + info.name);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateRoomEvent event) {
        getRoomSeatList(mStatus, mTempRoomName);
        getRoomStatistics();
    }
}
