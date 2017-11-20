package com.efrobot.salespromotion.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;


import com.efrobot.salespromotion.R;

import java.lang.ref.WeakReference;

/**
 * @类名称：BaseDialog
 * @类描述：dialog基类
 * @创建人：wangyonghui
 * @创建时间：2016/9/2714:07
 * @修改时间：2016/9/2714:07
 * @备注：
 */
public class BaseDialog extends Dialog {
    private int layoutId;
    private BaseHandler mBaseHandler;

    public BaseDialog(Context context, int layoutId) {
        super(context, R.style.dialog);
        this.layoutId = layoutId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        applyCompat();
    }


    /**
     * 隐藏状态栏
     */
    private void applyCompat() {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 通过viewid获取控件
     */
    public View getView(int viewId) {
        return findViewById(viewId);
    }

    /**
     * 初始化一个Handler，如果需要使用Handler，先调用此方法，
     * 然后可以使用postRunnable(Runnable runnable)，
     * sendMessage在handleMessage（Message msg）中接收msg
     */
    public void initHandler() {
        mBaseHandler = new BaseHandler(this);
    }

    /**
     * 返回Handler，在此之前确定已经调用initHandler（）
     *
     * @return Handler
     */
    public Handler getHandler() {
        return mBaseHandler;
    }

    /**
     * 同Handler的postRunnable
     * 在此之前确定已经调用initHandler（）
     */
    protected void postRunnable(Runnable runnable) {
        postRunnableDelayed(runnable, 0);
    }

    /**
     * 同Handler的postRunnableDelayed
     * 在此之前确定已经调用initHandler（）
     */
    protected void postRunnableDelayed(Runnable runnable, long delayMillis) {
        if (mBaseHandler == null) initHandler();
        mBaseHandler.postDelayed(runnable, delayMillis);
    }


    /**
     * 同Handler 的 handleMessage，
     * getHandler.sendMessage,发送的Message在此接收
     * 在此之前确定已经调用initHandler（）
     *
     * @param msg
     */
    protected void handleMessage(Message msg) {
    }

    protected static class BaseHandler extends Handler {
        private final WeakReference<BaseDialog> mObjects;

        public BaseHandler(BaseDialog mPresenter) {
            mObjects = new WeakReference<BaseDialog>(mPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseDialog mPresenter = mObjects.get();
            if (mPresenter != null)
                mPresenter.handleMessage(msg);
        }
    }

}
