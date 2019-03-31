package dev.jriley.nyt.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.jriley.nyt.data.StoryRepository
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class SplashViewModel(background: Scheduler,
                      storyRepository: StoryRepository,
                      private val compositeDisposable: CompositeDisposable = CompositeDisposable(),
                      private val behaviorSubject: BehaviorSubject<Boolean> = BehaviorSubject.create(),
                      val loadingObservable: Observable<Boolean> = behaviorSubject) : ViewModel() {
    init {
        compositeDisposable.add(storyRepository.isLoaded()
                .subscribeOn(background)
                .delay(3, TimeUnit.SECONDS, background)
                .doFinally { compositeDisposable.clear() }
                .subscribe({ b -> behaviorSubject.onNext(b) }, { t -> Timber.e(t) }))
    }

}

@Suppress("UNCHECKED_CAST")
class SplashViewModelFactory @Inject constructor(
    private val storyRepository: StoryRepository,
    @Named("io") private val ioScheduler: Scheduler = Schedulers.io()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(storyRepository = storyRepository, background = ioScheduler) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
