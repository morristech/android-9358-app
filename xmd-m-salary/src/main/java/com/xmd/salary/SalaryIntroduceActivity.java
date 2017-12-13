package com.xmd.salary;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.app.BaseActivity;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.salary.adapter.SalaryIntroduceAdapter;
import com.xmd.salary.adapter.ServiceCommissionAdapter;
import com.xmd.salary.httprequest.DataManager;
import com.xmd.salary.httprequest.response.CommissionSettingResult;
import com.xmd.salary.httprequest.response.SalarySettingResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.zhouchaoyuan.excelpanel.ExcelPanel;

/**
 * Created by Lhj on 17-11-21.
 */

public class SalaryIntroduceActivity extends BaseActivity {

    @BindView(R2.id.tv_salary_structure)
    TextView tvSalaryStructure;
    @BindView(R2.id.commodities_list)
    RecyclerView commoditiesList; //实物商品
    @BindView(R2.id.ll_commodities)
    LinearLayout llCommodities;
    @BindView(R2.id.commodities_cards)
    RecyclerView commoditiesCards; //次卡
    @BindView(R2.id.ll_card)
    LinearLayout llCard;
    @BindView(R2.id.ll_package)
    LinearLayout llPackage;
    @BindView(R2.id.commodities_orders)
    RecyclerView commoditiesOrders;   //订单
    @BindView(R2.id.ll_order)
    LinearLayout llOrder;
    @BindView(R2.id.service_excel)
    ExcelPanel serviceExcel;
    @BindView(R2.id.ll_commodities_view)
    LinearLayout llCommoditiesView;
    @BindView(R2.id.tv_commodities)
    TextView tvCommodities;
    @BindView(R2.id.ll_card_view)
    LinearLayout llCardView;
    @BindView(R2.id.tv_card)
    TextView tvCard;
    @BindView(R2.id.tv_package)
    TextView tvPackage;
    @BindView(R2.id.ll_package_view)
    LinearLayout llPackageView;

    private SalaryIntroduceAdapter mCommodityAdapter;
    private SalaryIntroduceAdapter mCardAdapter;
    private SalaryIntroduceAdapter mOrderAdapter;
    private SalaryIntroduceDataManager mSalaryIntroduceDataManager;
    private ServiceCommissionAdapter mServiceCommissionAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary_introduce);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.salary_introduce_title));
        setBackVisible(true);
        getTechSalarySetting();
        getCommissionSetting();

        mSalaryIntroduceDataManager = SalaryIntroduceDataManager.getSalaryIntroduceInstance();
        mCommodityAdapter = new SalaryIntroduceAdapter(SalaryIntroduceActivity.this);
        mCardAdapter = new SalaryIntroduceAdapter(SalaryIntroduceActivity.this);
        mOrderAdapter = new SalaryIntroduceAdapter(SalaryIntroduceActivity.this);
        commoditiesList.setHasFixedSize(true);
        commoditiesList.setLayoutManager(new LinearLayoutManager(SalaryIntroduceActivity.this));
        commoditiesList.setItemAnimator(new DefaultItemAnimator());
        commoditiesList.setAdapter(mCommodityAdapter);
        commoditiesCards.setHasFixedSize(true);
        commoditiesCards.setLayoutManager(new LinearLayoutManager(SalaryIntroduceActivity.this));
        commoditiesCards.setItemAnimator(new DefaultItemAnimator());
        commoditiesCards.setAdapter(mCardAdapter);
        commoditiesOrders.setHasFixedSize(true);
        commoditiesOrders.setLayoutManager(new LinearLayoutManager(SalaryIntroduceActivity.this));
        commoditiesOrders.setItemAnimator(new DefaultItemAnimator());
        commoditiesOrders.setAdapter(mOrderAdapter);
        mServiceCommissionAdapter = new ServiceCommissionAdapter(this);
        serviceExcel.setAdapter(mServiceCommissionAdapter);

    }

    private void getTechSalarySetting() {
        DataManager.getInstance().getTechSalarySetting(new NetworkSubscriber<SalarySettingResult>() {
            @Override
            public void onCallbackSuccess(SalarySettingResult result) {
                String salarySetting = "工资合计 = ";
                for (int i = 0; i < result.getRespData().size(); i++) {
                    salarySetting += result.getRespData().get(i).name + " + ";
                }
                tvSalaryStructure.setText(salarySetting.substring(0, salarySetting.length() - 2));
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }

    private void getCommissionSetting() {
        DataManager.getInstance().getCommissionSetting(new NetworkSubscriber<CommissionSettingResult>() {
            @Override
            public void onCallbackSuccess(CommissionSettingResult result) {
                // mSalaryIntroduceAdapter.setData(result.getRespData().salesCommissionList.get(0).commissionList);
                mSalaryIntroduceDataManager.setCommissionListBeen(result.getRespData());
                notifyAllDataChanged();
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }

    private void notifyAllDataChanged() {
        if (mSalaryIntroduceDataManager.getCommissionList().size() > 0) {
            mCommodityAdapter.setData(mSalaryIntroduceDataManager.getCommissionList());
            llCommoditiesView.setVisibility(View.VISIBLE);
            tvCommodities.setVisibility(View.GONE);
        } else {
            llCommoditiesView.setVisibility(View.GONE);
            tvCommodities.setVisibility(View.VISIBLE);
        }
        if (mSalaryIntroduceDataManager.getCardCommissionList().size() > 0) {
            mCardAdapter.setData(mSalaryIntroduceDataManager.getCardCommissionList());
            llCardView.setVisibility(View.VISIBLE);
            tvCard.setVisibility(View.GONE);
        } else {
            llCardView.setVisibility(View.GONE);
            tvCard.setVisibility(View.VISIBLE);
        }
        if (mSalaryIntroduceDataManager.getOrderCommissionList().size() > 0) {
            mOrderAdapter.setData(mSalaryIntroduceDataManager.getOrderCommissionList());
            llPackageView.setVisibility(View.VISIBLE);
            tvPackage.setVisibility(View.GONE);
        } else {
            llPackageView.setVisibility(View.GONE);
            tvPackage.setVisibility(View.VISIBLE);
        }


        showServiceExcelView();
    }

    //服务项目Excel
    private void showServiceExcelView() {
        mServiceCommissionAdapter.setAllData(mSalaryIntroduceDataManager.getServiceItemList(), mSalaryIntroduceDataManager.getBellList(), mSalaryIntroduceDataManager.getServiceCellList());
    }

}
