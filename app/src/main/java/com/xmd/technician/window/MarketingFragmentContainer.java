package com.xmd.technician.window;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.xmd.technician.common.Logger;
import com.xmd.technician.reactnative.ReactManager;

/**
 * 启动一个React应用并把它设置为Activity的主要内容视图。
 */
public class MarketingFragmentContainer extends BaseFragment implements DefaultHardwareBackBtnHandler {

    private ReactRootView mReactRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mReactRootView = new ReactRootView(getActivity());
        mReactRootView.startReactApplication(ReactManager.sReactInstanceManager, "MarketingFragmentContainer", null);
        return mReactRootView;
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        Logger.v("MarketingFragmentContainer.invokeDefaultOnBackPressed");
        if (mIFragmentCallback != null) {
            mIFragmentCallback.handleBackPressed();
        }
    }

    /**
     * 传递一些Activity的生命周期事件到ReactInstanceManager
     */
    @Override
    public void onPause() {
        super.onPause();
        if (ReactManager.sReactInstanceManager != null) {
            ReactManager.sReactInstanceManager.onPause();
        }
    }

    /**
     * 传递一些Activity的生命周期事件到ReactInstanceManager
     */
    @Override
    public void onResume() {
        super.onResume();
        if (ReactManager.sReactInstanceManager != null) {
            ReactManager.sReactInstanceManager.onResume(getActivity(), this);
        }
    }
}
