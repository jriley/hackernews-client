package dev.jriley.nyt

import androidx.lifecycle.ViewModel
import dev.jriley.nyt.data.Story
import dev.jriley.nyt.data.StoryRepository
import dev.jriley.nyt.data.StoryRepositoryFactory
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

class StoryFragmentViewModel(
    private val localBehaviorSubject: BehaviorSubject<List<Story>> = BehaviorSubject.create<List<Story>>(),
    val observableListStory: Observable<List<Story>> = localBehaviorSubject,
    var storyTypesFilter: StoryTypes = StoryTypes.NEW,
    var disposable: Disposable? = null,
    storyRepository: StoryRepository = StoryRepositoryFactory.storyRepository,
    ioScheduler: Scheduler = Schedulers.io()
) : ViewModel() {
    init {
        disposable = storyRepository.flowBest()
            .map { list -> list.filter { it.storyTypes == storyTypesFilter.ordinal } }
            .doFinally { disposable?.dispose() }
            .subscribeOn(ioScheduler)
            .subscribe(localBehaviorSubject::onNext) { t -> Timber.tag("@@").e(t, "StoryFragmentViewModel") }
    }

}