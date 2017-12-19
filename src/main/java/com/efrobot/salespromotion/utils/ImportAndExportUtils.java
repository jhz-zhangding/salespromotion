package com.efrobot.salespromotion.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.base.utils.L;
import com.efrobot.library.mvp.utils.RobotToastUtil;
import com.efrobot.salespromotion.Env.SalesConstant;
import com.efrobot.salespromotion.R;
import com.efrobot.salespromotion.SalesApplication;
import com.efrobot.salespromotion.base.CompressStatus;
import com.efrobot.salespromotion.bean.MainContentBean;
import com.efrobot.salespromotion.bean.MainItemContentBean;
import com.efrobot.salespromotion.bean.ModelContentBean;
import com.efrobot.salespromotion.bean.ModelNameBean;
import com.efrobot.salespromotion.bean.PlayModeBean;
import com.efrobot.salespromotion.db.ModeManager;
import com.efrobot.salespromotion.db.ModelContentManager;
import com.efrobot.salespromotion.db.ModelNameDataManager;
import com.efrobot.salespromotion.interfaces.IZipFileListener;
import com.efrobot.salespromotion.interfaces.ImportEvent;
import com.efrobot.salespromotion.main.MainActivity;
import com.efrobot.salespromotion.utils.ui.CustomHintDialog;
import com.efrobot.salespromotion.utils.ui.ImportDialog;
import com.efrobot.salespromotion.utils.ui.LoadingDialog;
import com.efrobot.salespromotion.utils.ui.StorageSelectedDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zd on 2017/12/11.
 */
public class ImportAndExportUtils {

    private Context context;
    private ModelNameDataManager modelNameDataManager;
    private ModelContentManager dataManager;
    private MainItemContentBean mainItemContentBean;

    private StorageSelectedDialog mStorageSelecteDialog;
    private StorageSelectedDialog mImportSelecteDialog;
    public static String usbPath;
    public final String SOURCE_JSON_PARENT_FILE = "/salespromotionzipfile";
    public final String SOURCE_JSON_FILE = "/salespromotionzipfile/salespromotionsource";
    public final String FILE_JSON_TXT = "SalesPromotionJson.txt";

    private ImportEvent importEvent;

    private List<ModelContentBean> checkList;

    public ImportAndExportUtils(Context context) {
        this.context = context;
        dataManager = ModelContentManager.getInstance(context);
        modelNameDataManager = ModelNameDataManager.getInstance(context);
        mainItemContentBean = SalesApplication.getAppContext().getMainItemContentBean();
    }

    /***
     * 导入
     *
     * @param importEvent 接收导入进度消息
     */
    public void importData(ImportEvent importEvent) {
        //导入
        this.importEvent = importEvent;
//        final CustomHintDialog customHintDialog = new CustomHintDialog(context, -1);
//        customHintDialog.setTitle("注意");
//        customHintDialog.setMessage("导入会覆盖原有数据，是否继续");
//        customHintDialog.setCancleButton("取消", new CustomHintDialog.IButtonOnClickLister() {
//            @Override
//            public void onClickLister() {
//                customHintDialog.dismiss();
//            }
//        });
//        customHintDialog.setSubmitButton("确认", new CustomHintDialog.IButtonOnClickLister() {
//            @Override
//            public void onClickLister() {
//                customHintDialog.dismiss();
//            }
//        });
//        customHintDialog.show();


        if (mImportSelecteDialog == null) {
            mImportSelecteDialog = new StorageSelectedDialog(context, StorageSelectedDialog.TYPE_IMPORT);
        }
        mImportSelecteDialog.setSelectedFileListener(new StorageSelectedDialog.ISelectedFilePathListener() {
            @Override
            public void onSelected(List<String> path) {
                mImportSelecteDialog.dismiss();
                startImportData(path);
            }
        });
        mImportSelecteDialog.show();
    }

