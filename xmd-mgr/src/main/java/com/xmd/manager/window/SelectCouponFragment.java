package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.adapter.CouponOperateSelectAdapter;
import com.xmd.manager.beans.CouponBean;
import com.xmd.manager.common.CouponDataFilterManager;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CouponListResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;

/**
 * Created by Lhj on 17-10-18.
 */

public class SelectCouponFragment extends BaseFragment {
    @BindView(R.id.available_coupon)
    RecyclerView availableCoupon;
    @BindView(R.id.img_tag)
    ImageView imgTag;
    @BindView(R.id.ll_unusable)
    LinearLayout llUnusable;
    @BindView(R.id.unusable_coupon)
    RecyclerView unusableCoupon;
    @BindView(R.id.tv_all_coupon)
    TextView tvAllCoupon;
    Unbinder unbinder;

    private boolean unusableIsOpen;
    private CouponOperateSelectAdapter mUsableCouponAdapter;
    private CouponOperateSelectAdapter mUnusableCouponAdapter;
    public List<CouponBean> mUsableCouponOperateList;
    public List<CouponBean> mUnusableCouponOperateList;
    public CouponBean mSelectedCoupon;
    public int mSelectedPosition = -1;
    private Map<String, String> mUsableParams;
    private Map<String, String> mUnusableParams;
    public Subscription mCouponListSubscription;
    public CouponDataFilterManager mDataFilterManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_coupon, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        showLoading(getActivity(), "正在加载...");
        mSelectedCoupon = getArguments().getParcelable(CouponFilterActivity.COUPON_FILTER_SELECTED_COUPON);
        unusableIsOpen = false;
        tvAllCoupon.setSelected(null == mSelectedCoupon ? true : false);
        initUsableCouponRecyclerView();
        initUnusableCouponRecyclerView();
        mCouponListSubscription = RxBus.getInstance().toObservable(CouponListResult.class).subscribe(
                result -> handleCouponListResult(result)
        );
        mDataFilterManager = CouponDataFilterManager.getCouponFilterManagerInstance();
        if (mUsableParams == null) {
            mUsableParams = new HashMap<>();
        }

