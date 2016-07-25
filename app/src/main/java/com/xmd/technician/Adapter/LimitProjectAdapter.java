package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.technician.R;

import java.util.List;

/**
 * Created by Administrator on 2016/7/25.
 */
public class LimitProjectAdapter extends RecyclerView.Adapter<LimitProjectAdapter.LimitViewHolder>{
    private Context mContext;
    private List<String> mData;

    public LimitProjectAdapter(Context context,List<String> data){
        this.mContext =context;
        this.mData = data;
    }

    @Override
    public LimitProjectAdapter.LimitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.limit_project_item,parent,false);
        LimitViewHolder holder = new LimitViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(LimitProjectAdapter.LimitViewHolder holder, int position) {
        holder.tv.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

   class  LimitViewHolder extends RecyclerView.ViewHolder {
       TextView tv;
       public LimitViewHolder(View itemView) {
           super(itemView);
           tv = (TextView) itemView.findViewById(R.id.limit);
       }


   }



}
