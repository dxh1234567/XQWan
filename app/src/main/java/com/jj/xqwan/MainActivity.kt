package com.jj.xqwan

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.jj.base.baseclass.BaseActivity
import com.jj.xqwan.activity.home.TAG_USER_PAGE_TYPE

open class MainActivity : BaseActivity() {
    private var pageType = 1
    private lateinit var navController: NavController

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.e("--22--",event.x.toString())
        Log.e("--22--",event.y.toString())
        return super.onTouchEvent(event)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBaseContentView(R.layout.activity_main)
        window.decorView.setBackgroundColor(Color.WHITE)
        pageType = intent.getIntExtra(TAG_USER_PAGE_TYPE, 0)
    }
}

fun Activity.isLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

fun Fragment.isLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}
