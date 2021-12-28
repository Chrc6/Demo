package com.netease.qa.jetpack.viemodel

import android.app.Person
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    val persons: MutableLiveData<List<Person>> by lazy {
        MutableLiveData<List<Person>>().apply {
            loadPersons()
        }
    }

    private fun loadPersons() {

    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel==="," GameViewModel onCleared")
    }
}