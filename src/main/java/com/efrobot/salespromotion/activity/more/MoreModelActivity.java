package com.efrobot.salespromotion.activity.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private String goodsGroupStr, goodsContentStr;
    private int modelType = 0;
    private MainItemContentBean mainItemContentBean;

    private EditText modelName, modelDetail;

    private TextView saveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_model);

        mainItemContentBean = SalesApplication.getAppContext().getMainItemContentBean();
        modelType = mainItemContentBean.getItemType();
        mContent = mainItemContentBean.getGoodsGroup();

        initView();
        initData();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.sales_more_group_list);
        contentRecylerView = (RecyclerView) findViewById(R.id.sales_more_group_content_list);
        modelName = (EditText) findViewById(R.id.sales_setting_name);
        modelDetail = (EditText) findViewById(R.id.sales_setting_activity);
        findViewById(R.id.sales_more_finish_btn).setOnClickListener(this);

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
            mainItemContentBean.setGoodsName(modelName.getText().toString());
            mainItemContentBean.setGoodsGroup(goodsGroupStr);
            mainItemContentBean.setGoodsDescription(modelDetail.getText().toString());
            MainDataManager.getInstance(this).insertContent(mainItemContentBean);

            isSuccess = true;
        } else {
            isSuccess = false;
        }
        isExcute = false;
        return isSuccess;
    }
}
