package dev.jriley.nyt

import android.content.Context
import androidx.room.Room
import dagger.Component
import dagger.Module
import dagger.Provides
import dev.jriley.nyt.data.AppDatabase
import dev.jriley.nyt.data.StoryData
import dev.jriley.nyt.data.StoryRepository
import dev.jriley.nyt.service.HackerNewsService
import dev.jriley.nyt.service.UpdateStoriesService
import dev.jriley.nyt.splash.SplashActivity
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ServiceModule::class, DatabaseModule::class, SchedulersModule::class])
interface AppComponent {
    fun injectStoryRepository(storyRepository: StoryRepository)
    fun inject(storyFragmentViewModel: StoryFragmentViewModel)
    fun inject(topFragment: TopFragment)
    fun inject(bestFragment: BestFragment)
    fun inject(newFragment: NewFragment)
    fun inject(splashActivity: SplashActivity)
    fun inject(updateStoriesService: UpdateStoriesService)
}

@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideContext() = context
}

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "hn3w5_db").build()

    @Provides
    @Singleton
    fun providesStoryData(appDatabase: AppDatabase): StoryData =
            appDatabase.storyEntityDao()
}

@Module
class SchedulersModule {

    @Provides
    @Named("io")
    fun provideIoScheduler() : Scheduler = Schedulers.io()

    @Provides
    @Named("computation")
    fun provideComputationScheduler() : Scheduler = Schedulers.computation()
}

@Module
class ServiceModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl("https://hacker-news.firebaseio.com")
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    fun provideHackerNewsService(retrofit: Retrofit): HackerNewsService =
        retrofit.create(HackerNewsService::class.java)

}
