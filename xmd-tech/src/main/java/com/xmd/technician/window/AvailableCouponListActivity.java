package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;


/**
 * Created by Lhj on 2016/8/3.
 */
public class AvailableCouponListActivity extends BaseActivity implements View.OnClickListener, ChatCouponAdapter.OnItemClickListener {
    private static List<CouponInfo> couponInfoList;
    @Bind(R.id.list)
    RecyclerView mListView;
    @Bind(R.id.toolbar_right_share)
    TextView toolbarRightShare;
    private ChatCouponAdapter adapter;
    private List<CouponInfo> mSelectedCouponInfo;
    protected LinearLayoutManager mLayoutManager;
    private Subscription mGetRedpacklistSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availabel_coupon_deatil);
        ButterKnife.bind(this);
        initView();
    }

    protected void initView() {
        setTitle(ResourceUtils.getString(R.string.check_coupon));
        toolbarRightShare.setVisibility(View.VISIBLE);
        toolbarRightShare.setEnabled(false);
        toolbarRightShare.setOnClickListener(this);
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
        if (mSelectedCouponInfo.size() > 0) {
            toolbarRightShare.setEnabled(true);
            toolbarRightShare.setText(String.format("分享(%s)", String.valueOf(mSelectedCouponInfo.size())));
        } else {
            toolbarRightShare.setEnabled(false);
            toolbarRightShare.setText("分享");
        }
        adapter.notifyItemChanged(position);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_right_share) {
            Intent resultIntent = new Intent();
            resultIntent.putParcelableArrayListExtra(TechChatActivity.REQUEST_COUPON_TYPE, (ArrayList<? extends Parcelable>) mSelectedCouponInfo);
            setResult(RESULT_OK, resultIntent);
            this.finish();
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
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetRedpacklistSubscription);
    }


}
