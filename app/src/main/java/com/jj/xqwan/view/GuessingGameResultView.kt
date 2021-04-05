package com.jj.xqwan.view

import android.animation.*
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.jj.base.common.ThreadManager
import com.jj.base.common.anim.AnimUtils
import com.jj.base.utils.ScreenUtil
import com.jj.base.utils.Utility
import com.airbnb.lottie.LottieAnimationView
import com.jj.xqwan.R
import com.jj.xqwan.constant.RoomConstant
import com.jj.xqwan.databinding.RoomGuessingGameLayoutBindingImpl
import com.jj.xqwan.entity.GuessGamePromotionInfo
import com.jj.xqwan.entity.GuessGameResultInfo
import org.jetbrains.anko.find

class GuessingGameResultView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private val SCREENW_WIDTH = ScreenUtil.getScreenWidth()
    private val SCREENW_WIDTH_MIDDLE = ScreenUtil.getScreenWidth() / 2
    private var lottieView: LottieAnimationView? = null
    private var userLottieView: LottieAnimationView? = null
    private var streamerLottieView: LottieAnimationView? = null
    private var ivUserCover: ImageView? = null
    private var ivStreamCover: ImageView? = null
    private var ivChallengeResultStatus: ImageView? = null
    private var userBackground: View? = null
    private var streamBackGround: View? = null
    private var ivSun: View? = null
    private var tvStreamerName: TextView? = null
    private var tvUserName: TextView? = null
    private var tvPunishmentContent: TextView? = null
    private var mBackgroundAnim: ValueAnimator = ValueAnimator.ofFloat(1f, 0f)
    private var headAlphaAnimator: ValueAnimator = ValueAnimator.ofFloat(1f, 0f)
    private var allAlphaAnimator: ValueAnimator = ValueAnimator.ofFloat(1f, 0f)
    private var resultTextAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
    private var sumRotationAnimation: ObjectAnimator? = null
    private var alphaAnimatorSet = AnimatorSet()
    private var resultAnimatorSet = AnimatorSet()
    private var binding: RoomGuessingGameLayoutBindingImpl? = null
    private var guessingGameResultInfo: GuessGamePromotionInfo? = null

    //    private var guessingGameViewListener: GuessingGameViewListener? = null
    private var animationCount = 1

    //存放旧的状态
//    private var oldGameStatus = RoomConstant.GuessingGameStatus.STATUS_END

    private val scaleResultXAnim by lazy {
        createSpringAnimation(
            ivChallengeResultStatus,
            SpringAnimation.SCALE_X
        )
    }
    private val scaleResultYAnim by lazy {
        createSpringAnimation(
            ivChallengeResultStatus,
            SpringAnimation.SCALE_Y
        )
    }
    private val scaleSunXAnim by lazy {
        createSpringAnimation(
            ivSun,
            SpringAnimation.SCALE_X
        )
    }
    private val scaleSunYAnim by lazy {
        createSpringAnimation(
            ivSun,
            SpringAnimation.SCALE_Y
        )
    }

    init {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.room_guessing_game_layout, this,
            true
        )
        initView()
    }

