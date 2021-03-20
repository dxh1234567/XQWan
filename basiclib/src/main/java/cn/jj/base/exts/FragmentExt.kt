package cn.jj.base.exts

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

/**
 * DialogFragmentNavigator不支持launchSingleTop属性
 */
fun Fragment.navigateSingleTop(@IdRes resId: Int, args: Bundle? = null) {
    findNavController().apply {
        if (currentDestination?.id == currentDestination?.getAction(resId)?.destinationId) {
            return
        }
        navigate(resId, args)
    }
}

fun Fragment.navigateUp(): Boolean {
    return findNavController().navigateUp()
}

fun Fragment.navigateTo(
    @IdRes actionId: Int, bundle: Bundle? = null,
    navOptions: NavOptions? = null
) {
    findNavController().navigate(actionId, bundle, navOptions)
}

fun Fragment.popBackStack(): Boolean {
    return findNavController().popBackStack()
}

fun Fragment.popBackStack(@IdRes destinationId: Int, inclusive: Boolean): Boolean {
    return findNavController().popBackStack(destinationId, inclusive)
}