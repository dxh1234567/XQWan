package com.jj.xqwan.base.net.http

import com.jj.xqwan.base.ApiEmptyResponse
import com.jj.xqwan.base.ApiErrorResponse
import com.jj.xqwan.base.ApiResponse
import com.jj.xqwan.base.ApiSuccessResponse


import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.PageKeyedDataSource
import com.jj.base.common.ThreadManager
import com.jj.xqwan.entity.ItemInfo
import com.jj.xqwan.base.net.Resource
import java.util.concurrent.atomic.AtomicBoolean

abstract class CustomPageKeyedDataSource<ResultType, RequestType>(
    private val filterDuplicate: Boolean = false
) : PageKeyedDataSource<Int, ResultType>() {
    // keep a function reference for the retryRoomList event
    private var retry: (() -> Any)? = null

    val networkState = MediatorLiveData<Resource<*>>()
    val initialLoad = MediatorLiveData<Resource<*>>()

    var noMore: AtomicBoolean = AtomicBoolean(false)
    private val ids = HashSet<Int>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            ThreadManager.executeAsyncTask {
                it.invoke()
            }
        }
    }

    @WorkerThread
    private fun addId(list: List<RequestType>?) {
        if (filterDuplicate) {
            list?.forEach {
                ids.add(getKey(it))
            }
        }
    }

    @WorkerThread
    private fun filterData(data: List<RequestType>?) =
        if (!filterDuplicate)
            data
        else {
            data?.let {
                it.filter {
                    val id = getKey(it)
                    val res = !ids.contains(id)
                    if (res) {
                        ids.add(id)
                    }
                    res
                }
            } ?: data
        }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ResultType>) {
        // ignored, since we only ever append to our initial load
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ResultType>) {
        // set network value to loading.
//        LogUtil.i(TAG, "loadAfter")
        networkState.postValue(Resource.loading(null))
        val apiResponse = getAfterResponse(params)
        ThreadManager.postUI {
            networkState.addSource(apiResponse) { response ->
                //                LogUtil.i(TAG, "")
                networkState.removeSource(apiResponse)
                ThreadManager.postWorker {
                    when (response) {
                        is ApiSuccessResponse -> {
                            retry = null
                            networkState.postValue(Resource.success(null))
                            noMore.set(!response.data.hasNext)
                            callback.onResult(
                                processData(filterData(response.data.list)),
                                if (response.data.hasNext) response.data.pageNum + 1 else null
                            )
                        }
                        is ApiEmptyResponse -> {
                            retry = null
                            networkState.postValue(Resource.success(null))
                            callback.onResult(emptyList(), params.key)
                        }
                        is ApiErrorResponse -> {
                            retry = {
                                loadAfter(params, callback)
                            }
                            if (!response.data?.list.isNullOrEmpty()) {
                                callback.onResult(
                                    processData(filterData(response.data!!.list)),
                                    params.key
                                )
                            }
                            networkState.postValue(Resource.error(response.code, null))
                        }
                    }
                }
            }
        }
    }

    /**
     * 刷新或初次加载数据
     * @note ui必须调用networkState进行监听，否则无法发出网络请求
     * */
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, ResultType>
    ) {
//        LogUtil.i(TAG, "")
        val apiResponse = getInitailResponse(params)
        networkState.postValue(Resource.loading(null))
        initialLoad.postValue(Resource.loading(null))
        ThreadManager.postUI {
            networkState.addSource(apiResponse) { response ->
                networkState.removeSource(apiResponse)
                ThreadManager.postWorker {
                    ids.clear()
                    when (response) {
                        is ApiSuccessResponse -> {
                            retry = null
                            networkState.postValue(Resource.success(null))
                            addId(response.data.list)
                            noMore.set(!response.data.hasNext)
                            callback.onResult(
                                processData(response.data.list),
                                response.data.pageNum,
                                if (response.data.hasNext) response.data.pageNum + 1 else null
                            )
                            initialLoad.postValue(Resource.success(null))
                        }
                        is ApiEmptyResponse -> {
                            retry = null
                            networkState.postValue(Resource.success(null))
                            callback.onResult(emptyList(), null, null)
                            initialLoad.postValue(Resource.success(null))
                        }
                        is ApiErrorResponse -> {
                            retry = {
                                loadInitial(params, callback)
                            }
                            if (!response.data?.list.isNullOrEmpty()) {
                                addId(response.data!!.list)
                                callback.onResult(
                                    processData(response.data!!.list),
                                    null,
                                    null
                                )
                            }
                            networkState.postValue(Resource.error(response.code, null))
                            initialLoad.postValue(Resource.error(response.code, null))
                        }
                    }
                }
            }
        }
    }


    abstract fun getInitailResponse(
        params: LoadInitialParams<Int>
    ): LiveData<ApiResponse<PagedListResultInfo<List<RequestType>>>>

    abstract fun getAfterResponse(
        params: LoadParams<Int>
    ): LiveData<ApiResponse<PagedListResultInfo<List<RequestType>>>>

    @WorkerThread
    abstract fun processData(data: List<RequestType>?): List<ResultType>

    @WorkerThread
    open fun getKey(value: RequestType): Int {
        return when (value) {
            is ItemInfo -> value.id
            is Int -> value
            else -> throw UnsupportedOperationException("不支持RequestType:$value")
        }
    }

    companion object {
        private const val TAG = "CustomPageKeyedDataSource"
    }
}