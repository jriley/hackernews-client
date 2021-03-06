package dev.jriley.nyt.data

import dev.jriley.nyt.AppComponent
import dev.jriley.nyt.NewsApp
import dev.jriley.nyt.StoryTypes
import dev.jriley.nyt.service.HackerNewsService
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class StoryRepository @Inject constructor(
    private val storyData: StoryData,
    private val hackerNewsService: HackerNewsService,
    @Named("io") private var scheduler: Scheduler,
    component: AppComponent = NewsApp.component
) {
    init {
        component.injectStoryRepository(this)
    }

    fun flowBest(): Flowable<List<Story>> = storyData.storyList()
    fun isLoaded(): Single<Boolean> = storyData.isLoaded().subscribeOn(scheduler)
    fun loadNewIds(): Single<List<Long>> = hackerNewsService.new.subscribeOn(scheduler)
    fun loadTopIds(): Single<List<Long>> = hackerNewsService.top.subscribeOn(scheduler)
    fun loadBestIds(): Single<List<Long>> = hackerNewsService.best.subscribeOn(scheduler)
    fun fetchStory(id: String): Single<Response<Story>> = hackerNewsService.getStory(id).subscribeOn(scheduler)

    fun fetchNewStories(): Single<List<Long>> = fetchAndInsert(loadNewIds(), StoryTypes.NEW)

    fun fetchTopStories(): Single<List<Long>> = fetchAndInsert(loadTopIds(), StoryTypes.TOP)

    fun fetchBestStories(): Single<List<Long>> = fetchAndInsert(loadBestIds(), StoryTypes.BEST)

    private fun fetchAndInsert(api: Single<List<Long>>, filter: StoryTypes): Single<List<Long>> =
        api.map { id -> Timber.tag("@@@@").i("Original  size: ${id.size}"); id.filter { !storyData.isLoaded(it) } }
            .toObservable()
            .flatMapIterable { filteredList -> Timber.tag("@").i("Filtered size:${filteredList.size}"); filteredList }
            .map { filteredId -> hackerNewsService.getStory(filteredId.toString()) }
            .flatMapSingle { single -> single }
            .map { story -> story.body()?.run { filterStoriesOnlyWithUrl(filter) } ?: Single.just(-1L) }
            .flatMapSingle { single -> single }
            .filter { it > 0 }
            .toList()
            .subscribeOn(scheduler)

    private fun Story.filterStoriesOnlyWithUrl(filter: StoryTypes): Single<Long> =
        if (url.isNotBlank()) storyData.insert(Story(this, filter)) else Single.just(-1L)
}

interface StoryData {
    fun isLoaded(): Single<Boolean>

    fun insert(story: Story): Single<Long>

    fun update(story: Story): Single<Int>

    fun isLoaded(id: Long): Boolean

    fun storyList(): Flowable<List<Story>>
}
