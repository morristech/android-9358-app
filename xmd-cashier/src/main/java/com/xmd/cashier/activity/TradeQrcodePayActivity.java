package com.xmd.cashier.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.TradeQrcodePayContract;
import com.xmd.cashier.dal.event.QRScanStatusEvent;
import com.xmd.cashier.dal.event.TradeQrcodeCloseEvent;
import com.xmd.cashier.presenter.TradeQrcodePayPresenter;
import com.xmd.cashier.widget.zxing.CameraManager;
import com.xmd.cashier.widget.zxing.CaptureActivityHandler;
import com.xmd.cashier.widget.zxing.InactivityTimer;
import com.xmd.cashier.widget.zxing.ViewfinderView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by zr on 17-5-12.
 * 小摩豆在线买单
 */

public class TradeQrcodePayActivity extends BaseActivity implements TradeQrcodePayContract.View, SurfaceHolder.Callback {
    private TradeQrcodePayContract.Presenter mPresenter;

    private LinearLayout mBitmapLayout;
    private TextView mAmountText;
    private ImageView mQRCodeImg;
    private ImageView mScanTipText;
    private RelativeLayout mScanStatusLayout;
    private LinearLayout mQRCodeErrorLayout;
    private TextView mQRCodeErrorText;
    private TextView mQRCodeExpireText;
    private TextView mGiftActivityText;

    private CaptureActivityHandler handler;
    private FrameLayout mAuthLayout;
    private SurfaceView mAuthSurfaceView;
    private ViewfinderView mAuthFinderView;
    private Vector<BarcodeFormat> decodeFormats;

    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean hasSurface;
    private boolean playBeep;
    private boolean vibrate;

    private static final long VIBRATE_DURATION = 200L;
    private static final float BEEP_VOLUME = 1f;

    private int mTradeType;

    private TextView mAuthText;
    private TextView mBitmapText;

