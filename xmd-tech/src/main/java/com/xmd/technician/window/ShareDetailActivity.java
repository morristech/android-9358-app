package com.xmd.technician.window;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.widget.SmoothProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 2017/2/22.
 */

public class ShareDetailActivity extends BaseActivity implements View.OnClickListener {


    public static final String SHARE_URL = "share_url";
    public static final String SHARE_TITLE = "share_title";
    public static final String EXTRA_SHOW_MENU = "show_menu";
    public static final String EXTRA_FULLSCREEN = "fullScreen";
    @BindView(R.id.share_web_view)
    WebView shareWebView;
    @BindView(R.id.back_ImageView)
    ImageView back_ImageView;
    @BindView(R.id.go_next_ImageView)
    ImageView go_next_ImageView;
    @BindView(R.id.refresh_ImageView)
    ImageView refresh_ImageView;
    @BindView(R.id.home_ImageView)
    ImageView home_ImageView;
    @BindView(R.id.download_progressbar)
    SmoothProgressBar downloadProgressbar;
    @BindView(R.id.menu_LinearLayout)
    LinearLayout mMenuBar;
    private String mShareUrl;
    private String mTitle;
    private WebSettings ws;
    private boolean mShowMenu;

    public static void startShareDetailActivity(Context activity, String url, String title, Boolean showMenu) {
        Intent intent = new Intent(activity, ShareDetailActivity.class);
        intent.putExtra(SHARE_URL, url);
        intent.putExtra(SHARE_TITLE, title);
        intent.putExtra(EXTRA_SHOW_MENU, showMenu);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_detail);
        ButterKnife.bind(this);


        mShowMenu = getIntent().getBooleanExtra(EXTRA_SHOW_MENU, false);
        mShareUrl = getIntent().getStringExtra(SHARE_URL);
        mTitle = getIntent().getStringExtra(SHARE_TITLE);
        setTitle(mTitle);
        setBackVisible(true);
        back_ImageView.setOnClickListener(this);
        go_next_ImageView.setOnClickListener(this);
        refresh_ImageView.setOnClickListener(this);
        home_ImageView.setOnClickListener(this);

        ws = shareWebView.getSettings();
        ws.setUserAgentString(Constant.APP_BROWSER_USER_AGENT);
        ws.setBuiltInZoomControls(false);//隐藏缩放按钮
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        ws.setUseWideViewPort(true); //将图片调整到适合webview的大小
        ws.setLoadWithOverviewMode(true);
        ws.setSupportZoom(true); //支持缩放
        ws.setBuiltInZoomControls(true);

        ws.setSaveFormData(true); // 保存表单数据
        ws.setDomStorageEnabled(true);

        ws.setGeolocationEnabled(true);
        ws.setPluginState(WebSettings.PluginState.ON);

        //注入android接口到js中
        ws.setJavaScriptEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口

        shareWebView.setWebChromeClient(new WebChromeClient() {
            public void onReceivedTitle(WebView view, String title) {
                setTitle(mTitle);
            }

            public void onProgressChanged(WebView view, int newProgress) {
                downloadProgressbar.setTargetProgress(newProgress * 10);
            }


        });

        shareWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        shareWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith("android://back")) {
                    finish();
                } else {
                    view.loadUrl(url);
                }
                return true;   //解决重定向无法后退问题
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                ws.setBlockNetworkImage(true);
                go_next_ImageView.setEnabled(shareWebView.canGoForward());
                downloadProgressbar.setProgress(0);
                downloadProgressbar.setTargetProgress(10);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                ws.setBlockNetworkImage(false);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });

        if (!mShowMenu) {
            mMenuBar.setVisibility(View.GONE);
        }
        shareWebView.loadUrl(mShareUrl);
        shareWebView.addJavascriptInterface(new JsOperator(this), "browser");
    }

    @Override
    public void onBackPressed() {
        if (shareWebView.canGoBack() && mShowMenu) {
            shareWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_ImageView:
                if (shareWebView.canGoBack()) {
                    shareWebView.goBack();
                } else {
                    finish();
                }
                break;
            case R.id.go_next_ImageView:
                if (shareWebView.canGoForward()) {
                    shareWebView.goForward();
                }
                break;
            case R.id.refresh_ImageView:
                shareWebView.reload();
                break;
            case R.id.home_ImageView:
                finish();
                break;
            default:
                break;
        }
    }

    public void onResume() {
        super.onResume();
        try {
            shareWebView.getClass().getMethod("onResume").invoke(shareWebView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        super.onPause();

        try {
            shareWebView.getClass().getMethod("onPause").invoke(shareWebView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (shareWebView != null) {
            shareWebView.destroy();
        }
        super.onDestroy();
    }

    private class JsOperator {
        private Context mmContext;

        JsOperator(Context context) {
            mmContext = context;
        }

        @JavascriptInterface
        public void finishFuc() {
            ((Activity) mmContext).finish();
        }

        @JavascriptInterface
        public void goLoginFuc() {
            //Goto Login Activity
        }

        @JavascriptInterface
        public void shareFuc(String content) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, content);
            intent.putExtra(Intent.EXTRA_TITLE, mmContext.getString(R.string.app_name));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mmContext.startActivity(Intent.createChooser(intent, mmContext.getString(R.string.app_name)));
        }
    }

}
