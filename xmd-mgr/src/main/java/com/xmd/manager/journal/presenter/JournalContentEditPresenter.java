package com.xmd.manager.journal.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.soundcloud.android.crop.Crop;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.ServiceItem;
import com.xmd.manager.common.ImageTool;
import com.xmd.manager.common.Logger;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.UINavigation;
import com.xmd.manager.journal.contract.JournalContentEditContract;
import com.xmd.manager.journal.manager.CouponActivityManager;
import com.xmd.manager.journal.manager.ImageManager;
import com.xmd.manager.journal.manager.JournalManager;
import com.xmd.manager.journal.manager.TechnicianManager;
import com.xmd.manager.journal.manager.VideoManager;
import com.xmd.manager.journal.model.AlbumPhoto;
import com.xmd.manager.journal.model.CouponActivity;
import com.xmd.manager.journal.model.Journal;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalContentType;
import com.xmd.manager.journal.model.JournalItemBase;
import com.xmd.manager.journal.model.JournalItemCoupon;
import com.xmd.manager.journal.model.JournalItemImageArticle;
import com.xmd.manager.journal.model.JournalItemPhoto;
import com.xmd.manager.journal.model.JournalItemService;
import com.xmd.manager.journal.model.JournalItemTechnician;
import com.xmd.manager.journal.model.JournalItemVideo;
import com.xmd.manager.journal.model.Technician;
import com.xmd.manager.journal.model.TechnicianRanking;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.widget.AlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import rx.Subscription;

/**
 * Created by heyangya on 16-10-31.
 */

public class JournalContentEditPresenter implements JournalContentEditContract.Presenter {
    private JournalManager mJournalManager = JournalManager.getInstance();
    private TechnicianManager mTechnicianManager = TechnicianManager.getInstance();

    private Context mContext;
    private JournalContentEditContract.View mView;
    private Journal mJournal;
    private List<JournalContent> mDeletedContents = new ArrayList<>();

    private JournalContent mContentTechnician;
    private int mContentTechnicianItemIndex;

    private JournalContent mContentService;

    private int mContentPhotoAlbumItemIndex;
    private JournalContent mContentPhotoAlbum;

    private JournalContent mContentVideo;

    private JournalContent mContentImageArticle;
    private int mContentImageArticleIndex;


    private Subscription mSaveJournalSubscription;
    private Subscription mTechnicianRankingSubscription;
    private Subscription mPhotoIdsToUrlsSubscription;

    private AlertDialog mImageOperationDialog;

    private boolean mAddMode;
    private List<String> mUploadedImageIdList = new ArrayList<>(); //当前编辑界面已上传数据
    private List<String> mUploadedVideoIdList = new ArrayList<>(); //当前编辑界面已上传数据

    private Journal mOriginJournal; //原始期刊数据，当只预览不保存时，需要重新将数据写回
    private boolean mPreviewForward; //由于预览导致服务器数据比当前要新

    private com.xmd.image_tool.ImageTool xmdImageTool = new com.xmd.image_tool.ImageTool();

    public JournalContentEditPresenter(Context context, JournalContentEditContract.View view) {
        mView = view;
        mContext = context;
    }

    @Override
    public void onCreate() {
        mOriginJournal = mJournalManager.getOriginJournal();
        mJournal = mJournalManager.getTemporaryJournal();
        mAddMode = mOriginJournal.getId() == Journal.CREATE_JOURNAL_ID;

        mView.showJournal(mJournal, mDeletedContents);
    }

    @Override
    public void onDestroy() {
        if (mSaveJournalSubscription != null) {
            mSaveJournalSubscription.unsubscribe();
        }
        if (mTechnicianRankingSubscription != null) {
            mTechnicianRankingSubscription.unsubscribe();
        }
        if (mPhotoIdsToUrlsSubscription != null) {
            mPhotoIdsToUrlsSubscription.unsubscribe();
        }
    }

    @Override
    public void onClickSaveDraft() {
        if (needSave()) {
            Logger.i("content is changed ,save ...");
            saveOrPreview("保存", new Callback<Void>() {
                @Override
                public void onResult(Throwable error, Void result) {
                    ToastUtils.showToastShort(mContext, "保存成功！");
                    //删除无用数据
                    deleteUnusedData();
                    mPreviewForward = false;
                    finishSelf();
                }
            });
        } else {
            Logger.i("content is not changed ,just finish");
            finishSelf();
        }
    }

