package com.xmd.chat.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xmd.chat.R;

/**
 * Created by Lhj on 18-1-24.
 */

public class VoiceSendingView extends RelativeLayout {

    private AnimationDrawable frameAnimation;

    public VoiceSendingView(Context context) {
        this(context, null);
    }

    public VoiceSendingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.voice_sending, this);
        ImageView img = (ImageView) findViewById(R.id.microphone);
        img.setBackgroundResource(R.drawable.animation_voice);
        frameAnimation = (AnimationDrawable) img.getBackground();
    }

    public void showRecording() {
        frameAnimation.start();
    }

    public void release() {
        frameAnimation.stop();
    }
}
