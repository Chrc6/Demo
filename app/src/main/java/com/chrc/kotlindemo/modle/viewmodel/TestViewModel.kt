package com.chrc.kotlindemo.modle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *    @author : chrc
 *    date   : 2021/1/11  3:13 PM
 *    desc   :
 */
class TestViewModel: ViewModel() {

    private val mNameEvent: MutableLiveData<String?> = MutableLiveData()

    fun getNameEvent(): MutableLiveData<String?>? {
        return mNameEvent
    }
}