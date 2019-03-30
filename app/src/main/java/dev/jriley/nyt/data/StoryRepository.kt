package dev.jriley.nyt.data

import dev.jriley.nyt.service.HackerNewsService
import dev.jriley.nyt.service.KotlinServices
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


class StoryRepository(
    private val storyData: StoryData,
    private val hackerNewsService: HackerNewsService = KotlinServices.hackerNewsService,
    private val scheduler: Scheduler = Schedulers.io()
) {
    fun loadNewIds(): Single<List<Long>> = hackerNewsService.new.subscribeOn(scheduler)
    fun loadTopIds(): Single<List<Long>> = hackerNewsService.top.subscribeOn(scheduler)
    fun loadBestIds(): Single<List<Long>> = hackerNewsService.best.subscribeOn(scheduler)
    fun fetchStory(id: String): Single<Story> = hackerNewsService.getStory(id)
        .map { response -> response.body()?.let { it } ?: Story(-1) }
        .subscribeOn(scheduler)

    fun fetchNewStories(): Single<List<Long>> {
        return hackerNewsService.new
            .toObservable()
            .flatMapIterable {  list -> list}
            .map { id -> id.toString() }
            .map { id -> fetchStory(id) }
            .flatMapSingle { single -> single }
            .map { if(it.id > 0) storyData.insert(story = it); it }
            .map { it.id }
            .filter { it > 0 }
            .toList()
    }
}

interface StoryData {
    fun isLoaded(): Single<Boolean>

    fun insert(story: Story): Single<Long>

    fun update(story: Story): Single<Int>

    fun isLoaded(id: Long): Boolean

    fun storyList(): Flowable<List<Story>>
}
