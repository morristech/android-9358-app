package com.xmd.manager.journal.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;

import com.tencent.mm.algorithm.MD5;
import com.tencent.upload.Const;
import com.tencent.upload.UploadManager;
import com.tencent.upload.task.ITask;
import com.tencent.upload.task.IUploadTaskListener;
import com.tencent.upload.task.VideoAttr;
import com.tencent.upload.task.data.FileInfo;
import com.tencent.upload.task.impl.VideoUploadTask;
import com.xmd.manager.BuildConfig;
import com.xmd.manager.ClubData;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.common.Logger;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.model.JournalItemVideo;
import com.xmd.manager.journal.model.MicroVideo;
import com.xmd.manager.journal.net.NetworkSubscriber;
import com.xmd.manager.service.RetrofitServiceFactory;
import com.xmd.manager.service.response.BaseStringResult;
import com.xmd.manager.service.response.JournalVideoConfigResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by heyangya on 16-12-1.
 */
public class VideoManager {
    private static VideoManager ourInstance = new VideoManager();
    private UploadManager mUploadManager;
    private JournalVideoConfigResult.DATA mVideoConfig;
    private static final String QCLOUD_APPID = "10074729";
    private VideoUploadTask mVideoUploadTask;
    private Subscription mSignSubscription;
    private Subscription mConfigSubscription;

    public static VideoManager getInstance() {
        return ourInstance;
    }

    private VideoManager() {
        mVideoConfig = new JournalVideoConfigResult.DATA();
        mVideoConfig.bucketName = BuildConfig.QCLOUD_BUCKET;
        mVideoConfig.journalDir = "journal";
        mVideoConfig.bitrate = 1024 * 1024;
        mVideoConfig.frameRate = 24;
        mVideoConfig.width = 640;
        mVideoConfig.height = 480;
        mVideoConfig.videoLength = 30;
    }

    public void startUpload(Context context, JournalItemVideo contentVideo, VideoUploadListener listener) {
        if (mUploadManager == null) {
            mUploadManager = new UploadManager(context, QCLOUD_APPID, Const.FileType.Video, "videPersistenceId");
        }
        if (mVideoUploadTask != null) {
            mVideoUploadTask.cancel();
        }

        //请求上传签名
        if (mSignSubscription != null) {
            mSignSubscription.unsubscribe();
        }

        listener.onStart();//开始上传
        mSignSubscription = RetrofitServiceFactory.getSpaService().getJournalVideoUploadSign(SharedPreferenceHelper.getUserToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<>(new Callback<BaseStringResult>() {
                    @Override
                    public void onResult(Throwable error, BaseStringResult result) {
                        if (error == null) {
                            uploadToQCloud(context, contentVideo, result.respData, listener);
                        } else {
                            listener.onFinished("获取签名失败：" + error.getLocalizedMessage());
                        }
                    }
                }));

    }

    private void uploadToQCloud(Context context, JournalItemVideo contentVideo, String sign, VideoUploadListener listener) {
        final String saveFilePath = File.separator + mVideoConfig.journalDir + File.separator + ClubData.getInstance().getClubInfo().clubId
                + "_" + MD5.getMD5(new File(contentVideo.getMicroVideo().getLocalUrl())) + ".mp4";
        VideoAttr videoAttr = new VideoAttr();
        mVideoUploadTask = new VideoUploadTask(
                mVideoConfig.bucketName,
                contentVideo.getMicroVideo().getLocalUrl(),
                saveFilePath,
                "",
                videoAttr,
                false,
                new IUploadTaskListener() {
                    @Override
                    public void onUploadSucceed(FileInfo fileInfo) {
                        MicroVideo microVideo = contentVideo.getMicroVideo();
                        microVideo.setResourcePath(saveFilePath);
                        microVideo.setAccessUrl(fileInfo.url);
                        notifyFinished(listener, null);

                        createVideoCover(context, contentVideo.getMicroVideo().getLocalUrl(), fileInfo.url);
                    }

                    @Override
                    public void onUploadFailed(int i, String s) {
                        notifyFinished(listener, i + "," + s);
                    }

                    @Override
                    public void onUploadProgress(long totalSize, long sendSize) {
                        notifyProgress(listener, (int) (sendSize * 100 / totalSize));
                    }

                    @Override
                    public void onUploadStateChange(ITask.TaskState taskState) {
                        if (taskState.equals(ITask.TaskState.CANCEL)) {
                            notifyCanceled(listener);
                        }
                    }
                }
        );
        mVideoUploadTask.setAuth(sign);
        mUploadManager.upload(mVideoUploadTask);
    }

    public void cancelUpload() {
        if (mVideoUploadTask != null) {
            mVideoUploadTask.cancel();
            mVideoUploadTask = null;
        }
        if (mSignSubscription != null) {
            mSignSubscription.unsubscribe();
            mSignSubscription = null;
        }
    }

    private void notifyStart(VideoUploadListener listener) {
        ThreadManager.postToUI(new Runnable() {
            @Override
            public void run() {
                listener.onStart();
            }
        });
    }

