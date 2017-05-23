package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.xmd.technician.R;
import com.xmd.technician.bean.CreditAccountDetailResult;
import com.xmd.technician.bean.CreditDetailBean;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Administrator on 2016/8/29.
 */
public class CreditRecordFragment extends BaseListFragment<CreditDetailBean> {

    @Bind(R.id.status_progressbar)
    ProgressBar statusProgressbar;
    private Subscription getCreditUserRecordsSubscription;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit_record, container, false);
        ButterKnife.bind(this, view);
        return view;
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
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_USER_RECORDE, params);
    }

    @Override
    protected void initView() {
        statusProgressbar.setVisibility(View.VISIBLE);
        getCreditUserRecordsSubscription = RxBus.getInstance().toObservable(CreditAccountDetailResult.class).subscribe(
                detailResult -> handlerDetailResult(detailResult)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(getCreditUserRecordsSubscription);
        ButterKnife.unbind(this);
    }

    private void handlerDetailResult(CreditAccountDetailResult result) {
        statusProgressbar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            onGetListFailed(result.msg);
        } else {
            onGetListSucceeded(result.pageCount, result.respData);
        }
    }

    @Override
    public void onRefresh() {
        mIsLoadingMore = false;
        mPages = PAGE_START + 1;
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, "1");
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(mListAdapter.getItemCount() - 1));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_USER_RECORDE, params);
    }


}


