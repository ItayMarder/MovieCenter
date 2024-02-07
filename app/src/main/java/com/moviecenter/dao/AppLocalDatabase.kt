package com.app.moviecenter.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.moviecenter.base.MyApplication
import com.app.moviecenter.model.Review


@Database(entities = [Review::class], version = 3)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun reviewDao(): ReviewDao
}

object AppLocalDatabase {

    val db: AppLocalDbRepository by lazy {

        val context = MyApplication.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}