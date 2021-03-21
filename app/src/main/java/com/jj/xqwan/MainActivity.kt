package com.jj.xqwan

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import cn.jj.base.common.helper.FragmentBackHandler.handleBackPress
import com.jj.xqwan.activity.home.TAG_USER_PAGE_TYPE

open class MainActivity : AppCompatActivity() {
    private var pageType = 1
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.setBackgroundColor(Color.WHITE)
        pageType = intent.getIntExtra(TAG_USER_PAGE_TYPE, 0)


    }


    @CallSuper
    override fun onBackPressed() {
        try {
            if (!handleBackPress(this)) {
                if (!dealBack()) {
                    super.onBackPressed()
                }
            }
        } catch (e: Exception) {
            //java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
            finish()
        }
    }


    private fun dealBack(): Boolean {
        return false
    }

}

fun Activity.isLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

fun Fragment.isLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}
