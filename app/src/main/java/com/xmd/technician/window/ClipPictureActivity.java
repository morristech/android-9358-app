package com.xmd.technician.window;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;


import com.xmd.technician.R;
import com.xmd.technician.widget.ClipView;

import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClipPictureActivity extends BaseActivity implements OnTouchListener {
    public static final String EXTRA_OUTPUT = "output";
    public static final String EXTRA_INPUT = "input";
    private ImageView srcPic;
    private ClipView clipview;

    // These matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final String TAG = "11";
    private int mode = NONE;

    // Remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private Bitmap mBitmap;
    private int titleBarHeight = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_clip_picture);

        ButterKnife.bind(this);

        setTitle(R.string.title_activity_clip_picture);
        setBackVisible(true);
        setRightVisible(true, R.string.save);

        srcPic = (ImageView) this.findViewById(R.id.src_pic);
        srcPic.setOnTouchListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            titleBarHeight = toolbar.getMinimumHeight();
        }else {
            titleBarHeight = (int) getResources().getDimension(R.dimen.home_button_height);
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels - titleBarHeight;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                Uri uri = Uri.parse(bundle.getString(EXTRA_INPUT));
                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                mBitmap = ThumbnailUtils.extractThumbnail(mBitmap, height / 2, height / 2);
                srcPic.setImageBitmap(mBitmap);
                center();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        srcPic.setImageMatrix(matrix);
    }

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
//                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
//                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
//                    Log.d(TAG, "mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
//                Log.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // ...
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
//                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true; // indicate event was handled
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    @OnClick(R.id.toolbar_right)
    public void cropPicture(){
        showProgressDialog("正在保存图片...");
        new Thread(cropTask).start();
    }

    private Runnable cropTask = new Runnable() {
        @Override
        public void run() {
            Bitmap bitmap = getBitmap();
            Intent intent = new Intent();
            intent.putExtra(EXTRA_OUTPUT, MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
            setResult(RESULT_OK, intent);
            dismissProgressDialogIfShowing();
            bitmap.recycle();
            finish();
        }
    };

    private Bitmap getBitmap() {
        Bitmap screenShoot = takeScreenShot();

        clipview = (ClipView) this.findViewById(R.id.clip_view);
        int width = clipview.getWidth();
        int height = clipview.getHeight();
        Bitmap finalBitmap = Bitmap.createBitmap(screenShoot,
                (width - height / 2) / 2, height / 4 + titleBarHeight, height / 2, height / 2);
        screenShoot.recycle();
        return finalBitmap;
    }

    private Bitmap takeScreenShot() {
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    protected void center() {

        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, titleBarHeight, mBitmap.getWidth(), mBitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        DisplayMetrics dm;
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        if (height < screenHeight) {
            deltaY = (screenHeight - height) / 2 - rect.top;
        } else if (rect.bottom > screenHeight) {
            deltaY = rect.top - (rect.bottom - screenHeight) / 2;
        }

        int screenWidth = dm.widthPixels;
        if (width < screenWidth) {
            deltaX = (screenWidth - width) / 2 - rect.left;
        } else if (rect.right > screenWidth) {
            deltaX = rect.left - (rect.right - screenWidth) / 2;
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }
}