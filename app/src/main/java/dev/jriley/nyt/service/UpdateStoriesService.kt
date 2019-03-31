package dev.jriley.nyt.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import dev.jriley.nyt.NewsApp
import dev.jriley.nyt.data.StoryRepository
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import timber.log.Timber
import javax.inject.Inject

class UpdateStoriesService : IntentService(TAG) {

    @Inject
    lateinit var storyRepository: StoryRepository

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        NewsApp.component.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {

        Timber.tag(TAG).i("onHandleIntent  : $intent")

        val function3 = Function3<List<Long>, List<Long>, List<Long>, String> { t1, t2, t3 -> "Inserted t1:${t1.size}, t2:${t2.size}, t3:${t3.size}" }
        compositeDisposable.add(Single.zip(storyRepository.fetchNewStories(), storyRepository.fetchBestStories(), storyRepository.fetchTopStories(), function3)
                .doFinally { compositeDisposable.clear() }
                .subscribe({ Timber.tag("@@").i("get the List if inserted stories : \n $it") },
                        { Timber.tag("@@").e(it, "Opps") }))
    }

    companion object {
        const val TAG = "UpdateStoriesService"

        fun startActionMain(context: Context) {
            context.let {
                Timber.tag(TAG).i("startActionMain -> ${it.packageName}")
                it.startService(Intent(it, UpdateStoriesService::class.java))
            }
        }
    }
}
