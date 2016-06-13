package com.xmd.technician.share;

import android.graphics.Bitmap;

import com.xmd.technician.Constant;

import java.util.Map;

/**
 * Created by linms@xiaomodo.com on 16-5-4.
 */
public class BaseShareUtil {

    private static final int THUMB_SIZE = 181;
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
            Bitmap bmp = (Bitmap) thumbnail;
            mShareThumbnail = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
            bmp.recycle();
            //mShareThumbnail = (Bitmap) thumbnail;
        }
    }
}
