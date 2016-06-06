package com.xmd.technician.widget;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.xmd.technician.R;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by heyangya on 15-6-5.
 */
public class QRDialog extends Dialog {
    private String mShowText;
    private ImageView mQRImageView;
    private Bitmap mQRBitmap;
    private boolean mShowAsUrl = true;
    public QRDialog(Context context, String showText, boolean showAsUrl) {
        super(context, R.style.dialog_qr);
        mShowText=showText;
        mShowAsUrl = showAsUrl;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_qr_code);

        ImageView closeView=(ImageView)findViewById(R.id.qr_dialog_close);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        setCancelable(true);
        mQRImageView=(ImageView)findViewById(R.id.home_fragment_qr_code_image);
        if(mShowAsUrl){
            Glide.with(getContext()).load(mShowText).into(mQRImageView);
        }else {
            mQRImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if(mQRBitmap==null) {
                        try {
                            mQRBitmap=encodeAsBitmap(0, mShowText, mQRImageView.getWidth());
                            mQRImageView.setImageBitmap(mQRBitmap);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
            });
        }
    }

    public void updateQR(String txt){
        if(!mShowText.equals(txt)){
            if(mShowAsUrl){
                Glide.with(getContext()).load(mShowText).into(mQRImageView);
                mShowText = txt;
            }else {
                try {
                    mQRBitmap=encodeAsBitmap(0, txt, mQRImageView.getWidth());
                    mQRImageView.setImageBitmap(mQRBitmap);
                    mShowText = txt;
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private Bitmap encodeAsBitmap(int logoResourceId,String contentString,int dimension) throws WriterException {
        Map<EncodeHintType,Object> hints = new EnumMap<EncodeHintType,Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(contentString, BarcodeFormat.QR_CODE, dimension, dimension, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
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
