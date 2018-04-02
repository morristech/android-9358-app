package com.xmd.cashier.dal.net;

import com.xmd.m.network.BaseBean;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by zr on 17-12-27.
 */

public interface UploadService {
    @Multipart
    @POST(RequestConstant.URL_APP_UPLOAD_LOG)
    Call<BaseBean> uploadLog(@Part MultipartBody.Part file);
}
