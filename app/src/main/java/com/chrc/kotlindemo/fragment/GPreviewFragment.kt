package com.chrc.kotlindemo.fragment

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.chrc.demo.R
import com.chrc.demo.util.Util
import com.chrc.kotlindemo.modle.PreImageInfo
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.google.gson.Gson

/**
 *    @author : chrc
 *    date   : 2021/1/21  9:59 AM
 *    desc   :
 */
class GPreviewFragment: Fragment() {

    private lateinit var sdv: SimpleDraweeView
    private lateinit var consLayout: ConstraintLayout

    lateinit var animatorEnter: ValueAnimator
    lateinit var animatorOut: ValueAnimator

    private var preImageInfo: PreImageInfo? = null
    private var initRadius: Float = 0f
    private var needAnimator: Boolean = false

    companion object {
        fun newInstance(preImageInfo: PreImageInfo, needAnimator: Boolean): GPreviewFragment{
            var fragment = GPreviewFragment()
            var bundle = Bundle()
            bundle.putSerializable("data", preImageInfo)
            bundle.putBoolean("needAnimator", needAnimator)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_g_preview, container, false)
        consLayout = view.findViewById(R.id.cons_parent_layout)
        sdv = view.findViewById(R.id.gpre_sdv)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        enterAnimator()
    }

    private fun initData() {
        arguments?.apply {
            needAnimator = getBoolean("needAnimator") && userVisibleHint
            preImageInfo = getSerializable("data") as PreImageInfo
            Log.i("anifrag===","preImageInfo is null=${preImageInfo == null}")
        }


        var radiusDp: Double = if (preImageInfo != null) preImageInfo!!.radius.toDouble() else 0.0
        initRadius = Util.dip2px(context, radiusDp).toFloat()

        animatorEnter = ValueAnimator.ofFloat(0f, 1f)
        animatorOut = ValueAnimator.ofFloat(0f, 1f)
    }

