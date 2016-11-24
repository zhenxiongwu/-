package com.example.administrator.greedyfish.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.example.administrator.greedyfish.rocker.Rocker;

/**
 * Created by Administrator on 2016/11/17.
 */
public class Myfish extends GameObject {
    private Bitmap bitmapL;             //向左图片
    private Bitmap bitmapR;             //向右图片
    private int growScore;              //成长分


    public Myfish(int screen_width, int screen_height, Direction direction){
        this.screen_width = screen_width;
        this.screen_height = screen_height;
        object_x = screen_width/2;
        object_y = screen_height/2;
        this.direction = direction;
        speed = GameObject.SPEED_MIDDLE;
        score = 0;
        growScore = 0;

        /*暂时是根据所使用的图片的大小来设定我的鱼的大小，
        没使用到按屏幕比例来设定我的鱼的大小*/
/*        screen_scale = 0.01f;
        object_width = (int)(0.01*screen_width);
        object_height = (int)(0.01*screen_height);*/
        paint = new Paint();
    }

    /*初始化我的鱼的图片，并指明图片的方向*/
/*    @Override
    public Myfish initBitmap(Resources resources,int bitmapId,Direction direction){
        Bitmap bm = BitmapFactory.decodeResource(resources,bitmapId);
        this.direction = direction;
        object_width = bm.getWidth();
        object_height = bm.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(-1,1);
        if(direction == Direction.TO_LEFT){
            bitmapL = bm;
            bitmapR = Bitmap.createBitmap(bm,0,0,object_width,object_height,matrix,true);
        }
        else if(direction == Direction.TO_RIGHT){
            bitmapR = bm;
            bitmapL = Bitmap.createBitmap(bm,0,0,object_width,object_height,matrix,true);
        }
        return this;
    }*/


    @Override
    public GameObject initBitmap(Bitmap bitmap) {
        if(bitmap == null)return this;
        Log.i("Tag","长宽："+bitmap.getWidth()+ "  "+ bitmap.getHeight());
        object_width = bitmap.getWidth();
        object_height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(-1,1);
        if(direction == Direction.TO_LEFT){
            bitmapL = bitmap;
            bitmapR = Bitmap.createBitmap(bitmap,0,0,object_width,object_height,matrix,true);
        }
        else if(direction == Direction.TO_RIGHT){
            bitmapR = bitmap;
            bitmapL = Bitmap.createBitmap(bitmap,0,0,object_width,object_height,matrix,true);
        }
        return this;

    }

    @Override
    public GameObject draw(Canvas canvas) {
        if(bitmapL==null||bitmapR==null)return this;
        if(direction == Direction.TO_LEFT)
            canvas.drawBitmap(bitmapL,object_x,object_y,paint);
        else
            canvas.drawBitmap(bitmapR,object_x,object_y,paint);
        return this;
    }

    /*释放图片资源*/
    @Override
    public void release() {
        bitmapL.recycle();
        bitmapR.recycle();
    }

    /*更新坐标*/
    public Myfish updateLocation(Rocker rocker){
        double degree = rocker.getDegreesByNormalSystem();
        if(rocker.ifWorking()&&!Double.isNaN(degree)){
//            degrees  = view.rocker.degreesByNormalSystem;
            double rad = rocker.getRad();
            object_x = (float) (object_x + speed * Math.cos(rad ));
            object_y = (float) (object_y + speed * Math.sin(rad ));
            checkBorder();

            if(degree>90&&degree<270){
                direction = Direction.TO_RIGHT;
            }else{
                direction = Direction.TO_LEFT;
            }
        }

        return this;
    }

    /*限定我的鱼的显示边界*/
    private void checkBorder() {
        if(object_x<0){
            object_x = 0;
        }
        if(object_x>=screen_width-object_width){
            object_x=screen_width-object_width;
        }
        if(object_y<0){
            object_y = 0;
        }
        if(object_y>=screen_height-object_height){
            object_y=screen_height-object_height;
        }
    }

    public void grow(int score){
        this.score += score;
        growScore += score;
        if(growScore >= 10){                            //每当成长分大于等于10，我的鱼成长
            Matrix matrix = new Matrix();
            matrix.postScale(1.02f,1.02f);              //我的鱼的图片放大比例
            bitmapL = Bitmap.createBitmap(bitmapL,0,0,object_width,object_height,matrix,true);
            bitmapR = Bitmap.createBitmap(bitmapR,0,0,object_width,object_height,matrix,true);
            object_width = bitmapL.getWidth();
            object_height = bitmapL.getHeight();
            growScore = growScore % 10;                 //成长分模10循环

        }
    }
}
