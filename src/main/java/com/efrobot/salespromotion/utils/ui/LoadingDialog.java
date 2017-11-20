package com.efrobot.salespromotion.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.dialog.CommonDialog;
import com.efrobot.salespromotion.R;

/**
 * Created by lenovo on 2016/11/27.
 */
public class LoadingDialog {

    private Context mContext;
    private Dialog dialog;
    private TextView tvInfo;
    private ImageView pb_loadding;

    public LoadingDialog(Context context) {
        this.mContext = context;
        create();
    }

    private Animation hyperspaceJumpAnimation;

    public void setIsCancel(boolean isCancel) {
        dialog.setCancelable(isCancel);
    }

    private void create() {
//        , R.style.NewSettingDialog
        dialog = new CommonDialog(mContext, R.style.NewSettingDialog);
        DisplayMetrics m = mContext.getResources().getDisplayMetrics();
        View customView = LayoutInflater.from(mContext).inflate(R.layout.dialog_lodding, null);
        dialog.setContentView(customView);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (m.widthPixels * 0.5);
        params.height = (int) (m.heightPixels * 0.5);
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setGravity(Gravity.CENTER);

        tvInfo = (TextView) customView.findViewById(R.id.tvInfo);
        pb_loadding = (ImageView) customView.findViewById(R.id.pb_loadding);
        hyperspaceJumpAnimation = AnimationUtils.loadAnimation(mContext, R.anim.animation_progress_dialog); // 加载动画
        pb_loadding.startAnimation(hyperspaceJumpAnimation);// 使用ImageView显示动画
    }

    public void setInfo(String info) {
        tvInfo.setText(info);
    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

    public void show() {
        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        pb_loadding.startAnimation(hyperspaceJumpAnimation);// 使用ImageView显示动画
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            pb_loadding.clearAnimation();
            dialog.dismiss();
        }
    }
}
