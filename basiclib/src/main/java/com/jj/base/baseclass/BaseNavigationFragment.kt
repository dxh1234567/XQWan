package com.jj.base.baseclass

import com.jj.base.exts.navigateUp

/**
 * 使用Navigation的Fragment
 */
abstract class BaseNavigationFragment : BaseFragment() {

    /**
     * 当回到起始页面时，结束activity
     */
    override fun onBackPressed(): Boolean {
        if (!navigateUp()) {
            activity?.finish()
        }
        return true
    }
}
