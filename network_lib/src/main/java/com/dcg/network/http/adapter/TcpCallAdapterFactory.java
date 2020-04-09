package com.dcg.network.http.adapter;

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
public class TcpCallAdapterFactory extends CallAdapter.Factory {

    public static TcpCallAdapterFactory create() {
        return new TcpCallAdapterFactory();
    }

    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        //获取原始类型
        Class<?> rawType = getRawType(returnType);
        //返回值必须是 TcpCall 并且带有泛型（参数类型），根据 APIService接口中的方法返回值，确定returnType
        //如 TcpCall<String> getCategories(); 那确定 returnType 就是 TcpCall<String>
        if (rawType == TcpCall.class && returnType instanceof ParameterizedType) {
            Type callReturnType = getParameterUpperBound(0, (ParameterizedType) returnType);
            return new TcpCallAdapter(callReturnType);
        }
        return null;
    }

}