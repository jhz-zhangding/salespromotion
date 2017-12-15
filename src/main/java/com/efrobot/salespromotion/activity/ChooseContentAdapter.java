package com.efrobot.salespromotion.activity;

import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.efrobot.salespromotion.R;
import com.efrobot.salespromotion.bean.ModelNameBean;

import java.util.List;


public class ChooseContentAdapter extends RecyclerView.Adapter<ChooseContentAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private List<ModelNameBean> list;
    private String checkContent;


    public ChooseContentAdapter(List<ModelNameBean> list, OnChooseDanceAdapterItemListener onChooseDanceAdapterItemListener, String checkContent) {
        this.list = list;
        if (!TextUtils.isEmpty(checkContent)) {
            this.checkContent = checkContent;
        }
        this.onChooseDanceAdapterItemListener = onChooseDanceAdapterItemListener;
    }

    public void updateContent(String content) {
        this.checkContent = content;
        notifyDataSetChanged();
    }

    public void updateContent(List<ModelNameBean> list, String content) {
        this.list = list;
        this.checkContent = content;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_item_goods_content_list, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (list.get(position).getModelName() != null) {
            if (list.get(position).getModelName().equals(checkContent)) {
//            holder.textView.setBackgroundResource(R.drawable.diy_default_setting_btn_bg);
                holder.imageView.setBackgroundResource(R.mipmap.model_img_select);
                holder.textView.setTextColor(Color.rgb(0xff, 0x8f, 0x00));
            } else {
                holder.imageView.setBackgroundResource(R.mipmap.model_img_unselect);
                holder.textView.setTextColor(Color.WHITE);
            }
        }
        holder.textView.setText(list.get(position).getModelName());

    }

    @Override
    public int getItemCount() {

        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout linearLayout;
        ImageView imageView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.content_ll);
            imageView = (ImageView) itemView.findViewById(R.id.content_image);
            textView = (TextView) itemView.findViewById(R.id.content_text);
            linearLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            checkContent = "";
            if (onChooseDanceAdapterItemListener != null) {
                onChooseDanceAdapterItemListener.onItemClick(v, list.get(getPosition()).getModelName(), getPosition());
                checkContent = list.get(getPosition()).getModelName();
                notifyDataSetChanged();
            }
        }
    }


    private OnChooseDanceAdapterItemListener onChooseDanceAdapterItemListener;

    public interface OnChooseDanceAdapterItemListener {
        void onItemClick(View v, String danceName, int position);
    }

}
