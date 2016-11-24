package com.example.administrator.greedyfish;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.administrator.greedyfish.constant.ConstantUtil;
import com.example.administrator.greedyfish.surface.MenuSurface;
import com.example.administrator.greedyfish.surface.PlaySurface;

public class MainActivity extends Activity {
    private PlaySurface playSurface;
    private MenuSurface menuSurface;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Log.i("Tag","MainActivity "+msg.toString());
            if(msg.what == ConstantUtil.TO_PLAY_SURFACE){
                toPlaySurface();
            }
            else  if(msg.what == ConstantUtil.TO_MENU_SURFACE){
                toMenuSurface();
            }
            else  if(msg.what == ConstantUtil.END_GAME){
                endGame();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MenuSurface(this));
    }

    //显示游戏的主界面
    private void toPlaySurface(){
        playSurface = new PlaySurface(this);
        setContentView(playSurface);
    }

    //显示游戏的菜单界面
    private void toMenuSurface(){
        menuSurface = new MenuSurface(this);
        setContentView(menuSurface);
    }

    //结束游戏
    public void endGame(){
        this.finish();
    }

    //getter和setter方法
    public Handler getHandler() {
        return handler;
    }
}
