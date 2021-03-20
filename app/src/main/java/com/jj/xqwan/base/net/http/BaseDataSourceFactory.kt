package com.jj.xqwan.base.net.http

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.jj.xqwan.base.ApiResponse


/**
 *  Created By duXiaHui
 *  on 2021/1/30
 */

abstract class BaseDataSourceFactory<T> : DataSource.Factory<Int, T>() {
    val sourceLiveData = MutableLiveData<CustomPageKeyedDataSource<T, T>>()

    override fun create(): DataSource<Int, T> {
        val source = object : CustomPageKeyedDataSource<T, T>() {
            override fun getInitailResponse(params: LoadInitialParams<Int>) =
                this@BaseDataSourceFactory.getListHttp(
                    1,
                    params.requestedLoadSize
                )

            override fun getAfterResponse(params: LoadParams<Int>) =
                this@BaseDataSourceFactory.getListHttp(
                    params.key,
                    params.requestedLoadSize
                )

            override fun processData(data: List<T>?) = this@BaseDataSourceFactory.processData(data)
        }
        sourceLiveData.postValue(source)
        return source
    }

    open fun processData(data: List<T>?) = data ?: emptyList()

    abstract fun getListHttp(pageNum: Int, pageSize: Int):
            LiveData<ApiResponse<PagedListResultInfo<List<T>>>>
}