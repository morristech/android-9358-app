package com.xmd.manager.journal.contract;

import android.content.Intent;

import com.xmd.manager.journal.BasePresenter;
import com.xmd.manager.journal.BaseView;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.model.CouponActivity;
import com.xmd.manager.journal.model.Journal;
import com.xmd.manager.journal.model.JournalContent;

import java.util.List;

/**
 * Created by heyangya on 16-11-3.
 */

public interface JournalContentEditContract {
    interface Presenter extends BasePresenter {
        void onClickSaveDraft();

        void onClickPreView();

        void onCopyContent(JournalContent content);

        void onDeleteContent(JournalContent content);

        void onMoveUpContent(JournalContent content);

        void onRecoverContent(JournalContent content);

        void onSetTitle(String title);

        void onSetSubTitle(String subTitle);

        void onContentTitleChange(JournalContent content, String title);

        //刷新列表中子项内容
        void refreshListItemView(int viewPosition);

        //技师
        void onClickAddTechnician(JournalContent content);

        void onClickDeleteTechnician(JournalContent content, int index);

        void onClickReplaceTechnician(JournalContent content, int index);

        //项目
        void onClickAddServiceItem(JournalContent content);

        void onClickDeleteServiceItem(JournalContent content, int index);

        void onClickReplaceServiceItem(JournalContent content, int index);

        //相册
        void onLoadPhotoData(JournalContent content);

        void onClickAddPhoto(JournalContent content);

        void onClickDeletePhoto(JournalContent content, int index);

        void onClickReplacePhoto(JournalContent content, int index);

        void onUploadImageSuccess(String imageId);

        //排行榜
        void onLoadTechnicianRankingData(JournalContent content);

        //优惠活动
        void onLoadCouponActivityList(Callback<List<CouponActivity>> callback);

        void onSelectCouponActivity(JournalContent content, CouponActivity couponActivity, int itemIndex);

        //视频
        void onClickRecordVideo(JournalContent content);

        void onLoadVideoData(JournalContent content);

        void onUploadVideoSuccess(JournalContent content);

        //图文
        void onImageArticleImageClicked(JournalContent content, int index);

        void onImageArticleTextChanged(JournalContent content, int index);

        //返回结果处理
        void onActivityResult(int requestCode, int resultCode, Intent data);

        void onBackKey();

        void onClearContent();
    }

    interface View extends BaseView<JournalTemplateChoiceContract.Presenter> {
        void showJournal(Journal journal, List<JournalContent> deletedContentTypes);

        void updateJournalContentView();

        void updateJournalContentSubview(int viewPosition);

        int getJournalIdFromIntent();

    }
}
