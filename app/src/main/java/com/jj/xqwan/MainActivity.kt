package com.jj.xqwan

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.get
import com.jj.xqwan.activity.home.SettingFragmentType
import com.jj.xqwan.activity.home.TAG_USER_PAGE_TYPE
import com.jj.xqwan.activity.home.getDestId
import kotlinx.coroutines.CoroutineScope

class MainActivity : AppCompatActivity() {
    private var pageType = 1
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.setBackgroundColor(Color.WHITE)
        pageType = intent.getIntExtra(TAG_USER_PAGE_TYPE, 0)


    }


}