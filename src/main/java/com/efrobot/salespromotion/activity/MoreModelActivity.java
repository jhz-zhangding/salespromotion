package com.efrobot.salespromotion.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.efrobot.salespromotion.R;
import com.efrobot.salespromotion.SalesApplication;
import com.efrobot.salespromotion.bean.MainItemContentBean;
import com.efrobot.salespromotion.bean.ModelContentBean;
import com.efrobot.salespromotion.bean.ModelNameBean;
import com.efrobot.salespromotion.db.MainDataManager;
import com.efrobot.salespromotion.db.ModelContentManager;
import com.efrobot.salespromotion.db.ModelNameDataManager;
import com.efrobot.salespromotion.interfaces.ImportEvent;
import com.efrobot.salespromotion.utils.BitmapUtils;
import com.efrobot.salespromotion.utils.DisplayUtil;
import com.efrobot.salespromotion.utils.ImportAndExportUtils;
import com.efrobot.salespromotion.utils.PreferencesUtils;
import com.efrobot.salespromotion.utils.ui.CustomHintDialog;
import com.efrobot.salespromotion.utils.ui.EditContentDialog;

import java.util.ArrayList;
import java.util.List;

public class MoreModelActivity extends Activity implements View.OnClickListener {

    private RecyclerView recyclerView, contentRecylerView;

    private List<String> groupLists = new ArrayList<>();
    private List<ModelNameBean> contentLists = new ArrayList<>();

    private ChooseMoreAdapter groupAdapter;
    private ChooseContentAdapter contentAdapter;
    private String mContent;
    private List<String> picPaths = new ArrayList<>();
    private String goodsGroupStr, goodsContentStr;
    private int modelType = 0;
    private MainItemContentBean mainItemContentBean;

    private EditText modelName, modelDetail;

    private TextView saveBtn;

    private int id = 0;

    private IntentFilter intentFilter;
    private ResourceBroadcastReceiver resourcereceiver;
    private LinearLayout container;
    private String picPath;

    private ImportAndExportUtils mUtils;

