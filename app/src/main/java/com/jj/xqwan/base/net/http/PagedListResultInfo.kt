package com.jj.xqwan.base.net.http

import com.google.gson.annotations.SerializedName


/**
 * Created by yangxl on 2018/4/26.
 */

class PagedListResultInfo<T> {
    var pageNum: Int = 0
    var list: T? = null
    var hasNext: Boolean = false



    @SerializedName("pageSize")
    var pageSize: Int = 0
    @SerializedName("rows")
    var rows: T? = null
    @SerializedName("hasNextPage")
    var hasNextPage: Boolean = false
    override fun toString(): String {
        return "PagedListResultInfo(pageNum=$pageSize, list=$rows, hasNext=$hasNextPage)"
    }

}
