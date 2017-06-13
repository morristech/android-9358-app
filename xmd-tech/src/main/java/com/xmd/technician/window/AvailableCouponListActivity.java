package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.xmd.technician.Adapter.ChatCouponAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.bean.CouponType;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.gson.CouponListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;


/**
 * Created by Lhj on 2016/8/3.
 */
public class AvailableCouponListActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.toolbar_right_share)
    TextView toolbarRightShare;
    @Bind(R.id.view_emptyView)
    EmptyView viewEmptyView;
    @Bind(R.id.expandable_list_view)
    ExpandableListView expandableListView;

    private ChatCouponAdapter adapter;
    private List<CouponInfo> mSelectedCouponInfo;
    private List<CouponType> mCouponTypes;
    private List<CouponInfo> mPaidCoupons;
    private List<CouponInfo> mCashAndFavourables;
    private List<List<CouponInfo>> mCouponInfos;

    private Subscription mGetCouponListSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availabel_coupon_deatil);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.check_coupon));
        toolbarRightShare.setVisibility(View.VISIBLE);
        toolbarRightShare.setEnabled(false);
        toolbarRightShare.setOnClickListener(this);
        setBackVisible(true);
        viewEmptyView.setStatus(EmptyView.Status.Loading);
        mSelectedCouponInfo = new ArrayList<>();
        mCouponTypes = new ArrayList<>();
        mCouponInfos = new ArrayList<>();
        mPaidCoupons = new ArrayList<>();
        mCashAndFavourables = new ArrayList<>();
        adapter = new ChatCouponAdapter(this);
        adapter.setChildrenClickedInterface(new ChatCouponAdapter.OnChildrenClicked() {
            @Override
            public void onChildrenClickedListener(CouponInfo bean, int groupPosition, int childPosition, boolean isSelected) {
                if (isSelected) {
                    mSelectedCouponInfo.add(bean);
                    mCouponInfos.get(groupPosition).get(childPosition).selectedStatus = 1;
                    adapter.refreshChildData(mCouponInfos);
                } else {
                    mCouponInfos.get(groupPosition).get(childPosition).selectedStatus = 0;
                    adapter.refreshChildData(mCouponInfos);
                    mSelectedCouponInfo.remove(bean);
                }
                if (mSelectedCouponInfo.size() > 0) {
                    toolbarRightShare.setEnabled(true);
                    toolbarRightShare.setText(String.format("分享(%s)", mSelectedCouponInfo.size()));
                } else {
                    toolbarRightShare.setEnabled(false);
                    toolbarRightShare.setText("分享");
                }

            }

        });
        expandableListView.setDivider(null);
        expandableListView.setAdapter(adapter);
        mGetCouponListSubscription = RxBus.getInstance().toObservable(CouponListResult.class).subscribe(
                couponResult -> getCouponListResult(couponResult));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_LIST);
    }

    private void getCouponListResult(CouponListResult result) {
        if(result.respData == null){
            viewEmptyView.setStatus(EmptyView.Status.Empty);
            return;
        }
        Collections.sort(result.respData.coupons, (lhs, rhs) -> {
            if (Constant.COUPON_TYPE_PAID.equals(rhs.couponType)) {
                return 1;
            } else if (Constant.COUPON_TYPE_PAID.equals(lhs.couponType)) return -1;
            return 0;
        });
        if (result.respData.coupons != null && result.respData.coupons.size()>0) {
            viewEmptyView.setStatus(EmptyView.Status.Gone);
            mCouponTypes.clear();
            mPaidCoupons.clear();
            mCashAndFavourables.clear();
            for (CouponInfo info : result.respData.coupons) {
                info.selectedStatus = 0;
                if(info.useTypeName.equals(ResourceUtils.getString(R.string.delivery_coupon))){
                    mPaidCoupons.add(info);
                }else{
                    mCashAndFavourables.add(info);
                }
            }
            if(mPaidCoupons.size()>0){
                mCouponTypes.add(new CouponType("点钟券"));
                mCouponInfos.add(mPaidCoupons);
            }
            if(mCashAndFavourables.size()>0){
                mCouponTypes.add(new CouponType("优惠券"));
                mCouponInfos.add(mCashAndFavourables);
            }
            adapter.setData(mCouponTypes,mCouponInfos);
           if(mCouponInfos.size()>0){
               for (int i = 0; i < mCouponInfos.size(); i++) {
                   expandableListView.expandGroup(i,false);
               }

           }else{
               viewEmptyView.setStatus(EmptyView.Status.Empty);
           }
        }else{
            viewEmptyView.setStatus(EmptyView.Status.Empty);
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
        RxBus.getInstance().unsubscribe(mGetCouponListSubscription);
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
}
