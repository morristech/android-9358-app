package com.xmd.technician.reactnative;

import android.view.View;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.common.MapBuilder;
import com.facebook.stetho.common.StringUtil;
import com.xmd.technician.R;
import com.xmd.technician.TechApplication;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.widget.AlertDialogBuilder;
import com.xmd.technician.widget.CustomAlertDialog;

import java.util.Map;

/**
 * Created by linms@xiaomodo.com on 16-4-26.
 */
public class AlertDialogModule extends ReactContextBaseJavaModule{

    private static final String ACTION_CONFIRM_KEY = "key_confirm";
    private static final String ACTION_CANCEL_KEY = "key_cancel";

    private static final int ACTION_CANCEL = 0;
    private static final int ACTION_CONFIRM = 1;

    public AlertDialogModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = MapBuilder.newHashMap();
        constants.put(ACTION_CONFIRM_KEY, ACTION_CONFIRM);
        constants.put(ACTION_CANCEL_KEY, ACTION_CANCEL);
        return constants;
    }

    @Override
    public String getName() {
        return "CustomAlertDialog";
    }

    @ReactMethod
    public void alert(String message, String positiveText, String negativeText, Callback actionCallback) {
        AlertDialogBuilder builder = new AlertDialogBuilder(getCurrentActivity())
                .setMessage(message);
        if (Utils.isEmpty(positiveText)) {
            positiveText = ResourceUtils.getString(R.string.confirm);
        }

        if (actionCallback != null) {
            builder.setPositiveButton(positiveText, v -> {
                actionCallback.invoke(ACTION_CONFIRM, v);
            });
        } else {
            builder.setPositiveButton(positiveText, null);
        }

        if (Utils.isNotEmpty(negativeText)) {
            if (actionCallback != null) {
                builder.setNegativeButton(negativeText, v -> {
                    actionCallback.invoke(ACTION_CANCEL, v);
                });
            }
        }

        builder.setCancelable(true);
        builder.show();
    }
}
