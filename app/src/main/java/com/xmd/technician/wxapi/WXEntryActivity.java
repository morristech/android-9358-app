package com.xmd.technician.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xmd.technician.AppConfig;
import com.xmd.technician.R;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.share.ShareConstant;
import com.xmd.technician.window.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by sdcm on 15-12-11.
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    @Bind(R.id.wx_share_result) TextView mShareResult;
    @Bind(R.id.wx_share_result2) TextView mShareResult2;
    private IWXAPI api;
    public static boolean mHaveCode = true;
    private boolean bindSuccess;

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

        ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 800);
    }
    @Override
    public void onResp(BaseResp baseResp) {

        int result = 0;
        SendAuth.Resp re = (SendAuth.Resp) baseResp;
        String code = re.code;
      //  doGetWXCode(code);
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.wx_errcode_success;
              AppConfig.reportCouponShareEvent();
              //   getOpenID(code);
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
                    ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 400);


    }
//    private void getOpenID(String code) {
//          String urlStr= "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+ShareConstant.WX_APP_ID+"&secret="+ShareConstant.WX_APP_SECRET+
//                  "&code="+code+"&grant_type=authorization_code";
//
//        OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url(urlStr)
//                    .build();
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                        bindSuccess= false;
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    Gson gson = new Gson();
//                    WXShareResult result = gson.fromJson(response.body().string(),WXShareResult.class);
//                    if(!TextUtils.isEmpty(result.openid)){
//                     //   SharedPreferenceHelper.setBindSuccess(true);
//                        SharedPreferenceHelper.setUserWXOpenId(result.openid);
//                        SharedPreferenceHelper.setUserWXUnionid(result.unionid);
//                        bindSuccess = true;
//                    }else{
//                        makeShortToast(ResourceUtils.getString(R.string.wx_bind_failed_alert));
//                    }
//
//                }
//
//            });
//        if(bindSuccess){
//            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
//                @Override
//                public void run() {
//                    finish();
//                }
//            }, 400);
//        }
//        }
//    private void doGetWXCode(String urlStr){
//        Map<String, String> params = new HashMap<>();
//    //    params.put(RequestConstant.KEY_USER_WX_CODE,code);
//        params.put(RequestConstant.KEY_USER_WX_PAGE_URL,urlStr);
//        params.put(RequestConstant.KEY_USER_WX_SCOPE, "snsapi_base");
//        params.put(RequestConstant.KEY_USER_WX_STATE, "");
//        params.put(RequestConstant.KEY_USER_WX_WXMP, "9358_fw");
//        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_BIND_WX, params);
//    }



}
