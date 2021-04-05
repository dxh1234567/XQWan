package com.jj.xqwan.base.net.http.adapter

import android.util.Log
import androidx.lifecycle.LiveData
import com.jj.xqwan.base.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean


/**
 *  Created By duXiaHui
 *  on 2021/1/31
 */

private const val TAG = "LiveDataCallAdapter"

class LiveDataCallAdapter<R>(private val responseType: Type) :
    CallAdapter<R, LiveData<ApiResponse<R>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): LiveData<ApiResponse<R>> {


        call.enqueue(object :Callback<R>{
            override fun onFailure(call: Call<R>, t: Throwable) {

            }

            override fun onResponse(call: Call<R>, response: Response<R>) {
                val body = response.body()
                Log.e("77777",body.toString())
            }

        })
        return object : LiveData<ApiResponse<R>>() {
            private var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {

//                    call.enqueue(object : Callback<R> {
//                        override fun onResponse(call: Call<R>, response: Response<R>) {
//                            Log.e("success",response.body().toString())
//                            postValue(ApiResponse.create(response))
//                        }
//
//                        override fun onFailure(call: Call<R>, throwable: Throwable) {
//                            LogUtil.e(TAG, "throwable = ${throwable.message}")
//                            postValue(ApiResponse.create(throwable))
//                        }
//                    })
                }
            }
        }
    }
}
