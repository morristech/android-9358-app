package com.xmd.manager.common;

import android.view.animation.TranslateAnimation;

/**
 * Created by linms@xiaomodo.com on 16-5-19.
 */
public class TabIndicatorAnimation extends TranslateAnimation {

    public TabIndicatorAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        super(fromXDelta, toXDelta, fromYDelta, toYDelta);
        setFillAfter(true);
    }
}