//    fun setClickListener(listener: GuessingGameViewListener) {
//        guessingGameViewListener = listener
//    }


    private fun initView() {
        lottieView = find(R.id.lottie_view)
        userLottieView = find(R.id.iv_right_result_view)
        streamerLottieView = find(R.id.iv_left_result_view)
        ivUserCover = find(R.id.iv_user_cover)
        ivStreamCover = find(R.id.iv_stream_cover)
        streamBackGround = find(R.id.container_left_view)
        userBackground = find(R.id.container_right_view)
        ivSun = find(R.id.iv_rotation_sun)
        tvStreamerName = find(R.id.tv_stream_name)
        tvUserName = find(R.id.tv_user_name)
        ivChallengeResultStatus = find(R.id.iv_challenge_result_status)
        tvPunishmentContent = find(R.id.tv_punishment_content)
    }


    fun setPromotion(promotion: GuessGamePromotionInfo?) {
        applyInfo(promotion)
        visibility = View.VISIBLE
        startAnim()
    }


    private fun applyInfo(promotion: GuessGamePromotionInfo?) {
        binding?.promotionInfo = promotion
        cancelAnim()
        initGuessingGameResult(promotion!!.resultInfo!!)
        initStartView()
        ivChallengeResultStatus?.setImageDrawable(
            ContextCompat.getDrawable(
                Utility.getApplication(),
                R.drawable.icon_result_challenge_player
            )
        )
        if (measuredWidth == 0) {
            measureHeight()
        }
    }

    private fun startAnim() {
        startLottieAnimator()
        backGroundMoveInAnim()
    }


    /**
     * 昵称 头像移入动画
     */
    private fun backGroundMoveInAnim() {
        mBackgroundAnim.apply {
            cancel()
            addUpdateListener { animation ->
                val value = (animation.animatedValue as Float) * SCREENW_WIDTH_MIDDLE
                streamBackGround?.translationX = -value
                userBackground?.translationX = value
            }
            duration = 400
            interpolator = AccelerateInterpolator()
            addListener(backGroundAnimListener)
            start()
        }
    }


    /**
     * 合并动画结束监听
     */
    private val backGroundAnimListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            ThreadManager.removeUI(delayPlayAnimation)
            ThreadManager.postUI(delayPlayAnimation, 300L)
        }
    }


    private val resultLottieAnimListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            ThreadManager.removeUI(delayResultAnimation)
            ThreadManager.postUI(delayResultAnimation, 1000L)
        }
    }


    /**
     * 开始结果 弹性缩放
     */
    private val delayPlayAnimation = Runnable {
        ivChallengeResultStatus?.visibility = View.VISIBLE
        ivChallengeResultStatus?.visibility = View.VISIBLE
        mGameStatusViewAnimation()
    }

    /**
     * lottie 北京合并
     */
    private fun startLottieAnimator() {
        lottieView?.apply {
            removeAllAnimatorListeners()
            addAnimatorListener(lottieAnimListener)
            cancelAnimation()
            playAnimation()
        }
    }


    private val lottieAnimListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            headAlphaAnimator()
            userLottieView?.visibility = View.VISIBLE
            streamerLottieView?.visibility = View.VISIBLE
            startLeftRightLottieAnimator()
        }
    }


    /**
     * 隐藏头像的动画
     */
    private fun headAlphaAnimator() {
        headAlphaAnimator.apply {
            cancel()
            addUpdateListener { animation ->
                val value = (animation.animatedValue as Float) * 1f
                ivUserCover?.alpha = value
                ivStreamCover?.alpha = value
            }
            duration = 300
            start()
        }
    }


    /**
     * 开启结果动画
     */
    private fun startLeftRightLottieAnimator() {
        streamerLottieView?.apply {
            removeAllAnimatorListeners()
            cancelAnimation()
            playAnimation()

        }
        userLottieView?.apply {
            removeAllAnimatorListeners()
            addAnimatorListener(resultLottieAnimListener)
            cancelAnimation()
            playAnimation()
        }
    }


    private val delayResultAnimation = Runnable {
        val resultDrawable = when (guessingGameResultInfo!!.resultInfo!!.moraResult) {
            RoomConstant.GuessingGameResultType.TIE -> {
                R.drawable.icon_result_challenge_tie
            }
            RoomConstant.GuessingGameResultType.STREAMER_SUCCESS -> {
                R.drawable.icon_result_challenge_fail
            }
            else -> {
                R.drawable.icon_result_challenge_success
            }
        }
        ivChallengeResultStatus?.setImageDrawable(
            ContextCompat.getDrawable(
                Utility.getApplication(),
                resultDrawable
            )
        )
        if (resultDrawable == R.drawable.icon_result_challenge_success) {
            ivSun?.visibility = View.VISIBLE
            sunRotationAnimation()
        } else {
            ivSun?.visibility = View.INVISIBLE
        }
        mGameStatusViewAnimation()
        mGameSunViewAnimation()
    }


    private fun sunRotationAnimation() {
        sumRotationAnimation?.cancel()
        sumRotationAnimation = AnimUtils.ofPropertyValuesHolder(
            ivSun,
            PropertyValuesHolder.ofFloat(
                View.ROTATION,
                360f
            )
        ).apply {
            duration = 5000L
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }


    private fun initGuessingGameResult(msgInfo: GuessGameResultInfo) {
        val leftFilePath = when (msgInfo.hostGuess) {
            RoomConstant.GuessIngGageType.STONE -> "left_store.json"
            RoomConstant.GuessIngGageType.SHEAR -> "left_shear.json"
            else -> "left_cloth.json"
        }
        val rightFilePath = when (msgInfo.userGuess) {
            RoomConstant.GuessIngGageType.STONE -> "right_store.json"
            RoomConstant.GuessIngGageType.SHEAR -> "right_shear.json"
            else -> "right_cloth.json"
        }
//        streamerLottieView?.setAnimation("lottie/guessing_game/finger_guessing_game/${leftFilePath}")
//        streamerLottieView?.imageAssetsFolder = "lottie/guessing_game/finger_guessing_game/images"
//        userLottieView?.setAnimation("lottie/guessing_game/finger_guessing_game/${rightFilePath}")
//        userLottieView?.imageAssetsFolder = "lottie/guessing_game/finger_guessing_game/images"
    }

    /**
     * 状态放大动画
     */
    private fun mGameStatusViewAnimation() {
        scaleResultXAnim.cancel()
        scaleResultYAnim.cancel()
        ivChallengeResultStatus?.scaleX = 0.1f
        ivChallengeResultStatus?.scaleY = 0.1f
        scaleResultXAnim.removeEndListener(gameBestResultAnimation)
        if (animationCount == 1) {
            resultTextAnim()
        }
        if (animationCount > 1) {
            scaleResultXAnim.addEndListener(gameBestResultAnimation)
        }
        animationCount += 1
        scaleResultXAnim.start()
        scaleResultYAnim.start()
    }


    private fun resultTextAnim() {
        resultTextAnimator.apply {
            cancel()
            addUpdateListener { animation ->
                tvPunishmentContent?.alpha = (animation.animatedValue as Float)
            }
            duration = 300L
            start()
        }
    }

    private val gameBestResultAnimation =
        DynamicAnimation.OnAnimationEndListener { _, _, _, _ ->
            ThreadManager.removeUI(resultAnimEnd)
            ThreadManager.postUI(resultAnimEnd, 2000)
        }


    private val resultAnimEnd = Runnable {
        allAlphaAnimator()
    }

    private fun allAlphaAnimator() {
        allAlphaAnimator.apply {
            allAlphaAnimator.cancel()
            addUpdateListener { animation ->
                val value = (animation.animatedValue as Float) * 1f
                alpha = value
            }
            addListener(allAnimListener)
            duration = 100
            start()
        }
    }

    private val allAnimListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            if (ivSun?.visibility == View.VISIBLE) {
//                guessingGameViewListener?.onResultAnimationEnd()
            }
            cancelAnim()
        }
    }

    private fun mGameSunViewAnimation() {
        scaleSunXAnim.cancel()
        scaleSunYAnim.cancel()
        ivSun?.scaleX = 0.1f
        ivSun?.scaleY = 0.1f
        scaleSunXAnim.start()
        scaleSunYAnim.start()
    }


    private fun initStartView() {
        alpha = 1f
        animationCount = 1
        ivUserCover?.alpha = 1f
        ivStreamCover?.alpha = 1f
        tvPunishmentContent?.alpha = 0f
        ivSun?.visibility = View.INVISIBLE
        userLottieView?.visibility = View.INVISIBLE
        streamerLottieView?.visibility = View.INVISIBLE
        ivChallengeResultStatus?.visibility = View.INVISIBLE
        streamBackGround?.translationX = -SCREENW_WIDTH_MIDDLE.toFloat()
        userBackground?.translationX = SCREENW_WIDTH.toFloat()
    }

    private fun cancelAnim() {
        mBackgroundAnim.cancel()
        mBackgroundAnim.removeAllUpdateListeners()
        headAlphaAnimator.cancel()
        alphaAnimatorSet.cancel()
        resultAnimatorSet.cancel()
        sumRotationAnimation?.removeAllUpdateListeners()
        sumRotationAnimation?.cancel()
        scaleSunXAnim.cancel()
        scaleSunYAnim.cancel()
        scaleResultXAnim.removeEndListener(gameBestResultAnimation)
        allAlphaAnimator.removeAllUpdateListeners()
        scaleResultXAnim.cancel()
        scaleResultYAnim.cancel()
        allAlphaAnimator.cancel()
        resultTextAnimator.cancel()
        visibility = View.INVISIBLE
        ThreadManager.removeUI(delayPlayAnimation)
        ThreadManager.removeUI(delayResultAnimation)
        ThreadManager.removeUI(resultAnimEnd)
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelAnim()
    }

    private fun <K> createSpringAnimation(
        obj: K,
        property: FloatPropertyCompat<K>
    ): SpringAnimation {
        return SpringAnimation(obj, property).setSpring(SpringForce(1f).apply {
            stiffness = 700f
            dampingRatio = 0.3f
        })
    }

    private fun measureHeight() {
        val heightMeasure = View.MeasureSpec.makeMeasureSpec(5000, MeasureSpec.AT_MOST)
        val widthMeasure = MeasureSpec.makeMeasureSpec(5000, MeasureSpec.AT_MOST)
        measure(widthMeasure, heightMeasure)
    }
}