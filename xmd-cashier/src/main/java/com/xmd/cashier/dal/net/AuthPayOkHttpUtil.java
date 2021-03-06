package com.xmd.cashier.dal.net;

import android.text.TextUtils;

import com.shidou.commonlibrary.helper.XLogger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by zr on 18-3-28.
 * 用来处理主扫支付的接口
 */

public class AuthPayOkHttpUtil {
    private static final String TAG = "CustomOkHttpUtil";
    private static long mConnectTimeout;
    private static long mReadTimeout;
    private static long mWriteTimeout;
    private static String mCacheDirectory;
    private static long mCacheSize;

    private static AuthPayOkHttpUtil mInstance;
    private OkHttpClient.Builder mBuilder;
    private OkHttpClient mClient;
    private Map<String, String> mCommonHeader = new HashMap<>();
    private boolean mLog;
    private RequestPreprocess mRequestPreprocess;

    public static void init(String cacheDirectory, long cacheSize,
                            long connectTimeout, long readTimeout, long writeTimeout) {
        mCacheSize = cacheSize;
        mCacheDirectory = cacheDirectory;
        mConnectTimeout = connectTimeout;
        mReadTimeout = readTimeout;
        mWriteTimeout = writeTimeout;
        mInstance = new AuthPayOkHttpUtil();
    }

    public static AuthPayOkHttpUtil getInstance() {
        return mInstance;
    }

    //是否打开日志
    public void setLog(boolean log) {
        mLog = log;
    }

    //设置全局头部
    public void setCommonHeader(String key, String value) {
        if (value != null) {
            mCommonHeader.put(key, value);
        } else {
            removeCommonHeader(key);
        }
    }

    public void removeCommonHeader(String key) {
        mCommonHeader.remove(key);
    }

    //返回全局client
    public OkHttpClient getClient() {
        return mClient;
    }

    private AuthPayOkHttpUtil() {
        mBuilder = new OkHttpClient.Builder();
        if (mCacheDirectory != null && mCacheSize > 0) {
            mBuilder.cache(new Cache(new File(mCacheDirectory), mCacheSize));
        }
        mBuilder.connectTimeout(mConnectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(mReadTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(mWriteTimeout, TimeUnit.MILLISECONDS);
        mBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (mCommonHeader.size() > 0) {
                    //在所有请求中加入通用头部
                    Request.Builder builder = request.newBuilder();
                    for (String header : mCommonHeader.keySet()) {
                        builder.header(header, mCommonHeader.get(header));
                    }
                    request = builder.build();
                }

                if (mRequestPreprocess != null) {
                    request = mRequestPreprocess.preProcess(request);
                }

                if (mLog) {
                    XLogger.i(TAG, "---> " + requestToString(request));
                }

                long startNs = System.nanoTime();
                Response response = chain.proceed(request);
                long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

                if (mLog) {
                    XLogger.i(TAG, "<---"
                            + " (" + tookMs + "ms) " +
                            "[" + (response.networkResponse() == null ? "cache" : "network") + "]"
                            + response.request().method() + "-RESPONSE:" + response.toString()
                            + "\n" + responseToString(response));
                }

                return response;
            }
        });

        mBuilder.cookieJar(new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies == null ? new ArrayList<Cookie>() : cookies;
            }
        });

        mClient = mBuilder.build();
    }

    private String requestToString(Request request) {
        String result = request.method() + ":" + request.url().toString();
        if (request.method().equals("GET") && !result.contains("token") && !TextUtils.isEmpty(mCommonHeader.get("token"))) {
            result += (request.url().toString().contains("?") ? "&" : "?") + "token=" + mCommonHeader.get("token");
        }
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            if (requestBody instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                // result += " -d '";
                if (!result.contains("token") && !TextUtils.isEmpty(mCommonHeader.get("token"))) {
                    result += "?token=" + mCommonHeader.get("token") + "&";
                }
                for (int i = 0; i < body.size(); i++) {
                    if (result.contains("token") && body.name(i).equals("token")) {
                        continue;
                    }
                    result += body.name(i) + "=" + body.value(i) + "&";
                }
                //    result = result.substring(0, result.length() - 1) + "'";
                result = result.substring(0, result.length() - 1);
            } else {
                result += "----un form data!";
            }
        }
        return result;
    }

    private String responseToString(Response response) {
        try {
            ResponseBody responseBody = response.body();
            long contentLength = responseBody.contentLength();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(Charset.forName("UTF-8"));
            }

            if (contentLength != 0) {
                return buffer.clone().readString(charset);
            } else {
                return "response Empty";
            }
        } catch (Exception e) {
            return "response IOException";
        }
    }

    public void setRequestPreprocess(RequestPreprocess requestPreprocess) {
        mRequestPreprocess = requestPreprocess;
    }

    public interface RequestPreprocess {
        Request preProcess(Request request);
    }
}
