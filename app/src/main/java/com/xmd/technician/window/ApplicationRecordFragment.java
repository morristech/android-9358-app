package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.technician.R;
import com.xmd.technician.bean.ApplicationBean;
import com.xmd.technician.bean.CreditApplicationsResult;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

/**
 * Created by Administrator on 2016/8/29.
 */
public class ApplicationRecordFragment extends BaseListFragment<ApplicationBean> {

    private Subscription mGetApplicationListSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_credit_application, container, false);
    }

    @Override
    protected void dispatchRequest() {
        if (mPages != 1) {
            mPages = mListAdapter.getItemCount() / PAGE_SIZE + 1;
        }
        if (mListAdapter.getItemCount() % PAGE_SIZE > 1) {
            mPages++;
        }
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        params.put(RequestConstant.KEY_STATUS, "");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CREDIT_APPLICATIONS, params);
    }

    @Override
    protected void initView() {
        mGetApplicationListSubscription = RxBus.getInstance().toObservable(CreditApplicationsResult.class).subscribe(
                creditApplicationsResult -> handlerCreditApplicationsResult(creditApplicationsResult)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mGetApplicationListSubscription);
    }

    private void handlerCreditApplicationsResult(CreditApplicationsResult result) {
        if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            onGetListFailed(result.msg);
        } else {
            onGetListSucceeded(result.pageCount, result.respData);
        }

    }

    protected void refreshData() {
        mIsLoadingMore = false;
        mPages = PAGE_START + 1;
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, "1");
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(mListAdapter.getItemCount() - 1));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CREDIT_APPLICATIONS, params);
    }


}
