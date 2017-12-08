package com.efrobot.salespromotion.activity.more;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.efrobot.salespromotion.R;
import com.efrobot.salespromotion.SalesApplication;
import com.efrobot.salespromotion.activity.ModelNameBean;
import com.efrobot.salespromotion.bean.MainItemContentBean;
import com.efrobot.salespromotion.db.MainDataManager;
import com.efrobot.salespromotion.db.ModelNameDataManager;

import java.util.ArrayList;
import java.util.List;

public class MoreModelActivity extends Activity implements View.OnClickListener {

    private RecyclerView recyclerView, contentRecylerView;

    private List<String> groupLists = new ArrayList<>();
    private List<ModelNameBean> contentLists = new ArrayList<>();

    private ChooseMoreAdapter groupAdapter;
    private ChooseContentAdapter contentAdapter;
    private String mContent;
    private String picPath = "";
    private String goodsGroupStr, goodsContentStr;
    private int modelType = 0;
    private MainItemContentBean mainItemContentBean;

    private EditText modelName, modelDetail;

    private TextView saveBtn;

    private int id = 0;

    private TextView addPicBtn, picPathTv;

    private ImageView delPicImg;
    private IntentFilter intentFilter;
    private ResourceBroadcastReceiver resourcereceiver;

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_model);

        mainItemContentBean = SalesApplication.getAppContext().getMainItemContentBean();
        modelType = mainItemContentBean.getItemType();
        mContent = mainItemContentBean.getGoodsGroup();
        picPath = mainItemContentBean.getSpareOne();
        id = mainItemContentBean.getId();

        initView();
        initData();
        registerFileManager();

    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.sales_more_group_list);
        contentRecylerView = (RecyclerView) findViewById(R.id.sales_more_group_content_list);
        modelName = (EditText) findViewById(R.id.sales_setting_name);
        modelDetail = (EditText) findViewById(R.id.sales_setting_activity);
        findViewById(R.id.sales_more_finish_btn).setOnClickListener(this);


        addPicBtn = (TextView) findViewById(R.id.more_add_picture_btn);
        addPicBtn.setOnClickListener(this);
        picPathTv = (TextView) findViewById(R.id.more_add_picture_view);
        delPicImg = (ImageView) findViewById(R.id.more_del_picture_view);
        delPicImg.setOnClickListener(this);


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

        if(!TextUtils.isEmpty(picPath)){
            String showPath = picPath.substring(picPath.lastIndexOf("/") + 1, picPath.length());
            picPathTv.setText(showPath);
            delPicImg.setVisibility(View.VISIBLE);
        }

        groupLists.add("食品");
        groupLists.add("饮料");
        groupLists.add("日化");
        groupLists.add("其他");
        groupAdapter = new ChooseMoreAdapter(groupLists, onChooseGoodsAdapterItemListener, mContent);
        recyclerView.setAdapter(groupAdapter);
        goodsGroupStr = mContent;

        modelType = getTypeByName(mContent);
        contentLists = ModelNameDataManager.getInstance(this).queryListByType(modelType);
        goodsContentStr = contentLists.get(0).getModelName();
        contentAdapter = new ChooseContentAdapter(contentLists, onChooseContentAdapterItemListener, goodsContentStr);
        contentRecylerView.setAdapter(contentAdapter);
    }

    private ChooseMoreAdapter.OnChooseDanceAdapterItemListener onChooseGoodsAdapterItemListener = new ChooseMoreAdapter.OnChooseDanceAdapterItemListener() {
        @Override
        public void onItemClick(View v, String danceName, int position) {
            goodsGroupStr = danceName;
            modelType = getTypeByName(danceName);
            contentLists = ModelNameDataManager.getInstance(MoreModelActivity.this).queryListByType(modelType);
            goodsContentStr = contentLists.get(0).getModelName();
            if (contentAdapter != null) {
                contentAdapter.updateContent(contentLists, goodsContentStr);
            }

        }
    };

    private ChooseContentAdapter.OnChooseDanceAdapterItemListener onChooseContentAdapterItemListener = new ChooseContentAdapter.OnChooseDanceAdapterItemListener() {
        @Override
        public void onItemClick(View v, String danceName, int position) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sales_more_finish_btn:
                if (!isExcute) {
                    isExcute = true;
                    if (save()) {
                        Intent intent = new Intent();
                        intent.putExtra("modelName", goodsContentStr);
                        setResult(2, intent);
                        finish();
                    } else {
                        Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.more_add_picture_btn:
                if(TextUtils.isEmpty(picPathTv.getText())) {
                    toAddMedia("image");
                } else {
                    Toast.makeText(this, "已经添加了图片,请先删除", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.more_del_picture_view:
                picPathTv.setText("");
                delPicImg.setVisibility(View.GONE);
                picPath = "";
                break;
        }
    }

    private boolean save() {
        boolean isSuccess = false;
        if (!TextUtils.isEmpty(modelName.getText()) &&
                !TextUtils.isEmpty(goodsGroupStr) &&
                !TextUtils.isEmpty(modelDetail.getText())) {
            if (goodsGroupStr.equals(mContent)) {
                goodsContentStr = "";
            }
            MainItemContentBean mainItemContentBean = new MainItemContentBean();
            mainItemContentBean.setId(id);
            mainItemContentBean.setGoodsName(modelName.getText().toString());
            mainItemContentBean.setGoodsGroup(goodsGroupStr);
            mainItemContentBean.setGoodsDescription(modelDetail.getText().toString());
            mainItemContentBean.setSpareOne(picPath);
            MainDataManager.getInstance(this).updateContent(mainItemContentBean);

            isSuccess = true;
        } else {
            isSuccess = false;
        }
        isExcute = false;
        return isSuccess;
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
                        String shoePath = path.substring(path.lastIndexOf("/") + 1, path.length());
                        picPathTv.setText(shoePath);
                        delPicImg.setVisibility(View.VISIBLE);
                        picPath = path;
                    }
                }
            }
        }

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
        if(resourcereceiver != null) {
            unregisterReceiver(resourcereceiver);
        }
    }
}
