package com.netease.qa.jetpack.viemodel

import android.util.Log
import androidx.lifecycle.ViewModel

/**
 *    @author : chrc
 *    date   : 10/11/21  11:42 AM
 *    desc   :
 */
class GameViewModel: ViewModel() {
    init {
        Log.i("GameViewModel==="," GameViewModel init")
    }

    fun init() {

    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel==="," GameViewModel onCleared")
    }
}