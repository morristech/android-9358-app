package com.xmd.technician.window;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.google.zxing.Result;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.DecodeImage;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.widget.CustomWebView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.schedulers.NewThreadScheduler;

/**
 * Created by Administrator on 2016/8/17.
 */
public class IntroduceAccountActivity extends BaseActivity implements CustomWebView.LongClickCallBack {
    private CustomWebView mCustomWebView;
    private boolean isQR;//判断是否为二维码
    private Result result;//二维码解析结果
    private String url;
    private File file;
    private String mIntruduceUrl;
    private LayoutInflater layoutInflater;
    private View viewM;
    private View view;
    private PopupWindow window = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_account);
        initWebView();
        setBackVisible(true);
    }

    private void initWebView() {
        mCustomWebView = new CustomWebView(this, this);
        mIntruduceUrl = SharedPreferenceHelper.getServerHost() + RequestConstant.URL_INTRODUCE_BIND;
        mCustomWebView.loadUrl(mIntruduceUrl);//加载页面
        mCustomWebView.setFocusable(true);
        mCustomWebView.setFocusableInTouchMode(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        addContentView(mCustomWebView, lp);
    }

    @Override
    public void onLongClickCallBack(final String imgUrl) {
        url = imgUrl;
        MyAsyncTask mTask = new MyAsyncTask();
        mTask.execute(imgUrl);
        showBottomDialog();
    }

    /**
     * 判断是否为二维码
     * param url 图片地址
     * return
     */
    private boolean decodeImage(String sUrl) {
        result = DecodeImage.handleQRCodeFormBitmap(getBitmap(sUrl));
        if (result == null) {
            isQR = false;
        } else {
            isQR = true;
        }
        return isQR;
    }
    public class MyAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            decodeImage(params[0]);
            return null;
        }
    }
    /**
     * 根据地址获取网络图片
     *
     * @param sUrl 图片地址
     * @return
     * @throws IOException
     */
    public Bitmap getBitmap(String sUrl) {
        try {
            URL url = new URL(sUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                saveMyBitmap(bitmap, "code");//先把bitmap生成jpg图片
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * bitmap 保存为jpg 图片
     *
     * @param mBitmap 图片源
     * @param bitName 图片名
     */
    public void saveMyBitmap(Bitmap mBitmap, String bitName) {
        file = new File(Environment.getExternalStorageDirectory() + "/" + bitName + ".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 先保存到本地再广播到图库
     */
    public void saveImageToGallery(Context context) {
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), "code", null);
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                    + file)));
            makeShortToast("保存成功");
            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                @Override
                public void run() {
                   IntroduceAccountActivity.this.finish();
                }
            },1500);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void showBottomDialog() {
        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewM = layoutInflater.inflate(R.layout.introduce_view_layout, null);
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = 0.5f;
        lp.dimAmount = 0.5f;
        getWindow().setAttributes(lp);
        if (window == null) {
            window = new PopupWindow(viewM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            window.setAnimationStyle(R.style.popup_window_style);
            ColorDrawable dw = new ColorDrawable(Color.parseColor("#00FF0000"));
            window.setBackgroundDrawable(dw);
            window.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    lp.dimAmount = 1.0f;
                    getWindow().setAttributes(lp);
                }
            });
            LinearLayout introduceSave = (LinearLayout) viewM.findViewById(R.id.introduce_save);
            LinearLayout introduceCancel = (LinearLayout) viewM.findViewById(R.id.introduce_cancel);
            introduceSave.setOnClickListener((v) -> {
                window.dismiss();
                saveImageToGallery(IntroduceAccountActivity.this);
            });
            introduceCancel.setOnClickListener((v) -> {
                window.dismiss();
            });
        }
        try {
            if (window != null) {
                window.showAtLocation(mCustomWebView, Gravity.BOTTOM, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
