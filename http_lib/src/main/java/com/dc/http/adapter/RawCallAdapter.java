package com.dc.http.adapter;

import com.dc.http.R;
import retrofit2.Call;
import retrofit2.CallAdapter;

import java.lang.reflect.Type;

/**
 * @ Time  :  2020-03-07
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description :
 */
public class RawCallAdapter implements CallAdapter<R, RawCall<R>> {

    private final Type responseType;

    public RawCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    /**
     * 真正数据的类型，如Call<T> 中的 T，这个T会作为  Converter.Factory.responseConverter的第一个参数
     *
     * @return
     */
    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public RawCall<R> adapt(Call<R> call) {
        return new RawCall<>(call);
    }


}