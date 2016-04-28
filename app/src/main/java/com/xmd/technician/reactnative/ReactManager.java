package com.xmd.technician.reactnative;

import android.app.Application;

import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.shell.MainReactPackage;
import com.xmd.technician.BuildConfig;

/**
 * Created by sdcm on 16-4-15.
 */
public class ReactManager {

    public static ReactInstanceManager sReactInstanceManager;

    public static void initialize(Application application) {
        sReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(application)
                .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("index.android")
                .addPackage(new MainReactPackage())
                .addPackage(new AndroidReactPackage())
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
    }
}
