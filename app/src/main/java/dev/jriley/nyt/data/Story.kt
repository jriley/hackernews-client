package dev.jriley.nyt.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.jriley.nyt.StoryTypes


@Entity(tableName = story)
data class Story(
    @PrimaryKey(autoGenerate = false)
    val id: Long = -1,
    val by: String = "",
    val time: Long = -1,
    val url: String = "",
    val title: String = "",
    val storyTypes: Int = StoryTypes.NEW.ordinal
)
{
//    constructor(sr: StoryResponse): this(sr.id, sr.by, sr.time, sr.url, sr.title)
    constructor(story: Story, storyTypes: StoryTypes) : this(story.id, story.by, story.time, story.url,  story.title, storyTypes.ordinal)
    companion object
}

data class StoryResponse(val by: String,
                         val descendants: Long,
                         val id: Long,
                         val score: Long,
                         val time: Long,
                         val title: String,
                         val type :String,
                         val url:String
                         )