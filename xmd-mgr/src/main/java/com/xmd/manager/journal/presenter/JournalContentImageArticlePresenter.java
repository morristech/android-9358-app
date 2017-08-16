package com.xmd.manager.journal.presenter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.TextUtils;

import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.databinding.JournalImageArticleBinding;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.contract.JournalContentImageArticleContract;
import com.xmd.manager.journal.manager.ImageArticleManager;
import com.xmd.manager.journal.manager.ImageManager;
import com.xmd.manager.journal.model.AlbumPhoto;

import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalItemImageArticle;
import com.xmd.manager.beans.JournalTemplateImageArticleBean;
import com.xmd.manager.journal.widget.JournalEditImageArticleTemplateDialogFragment;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Subscription;

/**
 * Created by heyangya on 17-1-3.
 */

public class JournalContentImageArticlePresenter implements JournalContentImageArticleContract.Presenter {

    private Context mContext;
    private JournalContent mContent;
    private JournalContentImageArticleContract.View mView;
    private JournalEditImageArticleTemplateDialogFragment mTemplateView;
    private JournalImageArticleBinding mBinding;
    public ObservableBoolean mShowLoadingData = new ObservableBoolean();
    public ObservableField<String> mLoadDataError = new ObservableField<>();
    private JournalItemImageArticle mContentImageArticle;

    public ObservableBoolean mShowSelectTemplateButton = new ObservableBoolean();
    public ObservableBoolean mShowLoadingTemplate = new ObservableBoolean();
    public ObservableField<String> mLoadTemplateError = new ObservableField<>();
    private FragmentManager mFragmentManager;
    public JournalTemplateImageArticleBean mSelectedTemplate;


    private Subscription mLoadImageUrlSubscription;

    private boolean mIsUploadingImage;
    private String mTemplateId;
    private List<ImageManager.UploadTask> mImageUploadTasks;
    private AtomicInteger mUploadCount = new AtomicInteger(0);

    public JournalContentImageArticlePresenter(Context context, JournalContentImageArticleContract.View view,
                                               JournalImageArticleBinding binding, JournalContent content, String templateId) {
        mContext = context;
        mView = view;
        mBinding = binding;
        mContent = content;
        mTemplateId = templateId;

    }

    @Override
    public void onCreate() {
        mBinding.setPresenter(this);
        onDataSetChanged();
    }

    @Override
    public void onDestroy() {
        clearNetworkAccess();
    }

    private void clearNetworkAccess() {
        if (mLoadImageUrlSubscription != null) {
            mLoadImageUrlSubscription.unsubscribe();
            mLoadImageUrlSubscription = null;
        }
        if (mImageUploadTasks != null) {
            ImageManager.getInstance().cancelUpload(mImageUploadTasks);
            mImageUploadTasks = null;
        }
    }

    @Override
    public void onClickSelectTemplate() {
        openTemplateWindow(mTemplateId);
    }

    @Override
    public void onTemplateViewCreate(String templateId) {
        loadTemplates(templateId);
    }

    private void openTemplateWindow(String templateId) {
        if (mFragmentManager == null) {
            mFragmentManager = ((Activity) mContext).getFragmentManager();
        }
        Fragment prev = mFragmentManager.findFragmentByTag("journal_image_article");
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (prev != null) {
            ft.remove(prev);
        }
        mTemplateView = JournalEditImageArticleTemplateDialogFragment.newInstance(templateId);
        mTemplateView.setPresenter(this);
        mTemplateView.show(ft, "journal_image_article");
    }

    @Override
    public void onSelectTemplate(JournalTemplateImageArticleBean template) {
        mTemplateView.dismiss();
        if (mSelectedTemplate != null && mSelectedTemplate.id.equals(template.id)) {
            return;
        }
        mSelectedTemplate = template;
        if (mContent.getDataSize() > 0) {
            mContent.clearData();
        }
        //创建数据和视图
        mContentImageArticle = new JournalItemImageArticle(null);
        mContentImageArticle.templateId = template.id;
        if (template.articles != null && template.articles.size() > 0) {
            for (JournalTemplateImageArticleBean.Article article : template.articles) {
                mContentImageArticle.articleList.add(article.content);
            }
        }
        if (template.imageCount > 0) {
            for (int i = 0; i < template.imageCount; i++) {
                mContentImageArticle.imageList.add(new AlbumPhoto());
            }
        }
        mContent.addData(mContentImageArticle);
        mView.createView(template);
    }

    private void loadTemplates(String templateId) {
        //加载模版数据
        mShowLoadingTemplate.set(true);
        mLoadTemplateError.set(null);
        mTemplateView.showLoadTemplateSuccess(ImageArticleManager.getInstance().getTemplates(Integer.parseInt(templateId)), mSelectedTemplate);
        mShowLoadingTemplate.set(false);
    }

