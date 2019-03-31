package dev.jriley.nyt.data

import com.nhaarman.mockito_kotlin.*
import dev.jriley.nyt.AppComponent
import dev.jriley.nyt.service.HackerNewsService
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import test
import kotlin.random.Random

class StoryRepositoryTest {

    private lateinit var testObject: StoryRepository
    private val storyData: StoryData = mock()
    private val hackerNewsService: HackerNewsService = mock()
    private val testScheduler: TestScheduler = TestScheduler()
    private val component: AppComponent = mock()

    @Before
    fun setUp() {
        whenever(component.injectStoryRepository(any())).then {  }
        testObject = StoryRepository(storyData, hackerNewsService, testScheduler, component)
    }

    @Test
    fun `call new story services on io thread`() {
        val expectedIds = (0..Random.nextLong(1, 20)).toList()
        whenever(hackerNewsService.new).thenReturn(Single.just(expectedIds))

        val testObserver = testObject.loadNewIds().test()

        testObserver.apply {
            assertNoErrors()
            assertNotComplete()
            assertNoValues()
        }

        testScheduler.triggerActions()

        testObserver.apply {
            assertNoErrors()
            assertComplete()
            assertValue(expectedIds)
        }
    }

    @Test
    fun flowFromRepo() {
        val expectedStoryList = listOf(Story.test())
        val behaviorSubject = BehaviorSubject.create<List<Story>>()
        val flowable: Flowable<List<Story>> = behaviorSubject.toFlowable(BackpressureStrategy.BUFFER)
        whenever(storyData.storyList()).thenReturn(flowable)
        testObject = StoryRepository(storyData, hackerNewsService, testScheduler, component)

        testObject.flowBest().test().apply {
            assertNoErrors()
            assertNotComplete()
            assertNoValues()
        }

        behaviorSubject.onNext(expectedStoryList)

        testObject.flowBest().test().apply {
            assertNoErrors()
            assertNotComplete()
            assertValues(expectedStoryList)
        }

    }

    @Test
    fun isLoadedDoNotCallService() {
        whenever(storyData.isLoaded()).thenReturn(Single.just(true))
        testObject = StoryRepository(storyData, hackerNewsService, testScheduler, component)

        val testObserver = testObject.isLoaded().test()

        testObserver.apply {
            assertNoValues()
            assertNotComplete()
            assertNoErrors()
        }

        testScheduler.triggerActions()

        testObserver.apply {
            assertValues(true)
            assertComplete()
            assertNoErrors()
        }

        verifyZeroInteractions(hackerNewsService)
    }

    @Test
    fun `call top story services on io thread`() {
        val expectedIds = (0..Random.nextLong(1, 20)).toList()
        whenever(hackerNewsService.top).thenReturn(Single.just(expectedIds))

        val testObserver = testObject.loadTopIds().test()

        testObserver.apply {
            assertNoErrors()
            assertNotComplete()
            assertNoValues()
        }

        testScheduler.triggerActions()

        testObserver.apply {
            assertNoErrors()
            assertComplete()
            assertValue(expectedIds)
        }
    }

    @Test
    fun `call best story services on io thread`() {
        val expectedIds = (0..Random.nextLong(1, 20)).toList()
        whenever(hackerNewsService.best).thenReturn(Single.just(expectedIds))

        val testObserver = testObject.loadBestIds().test()

        testObserver.apply {
            assertNoErrors()
            assertNotComplete()
            assertNoValues()
        }

        testScheduler.triggerActions()

        testObserver.apply {
            assertNoErrors()
            assertComplete()
            assertValue(expectedIds)
        }
    }

    @Test
    fun `call service to get the story data with an id`() {
        val expectedId = Random.nextLong()
        val story = Story.test(expectedId)
        val response = Response.success(200, story)
        whenever(hackerNewsService.getStory("$expectedId")).thenReturn(Single.just(response))

        val testObserver = testObject.fetchStory("$expectedId").test()

        testObserver.apply {
            assertNoErrors()
            assertNotComplete()
            assertNoValues()
        }

        testScheduler.triggerActions()

        testObserver.apply {
            assertNoErrors()
            assertComplete()
            assertValue(response)
        }

    }

