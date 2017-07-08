package com.xmd.chat;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.PowerManager;
import android.os.SystemClock;

import com.hyphenate.util.PathUtil;
import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;

import java.io.IOException;

/**
 * Created by mo on 17-7-8.
 * 语音管理
 */

public class VoiceManager {
    private static final VoiceManager ourInstance = new VoiceManager();
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private long recordStartTime;
    private long recordTime;

    public static VoiceManager getInstance() {
        return ourInstance;
    }

    private VoiceManager() {

    }

    public void play(String filePath, final Callback<Void> callback) {
        XLogger.d("play " + filePath);
        if (stopPlayVoiceListener != null) {
            stopPlayVoiceListener.onStop(mediaPlayer);
            stopPlayVoiceListener = null;
        }
        try {
            createVoicePlayer();
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.setWakeMode(XmdChat.getInstance().getContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    callback.onResponse(null, new Throwable("无法播放：what=" + what + ",extra=" + extra));
                    return true;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    callback.onResponse(null, null);
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            setStopPlayVoiceListener(new OnStopListener() {
                @Override
                public void onStop(MediaPlayer mediaPlayer) {
                    callback.onResponse(null, null);
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            callback.onResponse(null, e);
        }
    }

    public void stopPlayVoice() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            if (stopPlayVoiceListener != null) {
                stopPlayVoiceListener.onStop(mediaPlayer);
            }
        }
    }


    public void cleanResource() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
    }


    private void createVoicePlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    private interface OnStopListener {
        void onStop(MediaPlayer mediaPlayer);
    }

    private OnStopListener stopPlayVoiceListener;

    private OnStopListener getStopPlayVoiceListener() {
        return stopPlayVoiceListener;
    }

    private void setStopPlayVoiceListener(OnStopListener stopPlayVoiceListener) {
        this.stopPlayVoiceListener = stopPlayVoiceListener;
    }


    /**
     * 开始录音
     */
    public boolean startRecord() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setAudioChannels(2);
            mediaRecorder.setAudioSamplingRate(8000);
            mediaRecorder.setAudioEncodingBitRate(64);
        } else {
            mediaRecorder.stop();
            mediaRecorder.reset();
        }
        recordFile = PathUtil.getInstance().getVoicePath() + "/" + System.currentTimeMillis() + ".amr";
        mediaRecorder.setOutputFile(recordFile);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            recordStartTime = SystemClock.elapsedRealtime();
            return true;
        } catch (IOException e) {
            XLogger.e("record failed:" + e.getMessage());
            XToast.show("无法录音:" + e.getMessage());
        }
        return false;
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            recordTime = SystemClock.elapsedRealtime() - recordStartTime;
        }
    }

    public String getRecordFile() {
        return recordFile;
    }

    public long getRecordTime() {
        return recordTime;
    }
}
