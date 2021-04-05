package com.jj.xqwan.base

import com.jj.xqwan.base.net.ErrCode
import com.jj.xqwan.base.net.NetworkException
import com.jj.xqwan.base.net.TOKEN_ERROR_CODE


/**
 *  Created By duXiaHui
 *  on 2021/1/24
 */

import android.text.TextUtils
import android.util.Log
import com.jj.base.utils.ToastUtil
import com.jj.base.utils.Utility
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Common class used by API responses.
 * @param <T> the type of the response object
</T> */
@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<T> {
    companion object {
        private fun removeQuotation(msg: String): String {
            return msg.removeSuffix("\"").removePrefix("\"")
        }

        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            when (error) {
                is SocketTimeoutException -> return ApiErrorResponse(ErrCode.ERR_NETWORK_TIMEOUT)
                is ConnectException -> return ApiErrorResponse(ErrCode.ERR_NETWORK_TIMEOUT)
                is UnknownHostException -> return ApiErrorResponse(ErrCode.ERR_NETWORK_TIMEOUT)
                is NetworkException -> {
                    val errMsg =
                        if (!TextUtils.isEmpty(error.errMsg)) removeQuotation(error.errMsg) else null
                    if (error.errCode == ErrCode.ERR_TOAST && !TextUtils.isEmpty(errMsg)) {
                        ToastUtil.showShort(Utility.getApplication(), errMsg)
                    }
                    if (TOKEN_ERROR_CODE.contains(error.errCode)) {
                    }
                    return ApiErrorResponse(error.errCode, null, errMsg)
                }
            }
            return ApiErrorResponse(ErrCode.ERR_NETWORK_TIMEOUT)
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            if (response.isSuccessful) {
                val body = response.body()
                Log.e("---",response.message() +response.code())
                if (body == null || response.code() == 204) {
                    return ApiEmptyResponse()
                } else {
                    return ApiSuccessResponse(body)
                }
            }
            return ApiErrorResponse(ErrCode.ERR_NETWORK_TIMEOUT)
        }
    }
}

data class ApiSuccessResponse<T>(val data: T) : ApiResponse<T>()

data class ApiErrorResponse<T>(val code: Int, val data: T? = null, val errMsg: String? = null) :
    ApiResponse<T>()

/**
 * separate class for HTTP 204 resposes so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T> : ApiResponse<T>()