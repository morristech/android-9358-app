package com.xmd.manager.window;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CustomerCouponsResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linms@xiaomodo.com on 16-5-25.
 */
public class CustomerCouponsActivity extends BaseListActivity<CouponInfo, CustomerCouponsResult> {

    private String mCustomerId;

    @Override
    protected void setContentViewLayout() {
        setContentView(R.layout.activity_customer_coupons);
    }

    @Override
    protected void dispatchRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USER_ID, mCustomerId);
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_COUPONS, params);
    }

    @Override
    protected void initOtherViews() {
        mCustomerId = getIntent().getStringExtra(Constant.PARAM_CUSTOMER_ID);
        if (Utils.isEmpty(mCustomerId)) {
            finish();
        }
    }
}
