package com.efrobot.salespromotion.activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.efrobot.salespromotion.R;

import java.util.List;


public class ChooseMoreAdapter extends RecyclerView.Adapter<ChooseMoreAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private List<String> list;
    private int mSelectPosition = -1;
    private String checkContent;


    public ChooseMoreAdapter(List<String> list, OnChooseDanceAdapterItemListener onChooseDanceAdapterItemListener, String checkContent) {
        this.list = list;
        this.checkContent = checkContent;
        this.onChooseDanceAdapterItemListener = onChooseDanceAdapterItemListener;
    }

    public void updateContent(String content) {
        this.checkContent = content;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_item_goods_group_list, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (list.get(position).equals(checkContent)) {
//            holder.textView.setBackgroundResource(R.drawable.diy_default_setting_btn_bg);
            holder.textView.setSelected(true);
        } else {
            if (position == mSelectPosition) {
                holder.textView.setSelected(true);
            } else {
                holder.textView.setSelected(false);
            }
        }
        holder.textView.setText(list.get(position));

    }

    @Override
    public int getItemCount() {

        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.face_action_item_text);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            checkContent = "";
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
