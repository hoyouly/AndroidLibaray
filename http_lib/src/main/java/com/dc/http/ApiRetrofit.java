package com.dc.http;


import android.content.Context;
import android.util.Log;
import com.dc.http.adapter.RawCallAdapterFactory;
import com.dc.http.convert.RawConverterFactory;
import com.dc.http.cookie.CookieManger;
import com.dc.http.gson.DoubleDefault0Adapter;
import com.dc.http.gson.IntegerDefault0Adapter;
import com.dc.http.gson.LongDefault0Adapter;
import com.dc.http.utils.UnicodeUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * File descripition:   创建Retrofit
 *
 * @author lp
 * @date 2018/6/19
 */

public class ApiRetrofit {

    private static ApiRetrofit apiRetrofit;
    private Retrofit retrofit;
    private static Gson gson;
    private static final int DEFAULT_TIMEOUT = 10;

    private static String sBaseUrl;
    private static Context sContext;


    public static void init(Context context, String baseUrl) {
        sBaseUrl = baseUrl;
        sContext = context;
        apiRetrofit = null;
    }


    private ApiRetrofit() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder
            .cookieJar(new CookieManger(sContext))
            .addInterceptor(createHttpLoggingInterceptor())
            .addInterceptor(new HeadUrlInterceptor())
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true);//错误重联

        retrofit = new Retrofit.Builder()
            .baseUrl(sBaseUrl)
            .addConverterFactory(RawConverterFactory.create(buildGson()))
            .addConverterFactory(GsonConverterFactory.create(buildGson()))
            //支持RxJava2
            .addCallAdapterFactory(RawCallAdapterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClientBuilder.build())
            .build();


    }

    /**
     * 增加后台返回""和"null"的处理 1.int=>0 2.double=>0.00 3.long=>0L
     *
     * @return
     */
    public static Gson buildGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                .registerTypeAdapter(Integer.class, new IntegerDefault0Adapter())
                .registerTypeAdapter(int.class, new IntegerDefault0Adapter())
                .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(Long.class, new LongDefault0Adapter())
                .registerTypeAdapter(long.class, new LongDefault0Adapter())
                .create();
        }
        return gson;
    }

    public static ApiRetrofit getInstance() {
        if (apiRetrofit == null) {
            synchronized (ApiRetrofit.class) {
                if (apiRetrofit == null) {
                    apiRetrofit = new ApiRetrofit();
                }
            }
        }
        return apiRetrofit;
    }

    public <T> T getApiService(final Class<T> service) {
        return retrofit.create(service);
    }


    private static HttpLoggingInterceptor createHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            //打印retrofit日志
            try {
                Log.d("hoyouly", UnicodeUtils.convert(message));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    /**
     * 添加  请求头
     */
    public class HeadUrlInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                .newBuilder()
//                    .addHeader("Vary", "Accept-Encoding")
//                    .addHeader("Server", "Apache")
//                    .addHeader("Pragma", "no-cache")
//                    .addHeader("Cookie", "add cookies here")
//                    .addHeader("_identity",  cookie_value)
                .build();
            return chain.proceed(request);
        }
    }
}
