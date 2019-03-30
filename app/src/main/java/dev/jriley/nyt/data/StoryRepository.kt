package dev.jriley.nyt.data

import dev.jriley.nyt.StoryTypes
import dev.jriley.nyt.service.HackerNewsService
import dev.jriley.nyt.service.KotlinServices
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import timber.log.Timber

object StoryRepositoryFactory {
    val storyRepository: StoryRepository by lazy { StoryRepository() }
}

class StoryRepository(
    private val storyData: StoryData = DatabaseProvider.dataBase.storyEntityDao(),
    private val hackerNewsService: HackerNewsService = KotlinServices.hackerNewsService,
    private val scheduler: Scheduler = Schedulers.io()
) {

    fun flowBest(): Flowable<List<Story>> = storyData.storyList()
    fun isLoaded(): Single<Boolean> = storyData.isLoaded().subscribeOn(scheduler)
    fun loadNewIds(): Single<List<Long>> = hackerNewsService.new.subscribeOn(scheduler)
    fun loadTopIds(): Single<List<Long>> = hackerNewsService.top.subscribeOn(scheduler)
    fun loadBestIds(): Single<List<Long>> = hackerNewsService.best.subscribeOn(scheduler)
    fun fetchStory(id: String): Single<Response<Story>> = hackerNewsService.getStory(id).subscribeOn(scheduler)

    fun fetchNewStories(): Single<List<Long>> {
        return loadNewIds()
            .map { id -> Timber.tag("@@@@").i("Original  size: ${id.size}"); id.filter { !storyData.isLoaded(it) } }
            .toObservable()
            .flatMapIterable { filteredList -> Timber.tag("@").i("Filtered size:${filteredList.size}"); filteredList }
            .map { filteredId -> hackerNewsService.getStory(filteredId.toString()) }
            .flatMapSingle { single -> single }
            .map { story -> story.body()?.run { storyData.insert(Story(this, StoryTypes.NEW)) }?: Single.just(-1L) }
            .flatMapSingle { single -> single }
            .filter { it > 0 }
            .toList()
            .subscribeOn(scheduler)
    }

    fun fetchTopStories(): Single<List<Long>> {
        return loadTopIds()
            .map { id -> Timber.tag("@@@@").i("Original  size: ${id.size}"); id.filter { !storyData.isLoaded(it) } }
            .toObservable()
            .flatMapIterable { filteredList -> Timber.tag("@").i("Filtered size:${filteredList.size}"); filteredList }
            .map { filteredId -> hackerNewsService.getStory(filteredId.toString()) }
            .flatMapSingle { single -> single }
            .map { story -> story.body()?.run { storyData.insert(Story(this, StoryTypes.TOP)) }?: Single.just(-1L) }
            .flatMapSingle { single -> single }
            .filter { it > 0 }
            .toList()
            .subscribeOn(scheduler)
    }

    fun fetchBestStories(): Single<List<Long>> {
        return loadBestIds()
            .map { id -> Timber.tag("@@@@").i("Original  size: ${id.size}"); id.filter { !storyData.isLoaded(it) } }
            .toObservable()
            .flatMapIterable { filteredList -> Timber.tag("@").i("Filtered size:${filteredList.size}"); filteredList }
            .map { filteredId -> hackerNewsService.getStory(filteredId.toString()) }
            .flatMapSingle { single -> single }
            .map { story -> story.body()?.run { storyData.insert(Story(this, StoryTypes.BEST)) }?: Single.just(-1L) }
            .flatMapSingle { single -> single }
            .filter { it > 0 }
            .toList()
            .subscribeOn(scheduler)
    }
}

interface StoryData {
    fun isLoaded(): Single<Boolean>

    fun insert(story: Story): Single<Long>

    fun update(story: Story): Single<Int>

    fun isLoaded(id: Long): Boolean

    fun storyList(): Flowable<List<Story>>
}
