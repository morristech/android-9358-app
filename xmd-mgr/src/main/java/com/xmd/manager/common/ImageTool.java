package com.xmd.manager.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by heyangya on 16-11-15.
 */

public class ImageTool {
    public static void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    public static void loadImage(Context context, String url, ImageView imageView, int placeHolder) {
        Glide.with(context).load(url).placeholder(placeHolder).into(imageView);
    }

    public static void loadCircleImage(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).transform(new CircleBitmapTransformation(context)).into(imageView);
    }

    public static void loadCircleImage(Context context, String url, ImageView imageView, int placeHolder) {
        Glide.with(context).load(url).transform(new CircleBitmapTransformation(context)).
                placeholder(placeHolder).into(imageView);
    }

    private static class CircleBitmapTransformation extends BitmapTransformation {
        public CircleBitmapTransformation(Context context) {
            super(context);
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            int size = Math.min(toTransform.getWidth(), toTransform.getHeight());

            Bitmap bitmap = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(toTransform,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            int w = (toTransform.getWidth() - size) / 2;
            int h = (toTransform.getHeight() - size) / 2;
            if (w != 0 || h != 0) {
                Matrix matrix = new Matrix();
                matrix.setTranslate(-w, -h);
                shader.setLocalMatrix(matrix);
            }
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return bitmap;
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }
}
