package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.HelloReplyInfo;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zr on 17-4-6.
 */

public class FloatItemAdapter extends RecyclerView.Adapter<FloatItemAdapter.ViewHolder> {
    private Context mContext;
    private List<HelloReplyInfo> mList = new ArrayList<>();

    public FloatItemAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<HelloReplyInfo> list) {
        if (list != null) {
            mList.clear();
            mList.addAll(list);
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, () -> notifyDataSetChanged());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_float_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HelloReplyInfo info = mList.get(position);
        Glide.with(mContext).load(info.receiverAvatar).into(holder.mAvatar);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_multi_avatar)
        CircleImageView mAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
