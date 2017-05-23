package com.xmd.manager.journal.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.manager.journal.contract.JournalContentEditContract;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalContentType;
import com.xmd.manager.journal.widget.JournalEditActivityView;
import com.xmd.manager.journal.widget.JournalEditArticlesView;
import com.xmd.manager.journal.widget.JournalEditCouponView;
import com.xmd.manager.journal.widget.JournalEditImageArticleView;
import com.xmd.manager.journal.widget.JournalEditPhotoAlbumView;
import com.xmd.manager.journal.widget.JournalEditServiceItemView;
import com.xmd.manager.journal.widget.JournalEditTechnicianRankingView;
import com.xmd.manager.journal.widget.JournalEditTechnicianView;
import com.xmd.manager.journal.widget.JournalEditVideoView;

/**
 * Created by heyangya on 16-11-5.
 * 这里用来显示各种内容项
 */

public class JournalContentEditViewHelper {
    public static void setupContentView(Context context, ViewGroup parent, JournalContent content, JournalContentEditContract.Presenter presenter, int viewPosition) {
        View view = null;
        if (content.getViewHolder() != null) {
            view = content.getViewHolder().getView();
        }

        if (view == null) {
            switch (content.getType().getKey()) {
                case JournalContentType.CONTENT_KEY_TECHNICIAN:
                    view = new JournalEditTechnicianView(context, content, presenter);
                    break;
                case JournalContentType.CONTENT_KEY_PHOTO_ALBUM:
                    view = new JournalEditPhotoAlbumView(context, content, presenter);
                    break;
                case JournalContentType.CONTENT_KEY_TECHNICIAN_RANKING:
                    view = new JournalEditTechnicianRankingView(context, content, presenter);
                    break;
                case JournalContentType.CONTENT_KEY_SERVICE:
                    view = new JournalEditServiceItemView(context, content, presenter);
                    break;
                case JournalContentType.CONTENT_KEY_VIDEO:
                    view = new JournalEditVideoView(context, content, presenter);
                    break;
                case JournalContentType.CONTENT_KEY_COUPON:
                    view = new JournalEditCouponView(context, content, presenter);
                    break;
                case JournalContentType.CONTENT_KEY_ARTICLE:
                    view = new JournalEditArticlesView(context, content, presenter);
                    break;
                case JournalContentType.CONTENT_KEY_ACTIVITY:
                    view = new JournalEditActivityView(context, content, presenter);
                    break;
                case JournalContentType.CONTENT_KEY_IMAGE_ARTICLE:
                    view = new JournalEditImageArticleView(context, content, presenter);
                    break;
            }
        }
        if (view == null) {
            return;
        }
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        parent.addView(view);
        if (view instanceof JournalEditCouponView) {
            ((JournalEditCouponView) view).setViewPosition(viewPosition);
        }
    }
}
