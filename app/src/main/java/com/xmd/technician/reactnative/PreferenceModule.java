package com.xmd.technician.reactnative;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.Logger;

/**
 * Created by linms@xiaomodo.com on 16-4-21.
 */
public class PreferenceModule extends ReactContextBaseJavaModule {

    public PreferenceModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "SharedPreference";
    }

    @ReactMethod
    public void getConstants(Callback callback) {
        callback.invoke(SharedPreferenceHelper.getServerHost(), SharedPreferenceHelper.getUserToken());
    }
}