        if (mUnusableParams == null) {
            mUnusableParams = new HashMap<>();
        }
        getOnlineCouponList();
        getOfflineCouponList();

    }

    private void getOnlineCouponList() {
        mUsableParams.clear();
        mUsableParams.put(RequestConstant.KEY_PAGE, String.valueOf(1));
        mUsableParams.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(100));
        mUsableParams.put(RequestConstant.KEY_COUPON_TYPE, Constant.COUPON_ALL_TYPE);
        mUsableParams.put(RequestConstant.KEY_COUPON_ONLINE, Constant.COUPON_ONLINE_TRUE);
        mUsableParams.put(RequestConstant.KEY_COUPON_LIST_TYPE, Constant.COUPON_ONLINE_TYPE);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_COUPON_LIST_DATA, mUsableParams);
    }

    private void getOfflineCouponList() {
        mUnusableParams.clear();
        mUnusableParams.put(RequestConstant.KEY_PAGE, String.valueOf(1));
        mUnusableParams.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(100));
        mUnusableParams.put(RequestConstant.KEY_COUPON_TYPE, Constant.COUPON_ALL_TYPE);
        mUnusableParams.put(RequestConstant.KEY_COUPON_ONLINE, Constant.COUPON_ONLINE_FALSE);
        mUnusableParams.put(RequestConstant.KEY_COUPON_LIST_TYPE, Constant.COUPON_OFFLINE_TYPE);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_COUPON_LIST_DATA, mUnusableParams);
    }

    private void handleCouponListResult(CouponListResult result) {
        hideLoading();
        if (result.statusCode == 200) {
            switch (result.onLineType) {
                case Constant.COUPON_ONLINE_TYPE:
                    mDataFilterManager.setOnlineCoupons(result.respData, mSelectedCoupon);
                    mUsableCouponAdapter.setData(mDataFilterManager.getOnlineCoupons());
                    break;
                case Constant.COUPON_OFFLINE_TYPE:
                    mDataFilterManager.setOfflineCoupons(result.respData, mSelectedCoupon);
                    mUnusableCouponAdapter.setData(mDataFilterManager.getOfflineCoupons());
                    break;
            }
        }
    }

    private void initUsableCouponRecyclerView() {
        mUsableCouponOperateList = new ArrayList<>();
        availableCoupon.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mUsableCouponAdapter = new CouponOperateSelectAdapter(mUsableCouponOperateList);
        availableCoupon.setHasFixedSize(true);
        availableCoupon.setAdapter(mUsableCouponAdapter);
        mUsableCouponAdapter.setCouponItemClickedListener(usableCouponListener);
    }

    private void initUnusableCouponRecyclerView() {
        mUnusableCouponOperateList = new ArrayList<>();
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        manager.setAutoMeasureEnabled(true);
        unusableCoupon.setLayoutManager(manager);
        mUnusableCouponAdapter = new CouponOperateSelectAdapter(mUnusableCouponOperateList);
        unusableCoupon.setHasFixedSize(true);
        unusableCoupon.setAdapter(mUnusableCouponAdapter);
        mUnusableCouponAdapter.setCouponItemClickedListener(usableCouponListener);
    }

    @OnClick(R.id.ll_unusable)
    public void onLlUnusableClicked() {
        unusableIsOpen = !unusableIsOpen;
        imgTag.setImageDrawable(unusableIsOpen ? ResourceUtils.getDrawable(R.drawable.ic_coupon_filter_up) : ResourceUtils.getDrawable(R.drawable.ic_coupon_filter_down));
        unusableCoupon.setVisibility(unusableIsOpen ? View.VISIBLE : View.GONE);

    }

    @OnClick(R.id.tv_all_coupon)
    public void onAllCouponClicked() {
        setAllCouponSelected();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        RxBus.getInstance().unsubscribe(mCouponListSubscription);
        mDataFilterManager.clearAllSetting();
        super.onDestroyView();
    }

    CouponOperateSelectAdapter.CouponItemClickedListener usableCouponListener = new CouponOperateSelectAdapter.CouponItemClickedListener() {
        @Override
        public void couponItemClicked(CouponBean bean, int position) {
            if (mSelectedCoupon != null && mSelectedCoupon.isUsable.equals("Y")) {
                for (int i = 0; i < mDataFilterManager.getOnlineCoupons().size(); i++) {
                    if (mDataFilterManager.getOnlineCoupons().get(i).actId.equals(mSelectedCoupon.actId)) {
                        mSelectedPosition = i;
                    }
                }
            }
            if (mSelectedCoupon != null && mSelectedCoupon.isUsable.equals("N")) {
                for (int i = 0; i < mDataFilterManager.getOfflineCoupons().size(); i++) {
                    if (mDataFilterManager.getOfflineCoupons().get(i).actId.equals(mSelectedCoupon.actId)) {
                        mSelectedPosition = i;
                    }
                }
            }

            if (bean.isSelected == Constant.COUPON_IS_SELECTED_TRUE) {
                setAllCouponSelected();
            } else {
                tvAllCoupon.setSelected(false);
                if (mSelectedPosition == -1 || mSelectedCoupon == null) { //之前选中的全部
                    bean.isSelected = Constant.COUPON_IS_SELECTED_TRUE;
                    mSelectedCoupon = bean;
                    mSelectedPosition = position;
                    if (bean.isUsable.equals(Constant.COUPON_ONLINE_TRUE)) {
                        mUsableCouponAdapter.notifyItemChanged(position);
                    } else {
                        mUnusableCouponAdapter.notifyItemChanged(position);
                    }
                } else {
                    mSelectedPosition = position;
                    mSelectedCoupon = bean;
                    if (mSelectedCoupon.isUsable.equals(Constant.COUPON_ONLINE_TRUE)) {
                        mDataFilterManager.setPositionSelected(true,mSelectedPosition);
                    } else {
                        mDataFilterManager.setPositionSelected(false,mSelectedPosition);
                    }
                    mUsableCouponAdapter.setData(mDataFilterManager.getOnlineCoupons());
                    mUnusableCouponAdapter.setData(mDataFilterManager.getOfflineCoupons());

                }

            }


        }
    };

    public void setAllCouponSelected() {
        tvAllCoupon.setSelected(true);
        mSelectedCoupon = null;
        mSelectedPosition = -1;
        mDataFilterManager.clearAllSetting();
        mUsableCouponAdapter.setData(mDataFilterManager.getOnlineCoupons());
        mUnusableCouponAdapter.setData(mDataFilterManager.getOfflineCoupons());
    }

    public CouponBean getSelectedCouponOperate() {
        return mSelectedCoupon;
    }


}
