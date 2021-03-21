package com.jj.xqwan.common

import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import cn.jj.base.common.helper.FragmentBackHandler


/**
 *  Created By duXiaHui
 *  on 2021/3/21
 */
object FragmentBackHandler {

    fun handleBackPress(fm: FragmentManager): Boolean {
        fm.fragments.forEach {
            if (FragmentBackHandler.isFragmentBackHandled(it)
                || (it is NavHostFragment && FragmentBackHandler.handleBackPress(it))) {
                return true
            }
        }
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
            return true
        }
        return false
    }

}