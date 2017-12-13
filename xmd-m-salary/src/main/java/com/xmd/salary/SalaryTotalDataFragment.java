package com.xmd.salary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseFragment;
import com.xmd.app.utils.DateUtil;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.salary.bean.SalaryBean;
import com.xmd.salary.event.DateChangedEvent;
import com.xmd.salary.httprequest.DataManager;
import com.xmd.salary.httprequest.response.CommissionSumAmountResult;
import com.xmd.salary.httprequest.response.SalarySettingResult;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-11-20.
 */

public class SalaryTotalDataFragment extends BaseFragment {

    @BindView(R2.id.tv_tech_commission_total)
    TextView tvTechCommissionTotal;
    @BindView(R2.id.tv_tech_service_commission)
    TextView tvTechServiceCommission;
    @BindView(R2.id.tv_tech_sell_commission)
    TextView tvTechSellCommission;
    @BindView(R2.id.ll_tech_sell_commission)
    LinearLayout llTechSellCommission;
    @BindView(R2.id.ll_tech_service_commission)
    LinearLayout llTechServiceCommission;
    Unbinder unbinder;

    private boolean mServiceSelected = false;
    private boolean mSellSelected = false;
    private View view;
    private String mStartDate;
    private String mEndDate;
    private SalaryDataManager mSalaryDataManager;
    private String timeKey;
    private boolean onCreate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_salary_total_data, container, false);
        unbinder = ButterKnife.bind(this, view);
        mStartDate = DateUtil.getFirstDayOfMonth();
        mEndDate = DateUtil.getCurrentDate();
        mSalaryDataManager = SalaryDataManager.getSalaryDataInstance();
        onCreate = true;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getSalarySumAmount(mStartDate, mEndDate);
    }

    @OnClick(R2.id.ll_tech_sell_commission)
    public void onLlTechSellCommissionClicked() {
        mSellSelected = !mSellSelected;
        llTechSellCommission.setSelected(mSellSelected);
        EventBus.getDefault().post(new DateChangedEvent(getSelectedCommissionType(), "", ""));
    }

    @OnClick(R2.id.ll_tech_service_commission)
    public void onLlTechServiceCommissionClicked() {
        mServiceSelected = !mServiceSelected;
        llTechServiceCommission.setSelected(mServiceSelected);
        EventBus.getDefault().post(new DateChangedEvent(getSelectedCommissionType(), "", ""));
    }


    public String getSelectedCommissionType() {
        if (mServiceSelected && !mSellSelected) {
            return ConstantResource.SALARY_TYPE_SERVICE;
        } else if (!mServiceSelected && mSellSelected) {
            return ConstantResource.SALARY_TYPE_SELL;
        } else {
            return ConstantResource.SALARY_TYPE_ALL;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //动态设置，暂不考虑
    private void getSalarySetting() {
        DataManager.getInstance().getTechSalarySetting(new NetworkSubscriber<SalarySettingResult>() {
            @Override
            public void onCallbackSuccess(SalarySettingResult result) {

            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }

    public void getSalarySumAmount(String startDate, String endDate) {
        if (onCreate) {
            showLoading(getActivity(), "正在加载...", false);
        }
//        timeKey = startDate + endDate;
//        if (mSalaryDataManager != null && mSalaryDataManager.salaryContainKey(timeKey)) {
//            setViewData(mSalaryDataManager.getSalaryBean(timeKey));
//            return;
//        }

        DataManager.getInstance().getTechCommissionSumAmount(startDate, endDate, new NetworkSubscriber<CommissionSumAmountResult>() {
            @Override
            public void onCallbackSuccess(CommissionSumAmountResult result) {
                mSalaryDataManager.setSalaryBean(timeKey, result.getRespData());
                setViewData(result.getRespData());
            }

            @Override
            public void onCallbackError(Throwable e) {
                hideLoading();
                XLogger.e(e.getLocalizedMessage());
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    public void setViewData(SalaryBean bean) {
        hideLoading();
        if (bean == null) {
            return;
        }
        tvTechCommissionTotal.setText(String.valueOf(String.format("%1.2f", (bean.serviceCommission + bean.salesCommission) / 100f)));
        tvTechServiceCommission.setText(String.format("%1.2f", bean.serviceCommission / 100f));
        tvTechSellCommission.setText(String.format("%1.2f", bean.salesCommission / 100f));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSalaryDataManager.destroyData();
    }
}
