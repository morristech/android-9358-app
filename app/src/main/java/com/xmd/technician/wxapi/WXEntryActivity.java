package com.xmd.technician.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xmd.technician.Constant;
import com.xmd.technician.http.gson.BaseResult;
import com.xmd.technician.share.ShareConstant;
import com.xmd.technician.widget.ClearableEditText;
import com.xmd.technician.window.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 15-12-11.
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;
    public static boolean mHaveCode = true;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        api = WXAPIFactory.createWXAPI(this,ShareConstant.WX_APP_ID,false);
        api.handleIntent(getIntent(),this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.d("TAGG","req>>>"+req.toString());
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.i("TAGG","resp>>>"+resp.toString());
        String result = "";
        if(resp != null && !mHaveCode && ((SendAuth.Resp) resp).code != null){
                mHaveCode = true;
            getWXCode(resp);
        }
        switch (resp.errCode){
            case BaseResp.ErrCode.ERR_OK:
                result = "成功";
                Log.i("TAGG","err_ok>>"+result);
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "取消";
                Log.i("TAGG","err_cancel>>>"+result);
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "被拒绝";
                Log.i("TAGG","err_denied"+result);
                finish();
                break;
            default:
                result = "返回";
                Log.i("TAGG","default>>"+result);
                finish();
                break;
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent,this);
        finish();
    }
    private void getWXCode(BaseResp resp){
        if(resp!=null && resp.getType() == ConstantsAPI.COMMAND_SENDAUTH){

        }else{

        }

    }

}
