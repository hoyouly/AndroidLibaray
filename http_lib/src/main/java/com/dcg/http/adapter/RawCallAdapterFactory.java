package com.dcg.http.adapter;

import io.reactivex.annotations.Nullable;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @ Time  :  2020-03-07
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description :
 */
public class RawCallAdapterFactory extends CallAdapter.Factory {

    public static RawCallAdapterFactory create() {
        return new RawCallAdapterFactory();
    }

    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        //获取原始类型
        Class<?> rawType = getRawType(returnType);
        //返回值必须是RawCall并且带有泛型（参数类型），根据APIService接口中的方法返回值，确定returnType
        //如 RawCall<String> getCategories();，那确定returnType就是CustomCall<String>
        if (rawType == RawCall.class && returnType instanceof ParameterizedType) {
            Type callReturnType = getParameterUpperBound(0, (ParameterizedType) returnType);
            return new RawCallAdapter(callReturnType);
        }
        return null;
    }

}