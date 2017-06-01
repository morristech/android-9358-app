package com.xmd.manager.window;


import android.content.Intent;

import com.xmd.manager.R;
import com.xmd.manager.beans.GroupMessage;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.GroupMessageResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lhj on 2016/9/26.
 */
public class SendCouponDetailActivity extends BaseListActivity<GroupMessage, GroupMessageResult> {


    @Override
    protected void dispatchRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_GROUP_LIST, params);
    }

    @Override
    protected void setContentViewLayout() {
        super.setContentViewLayout();
        setContentView(R.layout.actvity_group_message_detail);
    }

    @Override
    protected void initOtherViews() {

        super.initOtherViews();
        setRightVisible(false, -1, null);
 //       setRightVisible(true, ResourceUtils.getString(R.string.new_create), view -> startActivity(new Intent(this, GroupMessageCustomerActivity.class)));
        setTitle(ResourceUtils.getString(R.string.send_group_detail));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
