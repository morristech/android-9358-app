package com.xmd.manager.journal.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.journal.contract.JournalContentEditContract;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalItemTechnician;
import com.xmd.manager.journal.model.Technician;

/**
 * Created by heyangya on 16-11-11.
 */

public class JournalEditTechnicianRankingView extends JournalEditTechnicianView {
    private boolean mIsInitData;

    public JournalEditTechnicianRankingView(Context context, JournalContent content, JournalContentEditContract.Presenter presenter) {
        super(context, content, presenter);
        if (content.getDataSize() > 0) {
            mIsInitData = true;
        }
    }

    @Override
    public void bindData(CustomCombineImageView view, Object data) {
        view.setShowCircleImage(true);
        super.bindData(view, data);
        view.hideDeleteView();
        view.hideReplaceView();

        JournalItemTechnician content = (JournalItemTechnician) data;
        Technician technician = content.getTechnician();
        if (technician == null) {
            //技师不存在了,那么不显示，设置默认图片
            view.setImage(getContext(), null, R.drawable.icon_default_technician);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!mIsInitData) {
            mIsInitData = true;
            notifyDataChanged();
        }
    }

    @Override
    public void notifyDataChanged() {
        super.notifyDataChanged();
        if (mContent.getDataSize() == 0) {
            removeAllViews();
            TextView textView = new TextView(getContext());
            addView(textView);
            textView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            textView.getLayoutParams().height = 128;
            textView.setText("正在加载数据...");
            textView.setGravity(Gravity.CENTER);
            mPresenter.onLoadTechnicianRankingData(mContent);
        }
    }
}
