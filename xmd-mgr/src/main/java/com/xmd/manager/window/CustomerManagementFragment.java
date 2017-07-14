package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.adapter.PageFragmentPagerAdapter;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.CustomerListResult;
import com.xmd.manager.service.response.TechListResult;
import com.xmd.manager.widget.ArrayBottomPopupWindow;
import com.xmd.manager.widget.ViewPagerTabIndicator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-5-17.
 */
public class CustomerManagementFragment extends BaseFragment {

    @BindView(R.id.tab_indicator)
    ViewPagerTabIndicator mViewPagerTabIndicator;

    @BindView(R.id.vp_customer)
    ViewPager mVpCustomer;
    @BindView(R.id.customer_group)
    TextView customerGroup;

    private PageFragmentPagerAdapter mPageFragmentPagerAdapter;
    private View view;
    private Subscription mGetTechCustomersSubscription;
    private Subscription mGetClubTechsSubscription;
    protected TextView mCustomerFilterBtn;
    private ArrayBottomPopupWindow<String> mCustomerPopupWindow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_management, container, false);
        return view;
    }

    @Override
    protected void initView() {
        ImageView imageLeft = (ImageView) view.findViewById(R.id.toolbar_left);
        ImageView imageRight = (ImageView) view.findViewById(R.id.toolbar_right_image);
        imageRight.setImageDrawable(ResourceUtils.getDrawable(R.drawable.ic_search_selector));
        imageRight.setVisibility(View.VISIBLE);
        imageRight.setOnClickListener(view -> startActivity(new Intent(getActivity(), CustomerSearchActivity.class)));
        imageLeft.setVisibility(View.GONE);
        mCustomerFilterBtn = (TextView) view.findViewById(R.id.toolbar_customer_filter_btn);
        mCustomerFilterBtn.setVisibility(View.VISIBLE);
        mCustomerFilterBtn.setText(ResourceUtils.getString(R.string.home_activity_customer_management));
        if (mCustomerFilterBtn != null) {
            mCustomerPopupWindow = new ArrayBottomPopupWindow<>(mCustomerFilterBtn, null, ResourceUtils.getDimenInt(R.dimen.customer_type_item_width));
            mCustomerPopupWindow.setDataSet(new ArrayList<>(Constant.CUSTOMER_TYPE_LABELS.keySet()));
            mCustomerPopupWindow.setItemClickListener((parent, view, position, id) -> {
                mCustomerFilterBtn.setText((CharSequence) parent.getAdapter().getItem(position));
                //// TODO: 16-7-26 filter customer
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_FILTER_CUSTOMER_TYPE, Constant.CUSTOMER_TYPE_LABELS.get(parent.getAdapter().getItem(position)));
            });
            mCustomerFilterBtn.setOnClickListener(v -> {
                mCustomerPopupWindow.showAsDownCenter(true);
            });
        }
        String[] tabTexts = new String[]{
                ResourceUtils.getString(R.string.customer_management_technician),
                ResourceUtils.getString(R.string.customer_management_active_degree),
                ResourceUtils.getString(R.string.customer_management_bac_comment)};

        mPageFragmentPagerAdapter = new PageFragmentPagerAdapter(getChildFragmentManager(), getActivity());
        for (int i = 0; i < tabTexts.length; i++) {
            Bundle args = new Bundle();
            args.putSerializable(CustMgmtDetailListFragment.BIZ_TYPE, i);
            mPageFragmentPagerAdapter.addFragment(CustMgmtDetailListFragment.class.getName(), args);
        }
        mVpCustomer.setAdapter(mPageFragmentPagerAdapter);
        mVpCustomer.setOffscreenPageLimit(3);

        mViewPagerTabIndicator.setTabTexts(tabTexts);
        mViewPagerTabIndicator.setWithIndicator(true);
        mViewPagerTabIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
        mViewPagerTabIndicator.setViewPager(mVpCustomer);
        mViewPagerTabIndicator.setup();

        mGetTechCustomersSubscription = RxBus.getInstance().toObservable(CustomerListResult.class).subscribe(
                result -> handleCustomersResult(result)
        );

        mGetClubTechsSubscription = RxBus.getInstance().toObservable(TechListResult.class).subscribe(
                result -> handleTechsResult(result)
        );

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mGetClubTechsSubscription, mGetTechCustomersSubscription);
    }

    private void handleTechsResult(TechListResult result) {
        if (result.statusCode == 200) {
            CustMgmtDetailListFragment fragment =
                    (CustMgmtDetailListFragment) mPageFragmentPagerAdapter.instantiateItem(mVpCustomer, CustMgmtDetailListFragment.TAB_TECHNICIAN);
            fragment.setTechnicianList(result);
        }
    }

    private void handleCustomersResult(CustomerListResult result) {
        CustMgmtDetailListFragment fragment =
                (CustMgmtDetailListFragment) mPageFragmentPagerAdapter.instantiateItem(mVpCustomer, Integer.valueOf(result.type));
        fragment.setCustomerList(result);
    }

    @OnClick(R.id.customer_group)
    public void onClick() {
        Intent intent = new Intent(getActivity(), CustomerGroupListActivity.class);
        startActivity(intent);
    }
}
