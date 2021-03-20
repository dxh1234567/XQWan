package com.jj.xqwan.activity.home.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jj.xqwan.base.net.PageListing
import com.jj.xqwan.base.net.Resource
import com.jj.xqwan.base.net.app.DataRepository
import com.jj.xqwan.base.viewmodel.BaseResultViewModel
import com.jj.xqwan.entity.HomeItemInfo
import kotlinx.coroutines.launch


/**
 *  Created By duXiaHui
 *  on 2021/1/30
 */
class HomeViewModel(private val repository: DataRepository) :
    BaseResultViewModel<HomeItemInfo>() {
    override fun getLimitResult(query: String): LiveData<Resource<List<HomeItemInfo>>> {

        return  MutableLiveData()
    }

    override fun getPageListing(query: String): LiveData<PageListing<HomeItemInfo>>  =
        repository.getHomeDataList(20)

}