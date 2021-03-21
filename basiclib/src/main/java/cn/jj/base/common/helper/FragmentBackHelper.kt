package cn.jj.base.common.helper

import androidx.fragment.app.*
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager.widget.ViewPager
import cn.jj.base.baseclass.OnBackListener


object FragmentBackHandler {

    /**
     * 将back事件分发给 FragmentManager 中管理的子Fragment，如果该 FragmentManager 中的所有Fragment都
     * 没有处理back事件，则尝试 FragmentManager.popBackStack()
     *
     * @return 如果处理了back键则返回 <b>true</b>
     * @see #handleBackPress(Fragment)
     * @see #handleBackPress(FragmentActivity)
     */
    @JvmStatic
    fun handleBackPress(fm: FragmentManager): Boolean {
        fm.fragments.forEach {
            if (isFragmentBackHandled(it)
                    || (it is NavHostFragment && handleBackPress(it))) {
                return true
            }
        }
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
            return true
        }
        return false
    }



    /**
     * 将back事件分发给Fragment中的子Fragment,
     * 该方法调用了 {@link #handleBackPress(FragmentManager)}
     *
     * @return 如果处理了back键则返回 <b>true</b>
     */
    fun handleBackPress(fragment: Fragment): Boolean {
        return handleBackPress(fragment.childFragmentManager)
    }

    /**
     * 将back事件分发给Activity中的子Fragment,
     * 该方法调用了 {@link #handleBackPress(FragmentManager)}
     *
     * @return 如果处理了back键则返回 <b>true</b>
     */
    fun handleBackPress(activity: FragmentActivity): Boolean {
        return handleBackPress(activity.supportFragmentManager)
    }

    /**
     * 将back事件分发给ViewPager中的Fragment,{@link #handleBackPress(FragmentManager)} 已经实现了对ViewPager的支持，所以自行决定是否使用该方法
     *
     * @return 如果处理了back键则返回 <b>true</b>
     * @see #handleBackPress(FragmentManager)
     * @see #handleBackPress(Fragment)
     * @see #handleBackPress(FragmentActivity)
     */
    fun handleBackPress(viewPager: ViewPager?): Boolean {
        if (viewPager == null) return false

        val adapter = viewPager.adapter ?: return false

        val currentItem = viewPager.currentItem
        val fragment: Fragment?
        if (adapter is FragmentPagerAdapter) {
            fragment = adapter.getItem(currentItem)
        } else if (adapter is FragmentStatePagerAdapter) {
            fragment = adapter.getItem(currentItem)
        } else {
            fragment = null
        }
        return isFragmentBackHandled(fragment)
    }

    /**
     * 判断Fragment是否处理了Back键
     *
     * @return 如果处理了back键则返回 <b>true</b>
     */
    fun isFragmentBackHandled(fragment: Fragment?): Boolean {
        return (fragment != null
                && fragment.isVisible
                && fragment.userVisibleHint //for ViewPager
                && fragment is OnBackListener
                && fragment.onBackPressed())
    }
}