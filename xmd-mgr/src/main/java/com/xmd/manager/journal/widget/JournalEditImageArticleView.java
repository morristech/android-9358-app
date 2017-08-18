package com.xmd.manager.journal.widget;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.common.ScreenUtils;
import com.xmd.manager.databinding.JournalImageArticleBinding;
import com.xmd.manager.journal.contract.JournalContentEditContract;
import com.xmd.manager.journal.contract.JournalContentImageArticleContract;
import com.xmd.manager.journal.model.AlbumPhoto;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalItemImageArticle;
import com.xmd.manager.beans.JournalTemplateImageArticleBean;
import com.xmd.manager.journal.presenter.JournalContentImageArticlePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 17-1-3.
 */

public class JournalEditImageArticleView extends LinearLayout implements JournalContentImageArticleContract.View {
    private JournalContentImageArticleContract.Presenter mPresenter;
    private JournalContentEditContract.Presenter mContentPresenter;
    private JournalImageArticleBinding mBinding;
    private JournalContent mContent;
    private List<CustomCombineImageView> mImageViews = new ArrayList<>();
    private List<EditText> mArticleViews = new ArrayList<>();
    private TextView mUploadButton;
    private boolean mIsUploading;

    public JournalEditImageArticleView(Context context, JournalContent content, JournalContentEditContract.Presenter presenter,String templateId) {
        super(context);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        ScreenUtils.initScreenSize(((Activity) context).getWindowManager());
        mContent = content;
        mBinding = JournalImageArticleBinding.inflate(LayoutInflater.from(getContext()), this, true);
        mPresenter = new JournalContentImageArticlePresenter(getContext(), this, mBinding, mContent,templateId);
        mContent.setView(mViewUpdater);
        mContentPresenter = presenter;

        mPresenter.onCreate();
    }


    protected BaseContentView mViewUpdater = new BaseContentView() {
        @Override
        public void onDestroy() {
            mPresenter.onDestroy();
        }

        @Override
        public View getView() {
            return JournalEditImageArticleView.this;
        }

        @Override
        public void notifyDataChanged() {
            mPresenter.onDataSetChanged();
        }

        @Override
        public void notifyItemChanged(int index) {
            mPresenter.showData(index);
        }

        @Override
        public void clearData() {
            removeAllViews();
            addView(mBinding.getRoot());
            mPresenter.clearData();
        }
    };

