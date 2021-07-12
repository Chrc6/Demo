package com.chrc.kotlindemo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.chrc.demo.R
import com.chrc.kotlindemo.db.AppDataBaseManager
import com.chrc.kotlindemo.modle.UserRoomModel

class RoomTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_test)
        insertData()
        queryData()
//        AppDataBaseManager.getAppDatabase().getUserRoomModelDao().deleteAll()
//        queryData()
    }

    private fun queryData() {
        var userRoomModel = AppDataBaseManager.getAppDatabase().getUserRoomModelDao().getByName("第3")
        userRoomModel?.apply {
            Log.i("roomtest===", "point id=${this.id} name=${this.name}")
        }

        var list = AppDataBaseManager.getAppDatabase().getUserRoomModelDao().getAll()
        list?.apply {
            for (i in this.indices) {
                Log.i("roomtest===", "all id=${this[i].id} name=${this[i].name}")
            }
        }
    }

    private fun insertData() {
        for (i in 0..5) {
            var userRoomModel = UserRoomModel(i.toLong(), "第${i}")
            AppDataBaseManager.getAppDatabase().getUserRoomModelDao().insert(userRoomModel)
        }
    }
}