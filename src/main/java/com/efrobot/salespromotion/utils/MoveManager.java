package com.efrobot.salespromotion.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import com.efrobot.library.RobotManager;

public class MoveManager {
    private static final int MOVEFRONT = 0;
    private static final int MOVEBACK = 1;
    private static final int MOVELEFT = 2;
    private static final int MOVERIGHT = 3;
    private static final int MOVESTOP = 4;
    private RobotManager robotManager;
    private boolean running;
    private Handler mHandler = new Handler();

    public MoveManager(Context context) {
        robotManager = RobotManager.getInstance(context);
    }

    private void controlDelay() {
        if (!running) {
            running = true;
            mHandler.postDelayed( new Runnable() {
                @Override
                public void run() {
                    running = false;
                }
            }, 300);
        }
    }

    public void onControlUp() {
        sendWheelOrder(MOVEFRONT);
        clearWheelElectricityState();
    }

    public void onControlDown() {
        sendWheelOrder(MOVEBACK);
        clearWheelElectricityState();
    }

    public void onControlLeft() {
        sendWheelOrder(MOVELEFT);
        clearWheelElectricityState();
    }

    public void onControlRight() {
        sendWheelOrder(MOVERIGHT);
        clearWheelElectricityState();
    }

    //发送身体运动命令
    private void sendWheelOrder(int dire) {
        /**
         * 判断如果上一个动作为暂停，那么下一个暂停动作将不再触发
         */
        switch (dire) {
            case MOVEFRONT:
                robotManager.getWheelInstance().moveFront();
                break;
            case MOVEBACK:
                robotManager.getWheelInstance().moveBack();
                break;
            case MOVELEFT:
                robotManager.getWheelInstance().moveLeft();
                break;
            case MOVERIGHT:
                robotManager.getWheelInstance().moveRight();
                break;
            case MOVESTOP:
                robotManager.getWheelInstance().stop();
                break;
        }
    }

    public void clearWheelElectricityState() {
        robotManager.clearWheelElectricityState();
    }

    public void doUpExecute() {
        robotManager.getWheelInstance().stop();
    }

    public void doDownExecute(int mouseCode) {
        Log.i("mouse_key_direction", "running = " + running);
        if (running)
            return;
        switch (mouseCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                Log.i("mouse_key_direction", "UP");
                onControlUp();
                controlDelay();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                Log.i("mouse_key_direction", "DOWN");
                onControlDown();
                controlDelay();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                Log.i("mouse_key_direction", "LEFT");
                onControlLeft();
                controlDelay();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                Log.i("mouse_key_direction", "RIGHT");
                onControlRight();
                controlDelay();
                break;
        }
    }
}
