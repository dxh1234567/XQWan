package com.jj.xqwan.base.net


/**
 *  Created By duXiaHui
 *  on 2021/1/31
 */

data class ApiResult<T>(val code: Int, val message: String, private val data: T) {
    fun apiData(): T {
        if (code == 0) {
            return data
        } else {
            throw Exception()
        }
    }
}

