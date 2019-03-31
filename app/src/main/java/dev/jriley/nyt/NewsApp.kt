package dev.jriley.nyt

import android.app.Application
import dev.jriley.nyt.service.UpdateStoriesService
import timber.log.Timber

class NewsApp : Application() {


    override fun onCreate() {
        super.onCreate()

        Timber.plant(LogTree())

        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()

        UpdateStoriesService.startActionMain(this)
    }

    companion object {
        lateinit var component: AppComponent
    }
}