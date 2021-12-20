package com.netease.qa.jetpack.page.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.chrc.annotalib.FRouterClass
import com.netease.qa.jetpack.R
import com.netease.qa.jetpack.bean.TestBean
import com.netease.qa.jetpack.databinding.FragmentTitleBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TitleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@FRouterClass(
        startDestination = "titleFragment",
        action_id = "to_title_fragment",
        action_destination = "titleFragment",
        fragment_id = "titleFragment",
//        fragment_name = "com.netease.qa.jetpack.page.fragment.TitleFragment",
        fragment_arguments_name = "title",
        fragment_arguments_argType = "string",
        fragment_arguments_defaultValue = "test")
class TitleFragment : Fragment() {

    private lateinit var etView: EditText
    private lateinit var updateTv: TextView

    private lateinit var binding: FragmentTitleBinding
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var testBean = TestBean()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_title, container, false)
        binding = DataBindingUtil.inflate<FragmentTitleBinding>(inflater, R.layout.fragment_title, container, false)
        binding.testbean = testBean
        testBean.id = "1"
        initView(binding.root)
        return binding.root
    }

    private fun initView(root: View) {
        etView = root.findViewById(R.id.et_input)
        updateTv = root.findViewById(R.id.tv_change)

        updateTv.setOnClickListener {
            //数据改变，用到的地方的值也会随之改变
//            binding.testbean = TestBean().apply {
//                etView.text?:"0"
//            }
            testBean.id = (etView.text?:"0").toString()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TitleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                TitleFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}