package com.xmd.manager.window;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xmd.manager.AppConfig;
import com.xmd.manager.R;
import com.xmd.manager.widget.SmoothProgressBar;

import butterknife.BindView;

/**
 * Created by sdcm on 15-10-27.
 */
public class BrowserActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_SHOW_MENU = "show_menu";
    public static final String EXTRA_FULLSCREEN = "fullScreen";

    @BindView(R.id.mainwebView)
    WebView mainWebView;
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

    private WebSettings ws;
    private boolean mShowMenu;
    private String mCurrentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        mShowMenu = getIntent().getBooleanExtra(EXTRA_SHOW_MENU, true);
        mCurrentUrl = getIntent().getStringExtra(EXTRA_URL);

        back_ImageView.setOnClickListener(this);
        go_next_ImageView.setOnClickListener(this);
        refresh_ImageView.setOnClickListener(this);
        home_ImageView.setOnClickListener(this);

        ws = mainWebView.getSettings();
        ws.setUserAgentString(AppConfig.getUserAgent());
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
        //支持flash播放
        //mainWebView.addJavascriptInterface(new ShareJavaScriptInterface(), "AppInterface");

        mainWebView.setWebChromeClient(new WebChromeClient() {
            public void onReceivedTitle(WebView view, String title) {
                setTitle(title);
            }

            public void onProgressChanged(WebView view, int newProgress) {
                downloadProgressbar.setTargetProgress(newProgress * 10);
            }
        });

        mainWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        mainWebView.setWebViewClient(new WebViewClient() {

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
                go_next_ImageView.setEnabled(mainWebView.canGoForward());
                downloadProgressbar.setProgress(0);
                downloadProgressbar.setTargetProgress(10);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                ws.setBlockNetworkImage(false);
                super.onPageFinished(view, url);
            }
        });

        if (!mShowMenu) {
            mMenuBar.setVisibility(View.GONE);
        }

        mainWebView.loadUrl(mCurrentUrl);
        mainWebView.addJavascriptInterface(new JsOperator(this), "browser");
    }

    @Override
    public void onBackPressed() {
        if (mainWebView.canGoBack() && mShowMenu) {
            mainWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_ImageView:
                if (mainWebView.canGoBack()) {
                    mainWebView.goBack();
                } else {
                    finish();
                }
                break;
            case R.id.go_next_ImageView:
                if (mainWebView.canGoForward()) {
                    mainWebView.goForward();
                }
                break;
            case R.id.refresh_ImageView:
                mainWebView.reload();
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
            mainWebView.getClass().getMethod("onResume").invoke(mainWebView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        super.onPause();

        try {
            mainWebView.getClass().getMethod("onPause").invoke(mainWebView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (mainWebView != null && mainWebView.getParent() != null) {
            mainWebView.setVisibility(View.GONE);
            ((ViewGroup) mainWebView.getParent()).removeView(mainWebView);
            mainWebView.destroy();
            mainWebView = null;
        }
        super.onDestroy();
    }

    public void share(String url) {
        Uri uri = Uri.parse(url);
        String title = uri.getQueryParameter("title");
        String text = uri.getQueryParameter("message");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, title));
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
