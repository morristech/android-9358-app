package com.xmd.technician.window;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/17.
 */
public class IntroduceAccountActivity extends BaseActivity {

    @Bind(R.id.introduce_account)
    WebView mIntroduceAccount;

    private String mIntruduceUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_account);
        ButterKnife.bind(this);
        setTitle(ResourceUtils.getString(R.string.introduce_bind));
        setBackVisible(true);
       // mIntruduceUrl = "http://baidu.com";
        mIntruduceUrl = SharedPreferenceHelper.getServerHost()+ RequestConstant.URL_INTRODUCE_BIND;

                InitIntroduce();
    }

    private void InitIntroduce() {
        mIntroduceAccount.getSettings().setAllowFileAccess(true);
        mIntroduceAccount.getSettings().setJavaScriptEnabled(true);
        mIntroduceAccount.getSettings().setDefaultTextEncodingName("UTF-8");
        mIntroduceAccount.getSettings().setSupportZoom(false);
        mIntroduceAccount.getSettings().setBuiltInZoomControls(true);
        mIntroduceAccount.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        mIntroduceAccount.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {

            }
        });
        if(mIntroduceAccount !=null){
            mIntroduceAccount.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    mIntroduceAccount.loadUrl(url);
                    return true;
                }
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);

                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                }

            });
            loadUrl(mIntruduceUrl);
        }
    }
    public void loadUrl(String url){
        if(mIntroduceAccount!=null){
            mIntroduceAccount.loadUrl(url);
            mIntroduceAccount.reload();
        }
    }
}
