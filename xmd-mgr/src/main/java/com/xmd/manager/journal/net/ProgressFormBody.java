package com.xmd.manager.journal.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;

/**
 * Created by heyangya on 16-11-19.
 */

public class ProgressFormBody extends RequestBody {
    private static final MediaType CONTENT_TYPE =
            MediaType.parse("application/x-www-form-urlencoded");

    private final List<String> encodedNames;
    private final List<String> encodedValues;

    private long mContentLength;
    private static final int WRITE_STEP = 10 * 1024;
    private IProgressListener mProgressListener;

    private ProgressFormBody(List<String> encodedNames, List<String> encodedValues, IProgressListener listener) {
        this.encodedNames = Util.immutableList(encodedNames);
        this.encodedValues = Util.immutableList(encodedValues);
        mProgressListener = listener;
    }

    /**
     * The number of key-value pairs in this form-encoded body.
     */
    public int size() {
        return encodedNames.size();
    }

    public String encodedName(int index) {
        return encodedNames.get(index);
    }

    public String name(int index) {
        try {
            return URLDecoder.decode(encodedName(index), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedName(index);
    }

    public String encodedValue(int index) {
        return encodedValues.get(index);
    }

    public String value(int index) {
        try {
            return URLDecoder.decode(encodedValue(index), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedValue(index);
    }

    @Override
    public MediaType contentType() {
        return CONTENT_TYPE;
    }

    @Override
    public long contentLength() {
        if (mContentLength == 0) {
            mContentLength = writeOrCountBytes(null, true);
        }
        return mContentLength;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        writeOrCountBytes(sink, false);
    }

    /**
     * Either writes this request to {@code sink} or measures its content length. We have one method
     * do double-duty to make sure the counting and content are consistent, particularly when it comes
     * to awkward operations like measuring the encoded length of header strings, or the
     * length-in-digits of an encoded integer.
     */
    private long writeOrCountBytes(BufferedSink sink, boolean countBytes) {
        long byteCount = 0L;

        Buffer buffer;
        if (countBytes) {
            buffer = new Buffer();
        } else {
            buffer = sink.buffer();
        }

        long doSize = 0;
        for (int i = 0, size = encodedNames.size(); i < size; i++) {
            if (i > 0) {
                buffer.writeByte('&');
            }
            buffer.writeUtf8(encodedNames.get(i));
            buffer.writeByte('=');
            String content = encodedValues.get(i);

            //分段传输
            int position = 0;
            int writeSize;
            int leftSize = content.length();
            do {
                writeSize = leftSize > WRITE_STEP ? WRITE_STEP : leftSize;
                buffer.writeUtf8(content, position, position + writeSize);
                position += writeSize;
                leftSize -= writeSize;
                if (!countBytes && mProgressListener != null) {
                    mProgressListener.onProgress(doSize, mContentLength, (int) (doSize * 100 / mContentLength));
                }
                doSize += buffer.size();
            } while (leftSize > 0);
        }

        if (countBytes) {
            byteCount = buffer.size();
            buffer.clear();
        }

        return byteCount;
    }

    public static final class Builder {
        private final List<String> names = new ArrayList<>();
        private final List<String> values = new ArrayList<>();
        private IProgressListener mListener;

        public ProgressFormBody.Builder add(String name, String value) {
            try {
                names.add(URLEncoder.encode(name, "UTF-8"));
                values.add(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return this;
        }

        public ProgressFormBody.Builder addEncoded(String name, String value) {
            try {
                names.add(URLEncoder.encode(name, "UTF-8"));
                values.add(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return this;
        }

        public ProgressFormBody.Builder listener(IProgressListener listener) {
            mListener = listener;
            return this;
        }

        public ProgressFormBody build() {
            return new ProgressFormBody(names, values, mListener);
        }
    }
}
