package com.xmd.manager.window;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.manager.ClubData;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.OperateReportBean;
import com.xmd.manager.common.ImageLoader;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.FinancialReportResult;
import com.xmd.manager.service.response.ReportDeleteResult;
import com.xmd.manager.service.response.ReportReadResult;
import com.xmd.manager.widget.AlertDialogBuilder;
import com.xmd.manager.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;

/**
 * Created by Lhj on 17-9-11.
 */

public class OperateListFragment extends BaseListFragment<OperateReportBean> {

    public static final String OPERATE_LIST_TYPE = "operateListType";
    public static final String OPERATE_LIST_DATE = "date";
    public static final String OPERATE_LIST_BY_DAY_TYPE = "day";
    public static final String OPERATE_LIST_BY_MONTH_TYPE = "month";
    public static final String OPERATE_LIST_BY_CUSTOM_TYPE = "custom";

    private EmptyView mEmptyView;
    private String mCurrentListType;
    private Map<String, String> mParams;
    private String mDate;
    private Subscription mOperateReportListSubscription;
    private Subscription mOperateReadResultSubscription;
    private Subscription mOperateDeleteResultSubscription;
    private List<OperateReportBean> mReportList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_operate_list, container, false);

        mEmptyView = (EmptyView) view.findViewById(R.id.empty_view);
        mOperateReportListSubscription = RxBus.getInstance().toObservable(FinancialReportResult.class).subscribe(
                result -> handleFinancialReportResult(result)
        );
        mOperateReadResultSubscription = RxBus.getInstance().toObservable(ReportReadResult.class).subscribe(
                result -> {
                    if (result.statusCode == 200) onRefresh();
                }
        );
        mOperateDeleteResultSubscription = RxBus.getInstance().toObservable(ReportDeleteResult.class).subscribe(
                result -> {
                    if (result.statusCode == 200) onRefresh();
                }
        );
        return view;
    }

    @Override
    protected void dispatchRequest() {
        mParams.put(RequestConstant.KEY_REPORT_DATE, TextUtils.isEmpty(mDate) ? "" : mDate);
        mParams.put(RequestConstant.KEY_REPORT_TYPE, mCurrentListType);
        mParams.put(RequestConstant.KEY_PAGE, String.valueOf(mPageCount));
        mParams.put(RequestConstant.KEY_PAGE_SIZE, "1000");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_REPORT_LIST, mParams);
    }

    @Override
    protected void initView() {
        mReportList = new ArrayList<>();
        mCurrentListType = getArguments().getString(OPERATE_LIST_TYPE);
        mDate = getArguments().getString(OPERATE_LIST_DATE);
        mParams = new HashMap<>();
    }

    private void handleFinancialReportResult(FinancialReportResult result) {
        hideLoading();
        if (!result.dateType.equals(mCurrentListType)) {
            return;
        }
        mReportList.clear();
        mReportList.addAll(result.respData);
        for (OperateReportBean reportBean : mReportList) {
            reportBean.type = mCurrentListType;
        }
        if (result.respData.size() == 0) {
            mSwipeRefreshLayout.setRefreshing(false);
            mEmptyView.setStatus(EmptyView.Status.Empty);
            return;
        } else {
            mEmptyView.setStatus(EmptyView.Status.Gone);
        }
        if (result.statusCode == 200) {
            onGetListSucceeded(result.pageCount, mReportList);
        } else {
            onGetListFailed(result.msg);
        }
    }

    @Override
    public void onDestroy() {
        RxBus.getInstance().unsubscribe(mOperateReportListSubscription, mOperateReadResultSubscription, mOperateDeleteResultSubscription);
        super.onDestroy();
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public void onPositiveButtonClicked(OperateReportBean bean) {
        doShare(bean.name, bean.shareId);
    }

    @Override
    public void onItemClicked(OperateReportBean bean) {
        Intent intent = new Intent(getActivity(), BrowserActivity.class);
        intent.putExtra(BrowserActivity.EXTRA_SHOW_MENU, false);
        intent.putExtra(BrowserActivity.EXTRA_URL, bean.shareUrl);
        startActivity(intent);
        if (bean.read == 0) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_REPORT_READ, String.valueOf(bean.id));
        }

    }

    @Override
    public void onNegativeButtonClicked(OperateReportBean bean) {
        super.onNegativeButtonClicked(bean);
        if (bean.type.equals(OPERATE_LIST_BY_CUSTOM_TYPE)) {
            new AlertDialogBuilder(getActivity())
                    .setTitle(ResourceUtils.getString(R.string.alert_tips_title))
                    .setMessage(ResourceUtils.getString(R.string.operate_delete_alter_message))
                    .setPositiveButton(ResourceUtils.getString(R.string.btn_confirm), v -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DELETE_REPORT, String.valueOf(bean.id)))
                    .setNegativeButton(ResourceUtils.getString(R.string.cancel), null)
                    .show();
        }
    }

    public void notifyDataChanged(String data) {
        mDate = data;
        showLoading(getActivity(), "正在加载...", false);
        onRefresh();
    }

    public void doShare(String name, String mShareUrl) {
        Map<String, Object> params = new HashMap<>();
        Bitmap thumbnail = ImageLoader.readBitmapFromFile(ClubData.getInstance().getClubImageLocalPath());
        params.put(Constant.PARAM_SHARE_THUMBNAIL, thumbnail);
        params.put(Constant.PARAM_SHARE_URL, mShareUrl);
        params.put(Constant.PARAM_SHARE_TITLE, "经营报表");
        params.put(Constant.PARAM_SHARE_DESCRIPTION, TextUtils.isEmpty(name) ? "经营报表" : name);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM, params);
    }

}
