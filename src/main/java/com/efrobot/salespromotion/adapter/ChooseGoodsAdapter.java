package com.efrobot.salespromotion.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.efrobot.salespromotion.R;

import java.util.List;

import static com.efrobot.salespromotion.R.id.face_action_item_img;


public class ChooseGoodsAdapter extends RecyclerView.Adapter<ChooseGoodsAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private List<String> list;
    private int mSelectPosition = -1;
    private String checkContent;


    public ChooseGoodsAdapter(List<String> list, OnChooseDanceAdapterItemListener onChooseDanceAdapterItemListener, String checkContent) {
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


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_group_list, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (list.get(position).equals(checkContent)) {
//            holder.textView.setBackgroundResource(R.drawable.diy_default_setting_btn_bg);
            holder.linearLayout.setSelected(true);
        } else {
            if (position == mSelectPosition) {
                holder.linearLayout.setSelected(true);
            } else {
                holder.linearLayout.setSelected(false);
            }
        }
        if(list.get(position).equals("食品")) {
            holder.imageView.setImageResource(R.mipmap.food_img);
        } else if(list.get(position).equals("饮料")) {
            holder.imageView.setImageResource(R.mipmap.drink_img);
        } else if(list.get(position).equals("日化")) {
            holder.imageView.setImageResource(R.mipmap.daily_img);
        } else if(list.get(position).equals("其他")) {
            holder.imageView.setImageResource(R.mipmap.other_img);
        }
        holder.textView.setText(list.get(position));

    }

    @Override
    public int getItemCount() {

        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout linearLayout;
        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.face_action_item_ll);
            textView = (TextView) itemView.findViewById(R.id.face_action_item_text);
            imageView = (ImageView) itemView.findViewById(face_action_item_img);
            linearLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onChooseDanceAdapterItemListener != null) {
                onChooseDanceAdapterItemListener.onItemClick(v, list.get(getPosition()), getPosition());
                mSelectPosition = getPosition();
                notifyDataSetChanged();
            }
        }
    }


    private OnChooseDanceAdapterItemListener onChooseDanceAdapterItemListener;

    public interface OnChooseDanceAdapterItemListener {
        void onItemClick(View v, String danceName, int position);
    }

}
