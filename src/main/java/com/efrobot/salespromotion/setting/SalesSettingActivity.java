package com.efrobot.salespromotion.setting;

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
import com.efrobot.salespromotion.adapter.ChooseGoodsAdapter;
import com.efrobot.salespromotion.bean.MainItemContentBean;
import com.efrobot.salespromotion.db.MainDataManager;

import java.util.ArrayList;
import java.util.List;

public class SalesSettingActivity extends Activity implements View.OnClickListener {

    private RecyclerView recyclerView;

    private List<String> groupLists = new ArrayList<>();

    private String goodsNameStr = "", goodsGroupStr = "", goodsDescriptionStr = "";

    private String mContent = "";

    private EditText goodsName;

    private EditText goodsDetail;

    private TextView saveBtn;
    private ChooseGoodsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_setting);

        //替换默认商品词条
        List<MainItemContentBean> list = MainDataManager.getInstance(this).queryAllContent();
        if (null != list && list.size() > 0) {
            int lastIndex = list.size() - 1;
            goodsNameStr = list.get(lastIndex).getGoodsName();
            goodsGroupStr = list.get(lastIndex).getGoodsGroup();
            goodsDescriptionStr = list.get(lastIndex).getGoodsDescription();
        }

        goodsName = (EditText) findViewById(R.id.sales_setting_name);
        goodsDetail = (EditText) findViewById(R.id.sales_setting_activity_detail);
        saveBtn = (TextView) findViewById(R.id.sales_setting_save);
        saveBtn.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.sales_setting_group_list);
        groupLists.add("食品");
        groupLists.add("饮料");
        groupLists.add("日化");
        groupLists.add("其他");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);

        adapter = new ChooseGoodsAdapter(groupLists, onChooseGoodsAdapterItemListener, mContent);
        recyclerView.setAdapter(adapter);

        if (!TextUtils.isEmpty(goodsNameStr)) {
            initData();
        }
    }

    private void initData() {
        goodsName.setText(goodsNameStr);
        mContent = goodsGroupStr;
        if (adapter != null) {
            adapter.updateContent(mContent);
        }
        goodsDetail.setText(goodsDescriptionStr);
    }

    private ChooseGoodsAdapter.OnChooseDanceAdapterItemListener onChooseGoodsAdapterItemListener = new ChooseGoodsAdapter.OnChooseDanceAdapterItemListener() {
        @Override
        public void onItemClick(View v, String danceName, int position) {
            goodsGroupStr = danceName;
        }
    };

    @Override
    public void onClick(View view) {
        if (view.equals(saveBtn)) {
            if (save()) {
                Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(1, intent);
                finish();
            } else
                Toast.makeText(this, "保存失败,输入不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean save() {
        boolean isSuccess = false;
        if (!TextUtils.isEmpty(goodsName.getText()) && !TextUtils.isEmpty(goodsGroupStr) && !TextUtils.isEmpty(goodsDetail.getText())) {
            MainItemContentBean mainItemContentBean = new MainItemContentBean();
            mainItemContentBean.setGoodsName(goodsName.getText().toString());
            mainItemContentBean.setGoodsGroup(goodsGroupStr);
            mainItemContentBean.setGoodsDescription(goodsDetail.getText().toString());
            MainDataManager.getInstance(this).insertContent(mainItemContentBean);
            isSuccess = true;
        } else {
            isSuccess = false;
        }
        return isSuccess;
    }
}
