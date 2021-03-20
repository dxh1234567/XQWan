package cn.jj.base.baseclass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import cn.jj.base.exts.printLog
import cn.jj.base.utils.LogUtil

/**
 * 使用Navigation时，从其他fragment回到当前fragment时，会导致当前fragment冲更新创建，
 * 此处将rootview持有，避免重新创建
 * */
private const val TAG = "BasePersistFragment"

abstract class BasePersistFragment : BaseFragment() {

    private var hasInitializedRootView = false
    protected var rootView: View? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        if (rootView == null) {
            rootView = onCreatePersistView(inflater, container, savedInstanceState)
        }
        registerListener()
        rootView?.parent?.let {
            it as ViewGroup
            it.removeView(rootView)
        }
        //页面跳转动画未执行完毕时，无法立即从父View中移除
        if (rootView?.parent != null) {
            hasInitializedRootView = false
            rootView = onCreatePersistView(inflater, container, savedInstanceState)
        }
        return rootView
    }

    //不要复写
    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        unregisterListener()
        printLog { LogUtil.d(TAG, "onDestroyView=${javaClass.simpleName}") }
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!hasInitializedRootView) {
            hasInitializedRootView = true
            printLog { LogUtil.d(TAG, "initView=${javaClass.simpleName}") }
            initView(savedInstanceState)
            initData()
        } else {
            rebindLifecycleOwner()
        }
    }

    abstract fun onCreatePersistView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    open fun initView(savedInstanceState: Bundle?) {}
    open fun rebindLifecycleOwner() {
        printLog { LogUtil.d(TAG, "rebindLifecycleOwner=${javaClass.simpleName}") }
    }

    open fun initData() {}

    open fun registerListener() {}
    open fun unregisterListener() {}
}
