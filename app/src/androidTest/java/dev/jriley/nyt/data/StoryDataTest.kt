package dev.jriley.nyt.data

import dev.jriley.nyt.DaoTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import test
import kotlin.random.Random

class StoryDataTest : DaoTest<StoryDao>() {
    override fun createDao(): StoryDao = database.storyEntityDao()

    private lateinit var testDataObject: StoryData

    @Before
    override fun setUp() {
        super.setUp()

        testDataObject = testObject
    }

    @Test
    fun isLoaded() {

        testDataObject.isLoaded().test().apply {
            assertNoErrors()
            assertValue(false)
            assertComplete()
        }

        val storyEntity = Story.test()
        testDataObject.insert(storyEntity).test().apply {
            assertNoErrors()
            assertComplete()
            assertValue(storyEntity.id)
        }

        testDataObject.isLoaded().test().apply {
            assertNoErrors()
            assertValue(true)
            assertComplete()
        }

        assertTrue(testDataObject.isLoaded(storyEntity.id))

        testObject.deleteStory()

        testDataObject.isLoaded().test().apply {
            assertNoErrors()
            assertValue(false)
            assertComplete()
        }

        assertFalse(testDataObject.isLoaded(storyEntity.id))
    }

    @Test
    fun stories() {
        val testSubscriber = testDataObject.storyList().test()

        testSubscriber.apply {
            assertNoErrors()
            assertNotComplete()
            assertValue(listOf())
        }

        val storyEntity2 = Story.test(time = 2)
        testObject.insertStory(storyEntity2)

        val storyEntity1 = Story.test(time = 1)
        testObject.insertStory(storyEntity1)

        testSubscriber.apply {
            assertNoErrors()
            assertNotComplete()
            assertValues(listOf(), listOf(storyEntity2), listOf(storyEntity2, storyEntity1))
        }
    }

    @Test
    fun updateStory() {
        val testSubscriber = testDataObject.storyList().test()

        testSubscriber.apply {
            assertNoErrors()
            assertNotComplete()
            assertValue(listOf())
        }

        val storyEntity1 = Story.test()
        testDataObject.insert(storyEntity1).test().apply {
            assertNoErrors()
            assertComplete()
            assertValue(storyEntity1.id)
        }

        testSubscriber.apply {
            assertNoErrors()
            assertNotComplete()
            assertValues(listOf(), listOf(storyEntity1))
        }

        val updatedStory = Story.test(storyEntity1.id, url = "url-${Random.nextLong()}")

        testDataObject.update(updatedStory).test().apply {
            assertNoErrors()
            assertComplete()
            assertValue(1)
        }

        testSubscriber.apply {
            assertNoErrors()
            assertNotComplete()
            assertValues(listOf(), listOf(storyEntity1), listOf(updatedStory))
        }
    }
}