package com.example.administrator.greedyfish.surface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.administrator.greedyfish.MainActivity;
import com.example.administrator.greedyfish.R;
import com.example.administrator.greedyfish.constant.ConstantUtil;

/**
 * Created by Administrator on 2016/11/16.
 */
public class MenuSurface extends SurfaceView implements SurfaceHolder.Callback{
    private float scalex;					// 背景图片的缩放比例
    private float scaley;
    private float screen_width;			    // 视图的宽度
    private float screen_height;			// 视图的高度
    private Paint paint; 					// 画笔对象
    private Canvas canvas; 				    // 画布对象
    private SurfaceHolder sfh;
    private float text_x;
    private float text_y;
    private float button_x;
    private float button_y;
    private float button_y2;
    private float strwid;
    private float strhei;
    private boolean isBtChange;				// 按钮图片改变的标记
    private boolean isBtChange2;
    private String startGame = "开始游戏";	// 按钮的文字
    private String exitGame = "退出游戏";
    private Bitmap text;					// 文字图片
    private Bitmap button;					// 按钮图片
    private Bitmap button2;					// 按钮图片
    private Bitmap background;				// 背景图片
    private Rect rect;						// 绘制文字的区域
    private MainActivity mainActivity;

    public MenuSurface(Context context) {
        super(context);
        mainActivity = (MainActivity) context;
        paint = new Paint();
        paint.setTextSize(40);
        rect = new Rect();
        sfh = getHolder();
        sfh.addCallback(this);
    }
    // 视图改变的方法
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        screen_width = arg2;
        screen_height = arg3;
        initBitmap();
        drawSelf();
    }
    // 视图创建的方法
    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
    }

    // 视图销毁的方法
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        release();
    }

    // 响应触屏事件的方法
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && event.getPointerCount() == 1) {
            float x = event.getX();
            float y = event.getY();
            //判断第一个按钮是否被按下
            if (x > button_x && x < button_x + button.getWidth()
                    && y > button_y && y < button_y + button.getHeight()) {
                isBtChange = true;
                drawSelf();
                mainActivity.getHandler().sendEmptyMessage(ConstantUtil.TO_PLAY_SURFACE);
            }
            //判断第二个按钮是否被按下
            else if (x > button_x && x < button_x + button.getWidth()
                    && y > button_y2 && y < button_y2 + button.getHeight()) {
                isBtChange2 = true;
                drawSelf();
                mainActivity.getHandler().sendEmptyMessage(ConstantUtil.END_GAME);
            }
            return true;
        }
        //响应屏幕单点移动的消息
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            float y = event.getY();
            if (x > button_x && x < button_x + button.getWidth()
                    && y > button_y && y < button_y + button.getHeight()) {
                isBtChange = true;
            } else {
                isBtChange = false;
            }
            if (x > button_x && x < button_x + button.getWidth()
                    && y > button_y2 && y < button_y2 + button.getHeight()) {
                isBtChange2 = true;
            } else {
                isBtChange2 = false;
            }
            return true;
        }
        //响应手指离开屏幕的消息
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            isBtChange = false;
            isBtChange2 = false;
            return true;
        }
        return false;
    }
    // 初始化图片资源方法
    public void initBitmap() {
        // TODO Auto-generated method stub
        background = BitmapFactory.decodeResource(getResources(), R.drawable.bg_01);
        text = BitmapFactory.decodeResource(getResources(), R.drawable.game_name);
        button = BitmapFactory.decodeResource(getResources(), R.drawable.button);
        button2 = BitmapFactory.decodeResource(getResources(),R.drawable.button2);
        scalex = screen_width / background.getWidth();
        scaley = screen_height / background.getHeight();
        text_x = screen_width / 2 - text.getWidth() / 2;
        text_y = screen_height / 2 - text.getHeight();
        button_x = screen_width / 2 - button.getWidth() / 2;
        button_y = screen_height / 2 + button.getHeight();
        button_y2 = button_y + button.getHeight() + 40;
        // 返回包围整个字符串的最小的一个Rect区域
        paint.getTextBounds(startGame, 0, startGame.length(), rect);
        strwid = rect.width();
        strhei = rect.height();
    }
    // 释放图片资源的方法
    public void release() {
        // TODO Auto-generated method stub
        if (!text.isRecycled()) {
            text.recycle();
        }
        if (!button.isRecycled()) {
            button.recycle();
        }
        if (!button2.isRecycled()) {
            button2.recycle();
        }
        if (!background.isRecycled()) {
            background.recycle();
        }
    }
    // 绘图方法
    public void drawSelf() {
        // TODO Auto-generated method stub
        try {
            canvas = sfh.lockCanvas();
            canvas.drawColor(Color.BLACK); 						// 绘制背景色
            canvas.save();
            canvas.scale(scalex, scaley, 0, 0);					// 计算背景图片与屏幕的比例
            canvas.drawBitmap(background, 0, 0, paint); 		// 绘制背景图
            canvas.restore();
            canvas.drawBitmap(text, text_x, text_y, paint);		// 绘制文字图片
            //当手指滑过按钮时变换图片
            if (isBtChange) {
                canvas.drawBitmap(button2, button_x, button_y, paint);
            }
            else {
                canvas.drawBitmap(button, button_x, button_y, paint);
            }
            if (isBtChange2) {
                canvas.drawBitmap(button2, button_x, button_y2, paint);
            }
            else {
                canvas.drawBitmap(button, button_x, button_y2, paint);
            }
            //开始游戏的按钮
            canvas.drawText(startGame, screen_width / 2 - strwid / 2, button_y
                    + button.getHeight() / 2 + strhei / 2, paint);
            //退出游戏的按钮
            canvas.drawText(exitGame, screen_width / 2 - strwid / 2, button_y2
                    + button.getHeight() / 2 + strhei / 2, paint);

        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            if (canvas != null)
                sfh.unlockCanvasAndPost(canvas);
        }
    }
}