    private void notifyProgress(VideoUploadListener listener, final int progress) {
        ThreadManager.postToUI(new Runnable() {
            @Override
            public void run() {
                listener.onProgress(progress);
            }
        });
    }

    private void notifyFinished(VideoUploadListener listener, final String error) {
        ThreadManager.postToUI(new Runnable() {
            @Override
            public void run() {
                listener.onFinished(error);
            }
        });
    }

    private void notifyCanceled(VideoUploadListener listener) {
        ThreadManager.postToUI(new Runnable() {
            @Override
            public void run() {
                listener.onCanceled();
            }
        });
    }


    public interface VideoUploadListener {
        void onStart();

        void onProgress(int progress);

        void onCanceled();

        void onFinished(String error);
    }


    public void deleteVideoInServer(String resourcePath) {
        RetrofitServiceFactory
                .getSpaService()
                .deleteJournalVideo(SharedPreferenceHelper.getUserToken(), resourcePath)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }


    private String getVideoCoverName(String videoPath) {
        String coverName;
        int splitIndex = videoPath.lastIndexOf("/");
        if (splitIndex > 0) {
            coverName = videoPath.substring(splitIndex + 1, videoPath.length());
        } else {
            coverName = videoPath;
        }
        coverName = coverName.replace(".mp4", ".jpg");
        return coverName;
    }

    //创建视频封面
    private String createVideoCover(Context context, String localVideoPath, String coverPath) {
        //先获取一个封面
        Bitmap bitmap = getVideoCoverFromFile(context, localVideoPath);
        if (bitmap == null) {
            return null;
        }
        //将封面保存到本地
        String saveName = getVideoCoverName(coverPath);
        File dir = new File(context.getFilesDir().getPath() + File.separator + "video_cover");
        if (!dir.exists() && !dir.mkdir()) {
            Logger.e("could not create dir:" + dir.getAbsolutePath());
            return null;
        }
        File saveFile = new File(dir.getPath() + File.separator + saveName);
        if (saveFile.exists()) {
            Logger.i("the video cover file is already exists!");
            return saveFile.getAbsolutePath();
        }
        try {
            if (!saveFile.exists() && !saveFile.createNewFile()) {
                Logger.e("could not create file:" + saveFile);
                return null;
            }
        } catch (IOException e) {
            Logger.e("could not create file:" + saveFile);
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            Logger.i("save video cover success !!");
            return saveFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //从本地文件获取视频封面
    public Bitmap getVideoCoverFromFile(Context context, String videoPath) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, Uri.parse(videoPath));
            return retriever.getFrameAtTime();
        } catch (Exception e) {
            Logger.e("getVideoCoverFromFile failed:" + e.getLocalizedMessage());
        }
        return null;
    }

    //跟据视频名字获取封面,需要在非UI线程中调用
    public String getVideoCoverPath(Context context, String videoPath) {
        //首先看缓存中有没有封面
        String saveName = getVideoCoverName(videoPath);
        File file = new File(context.getFilesDir().getPath() + File.separator + "video_cover" + File.separator + saveName);
        if (file.exists()) {
            return file.getAbsolutePath();
        }

        if (videoPath.startsWith("http://")) {
            //如果缓存中没有，视频路径是http地址的话，下载过来解码
            String tempFilePath = context.getCacheDir().getPath()
                    + File.separator + System.currentTimeMillis() + ".mp4";
            File tempFile = new File(tempFilePath);
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                Logger.e("could not create file " + tempFile.getAbsolutePath());
                return null;
            }
            //下载数据到本地
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(videoPath);
                urlConnection = (HttpURLConnection) url.openConnection();
                FileOutputStream fos = new FileOutputStream(tempFile);
                InputStream is = urlConnection.getInputStream();
                byte[] buffer = new byte[10 * 1024];
                int readLen;
                int totalBytes = 0;
                while (totalBytes < 10 * 1024 * 1024 && (readLen = is.read(buffer)) > 0) {
                    totalBytes += readLen;
                    fos.write(buffer, 0, readLen);
                    //每过100K，开始尝试能否解码
                    if (totalBytes % (100 * 1024) == 0) {
                        fos.flush();
                        String path = createVideoCover(context, tempFilePath, videoPath);
                        if (!TextUtils.isEmpty(path)) {
                            return path;
                        }
                    }
                }
                return createVideoCover(context, tempFilePath, videoPath);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                tempFile.delete();
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
        return null;
    }

    //视频配置信息
    public void loadVideoConfig() {
        if (mConfigSubscription != null) {
            mConfigSubscription.unsubscribe();
        }
        mConfigSubscription = RetrofitServiceFactory.getSpaService().getJournalVideoConfig(SharedPreferenceHelper.getUserToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<>(new Callback<JournalVideoConfigResult>() {
                    @Override
                    public void onResult(Throwable error, JournalVideoConfigResult result) {
                        if (error == null && result.respData != null) {
                            mVideoConfig = result.respData;
                        } else {
                            Logger.e("getJournalVideoConfig failed: " + error.getLocalizedMessage());
                        }
                    }
                }));
    }

    public JournalVideoConfigResult.DATA getVideoConfig() {
        return mVideoConfig;
    }
}
