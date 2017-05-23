package com.xmd.manager.journal.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ClipDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.common.ImageTool;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.journal.UINavigation;
import com.xmd.manager.journal.contract.JournalContentEditContract;
import com.xmd.manager.journal.manager.ThreadManager;
import com.xmd.manager.journal.manager.VideoManager;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalItemVideo;
import com.xmd.manager.journal.model.MicroVideo;

/**
 * Created by heyangya on 16-11-9.
 */

public class JournalEditVideoView extends LinearLayout {
    protected Context mContext;
    protected JournalContent mContent;
    private boolean mIsInitSubView;
    private ImageView mProgressImageView;
    private ClipDrawable mProgressClipDrawable;
    private JournalContentEditContract.Presenter mPresenter;

    private TextView mRecordButton;
    private TextView mCancelButton;
    private TextView mUploadButton;

    private View mPlayButton;
    private ImageView mVideoPreview;
    private View mUploadSuccessView;
    private View mVideoViewLayout;


    public JournalEditVideoView(Context context, JournalContent content, JournalContentEditContract.Presenter presenter) {
        super(context);
        mContent = content;
        mContext = context;
        mPresenter = presenter;
        mContent.setView(mViewUpdater);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!mIsInitSubView) {
            mIsInitSubView = true;
            View view = LayoutInflater.from(mContext).inflate(R.layout.journal_content_video, null);
            initView(view);
            addView(view);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void notifyDataChanged() {
        if (mContent.getDataSize() > 0) {
            JournalItemVideo journalContentVideo = (JournalItemVideo) mContent.getData(0);
            if (journalContentVideo.isUploading()) {
                mUploadButton.setVisibility(GONE);
                mRecordButton.setVisibility(GONE);
                mCancelButton.setVisibility(VISIBLE);
                mProgressImageView.setVisibility(VISIBLE);
                return;
            }

            MicroVideo microVideo = journalContentVideo.getMicroVideo();

            if (microVideo != null) {
                if (!TextUtils.isEmpty(microVideo.getAccessUrl())) {
                    mVideoViewLayout.setVisibility(VISIBLE);
                    mUploadSuccessView.setVisibility(VISIBLE);
                    mProgressImageView.setVisibility(GONE);
                    mRecordButton.setVisibility(VISIBLE);
                    mRecordButton.setText("重新录制");
                    mCancelButton.setVisibility(GONE);
                    mUploadButton.setVisibility(GONE);
                    loadVideoCover(microVideo, microVideo.getAccessUrl(), mVideoPreview);
                } else {
                    if (!TextUtils.isEmpty(microVideo.getLocalUrl())) {
                        mVideoViewLayout.setVisibility(VISIBLE);
                        mUploadSuccessView.setVisibility(GONE);
                        mProgressImageView.setVisibility(GONE);
                        mRecordButton.setVisibility(VISIBLE);
                        mRecordButton.setText("重新录制");
                        mCancelButton.setVisibility(GONE);
                        mUploadButton.setVisibility(VISIBLE);
                        loadVideoCover(microVideo, microVideo.getLocalUrl(), mVideoPreview);
                    } else {
                        showEmptyView();
                    }
                }
            } else {
                showEmptyView();
            }
        } else {
            showEmptyView();
        }
    }

    private void loadVideoCover(MicroVideo microVideo, String path, ImageView view) {
        if (!TextUtils.isEmpty(microVideo.getVideoCoverUrl())) {
            ImageTool.loadImage(getContext(), microVideo.getVideoCoverUrl(), view);
            return;
        }

        ThreadManager.run(new Runnable() {
            @Override
            public void run() {
                String coverPath = VideoManager.getInstance().getVideoCoverPath(getContext(), path);
                if (coverPath != null) {
                    ThreadManager.postToUI(new Runnable() {
                        @Override
                        public void run() {
                            ImageTool.loadImage(getContext(), coverPath, view);
                        }
                    });
                    return;
                }

                if (path.startsWith("/") || path.startsWith("file") || path.startsWith("content")) {
                    final Bitmap finalBitmap = VideoManager.getInstance().getVideoCoverFromFile(getContext(), path);
                    if (finalBitmap != null) {
                        ThreadManager.postToUI(new Runnable() {
                            @Override
                            public void run() {
                                view.setImageBitmap(finalBitmap);
                            }
                        });
                    }
                }
            }
        });
    }

    //presenter 使用它来通知view更新
    protected BaseContentView mViewUpdater = new BaseContentView() {
        @Override
        public void notifyDataChanged() {
            JournalEditVideoView.this.notifyDataChanged();
        }

        @Override
        public View getView() {
            return JournalEditVideoView.this;
        }
    };

    private void initView(View view) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        mRecordButton = (TextView) view.findViewById(R.id.btn_record);
        mCancelButton = (TextView) view.findViewById(R.id.btn_cancel);
        mUploadButton = (TextView) view.findViewById(R.id.btn_upload);


        mRecordButton.setOnClickListener(mOnClickRecord);
        mUploadButton.setOnClickListener(mOnClickUpload);
        mCancelButton.setOnClickListener(mOnClickCancel);

        mVideoViewLayout = view.findViewById(R.id.video_layout);
        mUploadSuccessView = view.findViewById(R.id.img_ok);
        mVideoPreview = (ImageView) view.findViewById(R.id.video_preview);
        mPlayButton = view.findViewById(R.id.btn_play);
        mPlayButton.setOnClickListener(mOnClickPlay);
        mProgressImageView = (ImageView) view.findViewById(R.id.progress_mask);
        mProgressClipDrawable = (ClipDrawable) mProgressImageView.getBackground();
        mProgressImageView.setVisibility(GONE);

        showEmptyView();
        if (mContent.getDataSize() > 0) {
            mPresenter.onLoadVideoData(mContent);
        }
    }

