package com.shidou.commonlibrary.helper;

import com.shidou.commonlibrary.util.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * Created by heyangya on 16-5-9.
 */
public class DiskCacheManager {
    private static DiskLruCache mDiskLruCache;
    private static DiskCacheManager instance = new DiskCacheManager();
    private static final String KEY_DEFAULT = "key_default";
    private static boolean init;

    public static void init(File diskCacheDirectory, int maxSize) throws IOException {
        if (init) {
            return;
        }
        if (!diskCacheDirectory.exists() && !diskCacheDirectory.mkdirs()) {
            XLogger.e("create cache file failed:" + diskCacheDirectory);
            return;
        }
        mDiskLruCache = DiskLruCache.open(diskCacheDirectory, 1, 1, maxSize);
        init = true;
    }

    public static boolean isInit() {
        return init;
    }

    private DiskCacheManager() {

    }

    public static DiskCacheManager getInstance() {
        return instance;
    }

    //缓存对像
    public void put(String key, Object value) {
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            ObjectOutputStream outputStream = new ObjectOutputStream(editor.newOutputStream(0));
            outputStream.writeObject(value);
            editor.commit();
            mDiskLruCache.flush();
        } catch (IOException e) {
            XLogger.e("DiskCacheManager.put [" + key + "] error:" + e.getMessage());
        }
    }

    public Object get(String key) {
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if (snapshot != null) {
                ObjectInputStream objectInputStream = new ObjectInputStream(snapshot.getInputStream(0));
                return objectInputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            XLogger.e("DiskCacheManager.get [" + key + "] error:" + e.getMessage());
        }
        return null;
    }
}

