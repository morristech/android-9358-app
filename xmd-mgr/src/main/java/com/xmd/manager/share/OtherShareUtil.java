package com.xmd.manager.share;

import android.content.Intent;

import com.xmd.app.XmdActivityManager;

import java.util.Map;

/**
 * Created by linms@xiaomodo.com on 16-6-1.
 */
public class OtherShareUtil extends BaseShareUtil {

    private static class InstanceHolder {
        private static OtherShareUtil sInstance = new OtherShareUtil();
    }

    private OtherShareUtil() {
    }

    public static OtherShareUtil getInstance() {
        return InstanceHolder.sInstance;
    }

    public void share(Map<String, Object> params) {
        explainParams(params);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, mShareTitle);
        intent.putExtra(Intent.EXTRA_TITLE, mShareDescription);
        intent.putExtra(Intent.EXTRA_TEXT, mShareUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        XmdActivityManager.getInstance().getCurrentActivity().startActivity(Intent.createChooser(intent, "请选择"));
    }
}
