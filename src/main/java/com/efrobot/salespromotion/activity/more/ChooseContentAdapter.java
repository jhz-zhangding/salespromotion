package com.efrobot.salespromotion.activity.more;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.efrobot.salespromotion.R;
import com.efrobot.salespromotion.activity.ModelContentBean;
import com.efrobot.salespromotion.activity.ModelNameBean;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_group_list, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (list.get(position).getModelName().equals(checkContent)) {
//            holder.textView.setBackgroundResource(R.drawable.diy_default_setting_btn_bg);
            holder.textView.setSelected(true);
        } else {
                holder.textView.setSelected(false);
        }
        holder.textView.setText(list.get(position).getModelName());

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
