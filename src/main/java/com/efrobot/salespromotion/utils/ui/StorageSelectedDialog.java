package com.efrobot.salespromotion.utils.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.dialog.CommonDialog;
import com.efrobot.salespromotion.Env.SalesConstant;
import com.efrobot.salespromotion.R;
import com.efrobot.salespromotion.adapter.FileAdapter;
import com.efrobot.salespromotion.interfaces.IDialogDismiss;
import com.efrobot.salespromotion.utils.DataFileUtils;
import com.efrobot.salespromotion.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lhy on 2017/8/1
 */
public class StorageSelectedDialog extends CommonDialog implements View.OnClickListener, FileAdapter.ICheckListener {

    public static final String TAG = "PreviewDialog";
    private Context mContext;
    private IDialogDismiss mDismissListener;
    public static final int TYPE_EXPORT = 1;
    public static final int TYPE_IMPORT = 2;
    private TextView tv_usb_surplus;
    private TextView tv_sdcard_surplus;
    private LinearLayout ll_usb;
    private LinearLayout ll_sd;
    private LinearLayout ll_storage;
    private ImageView iv_sd;
    private ImageView iv_storage;
    private ImageView iv_usb;
    private ImageView iv_back;
    private TextView tv_ok;
    private TextView tv_prompt;
    private String mUsbPath;
    private USBReceiver mUSBReceiver;
    private ISelectedStorageListener mISelectedStorageListener;
    private ISelectedFilePathListener mISelectedFileListener;
    private int mType;
    private RecyclerView rv_content;
    private FileAdapter mFileAdapter;
    private List<String> mSelectedList = new ArrayList<>();
    private List<String> mAllList = new ArrayList<>();

    public StorageSelectedDialog(Context context, int type) {
        super(context);
        mContext = context;
        mType = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View customView = LayoutInflater.from(mContext).inflate(R.layout.dialog_selecte_storage, null);
        setContentView(customView);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        DisplayMetrics m = mContext.getResources().getDisplayMetrics();
        params.width = (int) (m.widthPixels * 0.9);
        params.height = (int) (m.heightPixels * 0.8);
        getWindow().setAttributes(params);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);

        ll_storage = (LinearLayout) findViewById(R.id.ll_storage);
        iv_sd = (ImageView) findViewById(R.id.iv_sd);
        iv_storage = (ImageView) findViewById(R.id.iv_storage);
        iv_usb = (ImageView) findViewById(R.id.iv_usb);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        rv_content = (RecyclerView) findViewById(R.id.rv_content);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        tv_prompt = (TextView) findViewById(R.id.tv_prompt);

        iv_sd.setOnClickListener(this);
        iv_storage.setOnClickListener(this);
        iv_usb.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_ok.setOnClickListener(this);

