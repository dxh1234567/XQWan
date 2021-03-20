package com.jj.xqwan.base.net.app

import androidx.lifecycle.LiveData
import com.jj.xqwan.activity.container.controller.DDDD
import com.jj.xqwan.base.ApiResponse
import com.jj.xqwan.base.net.ApiResult
import com.jj.xqwan.base.net.http.PagedListResultInfo
import com.jj.xqwan.entity.HomeItemInfo
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Query


/**
 *  Created By duXiaHui
 *  on 2021/1/30
 */
interface ApiService {

    /**
     * 首页动态数据 labelId
     */
    @GET("label/product/list")
    fun getHomeLabelGoodsDataList(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("labelId") labelId: Long
    ): LiveData<ApiResponse<PagedListResultInfo<List<HomeItemInfo>>>>


    /**
     * 首页动态数据 labelId
     */

    @GET("label/product/list")
     fun getHomeLabelGoodsDataList1(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("labelId") labelId: Long
    ): LiveData<User<JSONObject>>
}