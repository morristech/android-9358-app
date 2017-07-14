package com.example.xmd_m_comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xmd_m_comment.httprequest.ConstantResources;
import com.example.xmd_m_comment.httprequest.RequestConstant;
import com.xmd.app.BaseFragment;
import com.xmd.app.PageFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-7-5.
 */

public class CustomerConsumeFragment extends BaseFragment {

    @BindView(R2.id.img_customer_visit)
    ImageView imgCustomerVisit;
    @BindView(R2.id.tv_customer_visit_text)
    TextView tvCustomerVisitText;
    @BindView(R2.id.tv_customer_visit_times)
    TextView tvCustomerVisitTimes;
    @BindView(R2.id.tv_customer_visit_unit)
    TextView tvCustomerVisitUnit;
    @BindView(R2.id.rl_customer_visit)
    RelativeLayout rlCustomerVisit;
    @BindView(R2.id.img_customer_consume)
    ImageView imgCustomerConsume;
    @BindView(R2.id.tv_customer_consume_text)
    TextView tvCustomerConsumeText;
    @BindView(R2.id.tv_customer_consume_money)
    TextView tvCustomerConsumeMoney;
    @BindView(R2.id.tv_customer_consume_unit)
    TextView tvCustomerConsumeUnit;
    @BindView(R2.id.rl_customer_consume)
    RelativeLayout rlCustomerConsume;
    @BindView(R2.id.img_customer_reward)
    ImageView imgCustomerReward;
    @BindView(R2.id.tv_customer_reward_text)
    TextView tvCustomerRewardText;
    @BindView(R2.id.tv_customer_reward_money)
    TextView tvCustomerRewardMoney;
    @BindView(R2.id.tv_customer_reward_unit)
    TextView tvCustomerRewardUnit;
    @BindView(R2.id.rl_customer_reward_view)
    RelativeLayout rlCustomerReward;
    @BindView(R2.id.customer_view_pager)
    ViewPager customerViewPager;

    public static final String BIZ_TYPE = "comment_type";


    Unbinder unbinder;
    private String mCurrentConsumeType;
    private PageFragmentAdapter mPageFragmentAdapter;
    private List<View> views;
    private String userId;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout_customer_consume, container, false);
        mCurrentConsumeType = getArguments().getString(ConstantResources.CUSTOMER_CONSUME_TYPE);
        unbinder = ButterKnife.bind(this, view);
        userId = getArguments().getString(RequestConstant.KEY_USER_ID);
        initViewPagerView();
        initConsumeTableView();
        return view;
    }

    private void initConsumeTableView() {
        views = new ArrayList<>();
        views.add(imgCustomerReward);
        views.add(tvCustomerRewardText);
        views.add(tvCustomerRewardMoney);
        views.add(tvCustomerRewardUnit);
        views.add(imgCustomerConsume);
        views.add(tvCustomerConsumeText);
        views.add(tvCustomerConsumeMoney);
        views.add(tvCustomerConsumeUnit);
        views.add(imgCustomerVisit);
        views.add(tvCustomerVisitText);
        views.add(tvCustomerVisitTimes);
        views.add(tvCustomerVisitUnit);
        setViewState(0);
    }

    public void setViewData(String visit, String consume, String reward) {
        tvCustomerVisitTimes.setText(visit);
        tvCustomerConsumeMoney.setText(consume);
        tvCustomerRewardMoney.setText(reward);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCurrentConsumeType = getArguments().getString(ConstantResources.CUSTOMER_CONSUME_TYPE);
    }

    private void initViewPagerView() {
        mPageFragmentAdapter = new PageFragmentAdapter(getFragmentManager(), getActivity());

        Bundle args = new Bundle();
        args.putSerializable(BIZ_TYPE, 0);
        args.putSerializable(RequestConstant.KEY_USER_ID, userId);
        args.putSerializable(ConstantResources.INTENT_TYPE, mCurrentConsumeType);
        mPageFragmentAdapter.addFragment(CustomerVisitorDetailFragment.class.getName(), args);
        mPageFragmentAdapter.addFragment(CustomerConsumeDetailFragment.class.getName(), args);
        mPageFragmentAdapter.addFragment(CustomerRewardDetailFragment.class.getName(), args);
        customerViewPager.setOffscreenPageLimit(3);
        customerViewPager.setAdapter(mPageFragmentAdapter);
        customerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setViewState(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setViewState(int position) {
        for (int i = 0; i < views.size(); i++) {
            views.get(i).setSelected(false);
        }
        switch (position) {
            case 0:
                imgCustomerVisit.setSelected(true);
                tvCustomerVisitText.setSelected(true);
                tvCustomerVisitTimes.setSelected(true);
                tvCustomerVisitUnit.setSelected(true);
                break;
            case 1:
                imgCustomerConsume.setSelected(true);
                tvCustomerConsumeText.setSelected(true);
                tvCustomerConsumeMoney.setSelected(true);
                tvCustomerConsumeUnit.setSelected(true);
                break;
            case 2:
                imgCustomerReward.setSelected(true);
                tvCustomerRewardText.setSelected(true);
                tvCustomerRewardMoney.setSelected(true);
                tvCustomerRewardUnit.setSelected(true);

                break;
        }
    }

    @OnClick(R2.id.rl_customer_visit)
    public void onRlCustomerVisitClicked() {
        customerViewPager.setCurrentItem(0);
    }

    @OnClick(R2.id.rl_customer_consume)
    public void onRlCustomerConsumeClicked() {
        customerViewPager.setCurrentItem(1);
    }

    @OnClick(R2.id.rl_customer_reward_view)
    public void onRlCustomerRewardClicked() {
        customerViewPager.setCurrentItem(2);
    }
}
