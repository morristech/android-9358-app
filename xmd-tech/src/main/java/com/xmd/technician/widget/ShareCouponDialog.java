package com.xmd.technician.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.share.ShareController;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by sdcm on 17-3-16.
 */

public class ShareCouponDialog extends Dialog {


    private Button mButtonShare;
    private ImageView btnDismiss, mCode;
    private String shareThumbnail, shareUrl, shareTitle, ShareDescription, type, actId, dialogTitle;
    private Context mContext;
    private TextView mDialogTitle, mDialogDes;


    public ShareCouponDialog(Context context, Map<String, Object> params) {
        this(context, R.style.default_dialog_style);
        this.mContext = context;
        shareThumbnail = (String) params.get(Constant.PARAM_SHARE_THUMBNAIL);
        shareUrl = (String) params.get(Constant.PARAM_SHARE_URL);
        shareTitle = (String) params.get(Constant.PARAM_SHARE_TITLE);
        ShareDescription = (String) params.get(Constant.PARAM_SHARE_DESCRIPTION);
        type = (String) params.get(Constant.PARAM_SHARE_TYPE);
        dialogTitle = (String) params.get(Constant.PARAM_SHARE_DIALOG_TITLE);
        actId = (String) params.get(Constant.PARAM_ACT_ID);
    }


    public ShareCouponDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ShareCouponDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.share_coupon_dialog);
        btnDismiss = (ImageView) findViewById(R.id.btn_dismiss);
        mButtonShare = (Button) findViewById(R.id.coupon_share_btn);
        mCode = (ImageView) findViewById(R.id.img_share_code);
        mDialogTitle = (TextView) findViewById(R.id.dialog_title_text);
        mDialogDes = (TextView) findViewById(R.id.des_share_text);
        mDialogTitle.setText(String.format("%s二维码", dialogTitle));
        if (dialogTitle.equals(ResourceUtils.getString(R.string.gift_message))) {
            mDialogDes.setText(String.format("扫码关注9358,兑换%s", dialogTitle));
        } else {
            mDialogDes.setText(String.format("扫码关注9358,购买%s", dialogTitle));
        }

        btnDismiss.setOnClickListener(v -> this.dismiss());
        mButtonShare.setOnClickListener(v -> {
            ShareController.doShare(shareThumbnail, shareUrl, shareTitle,
                    ShareDescription, type, actId);
            this.dismiss();
        });

        Bitmap bitmap = null;
        try {
            bitmap = encodeAsBitmap(1, shareUrl, Utils.dip2px(mContext, 160));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            dismiss();
        } else {
            mCode.setImageBitmap(bitmap);
        }

    }

    private Bitmap encodeAsBitmap(int logoResourceId, String contentString, int dimension) throws WriterException {
        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(contentString, BarcodeFormat.QR_CODE, dimension, dimension, hints);
        } catch (IllegalArgumentException iae) {
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        //TODO add logo to the bitmap
        return bitmap;
    }

}