    private String currentPriority;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_qrcode_pay);
        inactivityTimer = new InactivityTimer(this);
        CameraManager.init(getApplication());
        mPresenter = new TradeQrcodePayPresenter(this, this);
        initView();

        mTradeType = getIntent().getIntExtra(AppConstants.EXTRA_TRADE_TYPE, 0);
        hasSurface = false;
        mPresenter.onCreate();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mTradeType = getIntent().getIntExtra(AppConstants.EXTRA_TRADE_TYPE, 0);
        hasSurface = false;
        mPresenter.onCreate();
    }

    private void initView() {
        showToolbar(R.id.toolbar, "扫码收款");

        mAuthText = (TextView) findViewById(R.id.tv_auth_pay);
        mBitmapText = (TextView) findViewById(R.id.tv_bitmap_pay);

        mBitmapLayout = (LinearLayout) findViewById(R.id.layout_bitmap);
        mAmountText = (TextView) findViewById(R.id.tv_qrcode_amount);
        mQRCodeErrorLayout = (LinearLayout) findViewById(R.id.layout_qrcode_error);
        mQRCodeErrorText = (TextView) findViewById(R.id.tv_qrcode_error);
        mQRCodeImg = (ImageView) findViewById(R.id.img_scan_qrcode);
        mQRCodeExpireText = (TextView) findViewById(R.id.tv_qrcode_expire_tip);

        mScanTipText = (ImageView) findViewById(R.id.img_scan_tip);
        mScanStatusLayout = (RelativeLayout) findViewById(R.id.ly_scan_status);

        mGiftActivityText = (TextView) findViewById(R.id.tv_pay_activity);
        mGiftActivityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onGiftActivity();
            }
        });

        mAuthLayout = (FrameLayout) findViewById(R.id.layout_auth);
        mAuthSurfaceView = (SurfaceView) findViewById(R.id.capture_preview_view);
        mAuthFinderView = (ViewfinderView) findViewById(R.id.capture_viewfinder_view);

        mAuthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPriority != AppConstants.ONLINE_PAY_PRIORITY_AUTH) {
                    currentPriority = AppConstants.ONLINE_PAY_PRIORITY_AUTH;
                    mAuthLayout.setVisibility(View.VISIBLE);
                    mAuthText.setTextColor(ResourceUtils.getColor(R.color.colorText4));
                    mAuthText.setBackgroundColor(ResourceUtils.getColor(R.color.colorWhite));
                    mBitmapLayout.setVisibility(View.GONE);
                    mBitmapText.setTextColor(ResourceUtils.getColor(R.color.colorText3));
                    mBitmapText.setBackgroundColor(ResourceUtils.getColor(R.color.colorStoke));
                    resume();
                    mPresenter.onAuthClick();
                }
            }
        });

        mBitmapText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPriority != AppConstants.ONLINE_PAY_PRIORITY_BITMAP) {
                    currentPriority = AppConstants.ONLINE_PAY_PRIORITY_BITMAP;
                    mAuthLayout.setVisibility(View.GONE);
                    mAuthText.setTextColor(ResourceUtils.getColor(R.color.colorText3));
                    mAuthText.setBackgroundColor(ResourceUtils.getColor(R.color.colorStoke));
                    mBitmapLayout.setVisibility(View.VISIBLE);
                    mBitmapText.setTextColor(ResourceUtils.getColor(R.color.colorText4));
                    mBitmapText.setBackgroundColor(ResourceUtils.getColor(R.color.colorWhite));
                    pause();
                    mPresenter.onBitmapClick();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inactivityTimer.shutdown();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConstants.ONLINE_PAY_PRIORITY_AUTH.equalsIgnoreCase(currentPriority)) {
            resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (AppConstants.ONLINE_PAY_PRIORITY_AUTH.equalsIgnoreCase(currentPriority)) {
            pause();
        }
    }

    private void pause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    private void resume() {
        SurfaceHolder surfaceHolder = mAuthSurfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    public void setPresenter(TradeQrcodePayContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public void setAmount(String amount) {
        mAmountText.setText(amount);
    }

    @Override
    public void setQRCode(Bitmap bitmap) {
        mQRCodeImg.setImageBitmap(bitmap);
    }

    @Override
    public void updateScanStatus() {
        mScanStatusLayout.setVisibility(View.VISIBLE);
        mScanTipText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showQrError(String error) {
        mQRCodeErrorLayout.setVisibility(View.VISIBLE);
        mQRCodeImg.setVisibility(View.GONE);
        mQRCodeErrorText.setText(error);
    }

    @Override
    public void showQrSuccess() {
        mQRCodeErrorLayout.setVisibility(View.GONE);
        mQRCodeImg.setVisibility(View.VISIBLE);
        mQRCodeExpireText.setVisibility(View.VISIBLE);
    }

    @Override
    public void showGiftActivity() {
        mGiftActivityText.setVisibility(View.VISIBLE);
    }

    @Override
    public int getType() {
        return mTradeType;
    }

    @Override
    public void showView(String priority) {
        currentPriority = priority;
        switch (priority) {
            case AppConstants.ONLINE_PAY_PRIORITY_AUTH:
                mAuthLayout.setVisibility(View.VISIBLE);
                mAuthText.setTextColor(ResourceUtils.getColor(R.color.colorText4));
                mAuthText.setBackgroundColor(ResourceUtils.getColor(R.color.colorWhite));
                mBitmapLayout.setVisibility(View.GONE);
                mBitmapText.setTextColor(ResourceUtils.getColor(R.color.colorText3));
                mBitmapText.setBackgroundColor(ResourceUtils.getColor(R.color.colorStoke));
                break;
            case AppConstants.ONLINE_PAY_PRIORITY_BITMAP:
                mAuthLayout.setVisibility(View.GONE);
                mAuthText.setTextColor(ResourceUtils.getColor(R.color.colorText3));
                mAuthText.setBackgroundColor(ResourceUtils.getColor(R.color.colorStoke));
                mBitmapLayout.setVisibility(View.VISIBLE);
                mBitmapText.setTextColor(ResourceUtils.getColor(R.color.colorText4));
                mBitmapText.setBackgroundColor(ResourceUtils.getColor(R.color.colorWhite));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyEventBack() {
        mPresenter.onKeyEventBack();
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(QRScanStatusEvent qrScanStatusEvent) {
        // 主线程更新扫码状态
        updateScanStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TradeQrcodeCloseEvent tradeQrcodeCloseEvent) {
        finishSelf();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (Exception e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return mAuthFinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        mAuthFinderView.drawViewfinder();
    }

    public void handleDecode(final Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        pause();
        mPresenter.authPay(ResultParser.parseResult(obj).toString());
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mediaPlayer.seekTo(0);
        }
    };
}
