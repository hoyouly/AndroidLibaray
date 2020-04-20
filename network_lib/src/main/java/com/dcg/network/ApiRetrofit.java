package com.dcg.network;


import android.content.Context;
import android.util.Log;
import com.dcg.network.http.adapter.TcpCallAdapterFactory;
import com.dcg.network.http.convert.RawConverterFactory;
import com.dcg.network.http.cookie.CookieManger;
import com.dcg.network.http.gson.DoubleDefault0Adapter;
import com.dcg.network.http.gson.IntegerDefault0Adapter;
import com.dcg.network.http.gson.LongDefault0Adapter;
import com.dcg.network.utils.UnicodeUtils;
import com.dcg.network.socket.OnConnectListener;
import com.dcg.network.socket.netty.NettyClient;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * File descripition:   创建Retrofit
 *
 */

public class ApiRetrofit {

    private static ApiRetrofit apiRetrofit;
    private Retrofit retrofit;
    private Gson gson;
    private static final int DEFAULT_TIMEOUT = 10;

    private static Map<Integer, ApiRetrofit> map = new HashMap();


    public static final int HTTP_TYPE = 1;
    public static final int TCP_TYPE = 2;
    private int type;


    /**
     * 初始化操作，这个主要是真的HTTP请求的，
     *
     * @param context
     * @param baseUrl
     */
    public void init(Context context, String baseUrl) {
        Log.d("hoyouly", "ApiRetrofit : " + "init() baseUrl = [" + baseUrl + "]");
        retrofit = null;
        creatRetrofit(context, type, baseUrl);
    }

    /**
     * 初始化操作，这个主要是针对TCP请求的
     *
     * @param context
     * @param host     IP地址
     * @param port     端口号
     * @param listener 连接状态监听
     */
    public void init(Context context, String host, int port, OnConnectListener listener) {
        Log.d("hoyouly", "ApiRetrofit : " + "init() called with: host = [" + host + "], port = [" + port + "]");
        retrofit = null;
        boolean connect = NettyClient.getInstance().connect(host, port);
        if (listener != null) {
            listener.connectState(connect);
        }
        creatRetrofit(context, type, "http://" + host + ":" + port);

//        Observable
//            .create((ObservableOnSubscribe<Boolean>) emitter -> {
//                emitter.onNext(NettyClient.getInstance().connect(host, port));
//            })//子线程连接socket
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(result -> {
//                if (listener != null) {
//                    listener.connectState(result);
//                }
//                creatRetrofit(context, type, "http://" + host + ":" + port);
//            });
    }

    private ApiRetrofit(int type) {
        this.type = type;
    }

    private void creatRetrofit(Context context, int type, String baseUrl) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder
            .cookieJar(new CookieManger(context.getApplicationContext()))
//            .addInterceptor(new HeadUrlInterceptor())
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true);//错误重联

        if (type == TCP_TYPE) {
            httpClientBuilder.addInterceptor(new SocketInterceptor());
        } else {
            httpClientBuilder.addInterceptor(createHttpLoggingInterceptor());
        }
        retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(RawConverterFactory.create(buildGson()))
            .addConverterFactory(GsonConverterFactory.create(buildGson()))
            //支持RxJava2
            .addCallAdapterFactory(TcpCallAdapterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClientBuilder.build())
            .build();
    }

    /**
     * 增加后台返回""和"null"的处理 1.int=>0 2.double=>0.00 3.long=>0L
     *
     * @return
     */
    private Gson buildGson() {
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

    public static ApiRetrofit getHttpInstance() {
        return getInstance(HTTP_TYPE);
    }

    public static ApiRetrofit getTcpInstance() {
        return getInstance(TCP_TYPE);
    }

    public static ApiRetrofit getInstance(int type) {
        apiRetrofit = map.get(type);
        if (apiRetrofit == null) {
            synchronized (ApiRetrofit.class) {
                if (apiRetrofit == null) {
                    apiRetrofit = new ApiRetrofit(type);
                    map.put(type, apiRetrofit);
                }
            }
        }
        return apiRetrofit;
    }


    public <T> T getApiService(final Class<T> service) {
        if (retrofit == null) {
            throw new NullPointerException("请先进行初始化操作！");
        }
        return retrofit.create(service);
    }


    private HttpLoggingInterceptor createHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor((String message) -> {
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
