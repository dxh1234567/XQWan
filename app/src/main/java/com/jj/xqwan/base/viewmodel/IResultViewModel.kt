package com.jj.xqwan.base.viewmodel



/**
 *  Created By duXiaHui
 *  on 2021/1/30
 */


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.jj.xqwan.base.net.Resource


interface IResultViewModel<T> {
    var limit: Int
    var pageSize: Int
    val query: MutableLiveData<String>
    val searchState: LiveData<Resource<*>>
    val noMoreState: LiveData<Boolean>
    val searchRefreshState: LiveData<Resource<*>>
    val searchResult: LiveData<PagedList<T>>
    val limitResult: LiveData<Resource<List<T>>>

    fun setQuery(query: String?, forceRefresh: Boolean)

    fun setQuery(query: String?) {
        setQuery(query, false)
    }

    fun refresh()

    fun reTry()

    companion object {
        const val LIMIT = 4
        const val NO_LIMIT = -1
        const val PAGE_SIZE = 20
    }
}
