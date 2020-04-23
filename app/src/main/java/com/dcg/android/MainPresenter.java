package com.dcg.android;

import android.util.Log;
import com.dcg.network.ApiRetrofit;
import com.dcg.top.base.TopMvpPresenter;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.*;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ Time  :  2020-04-11
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description :
 */
public class MainPresenter extends TopMvpPresenter implements MainConstract.Perenter {

    protected Map<String, String> params;
    protected HttpApi httpServer;
    protected CompositeDisposable compositeDisposable;
    protected static final String BASE_URL = "http://36.110.117.58:9008";


    public MainPresenter() {
    }

    @Override
    public void subscribe() {
        super.subscribe();
        ApiRetrofit.getHttpInstance().init(mContext, BASE_URL);
        httpServer = ApiRetrofit.getHttpInstance().getApiService(HttpApi.class);
        params = new HashMap<>();
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void uploadPic(List<String> localPathList, int flag) {
        switch (flag) {
            case 1:
                retofit(localPathList);
                break;
            case 2:
                okhttp(localPathList);
                break;
            case 3:
                xutils(localPathList);
                break;
            default:
        }
    }

    private void okhttp(List<String> localPathList) {
        getView().showLoading();
        Disposable disposable = Observable.create((ObservableOnSubscribe<List<String>>) emitter -> {
            List<String> uploadSuccessList = new ArrayList<>();

            for (String path : localPathList) {
                if (!"frist_pic".equals(path)) {
                    File file = new File(path);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
                    //创建Request
                    final Request request = new Request.Builder().url(BASE_URL + "/api/uploadimg").post(requestFile).build();

                    OkHttpClient mOkHttpClient = new OkHttpClient();
                    final Call call = mOkHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
                    try {
                        Response response = call.execute();
                        if (response.isSuccessful()) {
                            String result = response.body().string();
                            Log.d("hoyouly", "okhttp : pic  " + result);
                            uploadSuccessList.add(result);
                        } else {
                            Log.e("hoyouly", "okhttp-post-err:" + response.code());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.d("hoyouly", " okhttp   execute error " + ex.getMessage());

                    }
                }
            }
            emitter.onNext(uploadSuccessList);
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(list -> {
                if (list.size() + 1 == localPathList.size()) {
                    getView().hideLoading();
                }
            }, throwable -> Log.d("hoyouly", "okhttp : error " + throwable.getMessage()));
        compositeDisposable.add(disposable);
    }

    private void xutils(List<String> localPathList) {
        getView().showLoading();
        Disposable disposable = Observable.create((ObservableOnSubscribe<List<String>>) emitter -> {
            List<String> uploadSuccessList = new ArrayList<>();

            for (String path : localPathList) {
                if (!"frist_pic".equals(path)) {
                    File file = new File(path);
                    RequestParams requestParams = new RequestParams(BASE_URL + "/api/uploadimg");
                    requestParams.addBodyParameter("body", file, "image/jpg");
                    String result = "";
                    try {
                        result = x.http().postSync(requestParams, String.class);
                        Log.d("hoyouly", " uploadPic:  " + result);
                        uploadSuccessList.add(result);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        Log.d("hoyouly", "postSync  " + throwable.getMessage());
                    }
                }
            }
            if (uploadSuccessList.size() + 1 == localPathList.size()) {
                //说明全部上传成功
                emitter.onNext(uploadSuccessList);
            } else {
                //说明有上传失败的，直接提示用户
                emitter.onError(new Exception());
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(paths -> {
                // 如果全部完成，调用成功接口
                getView().hideLoading();
                //图片上传成功，封装数据，
                StringBuffer sb = new StringBuffer();
                //图片信息(逗号间隔)
                if (paths.size() > 0) {
                    sb.append(paths.get(0));
                    for (int i = 1; i < paths.size(); i++) {
                        sb.append(",").append(paths.get(i));
                    }
                }
                Log.d("hoyouly", " 组合后： " + sb.toString());
            }, throwable -> {
                Log.d("hoyouly", "BoundDetailPresenter : " + throwable.getMessage());
                getView().hideLoading();
                getView().onError("上传失败，请重试  " + throwable.getMessage());
            });
        compositeDisposable.add(disposable);
    }

    private void retofit(List<String> uploadList) {
        getView().showLoading();
        List<String> uploadSuccessList = new ArrayList<>();
        Disposable disposable = Observable.fromIterable(uploadList)
            //过滤路径等于frist_pic 的
            .filter(s -> !"frist_pic".equals(s))
            .concatMap((Function<String, ObservableSource<String>>) path -> {
                File file = new File(path);
                RequestBody requestFile = RequestBody.create(null, file);
                MultipartBody.Part part = MultipartBody.Part.create(requestFile);
                return httpServer.updatePic(part)
                    .retry(2)// 失败重连
                    .subscribeOn(Schedulers.io());
            })
            .subscribe(s -> {
                uploadSuccessList.add(s);
                Log.d("hoyouly", "uploadPic " + s);
                if (uploadSuccessList.size() + 1 == uploadList.size()) {
                    getView().hideLoading();
                }

            }, throwable -> {
                Log.d("hoyouly", " retofit  onError:  " + throwable.getMessage());
                getView().hideLoading();
                getView().onError("上传失败，请重新再试");
            });
        compositeDisposable.add(disposable);
    }


    @Override
    public void unSubscribe() {
        super.unSubscribe();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    public Map<String, okhttp3.RequestBody> generateRequestBody(Map<String, String> requestDataMap) {
        Map<String, okhttp3.RequestBody> requestBodyMap = new HashMap<>();
        for (String key : requestDataMap.keySet()) {
            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(MediaType.parse("multipart/form-data"),
                requestDataMap.get(key) == null ? "" : requestDataMap.get(key));
            requestBodyMap.put(key, requestBody);
        }
        return requestBodyMap;
    }

    /**
     * 把一个文件转化为byte字节数组。
     *
     * @return
     */
    private byte[] fileConvertToByteArray(File file) {
        byte[] data = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            data = baos.toByteArray();
            fis.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}
