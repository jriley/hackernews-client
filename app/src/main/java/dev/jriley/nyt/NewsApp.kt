package dev.jriley.nyt

import android.app.Application
import androidx.room.Room
import dev.jriley.nyt.data.AppDatabase
import dev.jriley.nyt.data.DatabaseProvider
import dev.jriley.nyt.service.UpdateStoriesService
import timber.log.Timber

class NewsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(LogTree())

        DatabaseProvider.dataBase = Room.databaseBuilder(this, AppDatabase::class.java, "hn3w5_db").build()

        UpdateStoriesService.startActionMain(this)
    }
}