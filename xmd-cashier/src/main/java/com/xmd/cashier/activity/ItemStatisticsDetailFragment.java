package com.xmd.cashier.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.ItemStatisticsCategoryAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.ItemStatisticsDetailContract;
import com.xmd.cashier.dal.bean.ItemStatisticsInfo;
import com.xmd.cashier.presenter.ItemStatisticsDetailPresenter;
import com.xmd.cashier.widget.FullyGridLayoutManager;

import java.util.List;

/**
 * Created by zr on 17-12-11.
 */

public class ItemStatisticsDetailFragment extends Fragment implements ItemStatisticsDetailContract.View {
    private static final int STYLE_SUMMARY = 0;
    private static final int STYLE_DETAIL = 1;
    private ItemStatisticsDetailContract.Presenter mPresenter;

    private int mType;
    private boolean isInit = false;
    private boolean isLoad = false;

    private View mView;

    private LinearLayout mTimeSelectLayout;
    private TextView mSelectMinus;
    private TextView mSelectPlus;
    private TextView mSelectTimeText;

    private LinearLayout mTimeCustomLayout;
    private TextView mCustomStart;
    private TextView mCustomEnd;
    private TextView mCustomTimeConfirm;

    private LinearLayout mDataLayout;
    private TextView mDataChangeText;
    private LinearLayout mDetailTitle;
    private RelativeLayout mSummaryTitle;
    private TextView mSummaryAmount;
    private RecyclerView mDataList;
    private TextView mStatisticsStart;
    private TextView mStatisticsEnd;
    private Button mPrintBtn;

    private LinearLayout mErrorLayout;
    private TextView mErrorDesc;
    private TextView mErrorClick;
    private ImageView mErrorImage;

    private LinearLayout mLoadingLayout;

