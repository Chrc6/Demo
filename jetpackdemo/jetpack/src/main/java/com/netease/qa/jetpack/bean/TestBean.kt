package com.netease.qa.jetpack.bean

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.netease.qa.jetpack.BR

/**
 *    @author : chrc
 *    date   : 10/11/21  10:14 AM
 *    desc   :
 */
class TestBean(): BaseObservable() {
    var id: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.id)
        }
     @Bindable get
}