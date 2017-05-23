package com.xmd.manager.journal.camera;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.common.Logger;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.widget.AlertDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "camera";
    private static final int MSG_TIMER_START = 1;
    private static final int MSG_TIMER_END = 2;
    private static final int MSG_TIMER_COUNT = 3;

    public static final String EXTRA_STRING_VIDEO_PATH = "extra_string_video_path";
    //录制参数设置
    public static final String EXTRA_INT_WIDTH = "extra_int_width";
    public static final String EXTRA_INT_HEIGHT = "extra_int_height";
    public static final String EXTRA_INT_FRAME_RATE = "extra_int_frame_rate";
    public static final String EXTRA_INT_VIDEO_BIT_RATE = "extra_int_video_bit_rate";
    public static final String EXTRA_INT_TIME_SECOND = "extra_int_time_second";

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private int mPreviewOrientationDegree = 90;//预览界面默认竖屏状态
    private MediaRecorder mMediaRecorder;
    private String mRecordFilePath;
    private Camera.Size mRecordVideoSize;

    private View mCancelButton;
    private View mRecordButton;
    private View mFinishButton;
    private TextView mTimerTextView;

    private long mRecordEndTimeMS;

    private int mTimeLimit;
    private int mFrameRate;
    private int mVideoBitRate;
    private int mVideoWidth;
    private int mVideoHeight;

    private boolean needOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCancelButton = findViewById(R.id.btn_cancel);
        mRecordButton = findViewById(R.id.btn_record);
        mFinishButton = findViewById(R.id.btn_finish);
        mCancelButton.setOnClickListener(this);
        mRecordButton.setOnClickListener(this);
        mFinishButton.setOnClickListener(this);

        mTimerTextView = (TextView) findViewById(R.id.elapsed_time);

        //检查是否有摄像头
        if (!CameraTool.checkCameraHardware(this)) {
            showFinishErrorView("设备没有摄像头，无法进行录制");
            return;
        }
        //检查是否有相关权限
        if (!CameraTool.hasVideoPermissions(this)) {
            CameraTool.requestVideoPermissions(this);
        } else {
            needOpen = true;
        }


        Intent intent = getIntent();
        mTimeLimit = intent.getIntExtra(EXTRA_INT_TIME_SECOND, 30);
        mFrameRate = intent.getIntExtra(EXTRA_INT_FRAME_RATE, 24);
        mVideoBitRate = intent.getIntExtra(EXTRA_INT_VIDEO_BIT_RATE, 1024 * 1024);
        mVideoWidth = intent.getIntExtra(EXTRA_INT_WIDTH, 640);
        mVideoHeight = intent.getIntExtra(EXTRA_INT_HEIGHT, 480);

        if (!SharedPreferenceHelper.getIsWarnningVideoRecord()) {
            SharedPreferenceHelper.setIsWarnningVideoRecord(true);
            new AlertDialogBuilder(this)
                    .setMessage("为保证视频质量，请横向录制！")
                    .setCancelable(false)
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (needOpen) {
            openCamera();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeCamera();
    }

    private void openCamera() {
        showRecordView(false);
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            showFinishErrorView("无法打开摄像头");
            return;
        }

        mCameraPreview = new CameraPreview(this, mCamera);

        Camera.Parameters parameters = mCamera.getParameters();
        //选择录制视频大小,Camera支持的大小即能录制的大小
        List<Camera.Size> supportVideoSizes = parameters.getSupportedVideoSizes();
        if (supportVideoSizes == null) {
            supportVideoSizes = parameters.getSupportedPreviewSizes();
        }
        mRecordVideoSize = null;
        if (supportVideoSizes != null) {
            for (Camera.Size option : supportVideoSizes) {
                if (option.width == option.height * mVideoWidth / mVideoHeight && option.width <= mVideoWidth) {
                    mRecordVideoSize = option;
                }
            }
            if (mRecordVideoSize == null) {
                //没有找到同比例的，那么查找最大支持的大小
                for (Camera.Size option : supportVideoSizes) {
                    if (option.width <= mVideoWidth && option.height <= mVideoHeight) {
                        mRecordVideoSize = option;
                    }
                }
            }
        } else {
            mRecordVideoSize = parameters.getPreviewSize();
        }
        if (mRecordVideoSize == null) {
            ToastUtils.showToastLong(this, "无法支持录制尺寸：" + mVideoWidth + "x" + mVideoHeight);
            finish();
        }
        Log.i(TAG, "video size:" + mRecordVideoSize.width + "x" + mRecordVideoSize.height);

        //根据录制视频大小选择预览比例
        Camera.Size previewSize = CameraTool.chooseOptimalSize4x(parameters.getSupportedPreviewSizes(), 0, 0, mRecordVideoSize);
        Log.i(TAG, "preview size:" + previewSize.width + "x" + previewSize.height);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mCameraPreview.setAspectRatio(previewSize.width, previewSize.height);
            mPreviewOrientationDegree = 0;
        } else {
            mCameraPreview.setAspectRatio(previewSize.height, previewSize.width);
            mPreviewOrientationDegree = 90;
        }
        mCamera.setDisplayOrientation(mPreviewOrientationDegree);
        parameters.setPreviewSize(mRecordVideoSize.width, mRecordVideoSize.height);
        mCamera.setParameters(parameters);

        ((ViewGroup) findViewById(R.id.layout_preview)).addView(mCameraPreview);
        ((FrameLayout.LayoutParams) mCameraPreview.getLayoutParams()).gravity = Gravity.CENTER;
    }


    private void closeCamera() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mCameraPreview != null) {
            ((ViewGroup) findViewById(R.id.layout_preview)).removeAllViews();
            mCameraPreview = null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!CameraTool.onRequestPermissionsResult(requestCode, grantResults)) {
            showFinishErrorView("没有权限，无法进行录制");
            return;
        } else {
            openCamera();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showFinishErrorView(String error) {
        new AlertDialogBuilder(this)
                .setMessage(error)
                .setCancelable(false)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record:
                startRecordVideo();
                break;
            case R.id.btn_cancel:
                stopRecordVideo();
                File outputFile = new File(mRecordFilePath);
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                showRecordView(false);
                break;
            case R.id.btn_finish:
                saveData();
                break;
        }
    }

    private void showRecordView(boolean record) {
        if (record) {
            mRecordButton.setVisibility(View.GONE);
            mCancelButton.setVisibility(View.VISIBLE);
            mFinishButton.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessage(MSG_TIMER_START);
        } else {
            mRecordButton.setVisibility(View.VISIBLE);
            mCancelButton.setVisibility(View.INVISIBLE);
            mFinishButton.setVisibility(View.INVISIBLE);
            mHandler.sendEmptyMessage(MSG_TIMER_END);
        }
    }

    private boolean startRecordVideo() {
        if (!prepareMediaRecorder()) {
            return false;
        }
        if (mMediaRecorder == null) {
            return false;
        }
        mMediaRecorder.start();
        showRecordView(true);
        return true;
    }

    private boolean prepareMediaRecorder() {
        //创建文件用于保存录制数据
        mRecordFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + File.separator + "VID_" + String.valueOf(System.currentTimeMillis()) + ".mp4";
        File outputFile = new File(mRecordFilePath);
        try {
            if (!outputFile.createNewFile()) {
                ToastUtils.showToastLong("can not create file: " + mRecordFilePath);
            }
        } catch (IOException e) {
            return false;
        }
        //重新启动preview之后对焦会取消，所以在这里设置对焦模式
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> forcusModes = parameters.getSupportedFocusModes();
        if (forcusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else if (forcusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            parameters.setFocusMode(forcusModes.get(0));
        }
        mCamera.setParameters(parameters);
        mCamera.unlock();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setOrientationHint(mPreviewOrientationDegree);//保持和预览方向一致
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);

//        mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        /**480P参数
         audioBitRate = 128000
         audioChannels = 2
         audioCodec = 3
         audioSampleRate = 48000
         duration = 30
         fileFormat = 2
         quality = 4
         videoBitRate = 4500000
         videoCodec = 2
         videoFrameHeight = 480
         videoFrameRate = 30
         videoFrameWidth = 720
         */
        try {
            CamcorderProfile camcorderProfile;
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH)) {
                camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            } else {
                camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
            }
            camcorderProfile.videoBitRate = mVideoBitRate;
            camcorderProfile.videoFrameWidth = mRecordVideoSize.width;
            camcorderProfile.videoFrameHeight = mRecordVideoSize.height;
            camcorderProfile.videoCodec = MediaRecorder.VideoEncoder.H264;
            mMediaRecorder.setProfile(camcorderProfile);

            mMediaRecorder.setOutputFile(outputFile.getPath());
            mMediaRecorder.setPreviewDisplay(mCameraPreview.getHolder().getSurface());
        } catch (Exception e) {
            Logger.e("set record param faied:" + e.getLocalizedMessage());
            releaseMediaRecorder();
            ToastUtils.showToastLong(CameraActivity.this, "录制参数不支持，无法录制:" + e.getLocalizedMessage());
            return false;
        }

        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            Toast.makeText(CameraActivity.this, "media recorder failed : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();
    }

    private void stopRecordVideo() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            releaseMediaRecorder();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mMediaRecorder != null && keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static String getVideoPath(Intent intent) {
        return intent.getStringExtra(EXTRA_STRING_VIDEO_PATH);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TIMER_START:
                    mRecordEndTimeMS = SystemClock.elapsedRealtime() + mTimeLimit * 1000;
                    mTimerTextView.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessage(MSG_TIMER_COUNT);
                    break;
                case MSG_TIMER_END:
                    mHandler.removeMessages(MSG_TIMER_COUNT);
                    mTimerTextView.setVisibility(View.INVISIBLE);
                    break;
                case MSG_TIMER_COUNT:
                    long leftTime = (mRecordEndTimeMS - SystemClock.elapsedRealtime()) / 1000;
                    if (leftTime <= 0) {
                        saveData();
                    } else {
                        mTimerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", leftTime / 60, leftTime % 60));
                        mHandler.sendEmptyMessageDelayed(MSG_TIMER_COUNT, 1000);
                    }
                    break;
            }
        }
    };

    private void saveData() {
        ToastUtils.showToastShort(CameraActivity.this, "录制完成");
        stopRecordVideo();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_STRING_VIDEO_PATH, mRecordFilePath);
        setResult(Activity.RESULT_OK, intent);
        mHandler.removeCallbacksAndMessages(null);
        finish();
    }
}
