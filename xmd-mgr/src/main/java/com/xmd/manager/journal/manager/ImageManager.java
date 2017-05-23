package com.xmd.manager.journal.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.common.Logger;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.model.AlbumPhoto;
import com.xmd.manager.journal.net.NetworkSubscriber;
import com.xmd.manager.service.RetrofitServiceFactory;
import com.xmd.manager.service.response.BaseListResult;
import com.xmd.manager.service.response.JournalPhotoUploadResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by heyangya on 16-11-11.
 */

public class ImageManager {
    private static ImageManager instance = new ImageManager();
    private final Object mUploadTasksLocker = new Object();
    private static final int MAX_IMAGE_WIDTH = 1120;
    private static final int MAX_IMAGE_HEIGHT = 1448;
    private static ExecutorService mExecutorService;
    private static boolean mIsInit;

    private ImageManager() {

    }

    public static void init(Context context) {
        if (mIsInit) {
            return;
        }
        mIsInit = true;
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        Logger.i("available memory:" + memoryInfo.availMem / 1024 / 1024 + "MB");
        memoryInfo.availMem /= 3; //最多使用的内存
        int threadCount = (int) (memoryInfo.availMem / (MAX_IMAGE_HEIGHT * MAX_IMAGE_WIDTH * 4 * 2));
        if (threadCount > Runtime.getRuntime().availableProcessors()) {
            threadCount = Runtime.getRuntime().availableProcessors();
        }
        if (threadCount <= 0) {
            threadCount = 1;
        }
        if (threadCount > 3) {
            threadCount = 3; //最大并发数
        }
        Logger.i("thread pool size:" + threadCount);
        mExecutorService = new ThreadPoolExecutor(threadCount, threadCount,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        ((ThreadPoolExecutor) mExecutorService).allowCoreThreadTimeOut(true);
    }

    public static ImageManager getInstance() {
        return instance;
    }

    public void doUpload(final List<UploadTask> uploadTasks) {
        Iterator<UploadTask> iterator = uploadTasks.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            final int findex = index++;
            UploadTask task = iterator.next();
            task.listener.onWait();//通知进入等待队列
            task.setFuture(mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    //通知开始事件
                    UploadTask uploadTask = uploadTasks.get(findex);
                    notifyStart(uploadTask);
                    //重新编码
                    String content;
                    try {
                        content = encodeFileToBase64(uploadTask.data.getLocalPath());
                        Logger.i("content size:" + content.length() * 2);
                    } catch (Exception e) {
                        notifyFinished(uploadTask, e.getLocalizedMessage());
                        Logger.e("encode bitmap", e);
                        return;
                    }
                    //上传图片
                    RetrofitServiceFactory.getSpaService().uploadPhoto(SharedPreferenceHelper.getUserToken(), content)
                            .subscribe(new NetworkSubscriber<>(new Callback<JournalPhotoUploadResult>() {
                                @Override
                                public void onResult(Throwable error, JournalPhotoUploadResult result) {
                                    if (error == null) {
                                        uploadTask.data.setImageId(result.respData.imageId);
                                        uploadTask.data.setNeedUpload(false);
                                        notifyFinished(uploadTask, null);
                                    } else {
                                        if (error instanceof InterruptedException ||
                                                error.getLocalizedMessage() != null && error.getLocalizedMessage().contains("thread interrupt")) {
                                            notifyCanceled(uploadTask);
                                        } else {
                                            notifyFinished(uploadTask, error.getLocalizedMessage());
                                        }
                                    }
                                }
                            }));
                }
            }));
        }
    }

    public void cancelUpload(List<UploadTask> uploadTasks) {
        synchronized (mUploadTasksLocker) {
            for (int i = 0; i < uploadTasks.size(); i++) {
                uploadTasks.get(i).cancel();
            }
            uploadTasks.clear();
        }
    }

    public static class UploadTask {
        public Future future;
        public AlbumPhoto data;
        public ImageUploadListener listener;
        public Call mNetworkCall;

        public UploadTask(AlbumPhoto data, ImageUploadListener listener) {
            this.data = data;
            this.listener = listener;

        }

        public void setFuture(Future future) {
            this.future = future;
        }

        public void setNetworkCall(Call call) {
            mNetworkCall = call;
        }

        public void cancel() {
            if (mNetworkCall != null) {
                mNetworkCall.cancel();
            }
            future.cancel(true);
            listener.onCanceled();
        }
    }

    private void notifyStart(UploadTask uploadTask) {
        ThreadManager.postToUI(new Runnable() {
            @Override
            public void run() {
                uploadTask.listener.onStart();
            }
        });
    }

    private void notifyProgress(UploadTask uploadTask, final int progress) {
        ThreadManager.postToUI(new Runnable() {
            @Override
            public void run() {
                uploadTask.listener.onProgress(progress);
            }
        });
    }

    private void notifyFinished(UploadTask uploadTask, final String error) {
        ThreadManager.postToUI(new Runnable() {
            @Override
            public void run() {
                uploadTask.listener.onFinished(error);
            }
        });
    }

    private void notifyCanceled(UploadTask uploadTask) {
        ThreadManager.postToUI(new Runnable() {
            @Override
            public void run() {
                uploadTask.listener.onCanceled();
            }
        });
    }

    public interface ImageUploadListener {
        void onWait();

        void onStart();

        void onProgress(int progress);

        void onFinished(String error);

        void onCanceled();
    }


    private String encodeFileToBase64(String path) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = 1;
        while (options.outWidth / options.inSampleSize > MAX_IMAGE_WIDTH || options.outHeight / options.inSampleSize > MAX_IMAGE_HEIGHT) {
            options.inSampleSize <<= 1;
        }
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        Logger.i("image size:" + bitmap.getWidth() + "x" + bitmap.getHeight());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bitmap.recycle();
        String imgFile = "data:image/jpg;base64," + Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
        Runtime.getRuntime().gc(); //强制释放内存
        return imgFile;
    }

    public void deleteImageInServer(String imgId) {
        RetrofitServiceFactory
                .getSpaService()
                .deleteJournalPhoto(SharedPreferenceHelper.getUserToken(), imgId)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Subscription convertImageIdsToUrls(List<AlbumPhoto> photos, Callback<Void> callback) {
        if (photos == null || photos.size() == 0) {
            callback.onResult(null, null);
            return null;
        }
        StringBuilder idsBuilder = new StringBuilder();
        for (int i = 0; i < photos.size(); i++) {
            idsBuilder.append(photos.get(i).getImageId()).append(",");
        }
        if (idsBuilder.length() > 0) {
            idsBuilder.setLength(idsBuilder.length() - 1);
        }
        return RetrofitServiceFactory.getSpaService().getUrlByPhotoId(SharedPreferenceHelper.getUserToken(), idsBuilder.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<>(new Callback<BaseListResult<String>>() {
                    @Override
                    public void onResult(Throwable error, BaseListResult<String> result) {
                        if (error == null) {
                            int i = 0;
                            for (String url : result.respData) {
                                photos.get(i).setRemoteUrl(url);
                                i++;
                            }
                        }
                        callback.onResult(error, null);
                    }
                }));
    }
}
