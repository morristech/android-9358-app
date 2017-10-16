package com.xmd.chat;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.format.Time;

import com.hyphenate.chat.EMClient;
import com.hyphenate.util.PathUtil;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.XmdApp;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by mo on 17-7-8.
 * 语音管理
 */

public class VoiceManager {
    private static final VoiceManager ourInstance = new VoiceManager();
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
   // private String recordFile;
    private long recordStartTime;
    private long recordTime;
    private boolean audioSpeakerMode;
    private AudioManager audioManager;
    private OnPlayListener mOnPlayListener;
    private String voiceFilePath = null;
    private String voiceFileName = null;
    private File file;

    public static VoiceManager getInstance() {
        return ourInstance;
    }

    private VoiceManager() {
        audioManager = (AudioManager) XmdChat.getInstance().getContext().getSystemService(Context.AUDIO_SERVICE);
        audioSpeakerMode = XmdApp.getInstance().getSp().getBoolean(ChatConstants.SP_AUDIO_MODE_SPEAKER, false);
    }

    //播放语音
    public void startPlayVoice(final String filePath, final OnPlayListener listener) {
        if (!XmdApp.getInstance().getSp().contains(ChatConstants.SP_AUDIO_MODE_SPEAKER)) {
            XToast.show("长按消息可切换到杨声器模式");
            XmdApp.getInstance().getSp().edit().putBoolean(ChatConstants.SP_AUDIO_MODE_SPEAKER, audioSpeakerMode).apply();
        }
        XLogger.d("play " + filePath);
        if (mOnPlayListener != null) {
            mOnPlayListener.onStop();
            mOnPlayListener = null;
        }
        mOnPlayListener = listener;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        audioManager.setSpeakerphoneOn(audioSpeakerMode);
        audioManager.setMode(audioSpeakerMode ? AudioManager.MODE_NORMAL : AudioManager.MODE_IN_COMMUNICATION);
        mediaPlayer.setAudioStreamType(audioSpeakerMode ? AudioManager.STREAM_MUSIC : AudioManager.STREAM_VOICE_CALL);
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.setWakeMode(XmdChat.getInstance().getContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mOnPlayListener.onError("无法播放：what=" + what + ",extra=" + extra);
                    mOnPlayListener = null;
                    return true;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mOnPlayListener.onStop();
                    mOnPlayListener = null;
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mOnPlayListener.onPlay();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            mOnPlayListener.onError("无法播放：" + e.getMessage());
            mOnPlayListener = null;
        }
    }

    //停止播放
    public void stopPlayVoice() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            if (mOnPlayListener != null) {
                mOnPlayListener.onStop();
                mOnPlayListener = null;
            }
        }
    }

    //清除资源
    public void cleanResource() {
        stopPlayVoice();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
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
       // recordFile = PathUtil.getInstance().getVoicePath() + "/" + System.currentTimeMillis() + ".amr";
       // mediaRecorder.setOutputFile(recordFile);
        voiceFileName = getVoiceFileName(EMClient.getInstance().getCurrentUser());
        voiceFilePath = PathUtil.getInstance().getVoicePath() + "/" + voiceFileName;
        file = new File(voiceFilePath);
        mediaRecorder.setOutputFile(file.getAbsolutePath());
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

    private String getVoiceFileName(String uid) {
        Time now = new Time();
        now.setToNow();
        return uid + now.toString().substring(0, 15) + ".arm";
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
        return voiceFilePath;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public boolean isAudioSpeakerMode() {
        return audioSpeakerMode;
    }

    public void switchAudioMode() {
        audioSpeakerMode = !audioSpeakerMode;
        XmdApp.getInstance().getSp().edit().putBoolean(ChatConstants.SP_AUDIO_MODE_SPEAKER, audioSpeakerMode).apply();
        XToast.show(String.format(Locale.getDefault(), "已切换到%s模式", audioSpeakerMode ? "扬声器" : "听筒"));
    }


    public interface OnPlayListener {
        void onPlay();

        void onStop();

        void onError(String error);
    }


}
