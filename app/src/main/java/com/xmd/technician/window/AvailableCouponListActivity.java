package com.xmd.technician.window;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.xmd.technician.http.gson.CouponListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;


/**
 * Created by Administrator on 2016/8/3.
 */
public class AvailableCouponListActivity extends BaseActivity implements View.OnClickListener, ChatCouponAdapter.OnItemClickListener {
    private static List<CouponInfo> couponInfoList;
    @Bind(R.id.list)
    RecyclerView mListView;

    private ChatCouponAdapter adapter;
    private CheckedCoupon coupon;
    private List<CheckedCoupon> checkedCouponList = new ArrayList<>();
    private List<CouponInfo> mSelectedCouponInfo;
    private TextView sent;
    protected LinearLayoutManager mLayoutManager;
    private Subscription mGetRedpacklistSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availabel_coupon_deatil);
        ButterKnife.bind(this);

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
        mSelectedCouponInfo = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mLayoutManager);
        mListView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChatCouponAdapter(AvailableCouponListActivity.this, couponInfoList);
        adapter.setOnItemClickListener(this);
        mListView.setAdapter(adapter);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_LIST);
    }

    public static void setData(List<CouponInfo> couponInfo) {
        couponInfoList = couponInfo;
    }

    @Override
    public void onItemCheck(CouponInfo info, int position, boolean isChecked) {
        if (isChecked) {
            for (int i = 0; i < mSelectedCouponInfo.size(); i++) {
                if (mSelectedCouponInfo.get(i).actId.equals(info.actId)) {
                    mSelectedCouponInfo.remove(mSelectedCouponInfo.get(i));
                    break;
                }
            }
            info.selectedStatus = 1;
            couponInfoList.set(position, info);
        } else {
            info.selectedStatus = 2;
            couponInfoList.set(position, info);
            mSelectedCouponInfo.add(info);
        }
        adapter.notifyItemChanged(position);
    }


    @Override
    public void onClick(View v) {
        if (Utils.isNotFastClick()) {
            if (v.getId() == R.id.toolbar_right) {
                for (int i = 0; i < mSelectedCouponInfo.size(); i++) {
                    coupon = new CheckedCoupon(mSelectedCouponInfo.get(i).useTypeName, mSelectedCouponInfo.get(i).actValue, mSelectedCouponInfo.get(i).couponPeriod,
                            mSelectedCouponInfo.get(i).actId, mSelectedCouponInfo.get(i).couponType, i);
                    checkedCouponList.add(coupon);
                }
                if (couponInfoList.size() > 0) {
                    for (int i = 0; i < checkedCouponList.size(); i++) {
                        RxBus.getInstance().post(checkedCouponList.get(i));
                    }
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


    private void getRedpackListResult(CouponListResult result) {

        Collections.sort(result.respData.coupons, (lhs, rhs) -> {
            if (Constant.COUPON_TYPE_PAID.equals(rhs.couponType)) return 1;
            else if (Constant.COUPON_TYPE_PAID.equals(lhs.couponType)) return -1;
            return 0;
        });
        if (result.respData.coupons != null) {
            couponInfoList.clear();

            for (CouponInfo info : result.respData.coupons) {
                info.selectedStatus = 1;
                couponInfoList.add(info);
            }
            if (couponInfoList != null) {
                adapter.setCouponInfoData(couponInfoList);
            }

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetRedpacklistSubscription);
    }


}