    @Test
    fun `don't trust the service it returns null body from time to time`() {
        val expectedId = Random.nextLong()
        val response: Response<Story> = Response.success(null)
        whenever(hackerNewsService.getStory("$expectedId")).thenReturn(Single.just(response))

        val testObserver = testObject.fetchStory("$expectedId").test()

        testObserver.apply {
            assertNoErrors()
            assertNotComplete()
            assertNoValues()
        }

        testScheduler.triggerActions()

        testObserver.apply {
            assertNoErrors()
            assertComplete()
            assertValue(response)
        }
    }

    @Test
    fun `tie it together and insert into the database and return successful ids`() {
        val givenId1 = 2L
        val givenId2 = 3L
        val givenId4 = 4L
        val expectedIds = listOf(Long.MAX_VALUE, Long.MAX_VALUE)
        val wireIds = listOf(givenId4, givenId1, givenId2)
        val story1 = Story.test(givenId1, url = "url-1")
        val story2 = Story.test(givenId2, url = "url-2")
        val response1 = Response.success(200, story1)
        val response2 = Response.success(200, story2)
        val responseNull: Response<Story> = Response.success(null)
        whenever(hackerNewsService.new).thenReturn(Single.just(wireIds))
        whenever(storyData.insert(story1)).thenReturn(Single.just(Long.MAX_VALUE))
        whenever(storyData.insert(story2)).thenReturn(Single.just(Long.MAX_VALUE))
        whenever(hackerNewsService.getStory("$givenId1")).thenReturn(Single.just(response1))
        whenever(hackerNewsService.getStory("$givenId2")).thenReturn(Single.just(response2))
        whenever(hackerNewsService.getStory("$givenId4")).thenReturn(Single.just(responseNull))

        val testObserver = testObject.fetchNewStories().test()

        testObserver.apply {
            assertNoErrors()
            assertNotComplete()
            assertNoValues()
        }

        testScheduler.triggerActions()

        testObserver.apply {
            assertNoErrors()
            assertComplete()
            assertValue(expectedIds)
        }

        verify(storyData).insert(story1)
        verify(storyData).insert(story2)
        verify(hackerNewsService).getStory("$givenId1")
        verify(hackerNewsService).getStory("$givenId2")
        verify(hackerNewsService).getStory("$givenId4")
    }

    @Test
    fun `only use valid urls insert into the database`() {
        val givenId1 = 2L
        val givenId2 = 3L
        val givenId4 = 4L
        val expectedIds = listOf(Long.MAX_VALUE, Long.MAX_VALUE)
        val wireIds = listOf(givenId4, givenId1, givenId2)
        val story1 = Story.test(givenId1, url = "url-1")
        val story2 = Story.test(givenId2, url = "url-2")
        val story4 = Story.test(givenId4, url = "")
        val response1 = Response.success(200, story1)
        val response2 = Response.success(200, story2)
        val response4 = Response.success(200, story4)
        whenever(hackerNewsService.new).thenReturn(Single.just(wireIds))
        whenever(storyData.insert(story1)).thenReturn(Single.just(Long.MAX_VALUE))
        whenever(storyData.insert(story2)).thenReturn(Single.just(Long.MAX_VALUE))
        whenever(hackerNewsService.getStory("$givenId1")).thenReturn(Single.just(response1))
        whenever(hackerNewsService.getStory("$givenId2")).thenReturn(Single.just(response2))
        whenever(hackerNewsService.getStory("$givenId4")).thenReturn(Single.just(response4))

        val testObserver = testObject.fetchNewStories().test()

        testObserver.apply {
            assertNoErrors()
            assertNotComplete()
            assertNoValues()
        }

        testScheduler.triggerActions()

        testObserver.apply {
            assertNoErrors()
            assertComplete()
            assertValue(expectedIds)
        }

        verify(storyData).insert(story1)
        verify(storyData).insert(story2)
        verify(hackerNewsService).getStory("$givenId1")
        verify(hackerNewsService).getStory("$givenId2")
        verify(hackerNewsService).getStory("$givenId4")
    }
}