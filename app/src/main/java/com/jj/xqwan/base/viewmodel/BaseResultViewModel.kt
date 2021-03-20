package com.jj.xqwan.base.viewmodel


/**
 *  Created By duXiaHui
 *  on 2021/1/30
 */

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.jj.xqwan.base.net.PageListing
import com.jj.xqwan.base.net.Resource
import com.jj.xqwan.base.viewmodel.IResultViewModel.Companion.LIMIT
import com.jj.xqwan.base.viewmodel.IResultViewModel.Companion.PAGE_SIZE



abstract class BaseResultViewModel<T> : IResultViewModel<T>, ViewModel() {
    override val query = MutableLiveData<String>()

    private val pageListing = Transformations.switchMap(query) {
        getPageListing(it)
    }

    override var limit: Int = LIMIT
        set(value) {
            field = value
            setQuery(query.value, true)
        }

    override var pageSize: Int = PAGE_SIZE
        set(value) {
            field = value
//            setQuery(query.value, true)
        }

    override val searchState =
        Transformations.switchMap(pageListing) { it.networkState }

    override val searchRefreshState =
        Transformations.switchMap(pageListing) { it.refreshState }

    override val searchResult =
        Transformations.switchMap(pageListing) { it.pagedList }

    override val limitResult =
        Transformations.switchMap(query) {
            getLimitResult(it)
        }

    override val noMoreState =
        Transformations.switchMap(pageListing) { it.noMoreState }

    abstract fun getLimitResult(query: String): LiveData<Resource<List<T>>>

    abstract fun getPageListing(query: String): LiveData<PageListing<T>>

    override fun setQuery(query: String?, forceRefresh: Boolean) {
        if (query == null || query.isBlank() || (!forceRefresh && this.query.value == query)) {
            return
        }
        this.query.value = query
    }

    override fun refresh() {
        pageListing.value?.refresh?.invoke()
    }

    override fun reTry() {
        pageListing.value?.retry?.invoke()
    }
}