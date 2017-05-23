package com.xmd.manager.share;

import android.graphics.Bitmap;

import com.xmd.manager.Constant;

import java.util.Map;

/**
 * Created by linms@xiaomodo.com on 16-6-1.
 */
public class BaseShareUtil {

    protected String mShareUrl;
    protected String mShareTitle;
    protected String mShareDescription;
    protected Bitmap mShareThumbnail;

    protected void explainParams(Map<String, Object> params) {
        Object url = params.get(Constant.PARAM_SHARE_URL);
        if (url != null) {
            mShareUrl = url.toString();
        }

        Object title = params.get(Constant.PARAM_SHARE_TITLE);
        if (title != null) {
            mShareTitle = title.toString();
        }

        Object description = params.get(Constant.PARAM_SHARE_DESCRIPTION);
        if (description != null) {
            mShareDescription = description.toString();
        }

        Object thumbnail = params.get(Constant.PARAM_SHARE_THUMBNAIL);
        if (thumbnail != null) {
            mShareThumbnail = (Bitmap) thumbnail;
        }
    }
}
