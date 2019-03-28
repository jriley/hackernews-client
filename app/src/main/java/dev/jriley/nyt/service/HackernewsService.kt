package dev.jriley.nyt.service

import dev.jriley.nyt.StoryTypes
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HackerNewsService {

    @get:GET("v0/beststories.json")
    val best: Single<List<Long>>

    @get:GET("v0/topstories.json")
    val top: Single<List<Long>>

    @get:GET("v0/newstories.json")
    val new: Single<List<Long>>

    @GET("v0/item/{id}.json")
    fun getStory(@Path("id") item: String): Single<Response<Story>>
}

data class Story(val id: Long,
                 val by: String? = null,
                 val time: Long? = null,
                 val url: String? = null,
                 val title: String? = null,
                 val storyTypes: Int = StoryTypes.NEW.ordinal)
