package com.cvb.myapplication.application

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.cvb.myapplication.api.ImgurAPI
import com.cvb.myapplication.api.ImgurRepository
import retrofit2.Retrofit
import androidx.datastore.preferences.core.Preferences
import coil.ImageLoader
import com.cvb.myapplication.datastorage.HistorySaver


interface ISApplicationComponentProvider {
    fun getRepo(): ImgurRepository
    fun getHistorySaver(): HistorySaver
    fun getImageLoader(): ImageLoader
    suspend fun getGenerativeContent(query: String): String
}

//@HiltAndroidApp
class ISApplication(): Application(), ISApplicationComponentProvider {
    internal val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "HISTORY_STORE")

    /*
        store objects here for now.
     */
    companion object {
        private val retrofit: Retrofit? by lazy {
            AppModule.provideRetrofit()
        }
        private val apiService: ImgurAPI? by lazy {
            AppModule.provideApiService(retrofit!!)
        }
        internal val repository: ImgurRepository? by lazy {
            AppModule.provideRepository(apiService!!)
        }

        private val generativeModel = GenerativeModel(
            modelName = "gemini-pro-vision",
            apiKey = "AIzaSyClBBi9AOAmi5qA0RB-a1irkjRlazZUTuc"//BuildConfig.apiKey
        )

        internal suspend fun generateForAny(any: String): String {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(any)
                    }
                )
                return response.text ?: "error: Response none"
            } catch (e: Exception) {
                return "error: Response"
            }
        }
    }



    override fun onCreate() {
        super.onCreate()
        //note I was going to inject here for time constraints I am scrubbing hilt for now.
        repository?.run {
            // quick init prelaunch
        }
    }

    override fun getRepo(): ImgurRepository {
        return repository!!
    }

    override fun getHistorySaver(): HistorySaver {
        return AppModule.provideHistoryStorage(dataStore) // we can make this a little more singleton above
    }
    override fun getImageLoader(): ImageLoader {
        return AppModule.provideImageLoader(this) // can make this singleton
    }

    suspend override fun getGenerativeContent(query: String): String {
        return generateForAny(query)
    }
}
