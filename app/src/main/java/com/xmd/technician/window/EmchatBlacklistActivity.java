package com.xmd.technician.window;

import android.content.Intent;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.CustomerInfo;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.TechBlacklistResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

/**
 * Created by sdcm on 17-5-5.
 */
public class EmchatBlacklistActivity extends BaseListActivity<CustomerInfo>{

    private Subscription mGetBlacklistSubscription;

    @Override
    protected void dispatchRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_BLACKLIST, params);
    }

    @Override
    protected void initView() {
        setTitle(ResourceUtils.getString(R.string.blacklist_manager));
        setBackVisible(true);

        mGetBlacklistSubscription = RxBus.getInstance().toObservable(TechBlacklistResult.class).subscribe(
                result -> handlerGetBlacklistResult(result)
        );
    }

    @Override
    protected void setContentViewLayout() {
        setContentView(R.layout.activity_emchat_blacklist);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetBlacklistSubscription);
    }

    private void handlerGetBlacklistResult(TechBlacklistResult result){
        if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            onGetListFailed(result.msg);
        } else {
            onGetListSucceeded(result.pageCount, result.respData);
        }
    }

    @Override
    public void onItemClicked(CustomerInfo bean) {
        Intent intent = new Intent(this, ContactInformationDetailActivity.class);
        intent.putExtra(RequestConstant.KEY_CUSTOMER_ID, bean.id);
        intent.putExtra(RequestConstant.KEY_USER_ID, bean.userId);
        intent.putExtra(RequestConstant.KEY_CONTACT_TYPE, Constant.CONTACT_INFO_DETAIL_TYPE_CUSTOMER);
        intent.putExtra(RequestConstant.KEY_IS_MY_CUSTOMER,true);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            onRefresh();
        }
    }
}
