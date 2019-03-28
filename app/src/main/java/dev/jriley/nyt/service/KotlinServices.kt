package dev.jriley.nyt.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object KotlinServices {
    private object Holder {
        val OK_HTTP_INSTANCE: OkHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

        val HACKERNEWS_SERVICE_INSTANCE: HackerNewsService =
            Retrofit.Builder().baseUrl("https://hacker-news.firebaseio.com")
                .client(OK_HTTP_INSTANCE)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(HackerNewsService::class.java)
    }

    val hackerNewsService: HackerNewsService by lazy { Holder.HACKERNEWS_SERVICE_INSTANCE }
}
