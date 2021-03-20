package com.jj.xqwan.base.net


/**
 *  Created By duXiaHui
 *  on 2021/1/30
 */


data class Resource<  out  T>(
    val status: Status,
    val data: T?,
    val errCode: Int,
    val errMsg: String? = null
) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(
                Status.SUCCESS,
                data,
                0
            )
        }

        fun <T> error(errCode: Int, data: T? = null, errMsg: String? = null): Resource<T> {
            return Resource(
                Status.ERROR,
                data,
                errCode,
                errMsg
            )
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(
                Status.LOADING,
                data,
                0
            )
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}
