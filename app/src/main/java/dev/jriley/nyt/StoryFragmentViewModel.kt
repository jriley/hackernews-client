package dev.jriley.nyt

import androidx.lifecycle.ViewModel
import dev.jriley.nyt.data.Story
import dev.jriley.nyt.data.StoryRepository
import dev.jriley.nyt.data.StoryRepositoryFactory
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

class StoryFragmentViewModel(
    storyRepository: StoryRepository = StoryRepositoryFactory.storyRepository,
    private val localBehaviorSubject: BehaviorSubject<List<Story>> = BehaviorSubject.create<List<Story>>(),
    ioScheduler: Scheduler = Schedulers.io(),
    val observableListStory: Observable<List<Story>> = localBehaviorSubject,
    var storyTypesFilter: StoryTypes = StoryTypes.NEW) : ViewModel() {
    init {
        storyRepository.flowBest()
                .map { list -> list.filter { it.storyTypes == storyTypesFilter.ordinal } }
                .subscribeOn(ioScheduler)
                .subscribe(localBehaviorSubject::onNext) { t -> Timber.tag("@@").e(t, "StoryFragmentViewModel") }
    }

}