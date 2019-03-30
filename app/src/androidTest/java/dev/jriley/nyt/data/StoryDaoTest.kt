package dev.jriley.nyt.data

import dev.jriley.nyt.DaoTest
import org.junit.Assert.assertEquals
import org.junit.Test
import test

class StoryDaoTest : DaoTest<StoryDao>() {
    override fun createDao(): StoryDao = database.storyEntityDao()

    @Test
    fun storyDatabaseCount() {
        val story1 = Story.test()
        val story2 = Story.test()
        val story3 = Story.test()

        assertEquals(0, testObject.queryCount())
        assertEquals(story1.id, testObject.insertStory(story1))
        assertEquals(1, testObject.queryCount())

        assertEquals(story2.id, testObject.insertStory(story2))
        assertEquals(story3.id, testObject.insertStory(story3))
        assertEquals(3, testObject.queryCount())
        assertEquals(1, testObject.getStoryCount(story2.id))
        assertEquals(1, testObject.getStoryCount(story3.id))

        assertEquals(1, testObject.deleteStoryById(story1.id))
        assertEquals(2, testObject.queryCount())

        assertEquals(2, testObject.deleteStory())
        assertEquals(0, testObject.queryCount())
    }

    @Test
    fun observableStoryListOutOfDatabase() {
        val story1 = Story.test()

        val testSubscriber = testObject.observeStoryList().test()
        testSubscriber.apply {
            assertNoErrors()
            assertValue(listOf())
            assertNotComplete()
        }

        testObject.insertStory(story1)

        testSubscriber.apply {
            assertNoErrors()
            assertValues(listOf(), listOf(story1))
            assertNotComplete()
        }

    }

    @Test
    fun observableStoryListOutOfDatabase_1() {
        val story1 = Story.test(time = 1)
        val story2 = Story.test(time = 2)
        val story3 = Story.test(time = 3)

        val testSubscriber = testObject.observeStoryList().test()
        testSubscriber.apply {
            assertNoErrors()
            assertValue(listOf())
            assertNotComplete()
        }

        testObject.insertStory(story1)

        testSubscriber.apply {
            assertNoErrors()
            assertValues(listOf(), listOf(story1))
            assertNotComplete()
        }

        testObject.insertStory(story2)
        testObject.insertStory(story3)

        testSubscriber.apply {
            assertNoErrors()
            assertValues(listOf(), listOf(story1), listOf( story2, story1), listOf(story3, story2, story1))
            assertNotComplete()
        }

    }

    @Test
    fun updateStory() {
        val story1 = Story.test()

        val testSubscriber = testObject.observeStoryList().test()
        testSubscriber.apply {
            assertNoErrors()
            assertValue(listOf())
            assertNotComplete()
        }

        testObject.insertStory(story1)

        testSubscriber.apply {
            assertNoErrors()
            assertValues(listOf(), listOf(story1))
            assertNotComplete()
        }

        val updatedStory = Story.test(id = story1.id)

        assertEquals(1, testObject.updateStory(updatedStory.id, updatedStory.by, updatedStory.time!!, updatedStory.url,  updatedStory.title,  updatedStory.storyTypes))

        testSubscriber.apply {
            assertNoErrors()
            assertValues(listOf(), listOf(story1), listOf(updatedStory))
            assertNotComplete()
        }

        assertEquals(updatedStory, testObject.getStoryById(story1.id))
    }
}