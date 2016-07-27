package com.xmd.technician.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xmd.technician.AppConfig;
import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.share.ShareConstant;
import com.xmd.technician.window.BaseActivity;

import org.apache.http.params.HttpParams;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

/**
 * Created by sdcm on 15-12-11.
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    @Bind(R.id.wx_share_result) TextView mShareResult;
    @Bind(R.id.wx_share_result2) TextView mShareResult2;
    private IWXAPI api;
    public static boolean mHaveCode = true;
    private String s;
    private String urlStr;

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
        }, 1200);
    }

    private void getWXCode(BaseResp resp){
        if(resp !=null && resp.getType()== ConstantsAPI.COMMAND_SENDAUTH){


        }

    }


    @Override
    public void onResp(BaseResp baseResp) {

        int result = 0;
        SendAuth.Resp re = (SendAuth.Resp) baseResp;
        String code = re.code;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.wx_errcode_success;
           //    AppConfig.reportCouponShareEvent();
               getOpenID(code);
                Log.i("TAGG","baleable>>>");
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

       // mShareResult.setText(ResourceUtils.getString(result));
        mShareResult.setText(code+">>>>"+result+">>>>"+s);
//        ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        }, 1200);
    }
    private void getOpenID(String code){
//        String urlStr = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+ShareConstant.WX_APP_ID+"&secret="+ShareConstant.WX_APP_SECRET+
//                "&code="+code+"&grant_type=authorization_code";
          String urlStr= "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+ShareConstant.WX_APP_ID+"&secret="+ShareConstant.WX_APP_SECRET+
                  "&code="+code+"&grant_type=authorization_code";
//        OkHttpClient mOkHttpClient = new OkHttpClient();
//        final Request request = new Request.Builder().url(urlStr).build();
//        okhttp3.Call call  = mOkHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onResponse(okhttp3.Call call, Response response) throws IOException {
//                Log.i("JSON","json==>>>"+response.body().string());
//                s = response.body().string();
//            }
//            @Override
//            public void onFailure(okhttp3.Call call, IOException e) {
//                s = "失败了...";
//            }
//
//        });



        OkHttpClient mOkHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(urlStr).build();
        okhttp3.Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                s = "失败了...";
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                s ="成功了"+ response.body().string();
            }
        });
        mShareResult2.setText("》》》"+s);


    }

}