    private ItemStatisticsCategoryAdapter mCategoryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mType = getArguments().getInt(AppConstants.EXTRA_BIZ_TYPE);
        }
        mView = inflater.inflate(R.layout.fragment_item_statistics_detail, container, false);
        isInit = true;
        initView();
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter = new ItemStatisticsDetailPresenter(getActivity(), this);
        mPresenter.onCreate();
        loadData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    private void initView() {
        mTimeSelectLayout = (LinearLayout) mView.findViewById(R.id.layout_select);
        mSelectMinus = (TextView) mView.findViewById(R.id.tv_select_minus);
        mSelectPlus = (TextView) mView.findViewById(R.id.tv_select_plus);
        mSelectTimeText = (TextView) mView.findViewById(R.id.tv_select_date);
        mSelectMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onSelectMinus(mType);
            }
        });
        mSelectPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onSelectPlus(mType);
            }
        });

        mTimeCustomLayout = (LinearLayout) mView.findViewById(R.id.layout_custom);
        mCustomStart = (TextView) mView.findViewById(R.id.tv_custom_start);
        mCustomEnd = (TextView) mView.findViewById(R.id.tv_custom_end);
        mCustomTimeConfirm = (TextView) mView.findViewById(R.id.tv_custom_confirm);
        mCustomStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCustomStartPicker(v);
            }
        });
        mCustomEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCustomEndPicker(v);
            }
        });
        mCustomTimeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCustomConfirm();
            }
        });

        mDataLayout = (LinearLayout) mView.findViewById(R.id.layout_statistic_detail_data);
        mDataChangeText = (TextView) mView.findViewById(R.id.tv_statistics_change);
        mDataChangeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onStyleChange();
            }
        });
        mDetailTitle = (LinearLayout) mView.findViewById(R.id.layout_detail_title);
        mSummaryTitle = (RelativeLayout) mView.findViewById(R.id.layout_summary_title);
        mSummaryAmount = (TextView) mView.findViewById(R.id.tv_summary_total_amount);
        mDataList = (RecyclerView) mView.findViewById(R.id.rv_statistics_list);
        mStatisticsStart = (TextView) mView.findViewById(R.id.tv_statistics_start);
        mStatisticsEnd = (TextView) mView.findViewById(R.id.tv_statistics_end);
        mPrintBtn = (Button) mView.findViewById(R.id.btn_statistics_print);
        mPrintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onPrint();
            }
        });
        mCategoryAdapter = new ItemStatisticsCategoryAdapter(getActivity());
        mDataList.setHasFixedSize(true);
        mDataList.setNestedScrollingEnabled(false);
        mDataList.setLayoutManager(new FullyGridLayoutManager(getActivity(), 1));
        mDataList.setAdapter(mCategoryAdapter);

        mErrorLayout = (LinearLayout) mView.findViewById(R.id.layout_error);
        mErrorDesc = (TextView) mView.findViewById(R.id.tv_error);
        mErrorClick = (TextView) mView.findViewById(R.id.tv_click_error);
        mErrorImage = (ImageView) mView.findViewById(R.id.img_error);
        mErrorClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.loadData();
            }
        });

        mLoadingLayout = (LinearLayout) mView.findViewById(R.id.layout_loading);
    }

    private void loadData() {
        if (!isInit) {
            return;
        }
        if (getUserVisibleHint() && !isLoad) {
            mPresenter.initData(mType);
            mPresenter.loadData();
            isLoad = true;
        }
    }

    @Override
    public void setPresenter(ItemStatisticsDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {

    }

    @Override
    public void showError(String error) {
        ((ItemStatisticsActivity) getActivity()).showError(error);
    }

    @Override
    public void showToast(String toast) {
        ((ItemStatisticsActivity) getActivity()).showToast(toast);
    }

    @Override
    public void showLoading() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mErrorLayout.setVisibility(View.GONE);
        mDataLayout.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        mLoadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void showDataError(String error) {
        mDataLayout.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.VISIBLE);
        mErrorImage.setImageResource(R.drawable.ic_load_error);
        mErrorClick.setVisibility(View.VISIBLE);
        mErrorDesc.setText(error);
    }

    @Override
    public void showDataEmpty() {
        mDataLayout.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.VISIBLE);
        mErrorImage.setImageResource(R.drawable.ic_load_empty);
        mErrorClick.setVisibility(View.GONE);
        mErrorDesc.setText("暂无汇总数据");
    }

    @Override
    public void showData(List<ItemStatisticsInfo> list, int style) {
        long amount = 0;
        for (ItemStatisticsInfo itemStatisticsInfo : list) {
            amount += itemStatisticsInfo.totalAmount;
        }
        mSummaryAmount.setText("￥" + Utils.moneyToStringEx(amount));
        mCategoryAdapter.setStyle(style);
        mCategoryAdapter.setData(list);
    }

    @Override
    public void showDataSuccess() {
        mErrorLayout.setVisibility(View.GONE);
        mDataLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDay(String showDay, String startTime, String endTime) {
        mTimeSelectLayout.setVisibility(View.VISIBLE);
        mTimeCustomLayout.setVisibility(View.GONE);
        mSelectTimeText.setText(showDay);
        mStatisticsStart.setText("开始时间：" + startTime);
        mStatisticsEnd.setText("结束时间：" + endTime);
    }

    @Override
    public void initMonth(String showMonth, String startTime, String endTime) {
        mTimeSelectLayout.setVisibility(View.VISIBLE);
        mTimeCustomLayout.setVisibility(View.GONE);
        mSelectTimeText.setText(showMonth);
        mStatisticsStart.setText("开始时间：" + startTime);
        mStatisticsEnd.setText("结束时间：" + endTime);
    }

    @Override
    public void initCustom(String startTime, String endTime) {
        mTimeSelectLayout.setVisibility(View.GONE);
        mTimeCustomLayout.setVisibility(View.VISIBLE);
        mCustomStart.setText(startTime);
        mCustomEnd.setText(endTime);
        mStatisticsStart.setText("开始时间：" + startTime);
        mStatisticsEnd.setText("结束时间：" + endTime);
    }

    @Override
    public void setCustomStart(String startTime) {
        mCustomStart.setText(startTime);
    }

    @Override
    public void setCustomEnd(String endTime) {
        mCustomEnd.setText(endTime);
    }

    @Override
    public void showStyle(int style) {
        switch (style) {
            case STYLE_DETAIL:
                mDetailTitle.setVisibility(View.VISIBLE);
                mSummaryTitle.setVisibility(View.GONE);
                mDataChangeText.setText("查看汇总");
                break;
            case STYLE_SUMMARY:
                mDetailTitle.setVisibility(View.GONE);
                mSummaryTitle.setVisibility(View.VISIBLE);
                mDataChangeText.setText("查看明细");
                break;
            default:
                break;
        }
    }

    @Override
    public void updateDataByStyle(int style) {
        mCategoryAdapter.setStyle(style);
        mCategoryAdapter.notifyDataSetChanged();
    }
}
