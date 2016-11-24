package com.example.administrator.greedyfish.rocker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Administrator on 2016/11/16.
 */
public class Rocker {
    double degreesByNormalSystem = Double.NaN;//一般坐标系下的角度
    double rad = Double.NaN;//当前遥感的弧度
    int rockerColor = Color.GRAY;
    //定义两个圆形的中心点坐标与半径
    private static final float rockerCenterXMarginRight4ScreenWidthPercent = 0.01f;//摇杆右边界宽度相对于屏幕宽度百分比
    private static final float rockerCenterYMarginBottom4ScreenHeightPercent = 0.01f;//摇杆右边界高度相对于屏幕百分比
    private static final float rockerR4ScreenWidthPercent = 0.08f;
    private float smallCenterX, smallCenterY, smallCenterR;
    private float BigCenterX, BigCenterY, BigCenterR;
    Paint paint;

    private boolean WORKING = false;        //操纵杆的工作状态

		/*
		 * 因为要考虑适应屏幕，所以需要将屏幕宽高放进来
		 * */
    public Rocker(int screenWidth,int screenHeight) {
        super();
        paint = new Paint();
        paint.setAntiAlias(true);
        BigCenterR = screenWidth*rockerR4ScreenWidthPercent;
        BigCenterX = smallCenterX = screenWidth - BigCenterR*1.5f - rockerCenterXMarginRight4ScreenWidthPercent*screenWidth;
        BigCenterY = smallCenterY = screenHeight - BigCenterR*1.5f - rockerCenterYMarginBottom4ScreenHeightPercent*screenHeight;
        smallCenterR = BigCenterR/2;
    }

    public void draw(Canvas canvas){
        paint.setColor(rockerColor);
        paint.setAlpha(0x40);
        canvas.drawCircle(BigCenterX, BigCenterY, BigCenterR, paint);
        canvas.drawCircle(smallCenterX, smallCenterY, smallCenterR, paint);
/*        paint.setColor(Color.BLACK);
        canvas.drawText("原点在左上坐标系下的弧度:"+rad, 20, 20, paint);
        canvas.drawText("由该弧度计算得出角度:"+(rad*180/Math.PI), 20, 40, paint);
        canvas.drawText("原点在左下坐标系角度:"+degreesByNormalSystem, 20, 60, paint);*/
    }


    private float getDegrees(float firstX,float firstY,float secondX,float secondY ){
        float ret = (float)Math.atan((firstY-secondY)/(firstX-secondX))*180/(float)Math.PI;
        if(firstX<secondX){
            ret += 180;
        }else {
            ret += 360;
        }
        ret = ret >= 360 ? ret - 360: ret;
        return ret;
    }

    /**
     * 小圆针对于大圆做圆周运动时，设置小圆中心点的坐标位置
     * @param centerX
     *            围绕的圆形(大圆)中心点X坐标
     * @param centerY
     *            围绕的圆形(大圆)中心点Y坐标
     * @param R
     * 			     围绕的圆形(大圆)半径
     * @param rad
     *            旋转的弧度
     */
    public void setSmallCircleXY(float centerX, float centerY, float R, double rad) {
        //获取圆周运动的X坐标
        smallCenterX = (float) (R * Math.cos(rad)) + centerX;
        //获取圆周运动的Y坐标
        smallCenterY = (float) (R * Math.sin(rad)) + centerY;
    }

    /**
     * 得到两点之间的弧度
     * @param px1    第一个点的X坐标
     * @param py1    第一个点的Y坐标
     * @param px2    第二个点的X坐标
     * @param py2    第二个点的Y坐标
     * @return
     */
    public double getRad(float px1, float py1, float px2, float py2) {
        //得到两点X的距离
        float dx = px2 - px1;
        //得到两点Y的距离
        float dy = py1 - py2;
        //算出斜边长
        float Hypotenuse = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        //得到这个角度的余弦值（通过三角函数中的定理 ：邻边/斜边=角度余弦值）
        float cosAngle = dx / Hypotenuse;
        //通过反余弦定理获取到其角度的弧度
        float rad = (float) Math.acos(cosAngle);
        //当触屏的位置Y坐标<摇杆的Y坐标我们要取反值-0~-180  ？？？？
        if (py2 < py1) {
            rad = -rad;
        }
        return rad;
    }


    public void reset() {
        smallCenterX = BigCenterX;
        smallCenterY = BigCenterY;
        WORKING = false;
    }

    public void update(int pointX,int pointY) {
        if(WORKING){
            //判断用户点击的位置是否在大圆内
            if (Math.sqrt(Math.pow((BigCenterX - pointX), 2) + Math.pow((BigCenterY - pointY), 2)) <= (BigCenterR )) {
                //让小圆跟随用户触点位置移动
                smallCenterX = pointX;
                smallCenterY = pointY;
                this.rad = getRad(BigCenterX, BigCenterY, pointX, pointY);
            }
            else {
                this.rad = getRad(BigCenterX, BigCenterY, pointX, pointY);
                setSmallCircleXY(BigCenterX, BigCenterY, BigCenterR, rad);
            }
            degreesByNormalSystem = getDegrees(BigCenterX,BigCenterY,smallCenterX,smallCenterY);
        }
    }

    public void begin(int pointX,int pointY) {
        if (Math.sqrt(Math.pow((BigCenterX - pointX), 2) + Math.pow((BigCenterY - pointY), 2)) <= (BigCenterR )) {
            WORKING = true;
            update(pointX,pointY);
        } else{
            WORKING = false;
        }
    }

    public boolean ifWorking(){return WORKING;}
    public double getDegreesByNormalSystem(){return degreesByNormalSystem;}
    public double getRad(){return rad;}
}
