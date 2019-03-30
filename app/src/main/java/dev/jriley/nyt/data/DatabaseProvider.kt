package dev.jriley.nyt.data

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Single
import timber.log.Timber

const val story = "story"

object DatabaseProvider {
    lateinit var dataBase: AppDatabase
}

@Database(entities = [Story::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storyEntityDao(): StoryDao
}

@Dao
abstract class StoryDao : StoryData {

    override fun isLoaded(): Single<Boolean> = Single.fromCallable { queryCount() > 0 }

    override fun insert(story: Story): Single<Long> =
            Single.fromCallable { insertStory(story).apply { Timber.tag("@").i("Insert complete : ${story.id}:${story.storyTypes} ") } }

    override fun update(story: Story): Single<Int> =
            Single.fromCallable { updateStory(story.id, story.by, story.time, story.url, story.title, story.storyTypes) }

    override fun isLoaded(id: Long): Boolean = getStoryCount(id) > 0

    override fun storyList(): Flowable<List<Story>> = observeStoryList()
            .scan { _, t2 -> Timber.tag("@@@").d("T2 with size ${t2.size}");t2 }


    @Query("SELECT * from $story")
    internal abstract fun getStory(): Story?

    @Query("SELECT *  FROM $story WHERE id=:id")
    internal abstract fun getStoryById(id: Long): Story?

    @Query("SELECT COUNT(*) from $story WHERE id=:id")
    internal abstract fun getStoryCount(id: Long): Int

    @Query("DELETE FROM $story")
    internal abstract fun deleteStory(): Int

    @Query("SELECT COUNT(*) FROM $story")
    internal abstract fun queryCount(): Int

    /*where shoppingListId=:shoppingListId*/
    @Query("SELECT * FROM $story  ORDER BY time DESC")
    abstract fun observeStoryList(): Flowable<List<Story>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertStory(storyEntity: Story): Long

    @Query("UPDATE $story SET `by` =:by, time =:time, url=:url,  title=:title,  storyTypes=:storyTypes WHERE id=:id")
    internal abstract fun updateStory(id: Long, by: String?, time: Long?, url: String?, title: String?, storyTypes: Int): Int

    @Query("DELETE FROM $story WHERE id=:id")
    internal abstract fun deleteStoryById(id: Long): Int
}