package dev.jriley.nyt.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.jriley.nyt.StoryTypes

@Entity(tableName = story)
data class Story(@PrimaryKey(autoGenerate = false)
                 val id: Long,
                 val by: String? = null,
                 val time: Long? = null,
                 val url: String? = null,
                 val title: String? = null,
                 val storyTypes: Int = StoryTypes.NEW.ordinal){
    companion object
}