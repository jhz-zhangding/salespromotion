package com.efrobot.salespromotion.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.efrobot.salespromotion.R;
import com.efrobot.salespromotion.utils.FileUtil;

import java.util.List;

/**
 * Created by lhy on 2017/7/24
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private static final String TAG = "MoreDownloadAdapter";
    private List<String> mDownloadBeanList;
    private Context mContext;
    private ICheckListener mICheckListener;

    public FileAdapter(Context context, List<String> list) {
        mDownloadBeanList = list;
        mContext = context;
    }

    public void setOnCheckChangeListener(ICheckListener listener) {
        mICheckListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_import_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String bean = mDownloadBeanList.get(position);
        bean = FileUtil.getFileNameFromUrl(bean);
        holder.tv_name.setText(bean);
        holder.fl_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.iv_check.getVisibility() == View.VISIBLE) {
                    holder.iv_check.setVisibility(View.GONE);
                    if (mICheckListener != null) {
                        mICheckListener.onCheckedChange(position, false);
                    }
                } else {
                    mICheckListener.onCheckedChange(position, true);
                    holder.iv_check.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDownloadBeanList == null ? 0 : mDownloadBeanList.size();
    }

    public void replaceData(List<String> filePathList) {
        mDownloadBeanList = filePathList;
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_check;
        TextView tv_name;
        FrameLayout fl_root;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_check = (ImageView) itemView.findViewById(R.id.iv_check);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            fl_root = (FrameLayout) itemView.findViewById(R.id.fl_root);
        }
    }

    public interface ICheckListener {
        void onCheckedChange(int position, boolean isChecked);
    }

}
