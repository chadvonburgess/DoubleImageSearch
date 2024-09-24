package com.cvb.myapplication.application

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import coil.ImageLoader
import coil.disk.DiskCache
import coil.request.CachePolicy
import com.cvb.myapplication.api.ImgurAPI
import com.cvb.myapplication.api.ImgurRepository
import com.cvb.myapplication.api.ImgurRepositoryImpl
import com.cvb.myapplication.datastorage.HistorySaver
import com.cvb.myapplication.datastorage.HistorySaverImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.prefs.Preferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    internal fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build()
    }

    @Provides
    @Singleton
    internal fun getClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        getInterceptors().forEach {
            builder.addInterceptor(it)
        }
        return builder.build()
    }

    /**
     * Technically I think we can collectInto here and make this a set via hilt but overkill
     */
    private fun getInterceptors(): List<Interceptor> {
        return listOf(
//            HttpLoggingInterceptor(),
            Interceptor(){

                val request = it.request();
                Log.e("Image Viewer", request.toString())
                val response = it.proceed(request);
                Log.e("Image Viewer", response.peekBody(Long.MAX_VALUE).string())
                return@Interceptor response

            }
            // add auth interceptor eventually
        )
    }

    @Provides
    @Singleton
    internal fun provideApiService(retrofit: Retrofit): ImgurAPI {
        return retrofit.create(ImgurAPI::class.java)

    }

    @Provides
    @Singleton
    internal fun provideRepository(apiService: ImgurAPI): ImgurRepository {
        return ImgurRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    internal fun provideHistoryStorage(data: DataStore<androidx.datastore.preferences.core.Preferences>): HistorySaver {
        return HistorySaverImpl(data)

    }
    @Provides
    @Singleton
    internal fun provideImageLoader(isApplication: ISApplication): ImageLoader {
        // Set up a custom disk cache directory
        val cacheDir = File(isApplication.cacheDir, "image_cache")

        // Create the ImageLoader with disk cache enabled
        return ImageLoader.Builder(isApplication)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir)
                    .maxSizePercent(0.02) // 2% of the app's available storage
                    .build()
            }
            .memoryCachePolicy(CachePolicy.ENABLED) // Enable memory caching
            .diskCachePolicy(CachePolicy.ENABLED) // Enable disk caching
            .build()
    }
}