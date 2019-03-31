package dev.jriley.nyt

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import dev.jriley.nyt.data.Story
import dev.jriley.nyt.data.StoryRepository
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.Test
import test

class StoryFragmentViewModelTest {

    private lateinit var testObject: StoryFragmentViewModel
    private val storyRepository: StoryRepository = mock()
    private val behaviorSubject: BehaviorSubject<List<Story>> = BehaviorSubject.create()
    private val flowable: Flowable<List<Story>> = behaviorSubject.toFlowable(BackpressureStrategy.BUFFER)
    private val ioScheduler: TestScheduler = TestScheduler()
    private val component: AppComponent = mock()


    @Test
    fun getValuesFromRepoButNotErrors() {
        val expectedList = listOf(Story.test())
        whenever(storyRepository.flowBest()).thenReturn(flowable)
        testObject =
            StoryFragmentViewModel(storyRepository = storyRepository, ioScheduler = ioScheduler, component = component)

        val testObserver = testObject.observableListStory.test()

        behaviorSubject.onNext(expectedList)

        testObserver.apply {
            assertNoValues()
            assertNotComplete()
            assertNoErrors()
        }

        ioScheduler.triggerActions()

        testObserver.apply {
            assertValues(expectedList)
            assertNotComplete()
            assertNoErrors()
        }

        behaviorSubject.onError(RuntimeException("Yikes"))

        ioScheduler.triggerActions()

        testObserver.apply {
            assertValues(expectedList)
            assertNotComplete()
            assertNoErrors()
        }
    }

    @Test
    fun filterUponStoryTypeTop() {
        val topStory = Story.test(storyTypes = StoryTypes.TOP)
        val newStory = Story.test(storyTypes = StoryTypes.NEW)
        val bestStory = Story.test(storyTypes = StoryTypes.BEST)
        val originalList = listOf(topStory, newStory, bestStory)
        whenever(storyRepository.flowBest()).thenReturn(flowable)
        testObject = StoryFragmentViewModel(
            storyTypesFilter = StoryTypes.TOP,
            storyRepository = storyRepository,
            ioScheduler = ioScheduler,
            component = component
        )

        val testObserver = testObject.observableListStory.test()

        behaviorSubject.onNext(originalList)

        testObserver.apply {
            assertNoValues()
            assertNotComplete()
            assertNoErrors()
        }

        ioScheduler.triggerActions()

        testObserver.apply {
            assertValues(listOf(topStory))
            assertNotComplete()
            assertNoErrors()
        }

    }

    @Test
    fun filterUponStoryTypeNew() {
        val topStory = Story.test(storyTypes = StoryTypes.TOP)
        val newStory = Story.test(storyTypes = StoryTypes.NEW)
        val bestStory = Story.test(storyTypes = StoryTypes.BEST)
        val originalList = listOf(topStory, newStory, bestStory)
        whenever(storyRepository.flowBest()).thenReturn(flowable)
        testObject = StoryFragmentViewModel(
            storyTypesFilter = StoryTypes.NEW,
            storyRepository = storyRepository,
            ioScheduler = ioScheduler,
            component = component
        )

        val testObserver = testObject.observableListStory.test()

        behaviorSubject.onNext(originalList)

        testObserver.apply {
            assertNoValues()
            assertNotComplete()
            assertNoErrors()
        }

        ioScheduler.triggerActions()

        testObserver.apply {
            assertValues(listOf(newStory))
            assertNotComplete()
            assertNoErrors()
        }

    }

    @Test
    fun filterUponStoryTypeBest() {
        val topStory = Story.test(storyTypes = StoryTypes.TOP)
        val newStory = Story.test(storyTypes = StoryTypes.NEW)
        val bestStory = Story.test(storyTypes = StoryTypes.BEST)
        val originalList = listOf(topStory, newStory, bestStory)
        whenever(storyRepository.flowBest()).thenReturn(flowable)
        testObject = StoryFragmentViewModel(
            storyTypesFilter = StoryTypes.BEST,
            storyRepository = storyRepository,
            ioScheduler = ioScheduler,
            component = component
        )

        val testObserver = testObject.observableListStory.test()

        behaviorSubject.onNext(originalList)

        testObserver.apply {
            assertNoValues()
            assertNotComplete()
            assertNoErrors()
        }

        ioScheduler.triggerActions()

        testObserver.apply {
            assertValues(listOf(bestStory))
            assertNotComplete()
            assertNoErrors()
        }
    }
}