    /**
     * 导出
     *
     * @param modelName
     */
    public void exportData(String modelName) {
        List<ModelContentBean> list = ModelContentManager.getInstance(context).queryItem(modelName);
        this.checkList = list;
        if (checkList == null || checkList.size() == 0) {
            RobotToastUtil.getInstance(context).showToast("当前模板无数据");
            return;
        }
        //导出
        if (mStorageSelecteDialog == null) {
            mStorageSelecteDialog = new StorageSelectedDialog(context, StorageSelectedDialog.TYPE_EXPORT);
        }
        mStorageSelecteDialog.setSelectedListener(new StorageSelectedDialog.ISelectedStorageListener() {
            @Override
            public void onSelected(String path) {
                mStorageSelecteDialog.dismiss();
                usbPath = path;
                Log.e(TAG, "path=" + usbPath);
//                        tv_init_text.setText("正在导出,请稍候...");
//                        presenter.analysis();
                RobotToastUtil.getInstance(context).showToast("正在导出，请稍候", Toast.LENGTH_LONG);
                ThreadManager.getInstance().createShortPool().execute(runnable);
            }
        });
        mStorageSelecteDialog.show();
    }

    private String TAG;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String copyPath = usbPath + SOURCE_JSON_FILE;
            boolean bool = DataFileUtils.createFile(copyPath);
            Log.e(TAG, "create_1 file ...bool=" + bool);
            if (bool) {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                MainContentBean mainContentBean = new MainContentBean();
                mainContentBean.setContentBean(checkList);
                mainContentBean.setModeBean(ModeManager.getInstance(context).queryListByName(checkList.get(0).getModelName()));

//                String json = JsonUtil.getJsonStrFromList(checkList);
                String json = JsonUtil.getModeJsonStrFromList(mainContentBean);
                DataFileUtils.stringToFile(copyPath, FILE_JSON_TXT, json);

                List<String> robotPath = getMediaPath();
                List<String> copyUsbPath = getUsbCopyPath(robotPath);
                for (int copypath = 0; copypath < copyUsbPath.size(); copypath++) {
                    DataFileUtils.createAllFileOnUSB(robotPath.get(copypath), copyUsbPath.get(copypath));
                }
                String time = TimeUtil.timesOne(String.valueOf(System.currentTimeMillis()));
                String modelName = checkList.get(0).getModelName();
                if (modelName != null && !TextUtils.isEmpty(modelName)) {
                    ZipUtils.AddFolder(usbPath + SOURCE_JSON_PARENT_FILE + "/" + modelName + "-" + time + ".zip", usbPath + SOURCE_JSON_FILE);
                } else {
                    ZipUtils.AddFolder(usbPath + SOURCE_JSON_PARENT_FILE + "/" + mainItemContentBean.getGoodsName() + "-" + time + ".zip", usbPath + SOURCE_JSON_FILE);
                }
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        presenter.clearCheckItemSence();
//                        progressBarView.setVisibility(View.GONE);
//                        RobotToastUtil.getInstance(MainActivity.this).showToast("导出成功!路径为:" + usbPath + SOURCE_JSON_PARENT_FILE, Toast.LENGTH_LONG);
                        RobotToastUtil.getInstance(context).showToast("导出成功!路径为:" + "本机/sales" + SOURCE_JSON_PARENT_FILE, Toast.LENGTH_LONG);
                    }
                }, 10 * 1000);
            }
        }
    };

    private List<String> mList;//解压zip
    private String type;
    private int mCurrentNum = 0;
    private ImportDialog mImportDialog;

    private final String IS_IMPORT = "isImport";

    public void startImportData(List<String> path) {
        Log.e(TAG, "path=" + path);

        if (mImportDialog == null) {
            mImportDialog = new ImportDialog(context, R.layout.import_dialog);
            mImportDialog.setCanceledOnTouchOutside(false);
            mImportDialog.show();
        } else {
            if (!mImportDialog.isShowing()) {
                mImportDialog.show();
            }
        }
        TextView textView = (TextView) mImportDialog.findViewById(R.id.import_tv);
        textView.setText(context.getString(R.string.importing));
        Button button = ((Button) mImportDialog.findViewById(R.id.cancel_import_btn));
        button.setVisibility(View.INVISIBLE);
        type = CompressStatus.USBIMPORT;
        L.d(TAG, "存在文件");
        mList = path;
        if (mList != null) {
            mCurrentNum = 0;
            ThreadManager.getInstance().createShortPool().execute(new Runnable() {
                @Override
                public void run() {
                    uncompress();
                }
            });
        }
    }

    public void uncompress() {
        if (mCurrentNum < mList.size()) {
            ZipUtil.unZipFileWithProgress(mList.get(mCurrentNum), SalesConstant.FILE_PATH, false, new IZipFileListener() {
                @Override
                public void onStart() {
                    Log.e(TAG, "onStart: ");
                    if (type.equals(CompressStatus.USBIMPORT)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(CompressStatus.PERCENT, 0);
                        Message msg = new Message();
                        msg.what = CompressStatus.HANDLING;
                        msg.setData(bundle);
                        mHandle.sendMessage(msg);
                    }
                }

                @Override
                public void onProcess(int current) {
                    if (type.equals(CompressStatus.USBIMPORT)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(CompressStatus.PERCENT, current);
                        Message msg = new Message();
                        msg.what = CompressStatus.HANDLING;
                        msg.setData(bundle);
                        mHandle.sendMessage(msg);
                    }
                }

                @Override
                public void onSuccess(String filePath) {
                    L.e(TAG, "uncompress success, filePath=" + filePath);
                    try {
                        if (!TextUtils.isEmpty(filePath)) {
                            String json = FileUtil.readFile(SalesConstant.FILE_JSON);//读内容
                            //复制多媒体文件
                            int result = FileUtil.copy(SalesConstant.FILE_PATH + File.separator + "salespromotionsource", Environment.getExternalStorageDirectory().toString(), true);//把数据拷贝到根目录
                            if (result == 0) {
                                //多媒体文件复制成功，开始插库
                                MainContentBean mainContentBean = JsonUtil.getModeListFromJsonStr(json);
                                List<PlayModeBean> playModeBeanList = mainContentBean.getModeBean();
                                if (playModeBeanList != null) {
                                    for (int i = 0; i < playModeBeanList.size(); i++) {
                                        ModeManager.getInstance(context).insertContent(playModeBeanList.get(i));
                                    }
                                }


                                List<ModelContentBean> list = mainContentBean.getContentBean();
//                                List<ModelContentBean> list = JsonUtil.getListFromJsonStr(json);
                                if (list != null) {
                                    String mfilePath = mList.get(mCurrentNum);
                                    L.e(TAG, "uncompress isSuccess = " + " mfilePath=" + mfilePath);
                                    Boolean isImport = PreferencesUtils.getBoolean(context, IS_IMPORT, false);
                                    if (!isImport) {
                                        for (int i = 0; i < list.size(); i++) {
                                            ModelContentBean modelContentBean = list.get(i);
                                            if (mfilePath.contains("food")) {
                                                modelContentBean.setModelName("食品促销模版");
                                                modelContentBean.setModelType(0);

                                            } else if (mfilePath.contains("drink")) {
                                                modelContentBean.setModelName("饮料促销模版");
                                                modelContentBean.setModelType(1);

                                            } else if (mfilePath.contains("daily")) {
                                                modelContentBean.setModelName("日化促销模版");
                                                modelContentBean.setModelType(2);

                                            } else if (mfilePath.contains("other")) {
                                                modelContentBean.setModelName("其他促销模版");
                                                modelContentBean.setModelType(3);
                                            }
                                            ModelContentManager.getInstance(context).insertContent(modelContentBean);
                                            if (!modelNameDataManager.queryModelNameExits(modelContentBean.getModelName())) {
                                                modelNameDataManager.insertContent(new ModelNameBean(modelContentBean.getModelName(), modelContentBean.getModelType()));
                                            }
//                                            if (isSuccess) {
//                                                mCurrentNum++;
//                                                uncompress();
//                                            } else
                                            {
                                                mHandle.sendEmptyMessage(START_INIT);
                                            }
                                        }
                                        mCurrentNum++;
                                        uncompress();
                                    } else {
                                        Message message = mHandle.obtainMessage();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("json", json);
                                        message.what = IMPORT_DATA_BASE;
                                        message.obj = mfilePath;
                                        message.setData(bundle);
                                        mHandle.sendMessage(message);
                                    }

                                }

                            }

                        } else {
                            mHandle.sendEmptyMessage(CompressStatus.ERROR);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(int code, String message) {
                    Message msg = Message.obtain();
                    msg.what = code;
                    msg.obj = message;
                    mHandle.sendMessage(msg);
                }
            });
        } else

        {
            Boolean isImport = PreferencesUtils.getBoolean(context, IS_IMPORT, false);
            if (!isImport) {
                PreferencesUtils.putBoolean(context, IS_IMPORT, true);
                FileUtil.deleteFile(new File(Environment.getExternalStorageDirectory().toString() + "/salespromotionzipfile"));
            }

            mHandle.sendEmptyMessage(CompressStatus.SUCCESS);
        }

    }

    private List<String> getMediaPath() {
        return dataManager.queryAllMedia();
    }

    public List<String> getUsbCopyPath(List<String> robotPath) {
        List<String> usbCopyPath = new ArrayList<>();
        for (int i = 0; i < robotPath.size(); i++) {
            usbCopyPath.add(robotPath.get(i).replace("/storage/emulated/0/", usbPath + SOURCE_JSON_FILE + "/"));
            Log.e("zhang", "robotPath==" + robotPath.get(i));
            Log.e("zhang", "usbpath==" + usbCopyPath.get(i));
        }
        return usbCopyPath;
    }

    private String FOOD_MODEL = "食品促销模版";
    private String DRINK_MODEL = "饮料促销模版";
    private String DAILY_MODEL = "日化促销模版";
    private String OTHER_MODEL = "其他促销模版";

    private static final int LONG_TOUCH = 100005;
    private static final int MSG_BEGIN_IMPORT = 1005;
    private static final int MSG_UNCOMPRESS_DEFAULT = 1006;
    private static final int START_INIT = 125;
    private static final int IMPORT_DATA_BASE = 126;
    private String currentModelName;
    public Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CompressStatus.ERROR_UNKNOWN:
                    String name = (String) msg.obj;
                    RobotToastUtil.getInstance(context).showToast(name + "未知异常！");
                    if (mImportDialog != null && mImportDialog.isShowing()) {
                        mImportDialog.dismiss();
                    }
                    break;
                case CompressStatus.SUCCESS:
                    if (mainItemContentBean.getGoodsGroup().equals("食品")) {
                        currentModelName = FOOD_MODEL;
                    } else if (mainItemContentBean.getGoodsGroup().equals("饮料")) {
                        currentModelName = DRINK_MODEL;
                    } else if (mainItemContentBean.getGoodsGroup().equals("日化")) {
                        currentModelName = DAILY_MODEL;
                    } else if (mainItemContentBean.getGoodsGroup().equals("其他")) {
                        currentModelName = OTHER_MODEL;
                    }
                    PreferencesUtils.putString(context, "currentModelName", currentModelName);


                    if (mImportDialog != null && mImportDialog.isShowing()) {
                        mImportDialog.dismiss();
                    }
                    if (type.equals(CompressStatus.USBIMPORT)) {
                        RobotToastUtil.getInstance(context).showToast(context.getString(R.string.data_import_finish));
                    } else if (type.equals(CompressStatus.FIRSTIMPORT)) {
                        RobotToastUtil.getInstance(context).showToast(context.getString(R.string.data_first_import_finish));
                        SharedPreferences share = context.getSharedPreferences("cateringImport", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = share.edit();
                        editor.putBoolean("isImport", true);
                        editor.apply();
                    }
                    if (importEvent != null) {
                        importEvent.success();
                    }
//                    updateAdapterData();
                    L.d(TAG, "handleMessage 数据导入完成");
                    break;
                case CompressStatus.PACKAGE_BAD:
                    String badName = (String) msg.obj;
                    RobotToastUtil.getInstance(context).showToast(badName + "文件损坏！");
                    if (type.equals(CompressStatus.USBIMPORT)) {
                        if (mImportDialog != null && mImportDialog.isShowing()) {
                            mImportDialog.dismiss();
                        }
                    } else if (type.equals(CompressStatus.FIRSTIMPORT)) {
                        showLoadingDialog(false);
                    }
                    break;
                case CompressStatus.START:
                    L.d(TAG, "handleMessage 开始解压 mList=" + mList.size());
                    break;
                case CompressStatus.HANDLING:
                    int progress = 0;
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        progress = bundle.getInt(CompressStatus.PERCENT);
                    }
                    if (mImportDialog != null) {
                        ProgressBar progressBar = (ProgressBar) mImportDialog.findViewById(R.id.import_progress_btn);
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(progress);
                    }
                    L.d(TAG, "handleMessage progress=" + progress);
                    break;
                case CompressStatus.ERROR:
                    L.d(TAG, "handleMessage 解压失败");
                    if (type.equals(CompressStatus.USBIMPORT)) {
                        RobotToastUtil.getInstance(context).showToast(context.getString(R.string.data_import_fial));
                        if (mImportDialog != null && mImportDialog.isShowing()) {
                            mImportDialog.dismiss();
                        }
                    } else if (type.equals(CompressStatus.FIRSTIMPORT)) {
                        RobotToastUtil.getInstance(context).showToast(context.getString(R.string.data_first_import_fial));
                        showLoadingDialog(false);
                    }
                    if (importEvent != null) {
                        importEvent.failed();
                    }
                    break;
                case LONG_TOUCH:
                    break;
                case MSG_UNCOMPRESS_DEFAULT:
                    importantData(context);
                    break;
                case START_INIT:
                    RobotToastUtil.getInstance(context).showToast("正在初始化数据中，请稍候");
                    break;
                case IMPORT_DATA_BASE:
                    try {
                        String json = msg.getData().getString("json");

                        MainContentBean mainContentBean = JsonUtil.getModeListFromJsonStr(json);
                        List<ModelContentBean> mList = mainContentBean.getContentBean();

                        String fileName = (String) msg.obj;
                        String modelFileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());

                        if (mList != null && mList.size() > 0) {
                            String modelName = mList.get(0).getModelName();
                            if (modelNameDataManager.queryModelNameExits(modelName)) {
                                //提示框
                                dataManager.deleteContentByModelName(modelName);
                                showImportHint(mList, modelFileName);
                            } else {
                                //开始导入
                                startImportList(mList, modelName);
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    private void showImportHint(final List<ModelContentBean> mList, final String modelFileName) {
        final CustomHintDialog customHintDialog = new CustomHintDialog(context, -1);
        customHintDialog.setMessage("已存在该模板，是否继续并覆盖");
        customHintDialog.setCancleButton("取消", new CustomHintDialog.IButtonOnClickLister() {
            @Override
            public void onClickLister() {
                customHintDialog.dismiss();
            }
        });
        customHintDialog.setSubmitButton("确认", new CustomHintDialog.IButtonOnClickLister() {
            @Override
            public void onClickLister() {
                startImportList(mList, modelFileName);
                customHintDialog.dismiss();
            }
        });
        customHintDialog.show();
    }

    private void startImportList(List<ModelContentBean> mList, String modelFileName) {
        try {
            for (int i = 0; i < mList.size(); i++) {
                ModelContentBean itemsContentBean = mList.get(i);
                if (itemsContentBean.getModelName() == null || itemsContentBean.getModelName().isEmpty()) {
                    itemsContentBean.setModelName(modelFileName);
                }

                dataManager.insertContent(itemsContentBean);
                if (!modelNameDataManager.queryModelNameExits(itemsContentBean.getModelName())) {
                    modelNameDataManager.insertContent(new ModelNameBean(itemsContentBean.getModelName(), itemsContentBean.getModelType()));

                }
            }
            mCurrentNum++;
            uncompress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importantData(Context context) {
        boolean isImport = PreferencesUtils.getBoolean(context, IS_IMPORT, false);
        Log.e(TAG, "importantData:isImport= " + isImport);
        if (!isImport) {
            type = CompressStatus.FIRSTIMPORT;
            ThreadManager.getInstance().createShortPool().execute(mImportantDataRunnable);
        } else {
//            showLoadingDialog(false);
//            Intent intent = new Intent();
//            intent.setAction(SalesConstant.UPDATAACTION);
//            sendBroadcast(intent);
        }
    }

    private Runnable mImportantDataRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "important data.....first copy file");
            FileUtil.CopyAssets(context, "salespromotionzipfile", Environment.getExternalStorageDirectory().toString() + "/salespromotionzipfile");
            List<String> pathList = FileUtil.getFirstFileName(Environment.getExternalStorageDirectory().toString() + "/salespromotionzipfile");
            Log.e(TAG, "file path =" + pathList);
            if (pathList != null) {
                mList = pathList;
                mCurrentNum = 0;
                uncompress();
            }
        }
    };

    private LoadingDialog mLoadingDialog;

    public void showLoadingDialog(boolean bool) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(context);
        }
        if (bool) {
            mLoadingDialog.show();
        } else {
            mLoadingDialog.dismiss();
        }
    }


}
