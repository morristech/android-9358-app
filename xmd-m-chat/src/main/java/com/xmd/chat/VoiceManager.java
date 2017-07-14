package com.xmd.chat;

import android.content.Context;
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
    private boolean handsFree;
    private AudioManager audioManager;

    public static VoiceManager getInstance() {
        return ourInstance;
    }

    private VoiceManager() {
        audioManager = (AudioManager) XmdChat.getInstance().getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    public void play(String filePath, final Callback<Void> callback) {
        XLogger.d("play " + filePath);
        if (stopPlayVoiceListener != null) {
            stopPlayVoiceListener.onStop(mediaPlayer);
            stopPlayVoiceListener = null;
        }
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        audioManager.setSpeakerphoneOn(handsFree);
        audioManager.setMode(handsFree ? AudioManager.MODE_NORMAL : AudioManager.MODE_IN_COMMUNICATION);
        mediaPlayer.setAudioStreamType(handsFree ? AudioManager.STREAM_MUSIC : AudioManager.STREAM_VOICE_CALL);
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.setWakeMode(XmdChat.getInstance().getContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    stopPlayVoiceListener = null;
                    callback.onResponse(null, new Throwable("无法播放：what=" + what + ",extra=" + extra));
                    return true;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayVoiceListener = null;
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
                    stopPlayVoiceListener = null;
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
                stopPlayVoiceListener = null;
            }
        }
    }


    public void cleanResource() {
        stopPlayVoiceListener = null;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
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
        }
        mediaRecorder.reset();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setAudioChannels(2);
        mediaRecorder.setAudioSamplingRate(8000);
        mediaRecorder.setAudioEncodingBitRate(64);
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

    public boolean isHandsFree() {
        return handsFree;
    }

    public void setHandsFree(boolean handsFree) {
        this.handsFree = handsFree;
    }
}
