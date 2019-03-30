import dev.jriley.nyt.StoryTypes
import dev.jriley.nyt.data.Story
import kotlin.random.Random

private const val reasonableBoundSize = 10

fun Story.Companion.test(id: Long = Random.nextLong(),
                         by: String = "by-${Random.nextInt(reasonableBoundSize)}",
                         time: Long = Random.nextLong(),
                         url: String = "url-${Random.nextInt(reasonableBoundSize)}",
                         title: String = "title-${Random.nextInt(reasonableBoundSize)}",
                         storyTypes: StoryTypes = StoryTypes.NEW): Story {
    return Story(id, by, time, url, title, storyTypes.ordinal)
}