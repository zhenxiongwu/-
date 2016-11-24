package com.example.administrator.greedyfish.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Administrator on 2016/11/17.
 */
public class Enemyfish extends GameObject {

    public enum Type{
        BIG_FISH,MIDDLE_FISH,SMALL_FISH
    }

    public static final float BIG_SCALE = 0.2f;       //大鱼图片占屏幕比例
    public static final float MIDDLE_SCALE = 0.1f;    //中鱼图片占屏幕比例
    public static final float SMALL_SCALE = 0.05f;     //小鱼图片占屏幕比例

    public Enemyfish(Type stype, int screen_width, int screen_height,
                     Direction direction, float launchY, byte speed){
        isAlive = true;
        this.screen_width = screen_width;
        this.screen_height = screen_height;
        if(stype == Type.BIG_FISH){
            screen_scale =BIG_SCALE;
            score = 5;
        }
        else if(stype == Type.MIDDLE_FISH){
            screen_scale = MIDDLE_SCALE;
            score = 2;
        }
        else {
            screen_scale = SMALL_SCALE;
            score = 1;
        }
        object_width = (int)(screen_width * screen_scale);
        object_height = (int)(screen_height * screen_scale);
        this.direction = direction;
        if(direction == Direction.TO_LEFT){
            object_x = screen_width;
        }else if(direction == Direction.TO_RIGHT){
            object_x = -object_width;
        }
        object_y = launchY;
        this.speed = speed;
        paint = new Paint();
    }


    @Override
    public GameObject initBitmap(Bitmap bitmap) {
        if(bitmap == null)return this;
        this.bitmap = bitmap;
        return this;
    }

    @Override
    public Enemyfish draw(Canvas canvas){
        if(bitmap == null) return this;
        updateLocation();
        canvas.drawBitmap(bitmap,object_x,object_y,paint);
        return this;
    }

    @Override
    public void release() {

    }

    /*更新敌鱼坐标*/
    public Enemyfish updateLocation(){
        if(direction == Direction.TO_LEFT){         //向左运动
            object_x -= speed;
        }else if(direction == Direction.TO_RIGHT){  //向右运动
            object_x += speed;
        }
        if(isOutOfScreen())isAlive = false;         //当敌鱼游出屏幕范围，标为死亡
        return this;
    }

    /*判断敌鱼是否游出屏幕范围*/
    protected boolean isOutOfScreen(){
        if(direction == Direction.TO_LEFT){
            if(object_x<=-object_width) return true;
        }else if(direction == Direction.TO_RIGHT){
            if(object_x>=screen_width) return true;
        }
        return false;
    }

    // 检测碰撞的方法
    @Override
    public boolean isCollide(GameObject obj) {
        if(obj.getObject_x()>object_x+object_width)                 //物体obj在this的右边
            return false;
        else if(obj.getObject_x()+obj.getObject_width()<object_x)   //物体obj在this的左边
            return false;
        else if(obj.getObject_y()>object_y+object_height)           //物体obj在this的下方
            return false;
        else if(obj.getObject_y()+obj.getObject_height()<object_y)  //物体obj在this的上方
            return false;
        return true;
    }
}
