package dev.jriley.nyt

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import dev.jriley.nyt.data.AppDatabase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.util.*

abstract class DaoTest<T> {
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    protected val random = Random()
    protected lateinit var database: AppDatabase
    protected val testObject : T by lazy { createDao() }

    @Before
    open fun setUp() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext, AppDatabase::class.java).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    abstract fun createDao() : T
}