    @Override
    public void onClickPreView() {
        if (mAddMode || needSave()) {
            mPreviewForward = true;
            saveOrPreview("预览", new Callback<Void>() {
                @Override
                public void onResult(Throwable error, Void result) {
                    UINavigation.gotoWebBrowse(mContext, SharedPreferenceHelper.getServerHost() + mJournal.getPreviewUrl());
                }
            });
        } else {
            UINavigation.gotoWebBrowse(mContext, SharedPreferenceHelper.getServerHost() + mJournal.getPreviewUrl());
        }
    }

    private void saveOrPreview(String action, Callback<Void> callback) {
        if (!checkContentDataIsReady(mJournal, action)) {
            return;
        }
        mView.showLoadingAnimation();
        if (mSaveJournalSubscription != null) {
            mSaveJournalSubscription.unsubscribe();
        }
        mSaveJournalSubscription = mJournalManager.addOrUpdateJournal(mJournal, new Callback<Void>() {
            @Override
            public void onResult(Throwable error, Void result) {
                mView.hideLoadingAnimation();
                mSaveJournalSubscription = null;
                if (error != null) {
                    mView.showAlertDialog(action + "失败：" + error.getLocalizedMessage());
                } else {
                    callback.onResult(null, null);
                }
            }
        });
    }

    @Override
    public void onCopyContent(JournalContent content) {
        mJournal.copyAndAddContent(content);
        mView.updateJournalContentView();
    }

