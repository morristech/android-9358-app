package com.xmd.manager.journal.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.journal.contract.JournalContentEditContract;
import com.xmd.manager.journal.manager.ImageManager;
import com.xmd.manager.journal.model.AlbumPhoto;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalItemPhoto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by heyangya on 16-11-11.
 */

public class JournalEditPhotoAlbumView extends JournalEditImageView {
    private JournalContentEditContract.Presenter mPresenter;
    private boolean mIsShowUploadButton;
    private static final int BUTTON_STATE_CANCEL = 0;
    private static final int BUTTON_STATE_UPLOAD = 1;
    private int mButtonStatus;
    private AtomicInteger mUploadCount = new AtomicInteger(0);
    private boolean mIsInitData;
    private List<ImageManager.UploadTask> mUploadTasks;

    public JournalEditPhotoAlbumView(Context context, JournalContent content, JournalContentEditContract.Presenter presenter) {
        super(context, content);
        mPresenter = presenter;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!mIsInitData) {
            mIsInitData = true;
            if (mContent.getDataSize() > 0) {
                mPresenter.onLoadPhotoData(mContent);
            }
        }
    }

    @Override
    public void bindData(CustomCombineImageView view, Object data) {
        JournalItemPhoto contentPhoto = (JournalItemPhoto) data;
        AlbumPhoto photo = contentPhoto.getPhoto();
        if (photo != null) {
            String url = photo.getLocalPath();
            if (TextUtils.isEmpty(url)) {
                url = photo.getRemoteUrl();
            }
            view.setImage(mContext, url, R.drawable.icon_default_technician);
            view.showDeleteView();
            view.showReplaceView();
            if (photo.isNeedUpload()) {
                view.hideUploadSuccessView();
            } else {
                view.showUploadSuccessView();
            }
        }
    }

    @Override
    public void onClickAddItem() {
        mPresenter.onClickAddPhoto(mContent);
    }

    @Override
    public void onClickDeleteItem(int index) {
        mPresenter.onClickDeletePhoto(mContent, index);
    }

    @Override
    public void onClickReplaceItem(int index) {
        mPresenter.onClickReplacePhoto(mContent, index);
    }

    @Override
    public void notifyDataChanged() {
        super.notifyDataChanged();
        mIsShowUploadButton = false;
        checkAndShowUploadButtonView();
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();
        onClearData();
    }

    @Override
    public void onClearData() {
        super.onClearData();
        if (mUploadTasks != null) {
            ImageManager.getInstance().cancelUpload(mUploadTasks);
            mUploadTasks = null;
        }
    }

    @Override
    public void notifyItemChanged(int index) {
        super.notifyItemChanged(index);
        checkAndShowUploadButtonView();
    }

    private void checkAndShowUploadButtonView() {
        boolean needShow = false;
        if (mContent.getDataSize() > 0) {
            //显示上传和取消按钮
            for (int i = 0; i < mContent.getDataSize(); i++) {
                if (((JournalItemPhoto) mContent.getData(i)).getPhoto().isNeedUpload()) {
                    needShow = true;
                    break;
                }
            }
        }

        if (needShow) {
            if (!mIsShowUploadButton) {
                mIsShowUploadButton = true;
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.journal_photo_album_upload_view, null);
                addView(linearLayout);
            }
            TextView uploadButton = (TextView) getChildAt(getChildCount() - 1).findViewById(R.id.tv_upload);
            uploadButton.setOnClickListener(onUploadClickListener);
            setUploadButtonStatus(uploadButton, BUTTON_STATE_UPLOAD);
        } else if (mIsShowUploadButton) {
            //hide
            removeViewAt(getChildCount() - 1);
            mIsShowUploadButton = false;
        }
        showEditView();
    }

    private View.OnClickListener onUploadClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mButtonStatus == BUTTON_STATE_UPLOAD) {
                showUploadView();
                mUploadTasks = new ArrayList<>();
                for (int i = 0; i < mContent.getDataSize(); i++) {
                    AlbumPhoto photo = ((JournalItemPhoto) mContent.getData(i)).getPhoto();
                    if (photo.isNeedUpload()) {
                        mUploadTasks.add(new ImageManager.UploadTask(photo, new ImageUploadListener(i)));
                    }
                }
                //开始上传
                mUploadCount.set(mUploadTasks.size());
                ImageManager.init(getContext());
                ImageManager.getInstance().doUpload(mUploadTasks);
                setUploadButtonStatus(v, BUTTON_STATE_CANCEL);
            } else {
                //取消上传
                ImageManager.getInstance().cancelUpload(mUploadTasks);
                mUploadTasks = null;
                showEditView();
                setUploadButtonStatus(v, BUTTON_STATE_UPLOAD);
            }
        }
    };

    private void setUploadButtonStatus(View v, int status) {
        if (status == BUTTON_STATE_UPLOAD) {
            mButtonStatus = BUTTON_STATE_UPLOAD;
            ((TextView) v).setText("上传");
        } else {
            mButtonStatus = BUTTON_STATE_CANCEL;
            ((TextView) v).setText("取消");
        }
    }

    private void showUploadView() {
        for (int i = 0; i < mContent.getDataSize(); i++) {
            //隐藏所有图片的删除的替换操作
            CustomCombineImageView view = getCustomCombineImageView(i);
            view.hideReplaceView();
            view.hideDeleteView();
        }
        //隐藏增加操作
        showAddView(false);
    }

    private void showEditView() {
        for (int i = 0; i < mContent.getDataSize(); i++) {
            //显示所有图片的删除的替换操作
            CustomCombineImageView view = getCustomCombineImageView(i);
            view.showReplaceView();
            view.showDeleteView();
            if (((JournalItemPhoto) mContent.getData(i)).getPhoto().isNeedUpload()) {
                view.hideUploadSuccessView();
            } else {
                view.showUploadSuccessView();
            }
        }
        //显示增加操作
        showAddView(true);
    }


    private class ImageUploadListener implements ImageManager.ImageUploadListener {
        private int mIndex;

        public ImageUploadListener(int index) {
            mIndex = index;
        }

        @Override
        public void onWait() {
            getCustomCombineImageView(mIndex).getUpdateListener().onWait();
        }

        @Override
        public void onStart() {
            getCustomCombineImageView(mIndex).getUpdateListener().onStart();
        }

        @Override
        public void onProgress(int progress) {
            getCustomCombineImageView(mIndex).getUpdateListener().onProgress(progress);
        }

        @Override
        public void onFinished(String error) {
            getCustomCombineImageView(mIndex).getUpdateListener().onFinished(error);
            if (error == null) {
                AlbumPhoto photo = ((JournalItemPhoto) mContent.getData(mIndex)).getPhoto();
                mPresenter.onUploadImageSuccess(photo.getImageId());
            } else {
                ToastUtils.showToastShort(mContext, "图片上传失败：" + error);
            }
            if (mUploadCount.decrementAndGet() == 0) {
                checkAndShowUploadButtonView();
            }
        }

        @Override
        public void onCanceled() {
            getCustomCombineImageView(mIndex).getUpdateListener().onCanceled();
        }
    }

}
