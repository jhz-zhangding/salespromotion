package com.efrobot.salespromotion.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.efrobot.salespromotion.Env.SalesConstant;
import com.efrobot.salespromotion.R;
import com.efrobot.salespromotion.SalesApplication;
import com.efrobot.salespromotion.bean.ItemsContentBean;
import com.efrobot.salespromotion.bean.MainItemContentBean;
import com.efrobot.salespromotion.db.DataManager;
import com.efrobot.salespromotion.db.MainDataManager;

import java.util.List;

/**
 * Created by zd on 2017/11/11.
 */
public class MainItemAdapter extends BaseAdapter {

    private Context mContext;

    private List<ItemsContentBean> mList;

    private boolean isShowDeleteBtn = false;

    private String goodsNameStr = "";
    private String goodsGroupStr = "";
    private String goodsDetailStr = "";

    private String faceStr = "";
    private String actionStr = "";
    private String musicStr = "";
    private String mediaStr = "";
    private String danceStr = "";

    public MainItemAdapter(Context context, List<ItemsContentBean> list) {
        this.mContext = context;
        this.mList = list;
        setProjectInfo();
    }

    public void setProjectInfo() {
        //替换默认商品词条
        MainItemContentBean mainItemContentBean = SalesApplication.getAppContext().getMainItemContentBean();
        this.goodsNameStr = mainItemContentBean.getGoodsName() == null ? "" : mainItemContentBean.getGoodsName();
        this.goodsGroupStr = mainItemContentBean.getGoodsGroup() == null ? "" : mainItemContentBean.getGoodsGroup();
        this.goodsDetailStr = mainItemContentBean.getGoodsDescription() == null ? "" : mainItemContentBean.getGoodsDescription();
    }

    public void updateSourceData(List<ItemsContentBean> list) {
        this.mList = list;
        setProjectInfo();
        notifyDataSetChanged();
    }

    public void isShowDelBtn(boolean isShow) {
        isShowDeleteBtn = isShow;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_main_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.number = (TextView) convertView.findViewById(R.id.main_item_number);
            viewHolder.content = (TextView) convertView.findViewById(R.id.main_item_content);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.main_item_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.number.setText(position + 1 + "");
        String other = mList.get(position).getOther();

        faceStr = mList.get(position).getFace() == null ? "" : mList.get(position).getFace();
        actionStr = mList.get(position).getAction() == null ? "" : mList.get(position).getAction();
        musicStr = mList.get(position).getMusic() == null ? "" : mList.get(position).getMusic();
        mediaStr = mList.get(position).getMedia() == null ? "" : mList.get(position).getMedia();
        danceStr = mList.get(position).getDanceName() == null ? "" : mList.get(position).getDanceName();

        String otherStr = other.replace(SalesConstant.ProjectInfo.PRODUCT_NAME, goodsNameStr).
                replace(SalesConstant.ProjectInfo.PRODUCT_GROUP, goodsGroupStr).
                replace(SalesConstant.ProjectInfo.PRODUCT_DETAIL, goodsDetailStr);

        if (!TextUtils.isEmpty(faceStr)) {
            faceStr = "表情;";
        }

        if (!TextUtils.isEmpty(actionStr)) {
            actionStr = "动作;";
        }

        if (!TextUtils.isEmpty(musicStr)) {
            musicStr = "音乐:" + musicStr + ";";
        }

        if (!TextUtils.isEmpty(mediaStr)) {
            mediaStr = "视频和图片;" + mediaStr + ";";
        }

        if (!TextUtils.isEmpty(danceStr)) {
            danceStr = "舞蹈：<" + danceStr + "> ;";
        }

        String finalStr = faceStr + actionStr + musicStr + mediaStr + danceStr + otherStr;
        viewHolder.content.setText(finalStr);

        if (isShowDeleteBtn) {
            viewHolder.delete.setVisibility(View.VISIBLE);
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataManager.getInstance(mContext).deleteContentById(mList.get(position).getId());
                    mList.remove(position);
                    notifyDataSetChanged();
                }
            });
        } else {
            viewHolder.delete.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        TextView number;
        TextView content;
        ImageView delete;
    }

}
