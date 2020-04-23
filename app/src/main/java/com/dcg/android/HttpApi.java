package com.dcg.android;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import java.io.File;

/**
 * @ Time  :  2020-04-11
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description :
 */
public interface HttpApi {

    @Multipart
    @POST("/api/uploadimg")
    Observable<String> updatePic(@Part MultipartBody.Part imgs);

//    @POST("api/uploadimg")
//    Observable<String> updatePic(@Body RequestBody imgs);
}
