package com.example.administrator.greedyfish.surface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.administrator.greedyfish.MainActivity;
import com.example.administrator.greedyfish.R;
import com.example.administrator.greedyfish.constant.ConstantUtil;
import com.example.administrator.greedyfish.object.Enemyfish;
import com.example.administrator.greedyfish.object.GameObject;
import com.example.administrator.greedyfish.object.Myfish;
import com.example.administrator.greedyfish.rocker.Rocker;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Administrator on 2016/11/16.
 */
public class PlaySurface extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private SurfaceHolder surfaceHolder;
    private int screen_width, screen_height;
    private Thread thread;              //线程
    private boolean thread_flag;        //线程运行的标记
    private Canvas canvas;              //画布
    private Paint paint;                //画笔

    private Rocker rocker;              //操纵杆
    private Myfish myfish;              //我的鱼
    private ArrayList<Enemyfish> enemyfishs; //敌鱼队列

    private Bitmap bitmap_background;   //背景图片
    private Bitmap bitmap_myfish;       //我的鱼图片
    private Bitmap bitmap_bigfish_L;    //大、中、小敌鱼向左、向右图片
    private Bitmap bitmap_bigfish_R;
    private Bitmap bitmap_middlefish_L;
    private Bitmap bitmap_middlefish_R;
    private Bitmap bitmap_smallfish_L;
    private Bitmap bitmap_smallfish_R;

    private boolean isGameOver;             //游戏结束标记
    private String text_gameover = "游 戏 结 束";
    private String text_score = "您的得分：";
    private String text_button_replay = "重玩";
    private String text_button_menu = "返回菜单";

    private Bitmap button;					// 按钮图片
    private Bitmap button2;					// 按钮图片

    private float button_x;                 //按钮位置
    private float button_y;
    private float button_y2;
    private boolean isBtChange;				// 按钮被按下的标记
    private boolean isBtChange2;

    private MainActivity mainActivity;


    public PlaySurface(Context context) {
        super(context);
        mainActivity = (MainActivity)context;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        paint = new Paint();
        bitmap_background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        bitmap_myfish = BitmapFactory.decodeResource(getResources(),R.drawable.myfish);

        button = BitmapFactory.decodeResource(getResources(), R.drawable.button);
        button2 = BitmapFactory.decodeResource(getResources(),R.drawable.button2);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        screen_width = width;
        screen_height = height;

        rocker = new Rocker(width,height);      //创建操纵杆对象

        myfish = new Myfish(width,height, GameObject.Direction.TO_LEFT);
        myfish.initBitmap(bitmap_myfish);       //初始化我的鱼的图片

        enemyfishs = new ArrayList<>();         //创建敌鱼队列

        /*初始化敌鱼图片*/
        initEnemyfishBitmap(width , height);

        /*结束界面中按钮的位置*/
        button_x = screen_width / 2 - button.getWidth() / 2;
        button_y = screen_height / 2;
        button_y2 = button_y + button.getHeight() + 20;

        thread = new Thread(this);
        thread_flag = true;
        isGameOver = false;
        thread.start();
    }

    /*初始化大、中、小敌鱼分别向左和向右的图片，图片按照规定比例伸缩*/
    private void initEnemyfishBitmap(int screen_width, int screen_height){
        Canvas canvas = new Canvas();
        Matrix matrix = new Matrix();
        matrix.setScale(-1,1);                          //矩阵系数，将图片左右对调

        /*限定大中小三种鱼的图片的宽和高*/
        int bigfish_width = (int)(Enemyfish.BIG_SCALE * screen_width);
        int bigfish_height = (int)(Enemyfish.BIG_SCALE * screen_height);
        int middlefish_width = (int)(Enemyfish.MIDDLE_SCALE * screen_width);
        int middlefish_height = (int)(Enemyfish.MIDDLE_SCALE * screen_height);
        int smallfish_width = (int)(Enemyfish.SMALL_SCALE * screen_width);
        int smallfish_height = (int)(Enemyfish.SMALL_SCALE * screen_height);

        /*初始化大鱼图片*/
        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.fish_big);
        /*按屏幕比例创建图片*/
        bitmap_bigfish_L = Bitmap.createBitmap(bigfish_width,bigfish_height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap_bigfish_L);
        /*填充资源图片*/
        canvas.drawBitmap(bm , new Rect(0,0,bm.getWidth(),bm.getHeight()),
                new Rect(0,0,bigfish_width,bigfish_height),paint);
        /*将向左图片反转得到向右图片*/
        bitmap_bigfish_R = Bitmap.createBitmap(bitmap_bigfish_L,0,0,bigfish_width,bigfish_height,matrix,true);
        bm.recycle();

        /*初始化中鱼图片*/
        bm = BitmapFactory.decodeResource(getResources(),R.drawable.fish_middle);
        bitmap_middlefish_L = Bitmap.createBitmap(middlefish_width,middlefish_height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap_middlefish_L);
        canvas.drawBitmap(bm , new Rect(0,0,bm.getWidth(),bm.getHeight()),
                new Rect(0,0,middlefish_width,middlefish_height),paint);
        bitmap_middlefish_R = Bitmap.createBitmap(bitmap_middlefish_L,0,0,
                middlefish_width,middlefish_height,matrix,true);
        bm.recycle();

        /*初始化小鱼图片*/
        bm = BitmapFactory.decodeResource(getResources(),R.drawable.fish_small);
        bitmap_smallfish_L = Bitmap.createBitmap(smallfish_width,smallfish_height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap_smallfish_L);
        canvas.drawBitmap(bm , new Rect(0,0,bm.getWidth(),bm.getHeight()),
                new Rect(0,0,smallfish_width,smallfish_height),paint);
        bitmap_smallfish_R = Bitmap.createBitmap(bitmap_smallfish_L,0,0,
                smallfish_width,smallfish_height,matrix,true);
        bm.recycle();
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread_flag = false;
        bitmap_background.recycle();
        myfish.release();
        bitmap_smallfish_L.recycle();
        bitmap_smallfish_R.recycle();
        bitmap_middlefish_L.recycle();
        bitmap_middlefish_R.recycle();
        bitmap_bigfish_L.recycle();
        bitmap_bigfish_R.recycle();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isGameOver) {                                         //当游戏结束，监听按钮的触碰
            if (event.getAction() == MotionEvent.ACTION_DOWN
                    && event.getPointerCount() == 1) {
                float x = event.getX();
                float y = event.getY();
                //判断第一个按钮是否被按下
                if (x > button_x && x < button_x + button.getWidth()
                        && y > button_y && y < button_y + button.getHeight()) {
                    isBtChange = true;
                    drawTheEnd();
                    mainActivity.getHandler().sendEmptyMessage(ConstantUtil.TO_PLAY_SURFACE);
                }
                //判断第二个按钮是否被按下
                else if (x > button_x && x < button_x + button.getWidth()
                        && y > button_y2 && y < button_y2 + button.getHeight()) {
                    isBtChange2 = true;
                    drawTheEnd();
                    mainActivity.getHandler().sendEmptyMessage(ConstantUtil.TO_MENU_SURFACE);
                }
            }
        }else {                                                 //当游戏进行中，监听操作杆的触碰
            if (event.getAction() == MotionEvent.ACTION_UP) {
                rocker.reset();                                  //当用户手指抬起，应该恢复小圆到初始位置
            } else {
                int pointX = (int) event.getX();
                int pointY = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    rocker.begin(pointX, pointY);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    rocker.update(pointX, pointY);
                }
            }
        }
        return true;
    }

    private void draw(){
        canvas = surfaceHolder.lockCanvas();        //锁定画布

        /*绘制背景*/
        canvas.drawBitmap(bitmap_background,
                new Rect(0,0,bitmap_background.getWidth(),bitmap_background.getHeight()),
                new Rect(0,0,screen_width,screen_height),paint);

        /*绘制敌鱼*/
        for(Enemyfish enemyfish:enemyfishs){
            enemyfish.draw(canvas);
        }

        /*绘制我的鱼*/
        myfish.draw(canvas);

        /*绘制操纵杆*/
        rocker.draw(canvas);

        surfaceHolder.unlockCanvasAndPost(canvas);  //解锁画布
    }

    /*绘制结束界面*/
    private void drawTheEnd(){
        canvas = surfaceHolder.lockCanvas();        //锁定画布
        paint.setColor(Color.GRAY);
        canvas.drawRect(screen_width/4,screen_height/4,screen_width/4*3,screen_height/4*3,paint);
        paint.setTextSize(100);
        paint.setColor(Color.RED);
        Rect rect = new Rect();
        paint.getTextBounds(text_gameover, 0, text_gameover.length(), rect);
        canvas.drawText(text_gameover,screen_width/2-rect.width()/2,screen_height/16*6-rect.height()/2,paint);
        paint.setTextSize(80);
        paint.setColor(Color.YELLOW);
        String text = text_score + myfish.getScore();
        paint.getTextBounds(text,0,text.length(),rect);
        canvas.drawText(text,screen_width/2-rect.width()/2,screen_height/2-rect.height()/2,paint);

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
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        //重玩的按钮
        paint.getTextBounds(text_button_replay, 0, text_button_replay.length(), rect);
        canvas.drawText(text_button_replay, screen_width / 2 - rect.width() / 2, button_y
                + button.getHeight() / 2 + rect.height() / 2, paint);
        //返回菜单的按钮
        paint.getTextBounds(text_button_menu, 0, text_button_menu.length(), rect);
        canvas.drawText(text_button_menu, screen_width / 2 - rect.width() / 2, button_y2
                + button.getHeight() / 2 + rect.height() / 2, paint);

        surfaceHolder.unlockCanvasAndPost(canvas);  //解锁画布
    }

    private void fishsFactory(){
        Random random = new Random();
        Enemyfish.Type Type;                    //新添敌鱼类型
        GameObject.Direction dir;               //新添敌鱼运动方向
        byte fishSpeed;                         //新添敌鱼的运动速度
        int fishY;                              //新添敌鱼起点Y坐标
        int add = random.nextInt(4);            //新添敌鱼数
        for(int i = 0 ; i<add ; i++){

            /*新添敌鱼的类型*/
            int fishType = random.nextInt(10);
            if(fishType<6){
                Type = Enemyfish.Type.SMALL_FISH;
                fishY = random.nextInt((int)(screen_height*(1-Enemyfish.SMALL_SCALE)));
            }
            else if(fishType<9){
                Type = Enemyfish.Type.MIDDLE_FISH;
                fishY = random.nextInt((int)(screen_height*(1-Enemyfish.MIDDLE_SCALE)));
            }
            else {
                Type = Enemyfish.Type.BIG_FISH;
                fishY = random.nextInt((int)(screen_height*(1-Enemyfish.BIG_SCALE)));
            }

            /*新添敌鱼的方向*/
            int fishDirection = random.nextInt(2);
            if(fishDirection == 0)dir = GameObject.Direction.TO_LEFT;
            else dir = GameObject.Direction.TO_RIGHT;

            /*新添敌鱼的速度*/
            int speed = random.nextInt(3);
            if(speed == 0)fishSpeed = GameObject.SPEED_LOW;
            else if(speed == 1)fishSpeed = GameObject.SPEED_MIDDLE;
            else fishSpeed = GameObject.SPEED_HIGHT;

            Enemyfish enemyfish = new Enemyfish(Type,screen_width,screen_height,dir,fishY,fishSpeed);
            if(Type == Enemyfish.Type.SMALL_FISH ) {
                if (dir == GameObject.Direction.TO_LEFT)
                    enemyfish.initBitmap(bitmap_smallfish_L);
                else
                    enemyfish.initBitmap(bitmap_smallfish_R);
            }else if(Type == Enemyfish.Type.MIDDLE_FISH) {
                if (dir == GameObject.Direction.TO_LEFT)
                    enemyfish.initBitmap(bitmap_middlefish_L);
                else
                    enemyfish.initBitmap(bitmap_middlefish_R);
            }else {
                if (dir == GameObject.Direction.TO_LEFT)
                    enemyfish.initBitmap(bitmap_bigfish_L);
                else
                    enemyfish.initBitmap(bitmap_bigfish_R);
            }

            enemyfishs.add(enemyfish);
        }
    }

    /*碰撞检测*/
    private void checkCollide(){
        for(Enemyfish enemyfish:enemyfishs){
            if(enemyfish.isCollide(myfish)){
                if(myfish.size()>enemyfish.size()){
                    myfish.grow(enemyfish.getScore());
                    enemyfish.setAlive(false);              //比我小的鱼死亡
                }else{
                    thread_flag = false;                    //结束线程死循环
                    isGameOver = true;
                    break;
                }
            }
        }
    }

    //将已死亡的鱼从队列中移出
    private void removeDeath(){
        if(enemyfishs.size()!=0) {
            //克隆鱼对象的队列
            ArrayList<Enemyfish> enemyfishs1 = (ArrayList<Enemyfish>) enemyfishs.clone();
            for(Enemyfish enemyfish:enemyfishs1){           //遍历克隆队列
                if(!enemyfish.isAlive())
                    enemyfishs.remove(enemyfish);           //将死亡的鱼对象从原队列移出
            }
        }
    }

    @Override
    public void run() {
        int counter = 50;
        while (thread_flag){
            if(counter++ == 50){                //每50次循环添加新的鱼
                fishsFactory();
                counter = 0;
            }
            removeDeath();
            myfish.updateLocation(rocker);      //绘画前更新我的鱼的坐标
            draw();                             //绘画
            checkCollide();                     //检测碰撞
        }
        drawTheEnd();                           //绘画结束画面
    }
}
