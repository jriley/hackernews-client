package dev.jriley.nyt.data

import dev.jriley.nyt.service.HackerNewsService
import dev.jriley.nyt.service.KotlinServices
import dev.jriley.nyt.service.Story
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


class StoryRepository(
    private val hackerNewsService: HackerNewsService = KotlinServices.hackerNewsService,
    private val scheduler: Scheduler = Schedulers.io()
) {
    fun loadNewIds(): Single<List<Long>> = hackerNewsService.new.subscribeOn(scheduler)
    fun loadTopIds(): Single<List<Long>> = hackerNewsService.top.subscribeOn(scheduler)
    fun loadBestIds(): Single<List<Long>> = hackerNewsService.best.subscribeOn(scheduler)
    fun fetchStory(id: String): Maybe<Story> = hackerNewsService.getStory(id)
        .flatMapMaybe { response -> response.body()?.let { Maybe.just(it) } ?: Maybe.empty() }
        .subscribeOn(scheduler)
}