package com.example.administrator.greedyfish.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Administrator on 2016/11/16.
 */
public abstract class GameObject {

    protected Bitmap bitmap;        // 对象的图片
    protected float object_x; 		// 对象的横坐标
    protected float object_y;		// 对象的纵坐标
    protected Direction direction;  // 对象的运动方向
    protected int speed; 			// 对象的运动速度
    protected int object_width; 	// 对象的宽度
    protected int object_height; 	// 对象的高度
    protected int screen_width; 	// 屏幕的宽度
    protected int screen_height;    // 屏幕的高度
    protected float screen_scale;   // 占屏幕比例
    protected int score;            // 分数
    protected boolean isAlive;		// 判断是否存活
    protected Paint paint; 			// 画笔对象

    /*物体的运动方向有向左和向右两种*/
    public enum Direction{
        TO_LEFT,TO_RIGHT
    }

    /*物体运动的速度有快、中、慢三种*/
    public static byte SPEED_HIGHT = 20;
    public static byte SPEED_MIDDLE = 10;
    public static byte SPEED_LOW = 5;

/*    // 初始化数据,参数分别为:速度增加的倍数,x中心坐标,y中心坐标
    public void initial(int arg0,float arg1,float arg2){}*/

    // 初始化图片资源的
    public abstract GameObject initBitmap(Bitmap bitmap);


    // 对象的绘图方法
    public abstract GameObject draw(Canvas canvas);

    // 释放资源的方法
    public abstract void release();

    // 检测碰撞的方法
    public boolean isCollide(GameObject obj) {
        return true;
    }

    // 对象的逻辑方法
    public void logic(){}


    /*getter和setter方法*/

    public GameObject setScreenWH(int screen_width, int screen_height) {      // 设置屏幕宽度和高度
        this.screen_width = screen_width;
        this.screen_height = screen_height;
        return this;
    }
    public GameObject setDirection(Direction direction){
        this.direction = direction;
        return this;
    }
    public Direction getDirection(){
        return direction;
    }
    public int getSpeed(){
        return speed;
    }
    public GameObject setSpeed(int speed){
        this.speed = speed;
        return this;
    }
    public float getObject_x() {
        return object_x;
    }
    public GameObject setObject_x(float object_x) {
        this.object_x = object_x;
        return this;
    }
    public float getObject_y() {
        return object_y;
    }
    public GameObject setObject_y(float object_y) {
        this.object_y = object_y;
        return this;
    }
    public float getObject_width() {
        return object_width;
    }
    public GameObject setObject_width(int object_width) {
        this.object_width = object_width;
        return this;
    }
    public float getObject_height() {
        return object_height;
    }
    public GameObject setObject_height(int object_height) {
        this.object_height = object_height;
        return this;
    }
    public GameObject setScreen_scale(float scale){
        this.screen_scale = screen_scale;
        return this;
    }
    public boolean isAlive() {
        return isAlive;
    }
    public GameObject setAlive(boolean isAlive) {
        this.isAlive = isAlive;
        return this;
    }

    public int size(){return object_width*object_height;}
    public int getScore(){return score;}
}
