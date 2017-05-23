package com.xmd.manager.journal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.common.ImageTool;
import com.xmd.manager.journal.contract.TechnicianChoiceContract;
import com.xmd.manager.journal.model.Technician;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 16-10-31.
 */

public class TechnicianChoiceAdapter extends RecyclerView.Adapter<TechnicianChoiceAdapter.ViewHolder> {
    private TechnicianChoiceContract.Presenter mPresenter;
    private List<Technician> mTechnicians;
    private List<String> mForbiddenTechNoList;

    public TechnicianChoiceAdapter(TechnicianChoiceContract.Presenter presenter) {
        mPresenter = presenter;
        mTechnicians = new ArrayList<>();
    }

    public void setData(List<Technician> technicians, List<String> forbiddenTechNoList) {
        mTechnicians = technicians;
        mForbiddenTechNoList = forbiddenTechNoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_journal_technician, parent, false);
        return new ViewHolder(view, mPresenter);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mTechnicians != null) {
            Technician technician = mTechnicians.get(position);
            holder.bind(technician, mForbiddenTechNoList.contains(technician.getId()), position);
        }
    }

    @Override
    public int getItemCount() {
        if (mTechnicians != null) {
            return mTechnicians.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TechnicianChoiceContract.Presenter mPresenter;

        private Technician mTechnician;
        private int mItemViewPosition;
        private boolean mIsForbidden;

        private TextView mNoTextView;
        private TextView mNameTextView;
        private ImageView mIconImageView;
        private ImageView mSelectImageView;


        public ViewHolder(View itemView, TechnicianChoiceContract.Presenter presenter) {
            super(itemView);
            mPresenter = presenter;
            mNoTextView = (TextView) itemView.findViewById(R.id.tv_no);
            mNameTextView = (TextView) itemView.findViewById(R.id.tv_name);
            mIconImageView = (ImageView) itemView.findViewById(R.id.img_icon);
            mSelectImageView = (ImageView) itemView.findViewById(R.id.img_select);

            itemView.setOnClickListener(mOnClickItemView);
        }

        public void bind(Technician technician, boolean isForbidden, int itemViewPosition) {
            mTechnician = technician;
            mIsForbidden = isForbidden;
            mItemViewPosition = itemViewPosition;
            mNameTextView.setText(technician.getName());
            String techNo = technician.getNo();
            if (!TextUtils.isEmpty(techNo)) {
                mNoTextView.setVisibility(View.VISIBLE);
                mNoTextView.setText("[" + techNo + "]");
            } else {
                mNoTextView.setVisibility(View.GONE);
            }
            ImageTool.loadCircleImage(itemView.getContext(), technician.getIconUrl(), mIconImageView);
            if (isForbidden) {
                mSelectImageView.setImageResource(R.drawable.icon_checkbox_disabled);
            } else if (TextUtils.equals(mPresenter.getSelectedTechId(), technician.getId())) {
                mSelectImageView.setImageResource(R.drawable.icon_checkbox_checked);
            } else {
                mSelectImageView.setImageResource(R.drawable.icon_checkbox);
            }
        }

        private View.OnClickListener mOnClickItemView = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsForbidden) {
                    mSelectImageView.setImageResource(R.drawable.icon_checkbox_checked);
                    mPresenter.onSelectTechnician(mTechnician, mItemViewPosition, mSelectImageView);
                }
            }
        };
    }
}
