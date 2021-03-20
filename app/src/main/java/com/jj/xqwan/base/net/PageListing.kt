package com.jj.xqwan.base.net

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList


/**
 *  Created By duXiaHui
 *  on 2021/1/30
 */

data class PageListing<T>(
    // the LiveData of paged lists for the UI to observe
    val pagedList: LiveData<PagedList<T>>,
    // represents the network request status to show to the user
    val networkState: LiveData<Resource<*>>,
    // represents the refresh status to show to the user. Separate from networkState, this
    // value is importantly only when refresh is requested.
    val refreshState: LiveData<Resource<*>>,
    //是否还有更多
    val noMoreState: LiveData<Boolean> = MediatorLiveData(),
    // refreshes the whole data and fetches it from scratch.
    val refresh: () -> Unit,
    // retries any failed requests.
    val retry: () -> Unit
)