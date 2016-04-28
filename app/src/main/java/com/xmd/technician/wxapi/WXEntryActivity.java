package com.xmd.technician.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.share.ShareConstant;
import com.xmd.technician.window.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 15-12-11.
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    @Bind(R.id.wx_share_result) TextView mShareResult;
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wx_entry);
        ButterKnife.bind(this);

        api = WXAPIFactory.createWXAPI(this, ShareConstant.WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

        int result = 0;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.wx_errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.wx_errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.wx_errcode_deny;
                break;
            default:
                result = R.string.wx_errcode_unknown;
                break;
        }

        mShareResult.setText(ResourceUtils.getString(result));
        ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 300);
    }
}
