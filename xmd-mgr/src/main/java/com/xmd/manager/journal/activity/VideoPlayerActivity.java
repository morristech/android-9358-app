package com.xmd.manager.journal.activity;

import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.xmd.manager.R;
import com.xmd.manager.common.ToastUtils;

public class VideoPlayerActivity extends AppCompatActivity {
    private View mPlayerView;
    private TextView mMessageView;

    private VideoView mVideoView;

    private boolean mResetRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        getWindow().setBackgroundDrawable(new ColorDrawable(0xff000000));

        mResetRun = true;

        mVideoView = (VideoView) findViewById(R.id.video_view);
        mVideoView.setVideoURI(getIntent().getData());

        MediaController mediaController = new MediaController(VideoPlayerActivity.this);
        mVideoView.setMediaController(mediaController);
        mediaController.setMediaPlayer(mVideoView);

        mPlayerView = findViewById(R.id.player_view);
        mMessageView = (TextView) findViewById(R.id.message_view);

        mMessageView.setText("正在努力加载视频");
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayerView.setVisibility(View.GONE);
                //加入下面这段，保证从0开始播放 。。 PS：有些手机不时候不从0开始播放
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            while (mResetRun && !mVideoView.isPlaying()) {
                                Thread.sleep(100);
                            }
                            if (mResetRun)
                                mVideoView.seekTo(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                String error;
                switch (extra) {
                    case MediaPlayer.MEDIA_ERROR_IO:
                        error = "网络异常";
                        break;
                    case MediaPlayer.MEDIA_ERROR_MALFORMED:
                        error = "不是标准的视频流";
                        break;
                    case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        error = "连接服务器超时";
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        error = "视频格式不支持";
                        break;
                    default:
                        error = "系统错误";
                        break;
                }
                ToastUtils.showToastShort(VideoPlayerActivity.this, "播放遇到错误：" + error);
                return true;
            }
        });
        mVideoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mResetRun = false;
    }
}
