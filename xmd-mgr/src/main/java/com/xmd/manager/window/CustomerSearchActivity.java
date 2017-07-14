package com.xmd.manager.window;

import android.view.View;

import com.xmd.m.comment.CustomerInfoDetailActivity;
import com.xmd.m.comment.httprequest.ConstantResources;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.Customer;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.response.CustomerSearchListResult;
import com.xmd.manager.widget.ClearableEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by linms@xiaomodo.com on 16-5-31.
 */
public class CustomerSearchActivity extends BaseListActivity<Customer, CustomerSearchListResult> {

    @BindView(R.id.search_word)
    ClearableEditText mCetSearchWord;

    private String mSearchUserName = "";

    private boolean mIsLoaded = false;

    @Override
    protected void setContentViewLayout() {
        setContentView(R.layout.activity_customer_search);
    }

    @Override
    protected List<Customer> filterList(List<Customer> result) {
        for (Customer customer : result) {
            customer.type = Constant.CUSTOMER_TYPE_ITEM;
        }
        return result;
    }

    @OnClick({R.id.iv_back, R.id.iv_search})
    public void onClicked(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_search:
                mSearchUserName = mCetSearchWord.getText().toString();
                if (Utils.isEmpty(mSearchUserName)) {
                    onGetListSucceeded(0, new ArrayList<Customer>());
                    makeShortToast("请输入用户名称或者手机号码");
                    return;
                }
                mIsLoaded = true;
                onRefresh();
                break;
        }
    }

    @Override
    protected void dispatchRequest() {
        if (mIsLoaded) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SEARCH_CUSTOMERS, mSearchUserName);
        }
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public void onItemClicked(Customer bean) {
//        Intent intent = new Intent(this, CustomerActivity.class);
//        intent.putExtra(CustomerActivity.ARG_CUSTOMER, bean);
//        startActivity(intent);
        CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(CustomerSearchActivity.this, bean.userId, ConstantResources.INTENT_TYPE_MANAGER, false);
    }
}