    @Override
    public void createView(JournalTemplateImageArticleBean template) {
        removeAllViews();
        mImageViews.clear();
        mArticleViews.clear();
        if (template.imageCount > 0) {
            LinearLayout imageLayout = new LinearLayout(getContext());
            addView(imageLayout);
            imageLayout.setGravity(Gravity.CENTER);
            imageLayout.setOrientation(HORIZONTAL);
            ((LayoutParams) imageLayout.getLayoutParams()).width = LayoutParams.MATCH_PARENT;
            ((LayoutParams) imageLayout.getLayoutParams()).topMargin = ScreenUtils.dpToPx(8);
            ((LayoutParams) imageLayout.getLayoutParams()).bottomMargin = ScreenUtils.dpToPx(8);
            for (int i = 0; i < template.imageCount; i++) {
                CustomCombineImageView imageView = (CustomCombineImageView) LayoutInflater.from(getContext()).inflate(R.layout.journal_combine_image_view, null, false);
                imageLayout.addView(imageView);
                LinearLayout.LayoutParams layoutParams = (LayoutParams) imageView.getLayoutParams();
                layoutParams.width = ScreenUtils.getScreenWidth() / 4;
                layoutParams.height = ScreenUtils.getScreenWidth() / 3;
                layoutParams.leftMargin = ScreenUtils.dpToPx(16);
                layoutParams.rightMargin = ScreenUtils.dpToPx(16);
                final int contentIndex = i;
                imageView.setOnClickImageListener(new CustomCombineImageView.OnClickImageListener() {
                    @Override
                    public void onClick(View v) {
                        mContentPresenter.onImageArticleImageClicked(mContent, contentIndex);
                    }
                });
                mImageViews.add(imageView);
            }
            mUploadButton = new TextView(getContext());
            mUploadButton.setBackgroundResource(R.drawable.status_button_light_green);
            mUploadButton.setText("上传");
            mUploadButton.setTextColor(0xffffffff);
            mUploadButton.setMinWidth(ScreenUtils.dpToPx(96));
            mUploadButton.setMinHeight(ScreenUtils.dpToPx(32));
            mUploadButton.setGravity(Gravity.CENTER);
            addView(mUploadButton);
            LinearLayout.LayoutParams layoutParams = (LayoutParams) mUploadButton.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.topMargin = ScreenUtils.dpToPx(4);
            layoutParams.bottomMargin = ScreenUtils.dpToPx(8);

            mUploadButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.onClickUploadButton();
                }
            });
            mUploadButton.setVisibility(GONE);
        }
        if (template.articles != null && template.articles.size() > 0) {
            for (int i = 0; i < template.articles.size(); i++) {
                LinearLayout linearLayout = new LinearLayout(getContext());
                addView(linearLayout);
                LinearLayout.LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
                layoutParams.width = LayoutParams.MATCH_PARENT;
                layoutParams.topMargin = ScreenUtils.dpToPx(4);
                layoutParams.bottomMargin = ScreenUtils.dpToPx(4);
                TextView textView = new TextView(getContext());
                textView.setText("文字" + (i + 1) + ":");
                textView.setPadding(ScreenUtils.dpToPx(4), ScreenUtils.dpToPx(4), ScreenUtils.dpToPx(4), ScreenUtils.dpToPx(4));
                linearLayout.addView(textView);
                EditText editText = new EditText(getContext());
                linearLayout.addView(editText);
                editText.getLayoutParams().width = LayoutParams.MATCH_PARENT;
                editText.setPadding(ScreenUtils.dpToPx(4), ScreenUtils.dpToPx(4), ScreenUtils.dpToPx(4), ScreenUtils.dpToPx(4));
                editText.setBackgroundResource(R.drawable.stroke_light_gray_6);
                editText.setHint(template.articles.get(i).wordsLimit + "字以内");
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(template.articles.get(i).wordsLimit)});
                mArticleViews.add(editText);
                final int index = i;
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mPresenter.onTextChanged(index, s.toString());
                    }
                });
            }
        }
        TextView textView = new TextView(getContext());
        addView(textView);
        ((LayoutParams) textView.getLayoutParams()).topMargin = ScreenUtils.dpToPx(8);
        ((LayoutParams) textView.getLayoutParams()).bottomMargin = ScreenUtils.dpToPx(8);
        textView.setGravity(Gravity.RIGHT);
        textView.setText(Html.fromHtml("<u>更换样式</u>"));
        textView.setTextColor(getContext().getResources().getColor(R.color.primary_color));
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onClickChangeTemplate();
            }
        });
    }

    @Override
    public void showData(JournalItemImageArticle data) {
        for (int i = 0; i < data.imageList.size(); i++) {
            if (i < mImageViews.size()) {
                CustomCombineImageView imageView = mImageViews.get(i);
                AlbumPhoto image = data.imageList.get(i);
                if (!TextUtils.isEmpty(image.getLocalPath())) {
                    imageView.setImage(getContext(), image.getLocalPath());
                } else {
                    imageView.setImage(getContext(), image.getRemoteUrl());
                }
                if (!image.isNeedUpload()) {
                    imageView.showUploadSuccessView();
                    imageView.showReplaceView();
                }
            }
        }
        for (int i = 0; i < data.articleList.size(); i++) {
            if (i < mArticleViews.size()) {
                EditText editText = mArticleViews.get(i);
                editText.setText(data.articleList.get(i));
            }
        }
    }

    @Override
    public void updateImageView(JournalItemImageArticle data, int index) {
        if (index <= mImageViews.size()) {
            AlbumPhoto image = data.imageList.get(index);
            CustomCombineImageView imageView = mImageViews.get(index);
            if (!TextUtils.isEmpty(image.getLocalPath())) {
                imageView.setImage(getContext(), image.getLocalPath());
                imageView.showReplaceView();
            }
            if (!TextUtils.isEmpty(image.getImageId())) {
                imageView.showUploadSuccessView();
            } else {
                imageView.hideUploadSuccessView();
            }
        }
    }

    @Override
    public void setUploadButtonVisible(boolean visible) {
        mUploadButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setUploadButtonText(String text) {
        mUploadButton.setText(text);
    }

    @Override
    public void onImageUploadWait(int index) {
        mImageViews.get(index).getUpdateListener().onWait();
    }

    @Override
    public void onImageUploadStart(int index) {
        mImageViews.get(index).getUpdateListener().onStart();
    }

    @Override
    public void onImageUploadProgress(int index, int progress) {
        mImageViews.get(index).getUpdateListener().onProgress(progress);
    }

    @Override
    public void onImageUploadFinished(int index, String error) {
        mImageViews.get(index).getUpdateListener().onFinished(error);
        if (error == null) {
            AlbumPhoto image = ((JournalItemImageArticle) mContent.getData(0)).imageList.get(index);
            mContentPresenter.onUploadImageSuccess(image.getImageId());
        }
    }

    @Override
    public void onImageUploadCanceled(int index) {
        mImageViews.get(index).getUpdateListener().onCanceled();
    }
}
