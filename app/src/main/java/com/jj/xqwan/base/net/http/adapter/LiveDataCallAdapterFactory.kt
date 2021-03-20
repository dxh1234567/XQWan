package com.jj.xqwan.base.net.http.adapter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jj.xqwan.base.net.app.User
import org.json.JSONObject
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 *  Created By duXiaHui
 *  on 2021/1/31
 */

class LiveDataCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (CallAdapter.Factory.getRawType(returnType) != LiveData::class.java) {
            return null
        }
        val observableType = CallAdapter.Factory.getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = CallAdapter.Factory.getRawType(observableType)
        if (rawObservableType != User::class.java) {
            throw IllegalArgumentException("type must be a resource")
        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }
        Log.e("dddd",observableType.toString())
        Log.e("dddd",rawObservableType.toString())
        val callbackExecutor = retrofit.callbackExecutor()

        val bodyType = CallAdapter.Factory.getParameterUpperBound(0, observableType)
        return object :CallAdapter<JSONObject, LiveData<*>>{


            override fun responseType(): Type  = bodyType
            override fun adapt(call: Call<JSONObject>): LiveData<*> {
                call.enqueue(object :Callback<JSONObject>{
                    override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                    }

                    override fun onResponse(
                        call: Call<JSONObject>,
                        response: Response<JSONObject>
                    ) {

                        Log.e("-----",response.body().toString())
                    }
                })

                return MutableLiveData<User<*>>()
            }
        }
//        return LiveDataCallAdapter<Any>(bodyType)
    }
}
