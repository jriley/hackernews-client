package dev.jriley.nyt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.jriley.nyt.data.Story
import dev.jriley.nyt.data.StoryRepository
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class StoryFragmentViewModel @Inject constructor(
    private val localBehaviorSubject: BehaviorSubject<List<Story>> = BehaviorSubject.create<List<Story>>(),
    val observableListStory: Observable<List<Story>> = localBehaviorSubject,
    var storyTypesFilter: StoryTypes = StoryTypes.NEW,
    var disposable: Disposable? = null,
    storyRepository: StoryRepository,
    ioScheduler: Scheduler = Schedulers.io(),
    component: AppComponent = NewsApp.component
) : ViewModel() {
    init {
        component.inject(this)
        disposable = storyRepository.flowBest()
            .map { list -> list.filter { it.storyTypes == storyTypesFilter.ordinal } }
            .doFinally { disposable?.dispose() }
            .subscribeOn(ioScheduler)
            .subscribe(localBehaviorSubject::onNext) { t -> Timber.tag("@@").e(t, "StoryFragmentViewModel") }
    }

    override fun onCleared() {
        disposable?.dispose()
        super.onCleared()
    }

}

@Suppress("UNCHECKED_CAST")
class FragmentViewModelFactory @Inject constructor(
    private val storyRepository: StoryRepository,
    @Named("io") private val ioScheduler: Scheduler = Schedulers.io()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StoryFragmentViewModel::class.java) -> StoryFragmentViewModel(storyRepository = storyRepository, ioScheduler = ioScheduler) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")

        }

    }
}