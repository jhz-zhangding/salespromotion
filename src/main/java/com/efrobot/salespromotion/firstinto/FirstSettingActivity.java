package com.efrobot.salespromotion.firstinto;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.efrobot.salespromotion.Env.SalesConstant;
import com.efrobot.salespromotion.R;
import com.efrobot.salespromotion.adapter.ChooseGoodsAdapter;
import com.efrobot.salespromotion.bean.MainItemContentBean;
import com.efrobot.salespromotion.db.MainDataManager;
import com.efrobot.salespromotion.main.MainActivity;
import com.efrobot.salespromotion.utils.BitmapUtils;
import com.efrobot.salespromotion.utils.DisplayUtil;
import com.efrobot.salespromotion.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class FirstSettingActivity extends Activity implements View.OnClickListener {

    private EditText nameEt;

    private TextView stepOneFinish, stepTwoFinish, stepThreeFinish;

    private View page1, page2, page3;

    private RecyclerView recyclerView;

    private List<String> groupLists = new ArrayList<>();

    private EditText detailEt;

    private String goodsNameStr = "", goodsGroupStr = "", goodsDescriptionStr = "";

    private String mContent = "";

    private IntentFilter intentFilter;
    private ResourceBroadcastReceiver resourcereceiver;

    private List<String> picPaths = new ArrayList<>();
    private LinearLayout container;

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
        List<MainItemContentBean> list = MainDataManager.getInstance(this).queryAllContent();
        if (null != list && list.size() > 0) {
            MainActivity.startSelfActivity(this, null);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_setting);

        nameEt = (EditText) findViewById(R.id.first_product_name_et);
        page1 = findViewById(R.id.page_view_1);
        page2 = findViewById(R.id.page_view_2);
        page3 = findViewById(R.id.page_view_3);
        container = (LinearLayout) findViewById(R.id.picture_container);
        stepOneFinish = (TextView) findViewById(R.id.first_next_btn_1);
        stepOneFinish.setOnClickListener(this);
        stepTwoFinish = (TextView) findViewById(R.id.first_next_btn_2);
        stepTwoFinish.setOnClickListener(this);
        stepThreeFinish = (TextView) findViewById(R.id.first_next_btn_3);
        stepThreeFinish.setOnClickListener(this);
        findViewById(R.id.add_picture_btn).setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.sales_first_group_list);
        groupLists.add("食品");
        groupLists.add("饮料");
        groupLists.add("日化");
        groupLists.add("其他");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);

        ChooseGoodsAdapter adapter = new ChooseGoodsAdapter(groupLists, onChooseDanceAdapterItemListener, mContent);
        recyclerView.setAdapter(adapter);


        detailEt = (EditText) findViewById(R.id.first_product_detail_et);


        //文件管理数据接受广播
        intentFilter = new IntentFilter();
        //添加过滤的Action值；
        intentFilter.addAction("efrobot.robot.resoure");

        //实例化广播监听器；
        resourcereceiver = new ResourceBroadcastReceiver();

        //将广播监听器和过滤器注册在一起；
        registerReceiver(resourcereceiver, intentFilter);
    }

    private ChooseGoodsAdapter.OnChooseDanceAdapterItemListener onChooseDanceAdapterItemListener = new ChooseGoodsAdapter.OnChooseDanceAdapterItemListener() {
        @Override
        public void onItemClick(View v, String danceName, int position) {
            goodsGroupStr = danceName;
        }
    };

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_next_btn_1:
                if (!TextUtils.isEmpty(nameEt.getText())) {
                    goodsNameStr = nameEt.getText().toString();
                    page1.setVisibility(View.GONE);
                    page2.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "商品名字不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.first_next_btn_2:
                if (!TextUtils.isEmpty(goodsGroupStr)) {
                    page2.setVisibility(View.GONE);
                    page3.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "分类不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.first_next_btn_3:
                if (!TextUtils.isEmpty(detailEt.getText())) {
                    goodsDescriptionStr = detailEt.getText().toString();
                    if (save()) {
                        List<MainItemContentBean> lists = MainDataManager.getInstance(this).queryAllContent();
                        if (lists != null && lists.size() > 0) {
                            int id = lists.get(lists.size() - 1).getId();
                            PreferencesUtils.putInt(this, SalesConstant.LAST_OPEN_ACTIVITY_ID, id);
                        }
                        MainActivity.startSelfActivity(this, null);
                        finish();
                    }
                } else {
                    Toast.makeText(this, "活动描述不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.add_picture_btn:
                if (picPaths.size() > 4) {
                    Toast.makeText(this, "最多添加5张图片,请先删除", Toast.LENGTH_SHORT).show();
                } else {
                    toAddMedia("image");
                }
                break;
        }
    }

    private boolean save() {
        boolean isSuccess = false;
        if (!TextUtils.isEmpty(goodsNameStr) && !TextUtils.isEmpty(goodsGroupStr) && !TextUtils.isEmpty(goodsDescriptionStr)) {
            MainItemContentBean mainItemContentBean = new MainItemContentBean();
            mainItemContentBean.setGoodsName(goodsNameStr);
            mainItemContentBean.setGoodsGroup(goodsGroupStr);
            mainItemContentBean.setGoodsDescription(goodsDescriptionStr);

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

            MainDataManager.getInstance(this).insertContent(mainItemContentBean);
            isSuccess = true;
        } else {
            isSuccess = false;
        }
        return isSuccess;
    }

    class ResourceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("efrobot.robot.resoure")) {
                final String path = intent.getStringExtra("path");
                final String selectType = intent.getStringExtra("type");
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
            Toast.makeText(FirstSettingActivity.this, "打开文件管理失败", Toast.LENGTH_SHORT).show();
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
