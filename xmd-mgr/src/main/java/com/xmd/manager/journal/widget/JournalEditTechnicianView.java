package com.xmd.manager.journal.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.xmd.manager.R;
import com.xmd.manager.journal.contract.JournalContentEditContract;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalItemTechnician;
import com.xmd.manager.journal.model.Technician;

/**
 * Created by heyangya on 16-11-11.
 */

public class JournalEditTechnicianView extends JournalEditImageView {
    protected JournalContentEditContract.Presenter mPresenter;

    public JournalEditTechnicianView(Context context, JournalContent content, JournalContentEditContract.Presenter presenter) {
        super(context, content);
        mPresenter = presenter;
    }

    @Override
    public void bindData(CustomCombineImageView view, Object data) {
        JournalItemTechnician content = (JournalItemTechnician) data;
        Technician technician = content.getTechnician();
        if (technician != null) {
            view.setImage(mContext, technician.getIconUrl(), R.drawable.icon_default_technician);
            view.setImageSize(getViewWidth(), getViewWidth());
            String name = technician.getName();
            if (!TextUtils.isEmpty(technician.getNo())) {
                name += "[" + technician.getNo() + "]";
            }
            view.setName(name);
            view.showDeleteView();
            view.showReplaceView();
        }
    }

    @Override
    public int getViewHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public void onClickAddItem() {
        mPresenter.onClickAddTechnician(mContent);
    }

    @Override
    public void onClickDeleteItem(int index) {
        mPresenter.onClickDeleteTechnician(mContent, index);
    }

    @Override
    public void onClickReplaceItem(int index) {
        mPresenter.onClickReplaceTechnician(mContent, index);
    }
}
