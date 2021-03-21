package com.jj.xqwan.activity.home

import com.jj.xqwan.R
import com.jj.xqwan.isLandscape
import com.jj.xqwan.base.BaseFragment
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import com.jj.xqwan.entity.GuessGameResultInfo
import com.jj.xqwan.entity.GuessGamePromotionInfo
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.fragment_circle.*


/**
 *  Created By duXiaHui
 *  on 2021/3/14
 */
class CircleFragment : BaseFragment() {

    private var gameProConstraint: ConstraintSet? = null


    override fun initLayout(): Int = R.layout.fragment_circle

    override fun initView() {
        gameProConstraint = ConstraintSet().apply {
            clone(requireContext(), R.layout.fragment_circle_land)
        }
        btn1.setOnClickListener {
            val guessGamePromotionInfo = GuessGamePromotionInfo()
            guessGamePromotionInfo.resultInfo = GuessGameResultInfo().also {
                it.userNickName = "用户的昵称"
                it.hostNickName = "主播的昵称"
                it.userAvatar =
                    "https://www.paixin.com/static/img/2t4qgvwrg45wt54q3gfrefgvw45ygfvbwt5y.d9d53b4.png"
                it.hostAvatar =
                    "https://www.paixin.com/static/img/2t4qgvwrg45wt54q3gfrefgvw45ygfvbwt5y.d9d53b4.png"
            }
            guessing_view.setPromotion(guessGamePromotionInfo)
        }
    }


    override fun onBackPressed(): Boolean {
        if (isLandscape()) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            return true
        }
        return super.onBackPressed()
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (isDetached) {
            return
        }

//        if(gameProConstraint == null ){
//            gameProConstraint = ConstraintSet().apply {
//                clone(requireContext(),R.layout.area_picker_layout)
//            }
//        }

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gameProConstraint!!.apply {
                setVisibility(R.id.btn1, btn1.visibility)
                setMargin(R.id.btn1, ConstraintSet.TOP, 100)
                constrainHeight(R.id.btn1,200)
                constrainWidth(R.id.btn1,300)
            }.applyTo(container)
        } else {
            gameProConstraint!!.apply {
                setVisibility(R.id.btn1, btn1.visibility)
                setMargin(R.id.btn1, ConstraintSet.TOP, 0)
            }.applyTo(container)
        }
    }
}