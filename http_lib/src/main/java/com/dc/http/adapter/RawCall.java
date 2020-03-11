package com.dc.http.adapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

/**
 * @ Time  :  2020-03-07
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description :
 */
public class RawCall<R> {

    public final Call<R> call;

    public RawCall(Call<R> call) {
        this.call = call;
    }

    /**
     * 异步请求数据
     *
     * @param callback
     */
    public void enqueue(Callback<R> callback) {
        call.enqueue(callback);
    }

    /**
     * 同步请求数据
     *
     * @return
     * @throws IOException
     */
    public Response<R> execute() throws IOException {
        return call.execute();
    }

}