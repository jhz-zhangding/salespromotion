package com.efrobot.salespromotion.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.base.utils.L;
import com.efrobot.library.mvp.presenter.BasePresenter;
import com.efrobot.library.mvp.utils.RobotToastUtil;
import com.efrobot.salespromotion.Env.SalesConstant;
import com.efrobot.salespromotion.R;
import com.efrobot.salespromotion.SalesApplication;
import com.efrobot.salespromotion.activity.ModelContentBean;
import com.efrobot.salespromotion.activity.ModelNameBean;
import com.efrobot.salespromotion.activity.more.MoreModelActivity;
import com.efrobot.salespromotion.adapter.ChooseGoodsAdapter;
import com.efrobot.salespromotion.adapter.MainItemAdapter;
import com.efrobot.salespromotion.add.AddBodyShowView;
import com.efrobot.salespromotion.base.CompressStatus;
import com.efrobot.salespromotion.base.SalesBaseActivity;
import com.efrobot.salespromotion.bean.ItemsContentBean;
import com.efrobot.salespromotion.bean.MainItemContentBean;
import com.efrobot.salespromotion.db.DataManager;
import com.efrobot.salespromotion.db.ModelDataManager;
import com.efrobot.salespromotion.db.ModelNameDataManager;
import com.efrobot.salespromotion.interfaces.IZipFileListener;
import com.efrobot.salespromotion.setting.SalesSettingActivity;
import com.efrobot.salespromotion.utils.DataFileUtils;
import com.efrobot.salespromotion.utils.DisplayUtil;
import com.efrobot.salespromotion.utils.FileUtil;
import com.efrobot.salespromotion.utils.JsonUtil;
import com.efrobot.salespromotion.utils.PreferencesUtils;
import com.efrobot.salespromotion.utils.ThreadManager;
import com.efrobot.salespromotion.utils.TimeUtil;
import com.efrobot.salespromotion.utils.ZipUtil;
import com.efrobot.salespromotion.utils.ZipUtils;
import com.efrobot.salespromotion.utils.ui.CustomHintDialog;
import com.efrobot.salespromotion.utils.ui.ImportDialog;
import com.efrobot.salespromotion.utils.ui.LoadingDialog;
import com.efrobot.salespromotion.utils.ui.StorageSelectedDialog;
import com.j256.ormlite.stmt.query.In;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SalesBaseActivity<MainPresenter> implements IMain, AdapterView.OnItemClickListener, View.OnClickListener {

    private List<ItemsContentBean> list;

    public static void startSelfActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, MainActivity.class);
        if (null != bundle)
            intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private TextView title;

    private RelativeLayout powerLL, gameLL, homeLL, backLL;

    private ListView mListView;

    private MainItemAdapter adapter;

    private TextView addBtn, editBtn;

    private int currentType = 1;

    private boolean mCurrentShowDel = false;

    private MainItemContentBean mainItemContentBean;

    private int mainItemId = -1;

    private ImageView mainPlayKeyImg, mainPlayModeImg;

    private TextView updateGoodsBtn;

    private TextView playModeHintTv;

    private DataManager dataManager;
    private ModelNameDataManager modelNameDataManager;

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_main;
    }

    @Override
    public BasePresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void onViewInit() {
        super.onViewInit();
        SalesApplication.from(this).isNeedStartService = true;
        mainItemContentBean = SalesApplication.getAppContext().getMainItemContentBean();

        dataManager = DataManager.getInstance(getContext());
        modelNameDataManager = ModelNameDataManager.getInstance(getContext());

        mainItemId = PreferencesUtils.getInt(this, SalesConstant.LAST_OPEN_ACTIVITY_ID);

        title = (TextView) findViewById(R.id.title);

        powerLL = (RelativeLayout) findViewById(R.id.main_key_power);
        powerLL.setOnClickListener(this);
        gameLL = (RelativeLayout) findViewById(R.id.main_key_game);
        gameLL.setOnClickListener(this);
        homeLL = (RelativeLayout) findViewById(R.id.main_key_home);
        homeLL.setOnClickListener(this);
        backLL = (RelativeLayout) findViewById(R.id.main_key_back);
        backLL.setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.main_list_view);

        addBtn = (TextView) findViewById(R.id.main_item_add_btn);
        addBtn.setOnClickListener(this);
        editBtn = (TextView) findViewById(R.id.main_item_edit_btn);
        editBtn.setOnClickListener(this);

        mainPlayKeyImg = (ImageView) findViewById(R.id.main_play_key_img);
        mainPlayModeImg = (ImageView) findViewById(R.id.main_play_mode_img);
        mainPlayModeImg.setOnClickListener(this);