    private fun initView() {
        if (needAnimator) {
            preImageInfo?.apply {
                var constrainSet = ConstraintSet()
                constrainSet.clone(consLayout)
                constrainSet.clear(sdv.id)
                constrainSet.connect(sdv.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                constrainSet.applyTo(consLayout)

                sdv.x = this.x.toFloat()
                sdv.y = this.y.toFloat() - com.gyf.barlibrary.ImmersionBar.getStatusBarHeight(requireActivity())
                Log.d("testpoint===", "init x=${sdv.x} y=${sdv.y}")
                Log.i("testpoint===","x=${x} y=${y- com.gyf.barlibrary.ImmersionBar.getStatusBarHeight(requireActivity())}")
                val params: ViewGroup.LayoutParams = sdv.layoutParams
                params.width = this.width
                params.height = this.height
                sdv.layoutParams = params
            }

            var roundingParams = sdv.hierarchy.roundingParams
            if (roundingParams == null) {
                roundingParams = RoundingParams()
                sdv.hierarchy.roundingParams = roundingParams
            }
            //这里是设置你希望的圆角的值
            roundingParams.setCornersRadius(initRadius)
            consLayout.setBackgroundColor(Color.parseColor("#000000"))
            consLayout.background?.alpha = 0
        } else {
            consLayout.setBackgroundColor(Color.parseColor("#000000"))
            consLayout.background?.alpha = 255
        }
    }

    private fun enterAnimator() {
        layoutImageView(preImageInfo?.imageUrl)
    }

    fun exitAnimator() {
        if (preImageInfo != null) {
            Log.i("anifrag===","cur x=${preImageInfo?.x} y=${preImageInfo?.y} url=${preImageInfo?.imageUrl}" +
                    " screenheight=${Util.getDeviceHeightPixels(context)} statusbarheight=${com.gyf.barlibrary.ImmersionBar.getStatusBarHeight(requireActivity())}")
        }
        if (animatorOut.isRunning) {
            return
        }
        if (animatorEnter.isRunning) {
            animatorEnter.cancel()
        }
        preImageInfo?.apply {
            var targetTop = y- com.gyf.barlibrary.ImmersionBar.getStatusBarHeight(requireActivity())
            startExitAnimator(x, targetTop, x+width, targetTop+height)
        }
    }

    private fun layoutImageView(url: String?) {
        if (url == null) {
            return
        }
        val controllerListener: ControllerListener<ImageInfo?> = object : BaseControllerListener<ImageInfo?>() {
            override fun onFinalImageSet(id: String, imageInfo: ImageInfo?, anim: Animatable?) {
                if (imageInfo == null) {
                    return
                }
                val height = imageInfo.height
                val width = imageInfo.width

                val screenWidth: Int = Util.getDeviceWidthPixels(requireActivity())
                val viewHeight = screenWidth * height / width

                var targetTop = Util.getDeviceHeightPixels(requireActivity())/2 - viewHeight/2
                var targetBottom = Util.getDeviceHeightPixels(requireActivity())/2 + viewHeight/2
                if (needAnimator) {
                    sdv.post{
                        startEnterAnimator(0, targetTop, screenWidth, targetBottom)
                    }
                } else {
                    val params: ViewGroup.LayoutParams = sdv.layoutParams
                    params.width = screenWidth
                    params.height = viewHeight
                    sdv.layoutParams = params
                }
            }

            override fun onIntermediateImageSet(id: String, imageInfo: ImageInfo?) {
                Log.d("testpoint===", "Intermediate image received")
            }

            override fun onFailure(id: String, throwable: Throwable) {
                throwable.printStackTrace()
                Log.i("testpoint===","onFailure")
            }
        }
        val controller: DraweeController = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setAutoPlayAnimations(true)
                .setUri(Uri.parse(url))
                .build()
        sdv.controller = controller

    }

    fun startEnterAnimator(targetLeft: Int, targetTop: Int, targetRight: Int, targetBottom: Int) {
        animatorEnter.duration = 2000
        var width = sdv.width
        var height = sdv.height
        var x = sdv.x
        var y = sdv.y
        Log.i("testpoint===se","startEnterAnimator width=$width height=$height  x=$x y=$y")
        animatorEnter.addUpdateListener {
            val curValueFloat = animatorEnter.animatedValue as Float
            var radius = initRadius * (1-curValueFloat)
            resetViewParams(sdv,
                    width, height, x, y,
                    radius, curValueFloat, targetLeft, targetTop, targetRight, targetBottom)
            if (consLayout.background != null) {
                consLayout.background.alpha = (255 * curValueFloat).toInt()
            }
        }
        animatorEnter.start()
    }

    private fun startExitAnimator(targetLeft: Int, targetTop: Int, targetRight: Int, targetBottom: Int) {
        animatorOut.duration = 2000

        var width = sdv.width
        var height = sdv.height
        var x = sdv.x
        var y = sdv.y

        animatorOut.addUpdateListener {
            val curValueFloat = animatorOut.animatedValue as Float
            var radius = initRadius * curValueFloat
            resetViewParams(sdv,
                    width, height, x, y,
                    radius, curValueFloat, targetLeft, targetTop, targetRight, targetBottom)
            if (curValueFloat >= 1) {
                activity?.finish()
            }
            if (consLayout.background != null) {
                consLayout.background.alpha = (255 * (1-curValueFloat)).toInt()
            }
        }
        animatorOut.start()
    }

    private fun resetViewParams(userSv: SimpleDraweeView,
                                width: Int, height: Int, curX: Float, curY: Float,
                                radius: Float, rate: Float,
                                targetLeft: Int, targetTop: Int, targetRight: Int, targetBottom: Int) {
        Log.i("testpoint===","before width=$width height=$height targettop=$targetTop targetleft=$targetLeft" +
                " targetright=$targetRight targetbottom=$targetBottom x=$curX y=$curY radius=$radius")

        var roundingParams = sdv.hierarchy.roundingParams
//        var roundingParams = RoundingParams()
        if (roundingParams == null) {
            roundingParams = RoundingParams()
            sdv.hierarchy.roundingParams = roundingParams
        }
        //这里是设置你希望的圆角的值
        roundingParams.setCornersRadius(radius)
//        var builder = GenericDraweeHierarchyBuilder(resources)
//        var hierarchy = builder.build()
//        hierarchy.roundingParams = roundingParams
        //一定要先设置Hierarchy，再去加载图片，否则会加载不出来图片
//        userSv.hierarchy = hierarchy
        //这里传入你的服务器提供的图片地址，不设置的话，圆角设置无效，原因未知
        userSv.setImageURI(preImageInfo?.imageUrl)

        userSv.x = curX + (targetLeft - curX) * rate
        userSv.y = curY + (targetTop - curY) * rate

        val params: ViewGroup.LayoutParams = sdv.layoutParams
        params.width = (width + (targetRight - targetLeft - width) * rate).toInt()
        params.height = (height + (targetBottom - targetTop - height) * rate).toInt()

        Log.i("testpoint===","x=${userSv.x} y=${userSv.x} width=${params.width} height=${params.height}")

        sdv.layoutParams = params
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (animatorOut.isRunning) {
            animatorOut.cancel()
        }
    }

}