    private void loadData() {
        mShowLoadingData.set(true);
        mLoadDataError.set(null);

        if (ImageArticleManager.getInstance().getTemplates(Integer.parseInt(mTemplateId)) == null) {
            for (JournalTemplateImageArticleBean template : ImageArticleManager.getInstance().getTemplates(Integer.parseInt(mTemplateId))) {
                if (template.id.equals(mContentImageArticle.templateId)) {
                    mSelectedTemplate = template;
                    break;
                }
            }
            if (mContentImageArticle.imageList.size() > 0) {
                //转换ID为URL
                loadImageUrl();
            } else {
                mShowLoadingData.set(false);
                showData();
            }
        } else {
            mShowLoadingData.set(false);
            //  mLoadDataError.set(error.getLocalizedMessage());
        }
    }

    private void loadImageUrl() {
        if (mLoadImageUrlSubscription != null) {
            mLoadImageUrlSubscription.unsubscribe();
        }
        mLoadImageUrlSubscription = ImageManager.getInstance().convertImageIdsToUrls(mContentImageArticle.imageList, new Callback<Void>() {
            @Override
            public void onResult(Throwable error, Void result) {
                mLoadImageUrlSubscription = null;
                mShowLoadingData.set(false);
                if (error == null) {
                    showData();
                } else {
                    mLoadDataError.set(error.getLocalizedMessage());
                }
            }
        });
    }

    private void showData() {
        JournalTemplateImageArticleBean template = ImageArticleManager.getInstance().getTemplateById(mContentImageArticle.templateId);
        if (template != null) {
            mView.createView(template);
            mView.showData(mContentImageArticle);
        } else {
            mLoadDataError.set("数据错误，无法编辑");
        }
    }

    @Override
    public void onDataSetChanged() {
        if (mContent.getDataSize() > 0) {
            mContentImageArticle = (JournalItemImageArticle) mContent.getData(0);
            loadData();
        } else {
            mShowSelectTemplateButton.set(true);
        }
    }

    @Override
    public void clearData() {
        clearNetworkAccess();
        mContentImageArticle = null;
        mShowSelectTemplateButton.set(true);
    }

    @Override
    public void showData(int index) {
        mView.updateImageView(mContentImageArticle, index);
        checkNeedShowUploadButton();
    }

    @Override
    public void onClickUploadButton() {
        if (mIsUploadingImage) {
            //取消上传
            mView.setUploadButtonText("上传");
            ImageManager.getInstance().cancelUpload(mImageUploadTasks);
            mImageUploadTasks = null;
        } else {
            //开始上传
            mView.setUploadButtonText("取消");
            mImageUploadTasks = new ArrayList<>();
            for (int i = 0; i < mContentImageArticle.imageList.size(); i++) {
                AlbumPhoto image = mContentImageArticle.imageList.get(i);
                if (image.isNeedUpload()) {
                    mImageUploadTasks.add(new ImageManager.UploadTask(image, new ImageUploadListener(i)));
                }
            }
            ImageManager.init(mContext);
            mUploadCount.set(mImageUploadTasks.size());
            ImageManager.getInstance().doUpload(mImageUploadTasks);
        }
        mIsUploadingImage = !mIsUploadingImage;
    }

    @Override
    public void onTextChanged(int index, String text) {
        if (mContentImageArticle.articleList.size() > 0) {
            mContentImageArticle.articleList.set(index, text);
        }
    }

    @Override
    public void onClickChangeTemplate() {
        openTemplateWindow(mTemplateId);
    }

    private void checkNeedShowUploadButton() {
        boolean needUpload = false;
        if (mContentImageArticle.imageList.size() > 0) {
            for (AlbumPhoto image : mContentImageArticle.imageList) {
                if (!TextUtils.isEmpty(image.getLocalPath()) && image.isNeedUpload()) {
                    needUpload = true;
                    break;
                }
            }
        }
        mView.setUploadButtonVisible(needUpload);
    }

    private class ImageUploadListener implements ImageManager.ImageUploadListener {
        private int mIndex;

        public ImageUploadListener(int index) {
            mIndex = index;
        }

        @Override
        public void onWait() {
            mView.onImageUploadWait(mIndex);
        }

        @Override
        public void onStart() {
            mView.onImageUploadStart(mIndex);
        }

        @Override
        public void onProgress(int progress) {
            mView.onImageUploadProgress(mIndex, progress);
        }

        @Override
        public void onFinished(String error) {
            mView.onImageUploadFinished(mIndex, error);
            if (error != null) {
                ToastUtils.showToastShort(mContext, "图片上传失败：" + error);
            }
            if (mUploadCount.decrementAndGet() == 0) {
                mIsUploadingImage = false;
                mView.setUploadButtonText("上传");
                checkNeedShowUploadButton();
            }
        }

        @Override
        public void onCanceled() {
            mView.onImageUploadCanceled(mIndex);
        }
    }
}
