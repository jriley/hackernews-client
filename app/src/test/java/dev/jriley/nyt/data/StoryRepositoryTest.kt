package dev.jriley.nyt.data

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import dev.jriley.nyt.service.HackerNewsService
import dev.jriley.nyt.service.Story
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import retrofit2.Response
import kotlin.random.Random

class StoryRepositoryTest {

    private lateinit var testObject: StoryRepository
    private val hackerNewsService: HackerNewsService = mock()
    private val testScheduler: TestScheduler = TestScheduler()

    @Test
    fun `call new story services on io thread`() {
        val expectedIds = (0..Random.nextLong(1,20)).toList()
        whenever(hackerNewsService.new).thenReturn(Single.just(expectedIds))
        testObject = StoryRepository(hackerNewsService, testScheduler)

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
    fun `call top story services on io thread`() {
        val expectedIds = (0..Random.nextLong(1,20)).toList()
        whenever(hackerNewsService.top).thenReturn(Single.just(expectedIds))
        testObject = StoryRepository(hackerNewsService, testScheduler)

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
        val expectedIds = (0..Random.nextLong(1,20)).toList()
        whenever(hackerNewsService.best).thenReturn(Single.just(expectedIds))
        testObject = StoryRepository(hackerNewsService, testScheduler)

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
        val story = Story(expectedId)
        val response = Response.success(200, story)
        whenever(hackerNewsService.getStory("$expectedId")).thenReturn(Single.just(response))
        testObject = StoryRepository(hackerNewsService, testScheduler)

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
            assertValue(story)
        }

    }

    @Test
    fun `don't trust the service it returns null body from time to time`() {
        val expectedId = Random.nextLong()
        val response: Response<Story> = Response.success( null)
        whenever(hackerNewsService.getStory("$expectedId")).thenReturn(Single.just(response))
        testObject = StoryRepository(hackerNewsService, testScheduler)

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
            assertNoValues()
        }
    }
}