        tv_usb_surplus = (TextView) findViewById(R.id.usb_surplus);
        tv_sdcard_surplus = (TextView) findViewById(R.id.sdcard_surplus);


    }

    public void refreshUI() {
        //回复初始状态
        tv_prompt.setVisibility(View.GONE);
        rv_content.setVisibility(View.GONE);
        ll_storage.setVisibility(View.VISIBLE);
        tv_ok.setVisibility(View.GONE);
        if (mSelectedList != null) {
            mSelectedList.clear();
        }
        if (mAllList != null) {
            mAllList.clear();
        }


        //检测USB
        DataFileUtils.SDCardInfo usbCardInfo = DataFileUtils.getSDCardInfo(SalesConstant.PATH_USB);
        if (usbCardInfo != null && usbCardInfo.total != 0) {
            File file = new File(SalesConstant.PATH_USB_8_4);//判断是否含8_4路径
            boolean bool = file.exists();
            if (bool) {
                mUsbPath = SalesConstant.PATH_USB_8_4;
            } else {
                mUsbPath = SalesConstant.PATH_USB;
            }
            tv_usb_surplus.setText("");
            iv_usb.setClickable(true);
        } else {
//            tv_usb_surplus.setText("您还未插入USB");
            tv_usb_surplus.setTextColor(Color.RED);
            tv_usb_surplus.setText("暂未开放");
            iv_usb.setClickable(false);
        }

        //检测SD卡
        DataFileUtils.SDCardInfo sdCardInfo = DataFileUtils.getSDCardInfo(SalesConstant.PATH_SD);
        if (sdCardInfo != null && sdCardInfo.total != 0) {
            iv_sd.setClickable(true);
            tv_sdcard_surplus.setText("");
        } else {
//            tv_sdcard_surplus.setText("您还未插入SD卡");
            tv_sdcard_surplus.setTextColor(Color.RED);
            tv_sdcard_surplus.setText("暂未开放");
            iv_sd.setClickable(false);
        }

    }

    public void setDismissListener(IDialogDismiss listener) {
        mDismissListener = listener;
    }

    public void setSelectedListener(ISelectedStorageListener listener) {
        mISelectedStorageListener = listener;
    }

    public void setSelectedFileListener(ISelectedFilePathListener listener) {
        mISelectedFileListener = listener;
    }


    @Override
    public void show() {
        super.show();
        registerReceiver();
        refreshUI();
    }

    @Override
    public void dismiss() {
        if (mDismissListener != null) {
            mDismissListener.onDismiss();
        }
        unregisterReceiver();
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_storage:
                if (mType == TYPE_EXPORT) {
                    if (mISelectedStorageListener != null) {
                        mISelectedStorageListener.onSelected(SalesConstant.PATH_ROOT);
                    }
                } else if (mType == TYPE_IMPORT) {
                    entrySelectFile(SalesConstant.PATH_ROOT);
                }
                break;
            case R.id.iv_usb:
                if (mType == TYPE_EXPORT) {
                    if (mISelectedStorageListener != null) {
                        mISelectedStorageListener.onSelected(mUsbPath);
                    }
                } else if (mType == TYPE_IMPORT) {
                    entrySelectFile(mUsbPath);
                }

                break;
            case R.id.iv_sd:
                if (mType == TYPE_EXPORT) {
                    if (mISelectedStorageListener != null) {
                        mISelectedStorageListener.onSelected(SalesConstant.PATH_SD);
                    }
                } else if (mType == TYPE_IMPORT) {
                    entrySelectFile(SalesConstant.PATH_SD);
                }

                break;
            case R.id.iv_back:
                if (mType == TYPE_IMPORT) {
                    if (ll_storage.getVisibility() == View.VISIBLE) {
                        dismiss();
                    } else {
                        refreshUI();
                    }
                } else if (mType == TYPE_EXPORT) {
                    dismiss();
                }
                break;
            case R.id.tv_ok:
                if (mISelectedFileListener != null) {
                    mISelectedFileListener.onSelected(mSelectedList);
                }
                break;
        }
    }

    private void entrySelectFile(String path) {
        if (mFileAdapter == null) {
            mFileAdapter = new FileAdapter(mContext, mSelectedList);
        }
        mFileAdapter.setOnCheckChangeListener(this);

        mAllList = FileUtil.getFirstFileName(path + "/salespromotionzipfile");
        if (mAllList != null && mAllList.size() > 0) {
            mFileAdapter.replaceData(mAllList);
            GridLayoutManager manager = new GridLayoutManager(mContext, 4);
            rv_content.setLayoutManager(manager);
            rv_content.setAdapter(mFileAdapter);
            ll_storage.setVisibility(View.GONE);
            rv_content.setVisibility(View.VISIBLE);
        } else {
            tv_prompt.setText(path + "/editdiyzipfile" + "\\n\\r" + "没有有效文件！");
            tv_prompt.setVisibility(View.VISIBLE);
            ll_storage.setVisibility(View.GONE);
        }

    }

    @Override
    public void onCheckedChange(int position, boolean isChecked) {
        Log.e(TAG, "position=" + position + " isChecked=" + isChecked);
        String tmp = mAllList.get(position);
        if (isChecked) {
            if (!mSelectedList.contains(tmp)) {
                mSelectedList.add(tmp);
            }
        } else {
            if (mSelectedList.contains(tmp)) {
                mSelectedList.remove(tmp);
            }
        }
        tv_ok.setVisibility(mSelectedList.size() > 0 ? View.VISIBLE : View.GONE);
    }

    public interface ISelectedStorageListener {
        void onSelected(String path);
    }

    public interface ISelectedFilePathListener {
        void onSelected(List<String> path);
    }

    private void registerReceiver() {
        if (mUSBReceiver == null) {
            mUSBReceiver = new USBReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);//插入SD卡并且已正确安装（识别）时发出的广播
        filter.addAction(Intent.ACTION_MEDIA_EJECT);//已拔掉外部大容量储存设备发出的广播（比如SD卡，或移动硬盘）,不管有没有正确卸载都会发出此广播
        filter.addDataScheme("file");
        mContext.registerReceiver(mUSBReceiver, filter);
    }

    private void unregisterReceiver() {
        if (mUSBReceiver != null) {
            mContext.unregisterReceiver(mUSBReceiver);
        }
    }

    public class USBReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (rv_content.getVisibility() != View.VISIBLE) {
                refreshUI();
            }
        }
    }
}