    private String currentModelName = "";


    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_model);

        mUtils = new ImportAndExportUtils(this);
        currentModelName = getIntent().getStringExtra("currentModelName");

        mainItemContentBean = SalesApplication.getAppContext().getMainItemContentBean();
        modelType = mainItemContentBean.getItemType();
        mContent = mainItemContentBean.getGoodsGroup();
        picPath = mainItemContentBean.getSpareOne();
        id = mainItemContentBean.getId();
        container = (LinearLayout) findViewById(R.id.picture_container);

        initView();
        initData();
        registerFileManager();

        findViewById(R.id.rlImport).setOnClickListener(this);
        findViewById(R.id.rlExport).setOnClickListener(this);
        findViewById(R.id.rlDelete).setOnClickListener(this);
        findViewById(R.id.rlCreate).setOnClickListener(this);
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.sales_more_group_list);
        contentRecylerView = (RecyclerView) findViewById(R.id.sales_more_group_content_list);
        modelName = (EditText) findViewById(R.id.sales_setting_name);
        modelDetail = (EditText) findViewById(R.id.sales_setting_activity);
        findViewById(R.id.sales_more_finish_btn).setOnClickListener(this);
        findViewById(R.id.more_add_picture_btn).setOnClickListener(this);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);

        GridLayoutManager contentGridLayoutManager = new GridLayoutManager(this, 4);
        contentRecylerView.setLayoutManager(contentGridLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        contentRecylerView.setHasFixedSize(true);
    }

    private void initData() {
        modelName.setText(mainItemContentBean.getGoodsName());
        modelDetail.setText(mainItemContentBean.getGoodsDescription());

        if (!TextUtils.isEmpty(picPath)) {
            if (picPath.contains("@#")) {
                String[] strings = picPath.split("@#");
                for (int i = 0; i < strings.length; i++) {
                    picPaths.add(strings[i]);
                    Message message = handler.obtainMessage();
                    message.what = SHOW_PIC;
                    message.obj = strings[i];
                    handler.sendMessage(message);
                }
            } else {
                picPaths.add(picPath);
                Message message = handler.obtainMessage();
                message.what = SHOW_PIC;
                message.obj = picPath;
                handler.sendMessage(message);
            }
        }

        groupLists.add("食品");
        groupLists.add("饮料");
        groupLists.add("日化");
        groupLists.add("其他");

        //分类列表
        groupAdapter = new ChooseMoreAdapter(groupLists, onChooseGoodsAdapterItemListener, mContent);
        recyclerView.setAdapter(groupAdapter);
        goodsGroupStr = mContent;

        //模板列表
        modelType = getTypeByName(mContent);
        contentLists = ModelNameDataManager.getInstance(this).queryListByType(modelType);
        goodsContentStr = currentModelName;
        contentAdapter = new ChooseContentAdapter(contentLists, onChooseContentAdapterItemListener, goodsContentStr);
        contentRecylerView.setAdapter(contentAdapter);
    }

    private ChooseMoreAdapter.OnChooseDanceAdapterItemListener onChooseGoodsAdapterItemListener = new ChooseMoreAdapter.OnChooseDanceAdapterItemListener() {
        @Override
        public void onItemClick(View v, String danceName, int position) {
            //分类选中
            goodsGroupStr = danceName;
            modelType = getTypeByName(danceName);
            contentLists = ModelNameDataManager.getInstance(MoreModelActivity.this).queryListByType(modelType);
//            goodsContentStr = contentLists.get(0).getModelName();
            if (contentAdapter != null) {
                contentAdapter.updateContent(contentLists, goodsContentStr);
            }

        }
    };

    private ChooseContentAdapter.OnChooseDanceAdapterItemListener onChooseContentAdapterItemListener = new ChooseContentAdapter.OnChooseDanceAdapterItemListener() {
        @Override
        public void onItemClick(View v, String danceName, int position) {
            //
            goodsContentStr = danceName;

        }
    };

    private int getTypeByName(String name) {
        int type = 0;
        for (int i = 0; i < groupLists.size(); i++) {
            if (groupLists.get(i).equals(name)) {
                type = i;
                break;
            }
        }
        return type;
    }

    boolean isExcute = false;


    private final int SHOW_PIC = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_PIC:
                    addPicChildView(container, msg.obj.toString());
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sales_more_finish_btn:
                if (!isExcute) {
                    isExcute = true;
                    if (save()) {
                        finishByResult();
                    } else {
                        Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.more_add_picture_btn:
                if (picPaths.size() > 4) {
                    Toast.makeText(this, "最多添加5张图片,请先删除", Toast.LENGTH_SHORT).show();
                } else {
                    toAddMedia("image");
                }
                break;
            case R.id.rlImport:
                mUtils.importData(new ImportEvent() {
                    @Override
                    public void success() {
                        //导入成功
                    }

                    @Override
                    public void failed() {
                        //导入失败
                    }
                });
                break;
            case R.id.rlExport:
                List<ModelContentBean> list = ModelContentManager.getInstance(this).queryItem(currentModelName);
                mUtils.exportData(list);
                break;
            case R.id.rlDelete:
                showDeleteDialog("是否删除此模板", currentModelName);
                break;
            case R.id.rlCreate:
                showCreateDialog("");
                break;
        }
    }

    private boolean save() {
        boolean isSuccess = false;
        if (!TextUtils.isEmpty(modelName.getText()) &&
                !TextUtils.isEmpty(goodsGroupStr) &&
                !TextUtils.isEmpty(modelDetail.getText())) {
            MainItemContentBean mainItemContentBean = new MainItemContentBean();
            mainItemContentBean.setId(id);
            mainItemContentBean.setGoodsName(modelName.getText().toString());
            mainItemContentBean.setGoodsGroup(goodsGroupStr);
            mainItemContentBean.setGoodsDescription(modelDetail.getText().toString());

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < picPaths.size(); i++) {
                if (i < picPaths.size() - 1) {
                    sb.append(picPaths.get(i)).append("@#");
                } else {
                    sb.append(picPaths.get(i));
                }
            }
            String mSb = sb.toString();
            Log.e("mainItemContentBean", "mSb = " + mSb);
            mainItemContentBean.setSpareOne(mSb);

            MainDataManager.getInstance(this).updateContent(mainItemContentBean);

            isSuccess = true;
        } else {
            isSuccess = false;
        }
        isExcute = false;
        return isSuccess;
    }

    /**
     * 新建模板提示框
     */
    private void showCreateDialog(String content) {
        final EditContentDialog dialog = new EditContentDialog(this, -1);
        dialog.setMessage(content);
        dialog.setCancleButton(this.getString(R.string.cancel), new EditContentDialog.IButtonOnClickLister() {
            @Override
            public void onClickLister() {
                dialog.dismiss();
            }
        });
        dialog.setSubmitButton(this.getString(R.string.confirm), new EditContentDialog.IButtonOnClickLister() {
            @Override
            public void onClickLister() {
                String result = dialog.getEditTextContent();

                if (!TextUtils.isEmpty(result)) {
                    dialog.dismiss();
                    ModelNameBean modelNameBean = new ModelNameBean();
                    modelNameBean.setModelName(result);
                    modelNameBean.setModelType(modelType);
                    ModelNameDataManager.getInstance(MoreModelActivity.this).insertContent(modelNameBean);
                    goodsContentStr = result;
                    finishByResult();
                } else {
                    Toast.makeText(MoreModelActivity.this, "模版名称不能空", Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.show();
    }

    private void finishByResult() {
        Intent intent = new Intent();
        intent.putExtra("modelName", goodsContentStr);
        setResult(2, intent);
        PreferencesUtils.putString(MoreModelActivity.this, "currentModelName", goodsContentStr);
        finish();
    }

    /**
     * 删除模板提示框
     */
    private void showDeleteDialog(String content, final String mModelName) {
        final CustomHintDialog dialog = new CustomHintDialog(this, -1);
        dialog.setMessage(content);
        dialog.setCancleButton(this.getString(R.string.cancel), new CustomHintDialog.IButtonOnClickLister() {
            @Override
            public void onClickLister() {
                dialog.dismiss();
            }
        });
        dialog.setSubmitButton(this.getString(R.string.confirm), new CustomHintDialog.IButtonOnClickLister() {
            @Override
            public void onClickLister() {
                //删除模板
                deleteModel(mModelName);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteModel(String modelName) {
        ModelNameDataManager.getInstance(MoreModelActivity.this).deleteContentByName(modelName);
        ModelContentManager.getInstance(MoreModelActivity.this).deleteContentByModelName(modelName);
        //更新模版列表
        contentLists = ModelNameDataManager.getInstance(MoreModelActivity.this).queryListByType(modelType);
        if (contentAdapter != null) {
            contentAdapter.updateContent(contentLists, goodsContentStr);
        }
    }

    private void registerFileManager() {
        //文件管理数据接受广播
        intentFilter = new IntentFilter();
        //添加过滤的Action值；
        intentFilter.addAction("efrobot.robot.resoure");

        //实例化广播监听器；
        resourcereceiver = new ResourceBroadcastReceiver();

        //将广播监听器和过滤器注册在一起；
        registerReceiver(resourcereceiver, intentFilter);
    }

    class ResourceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("efrobot.robot.resoure")) {
                String path = intent.getStringExtra("path");
                String selectType = intent.getStringExtra("type");
                Log.e("zhang", "接受广播多媒体地址=====" + path);
                if (!TextUtils.isEmpty(path)) {
                    if ("image".equals(selectType)) {
                        picPaths.add(path);
                        Message message = handler.obtainMessage();
                        message.what = SHOW_PIC;
                        message.obj = path;
                        handler.sendMessage(message);
                    }
                }
            }
        }

    }

    private void addPicChildView(final LinearLayout parent, final String path) {
        final RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setGravity(Gravity.TOP | Gravity.RIGHT);

        final ImageView imageView = new ImageView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DisplayUtil.dipToPixel(this, 100), DisplayUtil.dipToPixel(this, 100));
        imageView.setLayoutParams(layoutParams);
        Bitmap bitmap = BitmapUtils.getimage(path);
        imageView.setImageBitmap(bitmap);
        relativeLayout.addView(imageView);

        ImageView delImageView = new ImageView(this);
        delImageView.setImageResource(R.mipmap.del);
        delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageBitmap(null);
                parent.removeView(relativeLayout);
                picPaths.remove(path);
            }
        });

        relativeLayout.addView(delImageView);
        parent.addView(relativeLayout);
    }


    /**
     * 添加图片或视频
     */
    public void toAddMedia(String check) {
        try {
            Intent intent = new Intent("efrobot.robot.bodyshow");
            intent.putExtra("pick_folder", true);
            intent.putExtra("come", 1);
            intent.putExtra("check", check);
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("zhang", "e=" + e.toString());
            Toast.makeText(this, "打开文件管理失败", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resourcereceiver != null) {
            unregisterReceiver(resourcereceiver);
        }
    }

    @Override
    public void onBackPressed() {

        //返回按键时需要更新一下主页面数据
        finishByResult();

        super.onBackPressed();
    }
}
