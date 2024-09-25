package com.shelflife.instrument.util

import android.view.View
import android.view.animation.*

class AnimateView(mView: View) {
    private var up: ScaleAnimation? = null
    private var down: ScaleAnimation? = null
    private var myView: View? = mView

    fun upDown() {
        val sizeAnimation = 1.05f
        val sizeAnimationDown = 0.9f

        up = ScaleAnimation(
            1.0f,
            sizeAnimation,
            1.0f,
            sizeAnimation,
            Animation.RELATIVE_TO_SELF,
            sizeAnimationDown,
            Animation.RELATIVE_TO_SELF,
            sizeAnimationDown
        )
        up?.duration = 200
        up?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                myView?.startAnimation(down)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        down = ScaleAnimation(
            sizeAnimation,
            1.0f,
            sizeAnimation,
            1.0f,
            Animation.RELATIVE_TO_SELF,
            sizeAnimationDown,
            Animation.RELATIVE_TO_SELF,
            sizeAnimationDown
        )
        down?.duration = 200

        myView?.startAnimation(up)
    }

    fun animateAlpha() {
        val animation1 = AlphaAnimation(0.2f, 1.0f)
        animation1.duration = 500
        try {
            myView?.startAnimation(animation1)
        } catch (e: Exception) {
        }
    }

    fun animateAlpha1To0() {
        val animation1 = AlphaAnimation(1.0f, 0.0f)
        animation1.duration = 500
        try {
            myView?.startAnimation(animation1)
        } catch (e: Exception) {
        }
    }

    fun animateAlpha0To1() {
        val animation1 = AlphaAnimation(0.0f, 1.0f)
        animation1.duration = 800
        try {
            myView?.startAnimation(animation1)
        } catch (e: Exception) {
        }
    }

    fun animateSpeedWay() {
        val pause = 400
        val offsetX = 1500f
        val scale = ScaleAnimation(
            0.8f, 1.0f, 0.8f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        scale.duration = pause.toLong()

        val translateAnimation = TranslateAnimation(offsetX, 0f, 0f, 0f)
        translateAnimation.duration = pause.toLong()

        val alphaAnimation = AlphaAnimation(0.8f, 1.0f)
        alphaAnimation.duration = pause.toLong()

        val animation = AnimationSet(true)
        animation.addAnimation(scale)
        animation.addAnimation(translateAnimation)
        animation.addAnimation(alphaAnimation)

        animation.interpolator = AnticipateOvershootInterpolator()
        myView?.startAnimation(animation)

    }

    fun leftRight() {

        val duration = 200L

        val animation1 = TranslateAnimation(0f, -50f, 0f, 0f)
        animation1.duration = duration
        animation1.fillAfter = true

        val animation2 = TranslateAnimation(-50f, 50f, 0f, 0f)
        animation2.duration = duration
        animation2.fillAfter = true

        val animation3 = TranslateAnimation(50f, 0f, 0f, 0f)
        animation3.duration = duration
        animation3.fillAfter = true

        animation1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                try {
                    myView?.startAnimation(animation2)
                } catch (e: Exception) {
                }
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })

        animation2.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                try {
                    myView?.startAnimation(animation3)
                } catch (e: Exception) {
                }
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })

        myView?.startAnimation(animation1)
    }

}