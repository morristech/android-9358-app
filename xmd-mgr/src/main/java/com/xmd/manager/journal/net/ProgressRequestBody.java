package com.xmd.manager.journal.net;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by heyangya on 16-11-19.
 */

public class ProgressRequestBody extends RequestBody {
    private final RequestBody mRequestBody;
    private final IProgressListener mListener;
    private long mContentLength;
    private BufferedSink mBufferedSink;

    public ProgressRequestBody(RequestBody requestBody, IProgressListener listener) {
        mRequestBody = requestBody;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        if (mContentLength == 0) {
            mContentLength = mRequestBody.contentLength();
        }
        return mContentLength;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (mBufferedSink == null) {
            mBufferedSink = Okio.buffer(sink(sink));
        }
        mRequestBody.writeTo(mBufferedSink);
        mBufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long totalBytesWrite = 0;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                totalBytesWrite += byteCount;
                mListener.onProgress(totalBytesWrite, contentLength(), (int) (totalBytesWrite * 100 / contentLength()));
            }
        };
    }
}
