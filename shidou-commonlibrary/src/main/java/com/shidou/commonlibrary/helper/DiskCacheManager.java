package com.shidou.commonlibrary.helper;

import com.shidou.commonlibrary.util.DiskLruCache;
import com.shidou.commonlibrary.util.MD5Utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by heyangya on 16-5-9.
 */
public class DiskCacheManager {
    private static DiskLruCache mDiskLruCache;
    private static DiskCacheManager instance = new DiskCacheManager();
    private static final String KEY_DEFAULT = "key_default";

    public static void init(File diskCacheDirectory, int maxSize) throws IOException {
        if (!diskCacheDirectory.exists() && !diskCacheDirectory.mkdirs()) {
            XLogger.e("create cache file failed:"+diskCacheDirectory);
            return;
        }
        mDiskLruCache = DiskLruCache.open(diskCacheDirectory, 1, 1, maxSize);
    }

    private DiskCacheManager() {

    }

    public static DiskCacheManager getInstance() {
        return instance;
    }

    private String getRealKey(String key) {
        return MD5Utils.MD5(key);
    }

    private void put(String key, String value) {
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(getRealKey(key));
            OutputStream outputStream = editor.newOutputStream(0);
            outputStream.write(value.getBytes());
            editor.commit();
            mDiskLruCache.flush();
        } catch (IOException e) {
            XLogger.e("DiskCacheManager.put [" + key + "] error:" + e.getMessage());
        }
    }

    private String get(String key) {
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(getRealKey(key));
            if (snapshot != null) {
                return snapshot.getString(0);
            }
        } catch (IOException e) {
            XLogger.e("DiskCacheManager.get [" + key + "] error:" + e.getMessage());
        }
        return null;
    }

    public synchronized String getCache(String key) {
        return get(KEY_DEFAULT + key);
    }

    public synchronized void putCache(String key, String value) {
        put(KEY_DEFAULT + key, value);
    }
}