//        updateGoodsBtn = (TextView) findViewById(R.id.main_exchange_goods);
//        updateGoodsBtn.setOnClickListener(this);

        playModeHintTv = (TextView) findViewById(R.id.main_play_mode_text);

        initAdapter();
        updateView(powerLL);
        updateModeView(SalesConstant.ItemType.POWER_TYPE);
        updateTitle();

        findViewById(R.id.rlExport).setOnClickListener(this);
        findViewById(R.id.rlImport).setOnClickListener(this);
//        findViewById(R.id.main_create_model_data).setOnClickListener(this);
        findViewById(R.id.main_more_model).setOnClickListener(this);

        mHandle.sendEmptyMessage(MSG_UNCOMPRESS_DEFAULT);
    }

    private static final int LONG_TOUCH = 100005;
    private static final int MSG_BEGIN_IMPORT = 1005;
    private static final int MSG_UNCOMPRESS_DEFAULT = 1006;
    public Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CompressStatus.ERROR_UNKNOWN:
                    String name = (String) msg.obj;
                    RobotToastUtil.getInstance(getContext()).showToast(name + "未知异常！");
                    if (mImportDialog != null && mImportDialog.isShowing()) {
                        mImportDialog.dismiss();
                    }
                    break;
                case CompressStatus.SUCCESS:
                    if (mImportDialog != null && mImportDialog.isShowing()) {
                        mImportDialog.dismiss();
                    }
                    if (type.equals(CompressStatus.USBIMPORT)) {
                        RobotToastUtil.getInstance(getContext()).showToast(getContext().getString(R.string.data_import_finish));
                    } else if (type.equals(CompressStatus.FIRSTIMPORT)) {
                        RobotToastUtil.getInstance(getContext()).showToast(getContext().getString(R.string.data_first_import_finish));
                        SharedPreferences share = getContext().getSharedPreferences("cateringImport", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = share.edit();
                        editor.putBoolean("isImport", true);
                        editor.apply();
                    }
                    updateAdapterData();
                    L.d(TAG, "handleMessage 数据导入完成");
                    break;
                case CompressStatus.PACKAGE_BAD:
                    String badName = (String) msg.obj;
                    RobotToastUtil.getInstance(getContext()).showToast(badName + "文件损坏！");
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
                        RobotToastUtil.getInstance(getContext()).showToast(getContext().getString(R.string.data_import_fial));
                        if (mImportDialog != null && mImportDialog.isShowing()) {
                            mImportDialog.dismiss();
                        }
                    } else if (type.equals(CompressStatus.FIRSTIMPORT)) {
                        RobotToastUtil.getInstance(getContext()).showToast(getContext().getString(R.string.data_first_import_fial));
                        showLoadingDialog(false);
                    }
                    break;
                case LONG_TOUCH:
                    break;
                case MSG_UNCOMPRESS_DEFAULT:
                    importantData(getContext());
                    break;
                case 125:
                    RobotToastUtil.getInstance(getContext()).showToast("正在初始化数据中，请稍候");
                    break;
            }
        }
    };

    private void updateTitle() {
        mainItemContentBean = SalesApplication.getAppContext().getMainItemContentBean();
        title.setText(mainItemContentBean.getGoodsName() + "促销");
    }

    private final String ISIMPORT = "isImport";

    private void importantData(Context context) {
        boolean isImport = PreferencesUtils.getBoolean(context, ISIMPORT, false);
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
            FileUtil.CopyAssets(getContext(), "salespromotionzipfile", Environment.getExternalStorageDirectory().toString() + "/salespromotionzipfile");
            List<String> pathList = FileUtil.getFirstFileName(Environment.getExternalStorageDirectory().toString() + "/salespromotionzipfile");
            Log.e(TAG, "file path =" + pathList);
            if (pathList != null) {
                mList = pathList;
                mCurrentNum = 0;
                uncompress();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_item_add_btn:
                Intent intent = new Intent(this, AddBodyShowView.class);
                intent.putExtra("itemNum", currentType);
                intent.putExtra("itemType", mainItemId);
                startActivityForResult(intent, 1);
                break;
            case R.id.main_item_edit_btn:
                //显示删除按钮
                if (adapter != null) {
                    if (mCurrentShowDel) {
                        adapter.isShowDelBtn(false);
                        mCurrentShowDel = false;
                    } else {
                        adapter.isShowDelBtn(true);
                        mCurrentShowDel = true;
                    }
                }
                break;
            case R.id.main_key_power:
                currentType = SalesConstant.ItemType.POWER_TYPE;
                mainPlayKeyImg.setBackgroundResource(R.mipmap.mouse_key_power);
                updateView(v);
                updateAdapterData();
                break;
            case R.id.main_key_game:
                currentType = SalesConstant.ItemType.GAME_TYPE;
                mainPlayKeyImg.setBackgroundResource(R.mipmap.mouse_key_game);
                updateView(v);
                updateAdapterData();
                break;
            case R.id.main_key_home:
                currentType = SalesConstant.ItemType.HOME_TYPE;
                mainPlayKeyImg.setBackgroundResource(R.mipmap.mouse_key_home);
                updateView(v);
                updateAdapterData();
                break;
            case R.id.main_key_back:
                currentType = SalesConstant.ItemType.BACK_TYPE;
                mainPlayKeyImg.setBackgroundResource(R.mipmap.mouse_key_back);
                updateView(v);
                updateAdapterData();
                break;
            case R.id.main_play_mode_img:
                String mSpPlayMode = getSpByType(currentType);
                int mMode = PreferencesUtils.getInt(this, mSpPlayMode, SalesConstant.CIRCLE_MODE);
                if (mMode == SalesConstant.CIRCLE_MODE) {
                    PreferencesUtils.putInt(this, mSpPlayMode, SalesConstant.ORDER_MODE);
                    showToast("已切换单条播放");
                } else if (mMode == SalesConstant.ORDER_MODE) {
                    PreferencesUtils.putInt(this, mSpPlayMode, SalesConstant.CIRCLE_MODE);
                    showToast("已切换循环播放");
                }
                updateModeView(currentType);
                break;
//            case R.id.main_exchange_goods:
//                Intent updateIntent = new Intent(this, SalesSettingActivity.class);
//                startActivityForResult(updateIntent, 1);
//                break;
            case R.id.rlExport:
                //导出
                checkList = dataManager.queryAllContent();
                if (mStorageSelecteDialog == null) {
                    mStorageSelecteDialog = new StorageSelectedDialog(this, StorageSelectedDialog.TYPE_EXPORT);
                }
                mStorageSelecteDialog.setSelectedListener(new StorageSelectedDialog.ISelectedStorageListener() {
                    @Override
                    public void onSelected(String path) {
                        mStorageSelecteDialog.dismiss();
                        usbPath = path;
                        Log.e(TAG, "path=" + usbPath);
//                        tv_init_text.setText("正在导出,请稍候...");
//                        presenter.analysis();
                        RobotToastUtil.getInstance(MainActivity.this).showToast("正在导出，请稍候", Toast.LENGTH_LONG);
                        ThreadManager.getInstance().createShortPool().execute(runnable);
                    }
                });
                mStorageSelecteDialog.show();
                break;
            case R.id.rlImport:
                //导入
                final CustomHintDialog customHintDialog = new CustomHintDialog(getContext(), -1);
                customHintDialog.setTitle("注意");
                customHintDialog.setMessage("导入会覆盖原有数据，是否继续");
                customHintDialog.setCancleButton("取消", new CustomHintDialog.IButtonOnClickLister() {
                    @Override
                    public void onClickLister() {
                        customHintDialog.dismiss();
                    }
                });
                customHintDialog.setSubmitButton("确认", new CustomHintDialog.IButtonOnClickLister() {
                    @Override
                    public void onClickLister() {
                        if (mImportSelecteDialog == null) {
                            mImportSelecteDialog = new StorageSelectedDialog(getContext(), StorageSelectedDialog.TYPE_IMPORT);
                        }
                        mImportSelecteDialog.setSelectedFileListener(new StorageSelectedDialog.ISelectedFilePathListener() {
                            @Override
                            public void onSelected(List<String> path) {
                                mImportSelecteDialog.dismiss();
                                importData(path);
                            }
                        });
                        mImportSelecteDialog.show();
                        customHintDialog.dismiss();
                    }
                });
                customHintDialog.show();
                break;
            case R.id.main_more_model:
                //进入模板页面
                Intent intent1 = new Intent(this, MoreModelActivity.class);
                startActivityForResult(intent1, 1);

                break;
//            case R.id.main_create_model_data:
            //新建模板
//                showDialog();
//                break;
        }
    }

    private Dialog modelDialog;
    private EditText modelName;
    private RecyclerView recyclerView;
    private TextView mCancel, mConfirm;
    private ChooseGoodsAdapter chooseGoodsAdapter;
    private List<String> groupLists = new ArrayList<>();
    private int modeType = -1;
    private String mContent;

    private void showDialog() {
        if (modelDialog == null) {
            modelDialog = new Dialog(this, R.style.Dialog_Custom);
            View currentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_create_model, null);

            modelName = (EditText) currentView.findViewById(R.id.sales_create_model_name_et);
            recyclerView = (RecyclerView) currentView.findViewById(R.id.sales_create_model_list);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
            recyclerView.setLayoutManager(gridLayoutManager);
            //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
            recyclerView.setHasFixedSize(true);

            mCancel = (TextView) currentView.findViewById(R.id.cancel_btn);
            mConfirm = (TextView) currentView.findViewById(R.id.confirm_btn);
            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modelDialog.dismiss();
                }
            });
            mConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        saveModeData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    modelDialog.dismiss();
                }
            });

            groupLists.clear();
            groupLists.add("食品");
            groupLists.add("饮料");
            groupLists.add("日化");
            groupLists.add("其他");
            chooseGoodsAdapter = new ChooseGoodsAdapter(groupLists, onChooseGoodsAdapterItemListener, mContent);
            recyclerView.setAdapter(chooseGoodsAdapter);

            modelDialog.setContentView(currentView);
            Window dialogWindow = modelDialog.getWindow();
            dialogWindow.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = DisplayUtil.dipToPixel(this, 1100);
            lp.height = DisplayUtil.dipToPixel(this, 450);
            dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        modelDialog.show();
    }

    private ChooseGoodsAdapter.OnChooseDanceAdapterItemListener onChooseGoodsAdapterItemListener = new ChooseGoodsAdapter.OnChooseDanceAdapterItemListener() {
        @Override
        public void onItemClick(View v, String danceName, int position) {
            for (int i = 0; i < groupLists.size(); i++) {
                String good = groupLists.get(i);
                if (danceName.equals(good)) {
                    modeType = i;
                }
            }

        }
    };

    private void saveModeData() throws Exception {
//        if (modelName != null && !TextUtils.isEmpty(modelName.getText())
//                && modeType != -1) {
//            String mModelName = modelName.getText().toString();
//            if (modelNameDataManager.queryModelNameExits(mModelName)) {
//                Toast.makeText(this, "该模版名称已存在", Toast.LENGTH_LONG).show();
//                return;
//            } else {
//                ModelNameBean nameBean = new ModelNameBean(mModelName);
//                modelNameDataManager.insertContent(nameBean);
//            }
//            List<ItemsContentBean> itemLists = DataManager.getInstance(this).queryAllContent();
//            for (int i = 0; i < itemLists.size(); i++) {
//                ItemsContentBean itemsContentBean = itemLists.get(i);
//                try {
//                    ModelContentBean modelContentBean = new ModelContentBean(itemsContentBean);
//                    modelContentBean.setModelName(modelName.getText().toString());
//                    modelContentBean.setModelType(modeType);
//                    ModelDataManager.getInstance(this).insertContent(modelContentBean);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }


    private StorageSelectedDialog mStorageSelecteDialog;
    private StorageSelectedDialog mImportSelecteDialog;
    public static String usbPath;
    private List<ItemsContentBean> checkList;
    public final String SOURCE_JSON_PARENT_FILE = "/salespromotionzipfile";
    public final String SOURCE_JSON_FILE = "/salespromotionzipfile/salespromotionsource";
    public final String FILE_JSON_TXT = "SalesPromotionJson.txt";

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String copyPath = usbPath + SOURCE_JSON_FILE;
            boolean bool = DataFileUtils.createFile(copyPath);
            Log.e(TAG, "create file ...bool=" + bool);
            if (bool) {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                String json = JsonUtil.getJsonStrFromList(checkList);
                DataFileUtils.stringToFile(copyPath, FILE_JSON_TXT, json);

                List<String> robotPath = getMediaPath();
                List<String> copyUsbPath = getUsbCopyPath(robotPath);
                for (int copypath = 0; copypath < copyUsbPath.size(); copypath++) {
                    DataFileUtils.createAllFileOnUSB(robotPath.get(copypath), copyUsbPath.get(copypath));
                }
                String time = TimeUtil.timesOne(String.valueOf(System.currentTimeMillis()));
                mainItemContentBean = SalesApplication.getAppContext().getMainItemContentBean();
                ZipUtils.AddFolder(usbPath + SOURCE_JSON_PARENT_FILE + "/" + mainItemContentBean.getGoodsName() + "-" + time + ".zip", usbPath + SOURCE_JSON_FILE);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        presenter.clearCheckItemSence();
//                        progressBarView.setVisibility(View.GONE);
//                        RobotToastUtil.getInstance(MainActivity.this).showToast("导出成功!路径为:" + usbPath + SOURCE_JSON_PARENT_FILE, Toast.LENGTH_LONG);
                        RobotToastUtil.getInstance(MainActivity.this).showToast("导出成功!路径为:" + "本机/sales" + SOURCE_JSON_PARENT_FILE, Toast.LENGTH_LONG);
                    }
                }, 10 * 1000);
            }
        }
    };

    public List<String> getUsbCopyPath(List<String> robotPath) {
        List<String> usbCopyPath = new ArrayList<>();
        for (int i = 0; i < robotPath.size(); i++) {
            usbCopyPath.add(robotPath.get(i).replace("/storage/emulated/0/", usbPath + SOURCE_JSON_FILE + "/"));
            Log.e("zhang", "robotPath==" + robotPath.get(i));
            Log.e("zhang", "usbpath==" + usbCopyPath.get(i));
        }
        return usbCopyPath;
    }


    private List<String> getMediaPath() {
        return dataManager.queryAllMedia();
    }


    private List<String> mList;//解压zip
    private String type;
    private int mCurrentNum = 0;
    private ImportDialog mImportDialog;

    public void importData(List<String> path) {
        Log.e(TAG, "path=" + path);

        if (mImportDialog == null) {
            mImportDialog = new ImportDialog(getContext(), R.layout.import_dialog);
            mImportDialog.setCanceledOnTouchOutside(false);
            mImportDialog.show();
        } else {
            if (!mImportDialog.isShowing()) {
                mImportDialog.show();
            }
        }
        TextView textView = (TextView) mImportDialog.findViewById(R.id.import_tv);
        textView.setText(getContext().getString(R.string.importing));
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
                    dataManager.deleteAllContent();
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
                    if (!TextUtils.isEmpty(filePath)) {
                        String json = FileUtil.readFile(SalesConstant.FILE_JSON);//读内容
                        //复制多媒体文件
                        int result = FileUtil.copy(SalesConstant.FILE_PATH + File.separator + "salespromotionsource", Environment.getExternalStorageDirectory().toString(), true);//把数据拷贝到根目录
                        if (result == 0) {
                            //多媒体文件复制成功，开始插库
                            List<ItemsContentBean> list = JsonUtil.getListFromJsonStr(json);
                            if (list != null) {
                                String mfilePath = mList.get(mCurrentNum);
                                L.e(TAG, "uncompress isSuccess = " + " filePath=" + filePath);
                                Boolean isImport = PreferencesUtils.getBoolean(MainActivity.this, ISIMPORT, false);
                                if (!isImport) {
                                    for (int i = 0; i < list.size(); i++) {
                                        ItemsContentBean itemsContentBean = list.get(i);
                                        try {
                                            ModelContentBean modelContentBean = new ModelContentBean(itemsContentBean);
                                            if (mfilePath.contains("food")) {
                                                modelContentBean.setModelName("食品促销模版");
                                                modelContentBean.setModelType(0);
                                                if (mainItemContentBean.getGoodsGroup().equals("食品")) {
                                                    dataManager.insertContentByResult(itemsContentBean);
                                                }
                                            } else if (mfilePath.contains("drink")) {
                                                modelContentBean.setModelName("饮料促销模版");
                                                modelContentBean.setModelType(1);
                                                if (mainItemContentBean.getGoodsGroup().equals("饮料")) {
                                                    dataManager.insertContentByResult(itemsContentBean);
                                                }
                                            } else if (mfilePath.contains("daily")) {
                                                modelContentBean.setModelName("日化促销模版");
                                                modelContentBean.setModelType(2);
                                                if (mainItemContentBean.getGoodsGroup().equals("日化")) {
                                                    dataManager.insertContentByResult(itemsContentBean);
                                                }
                                            } else if (mfilePath.contains("other")) {
                                                modelContentBean.setModelName("其他促销模版");
                                                modelContentBean.setModelType(3);
                                                if (mainItemContentBean.getGoodsGroup().equals("其他")) {
                                                    dataManager.insertContentByResult(itemsContentBean);
                                                }
                                            }
                                            ModelDataManager.getInstance(MainActivity.this).insertContent(modelContentBean);
                                            if (!modelNameDataManager.queryModelNameExits(modelContentBean.getModelName())) {
                                                modelNameDataManager.insertContent(new ModelNameBean(modelContentBean.getModelName(), modelContentBean.getModelType()));
                                            }
//                                            if (isSuccess) {
//                                                mCurrentNum++;
//                                                uncompress();
//                                            } else
                                            {
                                                mHandle.sendEmptyMessage(125);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    mCurrentNum++;
                                    uncompress();
                                } else {
                                    for (int i = 0; i < list.size(); i++) {
                                        ItemsContentBean itemsContentBean = list.get(i);
                                        boolean isSuccess = dataManager.insertContentByResult(itemsContentBean);
//                                        if (isSuccess) {
//
//                                        } else
                                        {
                                            mHandle.sendEmptyMessage(CompressStatus.ERROR);
                                        }
                                    }
                                    mCurrentNum++;
                                    uncompress();
                                }

                            }

                        } else {
                            mHandle.sendEmptyMessage(CompressStatus.ERROR);
                        }
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
        } else {
            Boolean isImport = PreferencesUtils.getBoolean(MainActivity.this, ISIMPORT, false);
            if (!isImport) {
                PreferencesUtils.putBoolean(MainActivity.this, ISIMPORT, true);
                FileUtil.deleteFile(new File(Environment.getExternalStorageDirectory().toString() + "/salespromotionzipfile"));
            }

            mHandle.sendEmptyMessage(CompressStatus.SUCCESS);
        }
    }

    /**
     * 复制assets下文件到SD卡
     */
    public static void CopyAssets(Context context, String assetDir, String dir) {
        String[] files;
        try {
            files = context.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return;
        }
        File mWorkingPath = new File(dir);
        // 如果文件路径不存在
        if (!mWorkingPath.exists()) {
            mWorkingPath.mkdirs();
        }
        for (int i = 0; i < files.length; i++) {
            try {
                // 获得每个文件的名字
                String fileName = files[i];
                // 根据路径判断是文件夹还是文件
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        CopyAssets(context, fileName, dir + fileName + "/");
                    } else {
                        CopyAssets(context, assetDir + "/" + fileName, dir + "/"
                                + fileName + "/");
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists())
                    outFile.delete();
                InputStream in = null;
                if (0 != assetDir.length())
                    in = context.getAssets().open(assetDir + "/" + fileName);
                else
                    in = context.getAssets().open(fileName);
                OutputStream out = new FileOutputStream(outFile);
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initAdapter() {
        list = dataManager.queryItem(SalesConstant.ItemType.POWER_TYPE, mainItemId);
        adapter = new MainItemAdapter(this, list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    private void updateAdapterData() {
        list = dataManager.queryItem(currentType, mainItemId);
        adapter.updateSourceData(list);
    }

    private View lastView;

    private void updateView(View view) {
        if (lastView != null) {
            lastView.setSelected(false);
        }
        view.setSelected(true);
        lastView = view;
        updateModeView(currentType);
    }

    private void updateModeView(int currentType) {
        String playModeTypeSp = getSpByType(currentType);
        int mMode = PreferencesUtils.getInt(this, playModeTypeSp, SalesConstant.CIRCLE_MODE);
        if (mMode == SalesConstant.CIRCLE_MODE) {
            mainPlayModeImg.setBackgroundResource(R.mipmap.circle);
            playModeHintTv.setText("顺序循环播放");
        } else if (mMode == SalesConstant.ORDER_MODE) {
            mainPlayModeImg.setBackgroundResource(R.mipmap.order);
            playModeHintTv.setText("顺序单条播放");
        }
    }

    private String getSpByType(int currentType) {
        String playModeTypeSp = "";
        if (currentType == SalesConstant.ItemType.BACK_TYPE) {
            playModeTypeSp = SalesConstant.POWER_PLAY_MODE;
        } else if (currentType == SalesConstant.ItemType.GAME_TYPE) {
            playModeTypeSp = SalesConstant.GAME_PLAY_MODE;
        } else if (currentType == SalesConstant.ItemType.HOME_TYPE) {
            playModeTypeSp = SalesConstant.HOME_PLAY_MODE;
        } else if (currentType == SalesConstant.ItemType.BACK_TYPE) {
            playModeTypeSp = SalesConstant.BACK_PLAY_MODE;
        }
        return playModeTypeSp;
    }

    private LoadingDialog mLoadingDialog;

    public void showLoadingDialog(boolean bool) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        if (bool) {
            mLoadingDialog.show();
        } else {
            mLoadingDialog.dismiss();
        }
    }

    public void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.equals(mListView)) {
            //TODO 进入编辑页面
            Intent intent = new Intent(this, AddBodyShowView.class);
            intent.putExtra("content", list.get(position));
            startActivityForResult(intent, currentType);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            updateAdapterData();
        } else if (resultCode == 2) {
            String modelName = data.getStringExtra("modelName");
            if (modelName != null && !TextUtils.isEmpty(modelName)) {
                dataManager.deleteAllContent();
                List<ModelContentBean> list = ModelDataManager.getInstance(this).queryItem(modelName);
                for (int i = 0; i < list.size(); i++) {
                    ModelContentBean modelContentBean = list.get(i);
                    try {
                        ItemsContentBean itemsContentBean = new ItemsContentBean(modelContentBean);
                        dataManager.insertContent(itemsContentBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            updateAdapterData();
            updateTitle();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SalesApplication.from(this).isNeedStartService = false;
    }
}
