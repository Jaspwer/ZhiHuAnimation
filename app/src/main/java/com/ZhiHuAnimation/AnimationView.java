package com.ZhiHuAnimation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by ZZG on 2017/12/26.
 */

public class AnimationView extends View {

    private Paint paint;  //设置画笔
    private final int POINT = 50;
    private int width, height;

    private ArrayList<Circle> circles = new ArrayList<>();

    public AnimationView(Context context) {
        super(context);
        initPaint();
        init();
        startThread();
    }

    public AnimationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.colorAccent)); //设置画笔的颜色
        paint.setStrokeWidth(1.7f);//设置线宽。

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < POINT; i++) {
            drawCricle(canvas, circles.get(i));  //画圆
        }
        for (int i = 0; i < POINT; i++) {
            for (int j = 0; j < POINT; j++) {
                if (i + j < POINT) {
                    float A = Math.abs(circles.get(i + j).x - circles.get(i).x),
                            B = Math.abs(circles.get(i + j).y - circles.get(i).y);
                    //Math.abs()取绝对值。
                    float lineLength = (float) Math.sqrt(A * A + B * B);//sqrt()取平方根。
                    float C = (float) (1 / lineLength * 7 - 0.009);
                    float lineOpacity = C > 0.03 ? (float) 0.03 : C;
                    if (lineOpacity > 0) {
                        Line line = new Line(circles.get(i).x, circles.get(i).y, circles.get(i + j).x, circles.get(i + j).y, lineOpacity);
                        drawLine(canvas, line); //线
                    }
                }
            }
        }
    }


    class Circle {
        public float x, y, r, moveX, moveY;

        public Circle(float x, float y, float r, float moveX, float moveY) {
            this.x = x;
            this.y = y;
            this.r = r;
            this.moveX = moveX;
            this.moveY = moveY;
        }

        @Override
        public String toString() {
            return "Circle{" +
                    "x=" + x +
                    ", y=" + y +
                    ", r=" + r +
                    ", moveX=" + moveX +
                    ", moveY=" + moveY +
                    '}';
        }
    }

    class Line {
        public float beginX, beginY, closeX, closeY, lineOpacity;

        public Line(float x, float y, float _x, float _y, float lineOpacity) {
            this.beginX = x;
            this.beginY = y;
            this.closeX = _x;
            this.closeY = _y;
            this.lineOpacity = lineOpacity;
        }

        @Override
        public String toString() {
            return "Line{" +
                    "beginX=" + beginX +
                    ", beginY=" + beginY +
                    ", closeX=" + closeX +
                    ", closeY=" + closeY +
                    '}';
        }
    }


    //生成max和min之间的随机数
    private float num(float max, float _min) {
        float min = 0;
        min = _min;
        return (float) Math.floor(Math.random() * (max - min + 1) + min);
    }

    // 绘制圆点
    private void drawCricle(Canvas cxt, Circle circle) {
        paint.setAlpha((int) circle.r * 2);  //设置透明度
        cxt.drawCircle(circle.x, circle.y, circle.r, paint);
    }

    //绘制线条
    private void drawLine(Canvas cxt, Line line) {
        paint.setAlpha((int) (line.lineOpacity * 500));
        cxt.drawLine(line.beginX, line.beginY, line.closeX, line.closeY, paint);
    }

    //初始化生成 原点集合。
    private void init() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        for (int i = 0; i < POINT; i++) {
            circles.add(new Circle(num(width, 0), num(height, 0), num(15, 2), num(10, -10) / 40, num(10, -10) / 40));
        }
    }

    Handler handler = new Handler();

    private void startThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < POINT; i++) {
                    Circle cir = circles.get(i);
                    cir.x += cir.moveX;
                    cir.y += cir.moveY;
                    if (cir.x > width) cir.x = 0;
                    else if (cir.x < 0) cir.x = width;
                    if (cir.y > height) cir.y = 0;
                    else if (cir.y < 0) cir.y = height;
                }
                invalidate();//请求重绘View树，即draw()过程
                handler.postDelayed(this, 5);
            }
        };
        handler.postDelayed(runnable, 0);
    }

}
