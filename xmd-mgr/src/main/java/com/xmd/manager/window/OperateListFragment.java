package com.xmd.manager.window;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.manager.ClubData;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.OperateReportBean;
import com.xmd.manager.common.ImageLoader;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.FinancialReportResult;

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

    private String mCurrentListType;
    private Map<String, String> mParams;
    private String mDate;
    private Subscription mOperateReportListSubscription;
    private List<OperateReportBean> mReportList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mOperateReportListSubscription = RxBus.getInstance().toObservable(FinancialReportResult.class).subscribe(
                result -> handleFinancialReportResult(result)
        );
        return inflater.inflate(R.layout.fragment_operate_list, container, false);
    }

    @Override
    protected void dispatchRequest() {
//        showLoading(getActivity(), "正在加载...", false);
//        mParams.put(RequestConstant.KEY_REPORT_DATE, TextUtils.isEmpty(mDate) ? "" : mDate);
//        mParams.put(RequestConstant.KEY_REPORT_TYPE, mCurrentListType);
//        mParams.put(RequestConstant.KEY_PAGE, String.valueOf(mPageCount));
//        mParams.put(RequestConstant.KEY_PAGE_SIZE, "1000");
//        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_REPORT_LIST, mParams);
        mReportList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mReportList.add(new OperateReportBean("+" + i, mCurrentListType, mDate));
        }
        onGetListSucceeded(1, mReportList);
    }

    @Override
    protected void initView() {
        mCurrentListType = getArguments().getString(OPERATE_LIST_TYPE);
        mDate = getArguments().getString(OPERATE_LIST_DATE);
        mParams = new HashMap<>();
    }

    private void handleFinancialReportResult(FinancialReportResult result) {
        hideLoading();
        if (!result.dateType.equals(mCurrentListType)) {
            return;
        }
        if (result.statusCode == 200) {
            onGetListSucceeded(result.pageCount, result.respData);
        } else {
            onGetListFailed(result.msg);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mOperateReportListSubscription);
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public void onPositiveButtonClicked(OperateReportBean bean) {
        XLogger.i(">>>", "此处是分享");
        doShare(bean.shareId);
    }

    @Override
    public void onItemClicked(OperateReportBean bean) {
        XLogger.i(">>>", "此处去到详情");
        Intent intent = new Intent(getActivity(), BrowserActivity.class);
        intent.putExtra(BrowserActivity.EXTRA_SHOW_MENU, false);
        intent.putExtra(BrowserActivity.EXTRA_URL,"https://www.baidu.com");
        startActivity(intent);
    }

    @Override
    public void onLongClicked(OperateReportBean bean) {
        super.onLongClicked(bean);
      //  MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DELETE_REPORT,bean.id);
    }

    public void notifyDataChanged(String data) {
        mDate = data;
        onRefresh();
    }

    public void doShare(String mShareUrl) {
        Map<String, Object> params = new HashMap<>();
        Bitmap thumbnail = ImageLoader.readBitmapFromFile(ClubData.getInstance().getClubImageLocalPath());
        params.put(Constant.PARAM_SHARE_THUMBNAIL, thumbnail);
        params.put(Constant.PARAM_SHARE_URL, mShareUrl);
        params.put(Constant.PARAM_SHARE_TITLE, "经营报表");
        params.put(Constant.PARAM_SHARE_DESCRIPTION, "经营报表描述语言");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM, params);
    }


}
