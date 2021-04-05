package com.jj.base

import androidx.annotation.StringRes


/**
 *  Created By duXiaHui
 *  on 2021/1/30
 */
interface BaseContractNew {
    interface BaseView {

        fun showBaseLoading()

        fun showBaseContent()

        fun showBaseEmpty()

        fun showBaseNetError()

        fun showLoading()

        fun showSuccess(msg: String?)

        fun showErrorMsg(msg: String?)

        fun showErrorMsg(@StringRes msg: Int)

        fun showErrorMsg(e: Throwable?)

        fun isContentShow(): Boolean

        fun refreshComplete()

        fun isShowBaseError(): Boolean
    }
}