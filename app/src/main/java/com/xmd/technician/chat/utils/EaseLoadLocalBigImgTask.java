package com.xmd.technician.chat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.hyphenate.util.ImageUtils;
import com.xmd.technician.R;
import com.xmd.technician.chat.model.EaseImageCache;
import com.xmd.technician.widget.scaleview.ScaleView;

/**
 * Created by Lhj  on 17-4-5.
 */

public class EaseLoadLocalBigImgTask extends AsyncTask<Void, Void, Bitmap> {

    private ProgressBar pb;
    private ScaleView photoView;
    private String path;
    private int width;
    private int height;
    private Context context;

    public EaseLoadLocalBigImgTask(Context context,String path, ScaleView photoView,
                                   ProgressBar pb, int width, int height) {
        this.context = context;
        this.path = path;
        this.photoView = photoView;
        this.pb = pb;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        int degree = ImageUtils.readPictureDegree(path);
        if (degree != 0) {
            pb.setVisibility(View.VISIBLE);
            photoView.setVisibility(View.INVISIBLE);
        } else {
            pb.setVisibility(View.INVISIBLE);
            photoView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = ImageUtils.decodeScaleImage(path, width, height);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        pb.setVisibility(View.INVISIBLE);
        photoView.setVisibility(View.VISIBLE);
        if (result != null)
            EaseImageCache.getInstance().put(path, result);
        else
            result = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.icon08);
        photoView.setImageBitmap(result);
    }
}

