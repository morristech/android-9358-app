package com.xmd.technician.window;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xmd.technician.Adapter.ChatCouponAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.CheckedCoupon;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.CouponListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;


/**
 * Created by Administrator on 2016/8/3.
 */
public class AvailableCouponListActivity extends BaseActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {
    private static List<CouponInfo> couponInfoList;
    @Bind(R.id.list)
    RecyclerView mListView;
    @Bind(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshWidget;
    private ChatCouponAdapter adapter;
    private CheckedCoupon coupon;
    private List<CheckedCoupon> checkedCouponList = new ArrayList<>();
    private TextView sent;
    private Map<String, String> map = new HashMap<>();
    protected LinearLayoutManager mLayoutManager;
    private Subscription mGetRedpacklistSubscription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availabel_coupon_deatil);
        ButterKnife.bind(this);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_LIST);
        sent = (TextView) findViewById(R.id.toolbar_right);
        sent.setOnClickListener(this);
        initView();
    }

    protected void initView() {
        setTitle(ResourceUtils.getString(R.string.check_coupon));
        setRightVisible(true, R.string.confirm);
        setBackVisible(true);
        mGetRedpacklistSubscription = RxBus.getInstance().toObservable(CouponListResult.class).subscribe(
                redpackResult -> getRedpackListResult(redpackResult));

        mSwipeRefreshWidget.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshWidget.setOnRefreshListener(this);
        mLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mLayoutManager);
        mListView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChatCouponAdapter(AvailableCouponListActivity.this, couponInfoList, new ChatCouponAdapter.OnItemClickListener() {
            @Override
            public void onItemCheck(int position, View view) {
                coupon = new CheckedCoupon(couponInfoList.get(position).useTypeName, couponInfoList.get(position).actValue, couponInfoList.get(position).couponPeriod,
                        couponInfoList.get(position).actId, couponInfoList.get(position).couponType, position);
                if (map.containsKey(String.valueOf(position))) {
                    view.setEnabled(false);
                    map.remove(String.valueOf(position));
                    for (int i = 0; i < checkedCouponList.size(); i++) {
                        if (checkedCouponList.get(i).position == position) {
                            checkedCouponList.remove(i);
                            return;
                        }
                    }
                } else {
                    checkedCouponList.add(coupon);
                    map.put(String.valueOf(position), "");
                    view.setEnabled(true);
                }
            }
        });
        mListView.setAdapter(adapter);

    }

    public static void setData(List<CouponInfo> couponInfo) {
        couponInfoList = couponInfo;
    }

    @Override
    public void onClick(View v) {
        if (Utils.isNotFastClick()) {
            if (v.getId() == R.id.toolbar_right) {
                for (int i = 0; i < checkedCouponList.size(); i++) {
                    RxBus.getInstance().post(checkedCouponList.get(i));
                }
                ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 600);
            }
        }
    }

    @Override
    public void onRefresh() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_LIST);
    }

    private void getRedpackListResult(CouponListResult result) {

        Collections.sort(result.respData.coupons, (lhs, rhs) -> {
            if(Constant.COUPON_TYPE_PAID.equals(rhs.couponType)) return 1;
            else if(Constant.COUPON_TYPE_PAID.equals(lhs.couponType)) return -1;
            return 0;
        });
        if (result.respData.coupons != null) {
            couponInfoList.clear();
            for (CouponInfo info : result.respData.coupons) {
                couponInfoList.add(info);
            }
        }
        adapter.notifyDataSetChanged();
        mSwipeRefreshWidget.setRefreshing(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetRedpacklistSubscription);
    }
}