package com.xmd.technician.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.contract.JoinClubContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 16-12-22.
 */

public class TechNoRecyclerViewAdapter extends RecyclerView.Adapter<TechNoRecyclerViewAdapter.ViewHolder> {
    private List<TechNo> mData;
    private JoinClubContract.Presenter mPresenter;
    private String mSelectedTechNo;

    public TechNoRecyclerViewAdapter() {
        mData = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView view = new TextView(parent.getContext());
        view.setGravity(Gravity.CENTER);
        view.setPadding(0, 12, 0, 12);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        view.setTextColor(parent.getResources().getColor(android.R.color.holo_blue_light));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mData.get(position), mData.get(position).name.equals(mSelectedTechNo));
    }

    public void setData(JoinClubContract.Presenter presenter, List<TechNo> data, String selectedTechNo) {
        mPresenter = presenter;
        mData = data;
        mSelectedTechNo = selectedTechNo;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TechNo mTechNo;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.onSelectTechNo(mTechNo);
                }
            });
        }

        public void bind(TechNo techNo, boolean selected) {
            ((TextView) itemView).setText(techNo.name);
            ((TextView) itemView).setTextColor(selected ? itemView.getResources().getColor(R.color.colorPrimary)
                    : itemView.getResources().getColor(android.R.color.holo_blue_light));
            mTechNo = techNo;
        }
    }

    public static class TechNo {
        public String name;
        public String id;

        public TechNo(String name, String id) {
            this.name = name;
            this.id = id;
        }

        public static TechNo DEFAULT_TECH_NO = new TechNo("管理员指定", null);
    }
}
