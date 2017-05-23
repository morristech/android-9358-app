package com.xmd.manager.journal.net;

/**
 * Created by heyangya on 16-11-19.
 */

public interface IProgressListener {
    void onProgress(long doSize, long totalSize, int percent);
}
