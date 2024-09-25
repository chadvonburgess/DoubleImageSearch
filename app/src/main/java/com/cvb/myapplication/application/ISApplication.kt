package com.cvb.myapplication.application

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
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
}
