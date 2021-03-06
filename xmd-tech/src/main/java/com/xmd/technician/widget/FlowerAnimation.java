package com.xmd.technician.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import com.xmd.technician.R;
import com.xmd.technician.bean.Flower;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 撒花 用到的知识点： 1、android属性动画 2、Path路径绘制 3、贝塞尔曲线
 */
public class FlowerAnimation extends View implements AnimatorUpdateListener {

    /**
     * 动画播放的时间
     */
    private final int time = 1500;
    /**
     * 动画间隔
     */
    private final int delay = 200;
    int[] ylocations = {-100, -50, -25, 0};
    ObjectAnimator mAnimator1;
    ObjectAnimator mAnimator2;
    ObjectAnimator mAnimator3;
    ObjectAnimator mAnimator4;
    ObjectAnimator mAnimator5;
    /**
     * 动画改变的属性值
     */
    private float phase1 = 0f;
    private float phase2 = 0f;
    private float phase3 = 0f;
    private float phase4 = 0f;
    private float phase5 = 0f;
    /**
     * 小球集合
     */
    private List<Flower> flowers1 = new ArrayList<Flower>();
    private List<Flower> flowers2 = new ArrayList<Flower>();
    private List<Flower> flowers3 = new ArrayList<Flower>();
    private List<Flower> flowers4 = new ArrayList<Flower>();
    private List<Flower> flowers5 = new ArrayList<Flower>();
    /**
     * 宽度
     */
    private int width = 0;
    /**
     * 高度
     */
    private int height = 0;
    /**
     * 曲线高度个数分割
     */
    private int quadCount = 5;
    /**
     * 曲度
     */
    private float intensity = 0.2f;
    /**
     * 第一批个数
     */
    private int fllowerCount = 6;
    /**
     * 曲线摇摆的幅度
     */
    private int range = (int) TypedValue
            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources()
                    .getDisplayMetrics());
    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 测量路径的坐标位置
     */
    private PathMeasure pathMeasure = null;
    /**
     * 高度往上偏移量,把开始点移出屏幕顶部
     */
    private float dy = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            40, getResources().getDisplayMetrics());
    private String tag = this.getClass().getSimpleName();

    /**
     * 资源ID
     */

    public FlowerAnimation(Context context) {
        super(context);
        init(context);
        // this.resId = resId;
    }

    @SuppressWarnings("deprecation")
    private void init(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = (int) (wm.getDefaultDisplay().getHeight() * 3 / 2f);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        // mPaint.setStrokeWidth(2);
        // mPaint.setColor(Color.BLUE);
        // mPaint.setStyle(Style.STROKE);

        pathMeasure = new PathMeasure();

        builderFollower(fllowerCount, flowers1);
        builderFollower(fllowerCount, flowers2);
        builderFollower(fllowerCount, flowers3);
        builderFollower(fllowerCount, flowers4);
        builderFollower(fllowerCount, flowers5);


    }

    /**
     * 创建花
     */
    private void builderFollower(int count, List<Flower> flowers) {

        int max = (int) (width * 3 / 4f);
        int min = (int) (width / 4f);
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int s = random.nextInt(max) % (max - min + 1) + min;
            Path path = new Path();
            CPoint CPoint = new CPoint(s, ylocations[random.nextInt(3)]);
            List<CPoint> points = builderPath(CPoint);
            drawFllowerPath(path, points);
            Flower flower = new Flower();
            flower.setPath(path);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.icon_gold);
            Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),
                    R.drawable.icon_gold2);
            if (i / 2 == 0) {
                flower.setResId(bitmap);
            } else {
                flower.setResId(bitmap2);
            }

            flowers.add(flower);
        }

    }

    /**
     * 画曲线
     *
     * @param path
     * @param points
     */
    private void drawFllowerPath(Path path, List<CPoint> points) {
        if (points.size() > 1) {
            for (int j = 0; j < points.size(); j++) {

                CPoint point = points.get(j);

                if (j == 0) {
                    CPoint next = points.get(j + 1);
                    point.dx = ((next.x - point.x) * intensity);
                    point.dy = ((next.y - point.y) * intensity);
                } else if (j == points.size() - 1) {
                    CPoint prev = points.get(j - 1);
                    point.dx = ((point.x - prev.x) * intensity);
                    point.dy = ((point.y - prev.y) * intensity);
                } else {
                    CPoint next = points.get(j + 1);
                    CPoint prev = points.get(j - 1);
                    point.dx = ((next.x - prev.x) * intensity);
                    point.dy = ((next.y - prev.y) * intensity);
                }

                // create the cubic-spline path
                if (j == 0) {
                    path.moveTo(point.x, point.y);
                } else {
                    CPoint prev = points.get(j - 1);
                    path.cubicTo(prev.x + prev.dx, (prev.y + prev.dy), point.x
                            - point.dx, (point.y - point.dy), point.x, point.y);
                }
            }
        }
    }

    /**
     * 画路径
     *
     * @param point
     * @return
     */
    private List<CPoint> builderPath(CPoint point) {
        List<CPoint> points = new ArrayList<CPoint>();
        Random random = new Random();
        for (int i = 0; i < quadCount; i++) {
            if (i == 0) {
                points.add(point);
            } else {
                CPoint tmp = new CPoint(0, 0);
                if (random.nextInt(100) % 2 == 0) {
                    tmp.x = point.x + i * 10;
                    //   tmp.x = point.x;
                } else {
                    tmp.x = point.x - i * 10;
                    //  tmp.x = point.x ;
                }
                tmp.y = (int) (height / (float) quadCount * i);
                points.add(tmp);
            }
        }
        return points;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawFllower(canvas, flowers1);
        drawFllower(canvas, flowers2);
        drawFllower(canvas, flowers3);
        drawFllower(canvas, flowers4);
        drawFllower(canvas, flowers5);


    }

    /**
     * @param canvas
     * @param flowers
     */
    private void drawFllower(Canvas canvas, List<Flower> flowers) {
        for (Flower flower : flowers) {
            float[] pos = new float[2];
            // canvas.drawPath(flower.getPath(),mPaint);
            pathMeasure.setPath(flower.getPath(), false);
            pathMeasure.getPosTan(height * flower.getValue(), pos, null);
            // canvas.drawCircle(pos[0], pos[1], 10, mPaint);
            canvas.drawBitmap(flower.getResId(), pos[0], pos[1] - dy, null);
        }
    }

    public void startAnimation() {
        if (mAnimator1 != null && mAnimator1.isRunning()) {
            mAnimator1.cancel();
        }
        mAnimator1 = ObjectAnimator.ofFloat(this, "phase1", 0f, 1f);
        mAnimator1.setDuration(time);
        mAnimator1.addUpdateListener(this);

        mAnimator1.start();
        mAnimator1.setInterpolator(new AccelerateInterpolator(1f));

        if (mAnimator2 != null && mAnimator2.isRunning()) {
            mAnimator2.cancel();
        }
        mAnimator2 = ObjectAnimator.ofFloat(this, "phase2", 0f, 1f);
        mAnimator2.setDuration(time);
        mAnimator2.addUpdateListener(this);
        mAnimator2.start();
        mAnimator2.setInterpolator(new AccelerateInterpolator(1f));
        mAnimator2.setStartDelay(delay);

        if (mAnimator3 != null && mAnimator3.isRunning()) {
            mAnimator3.cancel();
        }
        mAnimator3 = ObjectAnimator.ofFloat(this, "phase3", 0f, 1f);
        mAnimator3.setDuration(time);
        mAnimator3.addUpdateListener(this);
        mAnimator3.start();
        mAnimator3.setInterpolator(new AccelerateInterpolator(1f));
        mAnimator3.setStartDelay(delay * 2);

        if (mAnimator4 != null && mAnimator4.isRunning()) {
            mAnimator4.cancel();
        }
        mAnimator4 = ObjectAnimator.ofFloat(this, "phase4", 0f, 1f);
        mAnimator4.setDuration(time);
        mAnimator4.addUpdateListener(this);
        mAnimator4.start();
        mAnimator4.setInterpolator(new AccelerateInterpolator(1f));
        mAnimator4.setStartDelay(delay * 3);

        if (mAnimator5 != null && mAnimator5.isRunning()) {
            mAnimator5.cancel();
        }
        mAnimator5 = ObjectAnimator.ofFloat(this, "phase5", 0f, 1f);
        mAnimator5.setDuration(time);
        mAnimator5.addUpdateListener(this);
        mAnimator5.start();
        mAnimator5.setInterpolator(new AccelerateInterpolator(1f));
        mAnimator5.setStartDelay(delay * 4);
    }

    /**
     * 跟新小球的位置
     *
     * @param value
     * @param flowers
     */
    private void updateValue(float value, List<Flower> flowers) {
        for (Flower flower : flowers) {
            flower.setValue(value);
        }
    }

    /**
     * 动画改变回调
     */
    @Override
    public void onAnimationUpdate(ValueAnimator arg0) {

        updateValue(getPhase1(), flowers1);
        updateValue(getPhase2(), flowers2);
        updateValue(getPhase3(), flowers3);
        updateValue(getPhase4(), flowers4);
        updateValue(getPhase5(), flowers5);
        Log.i(tag, getPhase1() + "");
        invalidate();
    }

    public float getPhase1() {
        return phase1;
    }

    public void setPhase1(float phase1) {
        this.phase1 = phase1;
    }

    public float getPhase2() {
        return phase2;
    }

    public void setPhase2(float phase2) {
        this.phase2 = phase2;
    }

    public float getPhase3() {
        return phase3;
    }

    public void setPhase3(float phase3) {
        this.phase3 = phase3;
    }

    public float getPhase4() {
        return phase4;
    }

    public void setPhase4(float phase4) {
        this.phase4 = phase4;
    }

    public float getPhase5() {
        return phase5;
    }

    public void setPhase5(float phase5) {
        this.phase5 = phase5;
    }

    private class CPoint {

        public float x = 0f;
        public float y = 0f;

        /**
         * x-axis distance
         */
        public float dx = 0f;

        /**
         * y-axis distance
         */
        public float dy = 0f;

        public CPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

}
