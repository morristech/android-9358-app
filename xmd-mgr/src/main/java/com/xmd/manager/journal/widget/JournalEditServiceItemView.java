package com.xmd.manager.journal.widget;

import android.content.Context;
import android.view.ViewGroup;

import com.xmd.manager.beans.ServiceItem;
import com.xmd.manager.journal.contract.JournalContentEditContract;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalItemService;

/**
 * Created by heyangya on 16-11-11.
 */

public class JournalEditServiceItemView extends JournalEditImageView {
    private JournalContentEditContract.Presenter mPresenter;

    public JournalEditServiceItemView(Context context, JournalContent content, JournalContentEditContract.Presenter presenter) {
        super(context, content);
        mPresenter = presenter;
    }

    @Override
    public void bindData(CustomCombineImageView view, Object data) {
        JournalItemService contentService = (JournalItemService) data;
        ServiceItem serviceItem = contentService.getServiceItem();
        if (serviceItem != null) {
            view.showDeleteView();
            view.showReplaceView();
            view.setImage(mContext, serviceItem.imageUrl);
            view.setImageSize(getViewWidth(), getViewWidth());
            view.setName(serviceItem.name);
        }
    }

    @Override
    public int getViewHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public void onClickAddItem() {
        mPresenter.onClickAddServiceItem(mContent);
    }

    @Override
    public void onClickDeleteItem(int index) {
        mPresenter.onClickDeleteServiceItem(mContent, index);
    }

    @Override
    public void onClickReplaceItem(int index) {
        mPresenter.onClickReplaceServiceItem(mContent, index);
    }
}