    @Override
    public void onDeleteContent(JournalContent content) {
        new AlertDialogBuilder(mContext)
                .setMessage("确定删除吗？")
                .setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setNegativeButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        content.clearData();
                        mJournal.removeContent(content);
                        if (!mJournal.isContentContainType(content.getType())) {
                            mDeletedContents.add(new JournalContent(content.getType(), content.getSubContentCount()));
                        }
                        mView.updateJournalContentView();
                    }
                })
                .show();
    }

    @Override
    public void onMoveUpContent(JournalContent content) {
        mJournal.moveContentUp(content);
        mView.updateJournalContentView();
    }

    @Override
    public void onRecoverContent(JournalContent content) {
        mDeletedContents.remove(content);
        mJournal.addContent(content);
        mView.updateJournalContentView();
    }

    @Override
    public void onSetTitle(String title) {
        mJournal.setTitle(title);
    }

    @Override
    public void onSetSubTitle(String subTitle) {
        mJournal.setSubTitle(subTitle);
    }

    @Override
    public void onContentTitleChange(JournalContent content, String title) {
        content.setTitle(title);
    }

    @Override
    public void refreshListItemView(int viewPosition) {
        mView.updateJournalContentSubview(viewPosition);
    }


    /**********************************************************
     * 技师
     **********************************************************/
    @Override
    public void onClickAddTechnician(JournalContent content) {
        addOrReplaceTechnician(content, -1);
    }

    @Override
    public void onClickDeleteTechnician(JournalContent content, int index) {
        content.removeData(index);
        content.getViewHolder().notifyDataChanged();
    }

    @Override
    public void onClickReplaceTechnician(JournalContent content, int index) {
        addOrReplaceTechnician(content, index);
    }

    // index <0 表示增加，index >=0 表示替换
    private void addOrReplaceTechnician(JournalContent content, int index) {
        mContentTechnician = content;
        mContentTechnicianItemIndex = index;
        ArrayList<String> techNoList = new ArrayList<>();
        for (int i = 0; i < content.getDataSize(); i++) {
            Technician technician = ((JournalItemTechnician) content.getData(i)).getTechnician();
            if (technician != null) {
                techNoList.add(technician.getId());
            }
        }
        UINavigation.gotoTechnicianChoiceActivityForResult((Activity) mContext, techNoList);
    }


    /******************************************
     * 项目
     ************************************************/

    @Override
    public void onClickAddServiceItem(JournalContent content) {
        addOrReplaceService(content, -1);
    }

    @Override
    public void onClickDeleteServiceItem(JournalContent content, int index) {
        content.removeData(index);
        content.getViewHolder().notifyDataChanged();
    }

    @Override
    public void onClickReplaceServiceItem(JournalContent content, int index) {
        addOrReplaceService(content, index);
    }


    private void addOrReplaceService(JournalContent content, int index) {
        mContentService = content;
        ArrayList<ServiceItem> ServiceItemList = new ArrayList<>();
        for (int i = 0; i < content.getDataSize(); i++) {
            JournalItemService journalContentService = (JournalItemService) content.getData(i);
            if (journalContentService != null) {
                ServiceItem serviceItem = journalContentService.getServiceItem();
                if (serviceItem != null) {
                    ServiceItemList.add(serviceItem);
                }
            }
        }
        UINavigation.gotoProjectChoiceActivityForResult((Activity) mContext, ServiceItemList, content.getSubContentCount());
    }

    /**********************************************************************
     * 相册
     ************************************************/
    @Override
    public void onLoadPhotoData(JournalContent content) {
        if (mPhotoIdsToUrlsSubscription != null) {
            mPhotoIdsToUrlsSubscription.unsubscribe();
        }
        mPhotoIdsToUrlsSubscription = mJournalManager.getPhotoUrls(content, new Callback<Void>() {
            @Override
            public void onResult(Throwable error, Void result) {
                if (error == null) {
                    content.getViewHolder().notifyDataChanged();
                } else {
                    mView.showToast("获取图片URL失败：" + error.getLocalizedMessage());
                }
            }
        });
    }

    //检查权限，有权限返回true
    private boolean requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }

    @Override
    public void onClickAddPhoto(JournalContent content) {
        if (requestPermission()) {
            mContentPhotoAlbum = content;
            mContentPhotoAlbumItemIndex = -1;
            ArrayList<String> data = new ArrayList<>();
            int selectCount = content.getSubContentCount();
            for (int i = 0; i < mContentPhotoAlbum.getDataSize(); i++) {
                AlbumPhoto photo = ((JournalItemPhoto) mContentPhotoAlbum.getData(i)).getPhoto();
                if (!TextUtils.isEmpty(photo.getImageId()) && TextUtils.isEmpty(photo.getLocalPath())) {
                    selectCount--;
                } else {
                    data.add(photo.getLocalPath());
                }
            }
            MultiImageSelector.create()
                    .multi()
                    .count(selectCount)
                    .origin(data)
                    .start((Activity) mContext, UINavigation.REQUEST_CODE_CHOICE_IMAGE);
        }
    }

    @Override
    public void onClickDeletePhoto(JournalContent content, int index) {
        content.removeData(index);
        content.getViewHolder().notifyDataChanged();
    }

    @Override
    public void onClickReplacePhoto(JournalContent content, int index) {
        mContentPhotoAlbum = content;
        mContentPhotoAlbumItemIndex = index;

        JournalItemPhoto contentPhoto = (JournalItemPhoto) mContentPhotoAlbum.getData(index);
        if (TextUtils.isEmpty(contentPhoto.getPhoto().getLocalPath())) {
            MultiImageSelector.create()
                    .single()
                    .start((Activity) mContext, UINavigation.REQUEST_CODE_CHOICE_IMAGE);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.journal_photo_operations_view, null);
            view.findViewById(R.id.replace).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImageOperationDialog.dismiss();
                    MultiImageSelector.create()
                            .single()
                            .start((Activity) mContext, UINavigation.REQUEST_CODE_CHOICE_IMAGE);
                }
            });
            view.findViewById(R.id.crop).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImageOperationDialog.dismiss();
                    //裁剪
                    String path = ((JournalItemPhoto) mContentPhotoAlbum.getData(index)).getPhoto().getLocalPath();
                    Uri fromUri = new Uri.Builder().scheme("file").path(path).build();
                    Uri toUri = new Uri.Builder().scheme("file").path(mContext.getCacheDir().getPath() + File.separator + "crop-images" + File.separator + System.currentTimeMillis()).build();
                    Crop.of(fromUri, toUri).start((Activity) mContext, UINavigation.REQUEST_CODE_CROP_IMAGE);
                }
            });
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImageOperationDialog.dismiss();
                }
            });
            ImageTool.loadImage(mContext, contentPhoto.getPhoto().getLocalPath(), (ImageView) view.findViewById(R.id.image));
            mImageOperationDialog = new AlertDialog.Builder(mContext)
                    .setView(view)
                    .create();
            mImageOperationDialog.show();
        }

    }

    @Override
    public void onUploadImageSuccess(String imageId) {
        //图片上伟成功，记录到已上传图片数据中
        mUploadedImageIdList.add(imageId);
    }

    /*******************************
     * 技师排行板
     ***************************/
    @Override
    public void onLoadTechnicianRankingData(JournalContent content) {
        if (mTechnicianRankingSubscription != null) {
            mTechnicianRankingSubscription.unsubscribe();
        }
        mTechnicianRankingSubscription = mTechnicianManager.loadTechnicianRanking(
                RequestConstant.TECHNICIAN_RANKING_TYPE_COMMENT,
                new Callback<TechnicianRanking>() {
                    @Override
                    public void onResult(Throwable error, TechnicianRanking result) {
                        if (error == null) {
                            for (Technician item : result.getData()) {
                                if (content.getDataSize() < content.getSubContentCount()) {
                                    content.addData(new JournalItemTechnician(item));
                                } else {
                                    break;
                                }
                            }
                            content.getViewHolder().notifyDataChanged();
                        } else {
                            mView.showToast("加载服务之星失败!");
                        }
                    }
                });
    }

    /************************************
     * 优惠活动
     ***********************************************/
    @Override
    public void onLoadCouponActivityList(Callback<List<CouponActivity>> callback) {
        CouponActivityManager.getInstance().loadData(callback);
    }

    @Override
    public void onSelectCouponActivity(JournalContent content, CouponActivity couponActivity, int itemIndex) {
        if (content.getDataSize() == 0) {
            JournalItemCoupon journalContentCoupon = new JournalItemCoupon(couponActivity);
            journalContentCoupon.setSelectedItemIndex(itemIndex);
            content.addData(journalContentCoupon);
            content.getViewHolder().notifyDataChanged();
        } else {
            JournalItemCoupon journalContentCoupon = (JournalItemCoupon) content.getData(0);
            if (journalContentCoupon.getCouponActivity() != null && journalContentCoupon.getCouponActivity().getCategory().equals(couponActivity.getCategory())) {
                //没有切换类型,不需要刷新界面
                journalContentCoupon.setSelectedItemIndex(itemIndex);
            } else {
                journalContentCoupon.setCouponActivity(couponActivity);
                journalContentCoupon.setSelectedItemIndex(itemIndex);
                content.getViewHolder().notifyDataChanged();
            }
        }
    }

    /*************************************************
     * 录制视频
     ********************************************************/

    @Override
    public void onClickRecordVideo(JournalContent content) {
        mContentVideo = content;
        UINavigation.gotoRecordVideoActivityForResult((Activity) mContext);
    }

    @Override
    public void onLoadVideoData(JournalContent content) {
        mJournalManager.getVideoInfo((JournalItemVideo) content.getData(0), new Callback<Void>() {
            @Override
            public void onResult(Throwable error, Void result) {
                if (error == null) {
                    content.getViewHolder().notifyDataChanged();
                } else {
                    mView.showToast("获取视频信息失败：" + error.getLocalizedMessage());
                }
            }
        });
    }

    @Override
    public void onUploadVideoSuccess(JournalContent content) {
        mUploadedVideoIdList.add(((JournalItemVideo) content.getData(0)).getMicroVideo().getResourcePath());
    }

    /*********************************************
     * 图文版块
     *********************************************/
    @Override
    public void onImageArticleImageClicked(JournalContent content, int index) {
        mContentImageArticle = content;
        mContentImageArticleIndex = index;
        xmdImageTool.onlyPick(true).start((Activity) mContext, new com.xmd.image_tool.ImageTool.ResultListener() {
            @Override
            public void onResult(String s, Uri uri, Bitmap bitmap) {
                if (s == null && uri != null) {
                    if (mContentImageArticle.getDataSize() > 0) {
                        String localPath = uri.getPath();
                        JournalItemImageArticle imageArticle = (JournalItemImageArticle) mContentImageArticle.getData(0);
                        AlbumPhoto image = imageArticle.imageList.get(mContentImageArticleIndex);
                        image.setImageId(null);
                        image.setRemoteUrl(null);
                        image.setLocalPath(localPath);
                        mContentImageArticle.getViewHolder().notifyItemChanged(mContentImageArticleIndex);
                    }
                }
            }
        });
    }

    @Override
    public void onImageArticleTextChanged(JournalContent content, int index) {

    }


    /**********************************************************************************************/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        xmdImageTool.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == UINavigation.REQUEST_CODE_CHOICE_TECHNICIAN) {
                //技师
                String tecId = data.getStringExtra(UINavigation.EXTRA_STRING_TECHNICIAN_ID);
                Technician technician = mTechnicianManager.getTechnician(tecId);
                if (mContentTechnicianItemIndex >= 0) {
                    //---替换
                    mContentTechnician.removeData(mContentTechnicianItemIndex);
                    mContentTechnician.addData(mContentTechnicianItemIndex, new JournalItemTechnician(technician));
                    mContentTechnician.getViewHolder().notifyItemChanged(mContentTechnicianItemIndex);
                } else {
                    //---增加
                    mContentTechnician.addData(new JournalItemTechnician(technician));
                    mContentTechnician.getViewHolder().notifyDataChanged();
                }
            } else if (requestCode == UINavigation.REQUEST_CODE_CHOICE_IMAGE) {
                //相册
                ArrayList<String> result = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (mContentPhotoAlbumItemIndex >= 0) {
                    //---替换
                    mContentPhotoAlbum.removeData(mContentPhotoAlbumItemIndex); //先删除数据，再插入，为了触发onRemove事件
                    AlbumPhoto photo = new AlbumPhoto(result.get(0));
                    JournalItemPhoto journalContentPhoto = new JournalItemPhoto(photo);
                    mContentPhotoAlbum.addData(mContentPhotoAlbumItemIndex, journalContentPhoto);
                    mContentPhotoAlbum.getViewHolder().notifyItemChanged(mContentPhotoAlbumItemIndex);
                } else {
                    //---增加
                    List<JournalItemBase> needDeleteData = new ArrayList<>();
                    for (int i = 0; i < mContentPhotoAlbum.getDataSize(); i++) {
                        JournalItemPhoto journalContentPhoto = (JournalItemPhoto) mContentPhotoAlbum.getData(i);
                        AlbumPhoto photo = journalContentPhoto.getPhoto();
                        if (TextUtils.isEmpty(photo.getImageId())) {
                            needDeleteData.add(journalContentPhoto);  //远程链接为空的全部移除
                            continue;
                        }
                        if (!TextUtils.isEmpty(photo.getLocalPath())) {
                            if (!result.contains(photo.getLocalPath())) {
                                needDeleteData.add(journalContentPhoto); //远程和本地链接不为空，但结果不包含本地链接的，删除
                            } else {
                                result.remove(photo.getLocalPath()); //远程和本地链接不为空，且结果包含本地链接的，不删除，只从结果中移除避免重复添加
                            }
                        }
                    }
                    for (JournalItemBase deleteData : needDeleteData) {
                        mContentPhotoAlbum.removeData(deleteData);
                    }
                    for (int i = 0; i < result.size(); i++) {
                        mContentPhotoAlbum.addData(new JournalItemPhoto(new AlbumPhoto(result.get(i))));
                    }
                    mContentPhotoAlbum.getViewHolder().notifyDataChanged();
                }
            } else if (requestCode == UINavigation.REQUEST_CODE_CROP_IMAGE) {
                //图片裁剪
                Uri outputUri = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                AlbumPhoto item = ((JournalItemPhoto) mContentPhotoAlbum.getData(mContentPhotoAlbumItemIndex)).getPhoto();
                item.setLocalPath(outputUri.getPath());
                mContentPhotoAlbum.getViewHolder().notifyItemChanged(mContentPhotoAlbumItemIndex);
            } else if (requestCode == UINavigation.REQUEST_CODE_CHOICE_SERVICE) {
                //项目
                ArrayList<ServiceItem> serviceItems = data.getParcelableArrayListExtra(UINavigation.EXTRA_PARCELABLE_SELECTED_SERVICE_ITEMS);
                mContentService.clearData();
                for (int i = 0; i < serviceItems.size(); i++) {
                    mContentService.addData(i, new JournalItemService(serviceItems.get(i)));
                }
                mContentService.getViewHolder().notifyDataChanged();
            } else if (requestCode == UINavigation.REQUEST_CODE_RECORD_VIDEO) {
                //微视频
                String videoPath = UINavigation.getRecordVideoPath(data);
                if (mContentVideo.getDataSize() > 0) {
                    mContentVideo.removeData(0);
                }
                mContentVideo.addData(new JournalItemVideo());
                JournalItemVideo journalContentVideo = (JournalItemVideo) mContentVideo.getData(0);
                journalContentVideo.getMicroVideo().setLocalUrl(videoPath);
                mContentVideo.getViewHolder().notifyDataChanged();
            } else if (requestCode == UINavigation.REQUEST_CODE_CHOICE_IMAGE_ARTICLE_IMAGE) {
//                if (mContentImageArticle.getDataSize() > 0) {
//                    String localPath = data.getData().getPath();
//                    JournalItemImageArticle imageArticle = (JournalItemImageArticle) mContentImageArticle.getData(0);
//                    AlbumPhoto image = imageArticle.imageList.get(mContentImageArticleIndex);
//                    image.setImageId(null);
//                    image.setRemoteUrl(null);
//                    image.setLocalPath(localPath);
//                    mContentImageArticle.getViewHolder().notifyItemChanged(mContentImageArticleIndex);
//                }
            }
        }
    }

    @Override
    public void onBackKey() {
        if (needSave()) {
            new AlertDialogBuilder(mContext)
                    .setMessage("草稿还没有保存，是否保存并返回？")
                    .setPositiveButton("保存", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onClickSaveDraft();
                            //删除无用数据
                            deleteUnusedData();
                        }
                    })
                    .setNeutralButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .setNegativeButton("不保存", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mPreviewForward) {
                                if (mAddMode) {
                                    //需要删除草稿
                                    Logger.i("discard changes,delete preview data!");
                                    mJournalManager.deleteJournal(mJournal, null);
                                } else {
                                    //需要写回原来的数据
                                    Logger.i("discard changes,write origin data back!");
                                    mJournalManager.addOrUpdateJournal(mOriginJournal, null);
                                }
                            }
                            //删除已上传的数据
                            deleteUploadedData();
                            finishSelf();
                        }
                    })
                    .show();
        } else {
            finishSelf();
        }
    }

    @Override
    public void onClearContent() {
        new AlertDialogBuilder(mContext)
                .setMessage("确定清除所有数据？")
                .setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setNegativeButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //清空标题栏
                        mJournal.setTitle("");
                        mJournal.setSubTitle("");

                        //清空内容
                        //删除多余的版块，每种版块只留下一个
                        List<JournalContent> addBack = new ArrayList<>();
                        Iterator<JournalContent> iterator = mJournal.getContentIterator();
                        while (iterator.hasNext()) {
                            JournalContent content = iterator.next();
                            if (content.getViewHolder() != null) {
                                content.getViewHolder().clearData();
                            }
                            iterator.remove();
                            if (!mJournal.isContentContainType(content.getType())) {
                                addBack.add(content);
                            }
                        }
                        for (JournalContent content : addBack) {
                            mJournal.addContent(content);
                        }
                        //将已删除的版块添加回来
                        for (JournalContent content : mDeletedContents) {
                            mJournal.addContent(content);
                        }
                        mDeletedContents.clear();

                        //清除每个版块的内容
                        for (int i = 0; i < mJournal.getContentSize(); i++) {
                            JournalContent content = mJournal.getContent(i);
                            //排行榜不需要删除数据
                            if (!content.getType().getKey().equals(JournalContentType.CONTENT_KEY_TECHNICIAN_RANKING)) {
                                content.clearData();
                                if (content.getViewHolder() != null) {
                                    content.getViewHolder().notifyDataChanged();
                                }
                            }
                            content.setTitle(content.getType().getName());
                        }
                        //刷新界面
                        mView.showJournal(mJournal, mDeletedContents);
                    }
                })
                .show();
    }


    //查检数据
    private boolean checkContentDataIsReady(Journal journal, String action) {
        String ready = journal.AllDataIsReady();
        if (!TextUtils.equals("true", ready)) {
            mView.showAlertDialog(action + "失败：" + ready);
            return false;
        }
        return true;
    }

    //从期刊中获取当前所有的图片ID
    private List<String> getImageIdsFromJournal(Journal journal) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < journal.getContentSize(); i++) {
            JournalContent content = journal.getContent(i);
            if (content.getType().getKey().equals(JournalContentType.CONTENT_KEY_PHOTO_ALBUM)) {
                for (int j = 0; j < content.getDataSize(); j++) {
                    JournalItemPhoto journalContentPhoto = (JournalItemPhoto) content.getData(j);
                    if (journalContentPhoto.getPhoto() != null) {
                        result.add(journalContentPhoto.getPhoto().getImageId());
                    }
                }
            } else if (content.getType().getKey().equals(JournalContentType.CONTENT_KEY_IMAGE_ARTICLE)) {
                if (content.getDataSize() > 0) {
                    JournalItemImageArticle imageArticle = (JournalItemImageArticle) content.getData(0);
                    if (imageArticle.imageList.size() > 0) {
                        for (AlbumPhoto image : imageArticle.imageList) {
                            if (!TextUtils.isEmpty(image.getImageId())) {
                                result.add(image.getImageId());
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    //从期刊中获取当前所有的视频ID
    private List<String> getVideoIdsFromJournal(Journal journal) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < journal.getContentSize(); i++) {
            JournalContent content = journal.getContent(i);
            if (content.getType().getKey().equals(JournalContentType.CONTENT_KEY_VIDEO)) {
                if (content.getDataSize() > 0) {
                    JournalItemVideo video = (JournalItemVideo) content.getData(0);
                    if (video.getMicroVideo() != null) {
                        result.add(video.getMicroVideo().getResourcePath());
                    }
                }
            }
        }
        return result;
    }

    //不保存操作，需要删除已上传的数据
    private void deleteUploadedData() {
        //删除图片
        for (String imageId : mUploadedImageIdList) {
            Logger.i("delete photo " + imageId);
            ImageManager.getInstance().deleteImageInServer(imageId);
        }
        //删除视频
        for (String videoId : mUploadedVideoIdList) {
            Logger.i("delete video " + videoId);
            VideoManager.getInstance().deleteVideoInServer(videoId);
        }
    }

    //保存操作，需要删除无用的数据
    private void deleteUnusedData() {
        //删除图片
        mUploadedImageIdList.addAll(getImageIdsFromJournal(mOriginJournal));
        List<String> currentImageIdList = getImageIdsFromJournal(mJournal);
        for (String imageId : mUploadedImageIdList) {
            if (!currentImageIdList.contains(imageId)) {
                //这个图片没有用了
                Logger.i("delete photo " + imageId);
                ImageManager.getInstance().deleteImageInServer(imageId);
            }
        }

        //删除视频
        mUploadedVideoIdList.addAll(getVideoIdsFromJournal(mOriginJournal));
        List<String> currentVideoIdList = getVideoIdsFromJournal(mJournal);
        for (String videoId : mUploadedVideoIdList) {
            if (!currentVideoIdList.contains(videoId)) {
                //这个视频没有用了，需要删除
                Logger.i("delete video " + videoId);
                VideoManager.getInstance().deleteVideoInServer(videoId);
            }
        }
    }

    private boolean needSave() {
        return !mJournal.contentDataIsReady().equals("true") || !mJournal.saveDataEquals(mOriginJournal);
    }

    private void finishSelf() {
        //销毁View
        for (int i = 0; i < mJournal.getContentSize(); i++) {
            JournalContent content = mJournal.getContent(i);
            if (content.getViewHolder() != null) {
                content.getViewHolder().onDestroy();
                content.setView(null);
            }
        }
        mView.finishSelf();
    }
}
