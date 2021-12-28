package com.chrc.kotlindemo.db

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chrc.MainApplication


/**
 *    @author : chrc
 *    date   : 2021/2/25  4:14 PM
 *    desc   :
 */
object AppDataBaseManager {
    private const val DB_NAME = "rc_demo.db"
    private val dataBase: AppDataBase by lazy {
        buildDatabase(MainApplication.getApplication())
    }

    fun getAppDatabase():AppDataBase {
        return dataBase
    }

    var migration: Migration = object : Migration(1, 2) {
        override fun migrate(@NonNull database: SupportSQLiteDatabase) {
            database.execSQL("alter table student_table ADD  COLUMN begin_time INTEGER NOT NULL DEFAULT 0")
        }
    }

    private fun buildDatabase(context: Context): AppDataBase {
        return Room
                .databaseBuilder(context.applicationContext, AppDataBase::class.java, DB_NAME)
                .allowMainThreadQueries()
                //升级的时候用
//                .addMigrations(MigrationManager.getMigration1To2(), MigrationManager.getMigration2To3())
                .fallbackToDestructiveMigration()
                .build()
    }

    val MIGRATION_2_3: Migration = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE users "
                    + " ADD COLUMN last_update INTEGER")
        }
    }
}