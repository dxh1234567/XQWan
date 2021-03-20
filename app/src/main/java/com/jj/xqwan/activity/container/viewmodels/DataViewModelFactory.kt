package com.jj.xqwan.activity.container.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jj.xqwan.activity.home.viewmodels.HomeViewModel
import com.jj.xqwan.base.net.app.DataRepository


/**
 *  Created By duXiaHui
 *  on 2021/1/24
 */

object DataViewModelFactory : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return  with(modelClass){
            when{

                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(DataRepository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }as T
    }


    fun getHomeViewModel(fragment: Fragment): HomeViewModel {
        return ViewModelProvider(fragment, DataViewModelFactory)
            .get(HomeViewModel::class.java)
    }
}