    private void showEmptyView() {
        mVideoViewLayout.setVisibility(GONE);
        mCancelButton.setVisibility(GONE);
        mUploadButton.setVisibility(GONE);
    }

    private OnClickListener mOnClickPlay = new OnClickListener() {
        @Override
        public void onClick(View v) {
            JournalItemVideo journalContentVideo = (JournalItemVideo) mContent.getData(0);
            String path = journalContentVideo.getMicroVideo().getLocalUrl();
            if (TextUtils.isEmpty(path)) {
                path = journalContentVideo.getMicroVideo().getAccessUrl();
            }
            UINavigation.gotoPlayVideo(getContext(), path);
        }
    };


    private OnClickListener mOnClickRecord = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mPresenter.onClickRecordVideo(mContent);
        }
    };

    private OnClickListener mOnClickUpload = new OnClickListener() {
        @Override
        public void onClick(View v) {
            JournalItemVideo journalContentVideo = (JournalItemVideo) mContent.getData(0);
            VideoManager.getInstance().startUpload(getContext(), journalContentVideo, new VideoManager.VideoUploadListener() {
                @Override
                public void onStart() {
                    journalContentVideo.setUploading(true);
                    mProgressImageView.setVisibility(VISIBLE);
                    mProgressClipDrawable.setLevel(10000);
                    notifyDataChanged();
                }

                @Override
                public void onProgress(int progress) {
                    mProgressClipDrawable.setLevel(10000 - progress * 100);
                }

                @Override
                public void onCanceled() {
                    mProgressImageView.setVisibility(GONE);
                    journalContentVideo.setUploading(false);
                    notifyDataChanged();
                }

                @Override
                public void onFinished(String error) {

                    journalContentVideo.setUploading(false);
                    if (error == null) {
                        journalContentVideo.getMicroVideo().setAccessUrl(journalContentVideo.getMicroVideo().getLocalUrl());
                        mPresenter.onUploadVideoSuccess(mContent);
                    } else {
                        ToastUtils.showToastShort(mContext, "视频上传失败：" + error);
                    }
                    notifyDataChanged();
                }
            });

            notifyDataChanged();
        }
    };

    private OnClickListener mOnClickCancel = new OnClickListener() {
        @Override
        public void onClick(View v) {
            VideoManager.getInstance().cancelUpload();
        }
    };
}
