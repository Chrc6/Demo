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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chrc.demo.R
import com.chrc.demo.util.Util
import com.chrc.kotlindemo.adapter.PreImageAdapter
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
class TestRecyclerViewFragment: Fragment() {

    private val testImageUrl: String = "https://emoji.cdn.bcebos.com/yunque/hejirukou.jpg";
    private val testImageUrl2: String = "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1086710651,2760944106&fm=26&gp=0.jpg";
    private val testImageUrl3: String = "https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png";
    private val testImageUrl4: String = "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2699863970,3643884691&fm=26&gp=0.jpg";
    private val testImageUrl5: String = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=121352583,3553479540&fm=26&gp=0.jpg";
    private val testImageUrl6: String = "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3446442004,2207547936&fm=26&gp=0.jpg";
    private val testImageUrl7: String = "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3794661733,2934305289&fm=11&gp=0.jpg";
    private val testImageUrl8: String = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=146893042,4244628931&fm=26&gp=0.jpg";
    private val testImageUrl9: String = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1730713693,2130926401&fm=26&gp=0.jpg";
    private val testImageUrl10: String = "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1592300431,450815993&fm=26&gp=0.jpg";
    private val testImageUrl11: String = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3205720277,4209513487&fm=26&gp=0.jpg";
    private val testImageUrl12: String = "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1160360589,2429665544&fm=26&gp=0.jpg";

    companion object {
        fun newInstance(): TestRecyclerViewFragment{
            var fragment = TestRecyclerViewFragment()
            var bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_text_recycler_view, container, false)
        recyclerView = view.findViewById(R.id.image_pre_recycler_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
    }

    private fun initView() {
        var urls = mutableListOf<String>()
        urls.add(testImageUrl)
        urls.add(testImageUrl2)
        urls.add(testImageUrl3)
        urls.add(testImageUrl4)
        urls.add(testImageUrl5)
        urls.add(testImageUrl6)
        urls.add(testImageUrl7)
        urls.add(testImageUrl8)
        urls.add(testImageUrl9)
        urls.add(testImageUrl10)
        urls.add(testImageUrl11)
        urls.add(testImageUrl12)

        var gridLayoutManager = GridLayoutManager(context, 3)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = PreImageAdapter(requireActivity(), urls, gridLayoutManager, 3)
    }


}