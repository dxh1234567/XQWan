package com.jj.xqwan.base.net.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.paging.Config
import androidx.paging.toLiveData
import com.jj.xqwan.base.net.ApiServiceFactory
import com.jj.xqwan.base.net.PageListing
import com.jj.xqwan.base.net.http.BaseDataSourceFactory
import com.jj.xqwan.entity.HomeItemInfo


/**
 *  Created By duXiaHui
 *  on 2021/1/30
 */
object DataRepository{
     val apiService = ApiServiceFactory.create(
        ApiService::class.java
    )


    fun getHomeDataList(labelId : Long,pageSize :Int = 10): LiveData<PageListing<HomeItemInfo>> {
        val sourceFactory = object : BaseDataSourceFactory<HomeItemInfo>() {
            override fun getListHttp(
                pageNum: Int,
                pageSize: Int
            ) = apiService.getHomeLabelGoodsDataList(pageNum,pageSize,labelId)
        }


        val livePagedList = sourceFactory.toLiveData(
            // we use Config Kotlin ext. function here, could also use PagedList.Config.Builder
            config = Config(
                pageSize = pageSize,
                enablePlaceholders = false,
                initialLoadSizeHint = pageSize
            )
        )
        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }
        return MediatorLiveData<PageListing<HomeItemInfo>>().apply {
            value = PageListing(
                pagedList = livePagedList,
                networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                    it.networkState
                },
                retry = {
                    sourceFactory.sourceLiveData.value?.retryAllFailed()
                },
                refresh = {
                    sourceFactory.sourceLiveData.value?.invalidate()
                },
                refreshState = refreshState
            )
        }
    }
}