package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.bean.HelloTemplateInfo;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.model.HelloSettingManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZR on 17-3-18.
 */

public class HelloTemplateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<HelloTemplateInfo> mData = new ArrayList<>();
    private HelloSettingManager mHelloSettingManager;
    private int mCheckPos = -1;
    private OnTemplateItemClickListener mListener;

    private Integer mCheckId;

    public Integer getCheckId() {
        return mCheckId;
    }

    public HelloTemplateAdapter(Context context) {
        mContext = context;
        mHelloSettingManager = HelloSettingManager.getInstance();
    }

    public void setOnTemplateItemClickListener(OnTemplateItemClickListener listener) {
        this.mListener = listener;
    }

    public void setData(List<HelloTemplateInfo> list) {
        if (mData != null) {
            mData.clear();
            mData.addAll(list);
            // 初始化数据
            for (HelloTemplateInfo info : mData) {
                if (info.id == mHelloSettingManager.getTemplateId().intValue() || (mHelloSettingManager.getTemplateParentId() != null && info.id == mHelloSettingManager.getTemplateParentId().intValue())) {
                    mCheckId = info.id;
                    info.setSelected(true);
                    mCheckPos = mData.indexOf(info);
                }
            }
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, () -> notifyDataSetChanged());
        }
    }

    // 未选择系统模版
    public void unCheckedData() {
        mData.get(mCheckPos).setSelected(false);
        notifyItemChanged(mCheckPos);
        mCheckPos = -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SystemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_template_system_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HelloTemplateInfo info = mData.get(position);
        SystemViewHolder systemViewHolder = (SystemViewHolder) holder;
        systemViewHolder.mSystemContent.setText(info.contentText);
        systemViewHolder.mSystemCheck.setImageResource(info.isSelected() ? R.drawable.select : R.drawable.radio);
        systemViewHolder.mSystemCheck.setOnClickListener(v -> {
            notifyCheckItem(position);
            mCheckId = info.id;
            mListener.onTemplateItemClick(info);
        });
    }

    private void notifyCheckItem(int position) {
        if (mCheckPos != position) {
            if (mCheckPos != -1) {
                mData.get(mCheckPos).setSelected(false);
                notifyItemChanged(mCheckPos);
            }
            mCheckPos = position;
            mData.get(mCheckPos).setSelected(true);
            notifyItemChanged(mCheckPos);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class SystemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mSystemCheck;
        public TextView mSystemContent;

        public SystemViewHolder(View itemView) {
            super(itemView);
            mSystemCheck = (ImageView) itemView.findViewById(R.id.img_system_check);
            mSystemContent = (TextView) itemView.findViewById(R.id.tv_system_content);
        }
    }

    public interface OnTemplateItemClickListener {
        void onTemplateItemClick(HelloTemplateInfo info);
    }
}
