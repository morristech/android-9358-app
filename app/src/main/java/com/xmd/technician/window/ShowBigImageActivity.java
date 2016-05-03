package com.xmd.technician.window;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.ProgressBar;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.ImageUtils;
import com.xmd.technician.R;
import com.xmd.technician.chat.LoadLocalBigImgTask;
import com.xmd.technician.bean.ImageCache;
import com.xmd.technician.widget.scaleview.ScaleView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowBigImageActivity extends BaseActivity {

    private boolean mIsDownloaded;
    private ProgressDialog mProgressDialog;
    private int mDefaultRes = R.drawable.icon08;
    private String mLocalFilePath;
    private Bitmap mBitmap;

    @Bind(R.id.imageView) ScaleView mImageView;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_big_image);
        ButterKnife.bind(this);

        mDefaultRes = getIntent().getIntExtra("default_image", R.drawable.icon22);
        Uri uri = getIntent().getParcelableExtra("uri");
        String remotepath = getIntent().getExtras().getString("remotepath");
        mLocalFilePath = getIntent().getExtras().getString("localUrl");
        String secret = getIntent().getExtras().getString("secret");

        //本地存在，直接显示本地的图片
        if (uri != null && new File(uri.getPath()).exists()) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            // int screenWidth = metrics.widthPixels;
            // int screenHeight =metrics.heightPixels;
            mBitmap = ImageCache.getInstance().get(uri.getPath());
            if (mBitmap == null) {
                LoadLocalBigImgTask task = new LoadLocalBigImgTask(this, uri.getPath(), mImageView, mProgressBar, ImageUtils.SCALE_IMAGE_WIDTH,
                        ImageUtils.SCALE_IMAGE_HEIGHT);
                if (Build.VERSION.SDK_INT > 10) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }
            } else {
                mImageView.setImageBitmap(mBitmap);
            }
        } else if (remotepath != null) { //去服务器下载图片
            Map<String, String> maps = new HashMap<String, String>();
            if (!TextUtils.isEmpty(secret)) {
                maps.put("share-secret", secret);
            }
            downloadImage(remotepath, maps);
        } else {
            mImageView.setImageResource(mDefaultRes);
        }

        mImageView.setOnLongClickListener(v -> {
            finish();
            return false;
        });
    }

    /**
     * 下载图片
     *
     * @param remoteFilePath
     */
    private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
        String str1 = getResources().getString(R.string.download_the_pictures);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage(str1);
        mProgressDialog.show();
        File temp = new File(mLocalFilePath);
        final String tempPath = temp.getParent() + "/temp_" + temp.getName();
        final EMCallBack callback = new EMCallBack() {
            public void onSuccess() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new File(tempPath).renameTo(new File(mLocalFilePath));

                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int screenWidth = metrics.widthPixels;
                        int screenHeight = metrics.heightPixels;

                        mBitmap = ImageUtils.decodeScaleImage(mLocalFilePath, screenWidth, screenHeight);
                        if (mBitmap == null) {
                            mImageView.setImageResource(mDefaultRes);
                        } else {
                            mImageView.setImageBitmap(mBitmap);
                            ImageCache.getInstance().put(mLocalFilePath, mBitmap);
                            mIsDownloaded = true;
                        }
                        if (ShowBigImageActivity.this.isFinishing() || ShowBigImageActivity.this.isDestroyed()) {
                            return;
                        }
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                    }
                });
            }

            public void onError(int error, String msg) {
                File file = new File(tempPath);
                if (file.exists()&&file.isFile()) {
                    file.delete();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ShowBigImageActivity.this.isFinishing() || ShowBigImageActivity.this.isDestroyed()) {
                            return;
                        }
                        mImageView.setImageResource(mDefaultRes);
                        mProgressDialog.dismiss();
                    }
                });
            }

            public void onProgress(final int progress, String status) {
                final String str2 = getResources().getString(R.string.download_the_pictures_new);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ShowBigImageActivity.this.isFinishing() || ShowBigImageActivity.this.isDestroyed()) {
                            return;
                        }
                        mProgressDialog.setMessage(str2 + progress + "%");
                    }
                });
            }
        };

        EMClient.getInstance().chatManager().downloadFile(remoteFilePath, tempPath, headers, callback);

    }

    @Override
    public void onBackPressed() {
        if (mIsDownloaded)
            setResult(RESULT_OK);
        finish();
    }
}
