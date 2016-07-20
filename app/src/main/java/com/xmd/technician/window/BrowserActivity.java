package com.xmd.technician.window;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 15-10-27.
 */
public class BrowserActivity extends BaseActivity implements View.OnClickListener{

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_SHOW_MENU = "show_menu";
    public static final String EXTRA_FULLSCREEN = "fullScreen";

    @Bind(R.id.mainwebView) WebView mainWebView;
    @Bind(R.id.back_ImageView) ImageView back_ImageView;
    @Bind(R.id.go_next_ImageView) ImageView go_next_ImageView;
    @Bind(R.id.refresh_ImageView) ImageView refresh_ImageView;
    @Bind(R.id.home_ImageView) ImageView home_ImageView;
    @Bind(R.id.download_progressbar) SmoothProgressBar downloadProgressbar;
    @Bind(R.id.menu_LinearLayout) LinearLayout mMenuBar;

    //    private View mCustomView;
//    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private WebSettings ws;

    private boolean mShowMenu;
    private String mCurrentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        boolean fullScreen = getIntent().getBooleanExtra(EXTRA_FULLSCREEN, false);
//        if (fullScreen) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//        } else {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
        setContentView(R.layout.activity_browser);
        ButterKnife.bind(this);

        mShowMenu = getIntent().getBooleanExtra(EXTRA_SHOW_MENU, true);
        mCurrentUrl = getIntent().getStringExtra(EXTRA_URL);

        back_ImageView.setOnClickListener(this);
        go_next_ImageView.setOnClickListener(this);
        refresh_ImageView.setOnClickListener(this);
        home_ImageView.setOnClickListener(this);

        ws = mainWebView.getSettings();
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

        //mainWebView.addJavascriptInterface(new ShareJavaScriptInterface(), "AppInterface");

        mainWebView.setWebChromeClient(new WebChromeClient() {
            public void onReceivedTitle(WebView view, String title) {
                setTitle(title);
            }

            public void onProgressChanged(WebView view, int newProgress) {
                downloadProgressbar.setTargetProgress(newProgress * 10);
            }

//            @Override
//            public void onShowCustomView(View view, CustomViewCallback callback) {
//                if (mCustomViewCallback != null) {
//                    mCustomViewCallback.onCustomViewHidden();
//                    mCustomViewCallback = null;
//                    return;
//                }
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//                BrowserActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                ViewGroup parentViewGroup = (ViewGroup) mainWebView.getParent();
//                parentViewGroup.removeView(mainWebView);
//                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                view.setLayoutParams(layoutParams);
//                parentViewGroup.addView(view);
//                mCustomView = view;
//                mCustomViewCallback = callback;
//                mMenuBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onHideCustomView() {
//                if (mCustomView != null) {
//                    if (mCustomViewCallback != null) {
//                        mCustomViewCallback.onCustomViewHidden();
//                        mCustomViewCallback = null;
//                    }
//                    BrowserActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    ViewGroup parentViewGroup = (ViewGroup) mCustomView.getParent();
//                    parentViewGroup.removeView(mCustomView);
//                    parentViewGroup.addView(mainWebView);
//                    mCustomView = null;
//                    if (mShowMenu) {
//                        mMenuBar.setVisibility(View.VISIBLE);
//                    }
//
//                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                }
//            }
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

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
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
