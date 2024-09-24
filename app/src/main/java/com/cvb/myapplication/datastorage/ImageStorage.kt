package com.cvb.myapplication.datastorage

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.cvb.myapplication.api.ImgurRepoImage
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

const val SAVED_SEARCHES = "saved_searches"
const val SAVED_FAVORITES = "saved_favorites"

data class SavedSearches(val searches: MutableMap<String, List<ImgurRepoImage>> = mutableMapOf())
data class SavedFavorites(val favorites: MutableSet<ImgurRepoImage> = mutableSetOf())


class HistorySaverImpl(val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>): HistorySaver {
    val GSON = Gson()
    override suspend fun saveSearch(query: String, results: List<ImgurRepoImage>) {
        val key = stringPreferencesKey(SAVED_SEARCHES)
        dataStore.data.map { preferences ->
            preferences[key] ?: ""
        }?.map {
            try {
                GSON.fromJson(it, SavedSearches::class.java) ?: SavedSearches()
            } catch (e: Throwable) {
                Log.e("HistorySaverImpl", "Error parsing JSON", e)
                SavedSearches()
            }
        }?.map {
            it.searches[query] = results
            GSON.toJson(it)
        }?.collect {
            dataStore.edit { preferences ->
                preferences[key] = it
            }
        }
    }

    override suspend fun recallSearch(query: String): List<ImgurRepoImage> {
        val key = stringPreferencesKey(SAVED_SEARCHES)
        return dataStore.data.map { preferences ->
            preferences[key] ?: ""
        }?.map {
            try {
                GSON.fromJson(it, SavedSearches::class.java) ?: SavedSearches()
            } catch (e: Throwable) {
                Log.e("HistorySaverImpl", "Error parsing JSON", e)
                SavedSearches()
            }
        }?.map {
            it.searches[query] ?: emptyList()
        }?.first() ?: emptyList()
    }

    override suspend fun getSearches(): Set<String> {
        val key = stringPreferencesKey(SAVED_SEARCHES)
        return dataStore.data.map { preferences ->
            preferences[key] ?: ""
        }?.map {
            try {
                GSON.fromJson(it, SavedSearches::class.java) ?: SavedSearches()
            } catch (e: Throwable) {
                Log.e("HistorySaverImpl", "Error parsing JSON", e)
                SavedSearches()
            }
        }?.map { it.searches.keys }?.first() ?: emptySet()
    }

    override suspend fun getFavorites(): List<ImgurRepoImage> {
        val key = stringPreferencesKey(SAVED_FAVORITES)
        return dataStore.data.map { preferences ->
            preferences[key] ?: ""
        }?.map {
            try {
                GSON.fromJson(it, SavedFavorites::class.java) ?: SavedFavorites()
            } catch (e: Throwable) {
                Log.e("HistorySaverImpl", "Error parsing JSON", e)
                SavedFavorites()
            }
        }?.map {
            it.favorites.toList()
        }?.first() ?: emptyList()
    }

    override suspend fun addFavorite(image: ImgurRepoImage) {
        val key = stringPreferencesKey(SAVED_FAVORITES)
        dataStore.data.map { preferences ->
            preferences[key] ?: ""
        }?.map {
            try {
                GSON.fromJson(it, SavedFavorites::class.java) ?: SavedFavorites()
            } catch (e: Throwable) {
                Log.e("HistorySaverImpl", "Error parsing JSON", e)
                SavedFavorites()
            }
        }?.map {
            it.favorites.add(image)
            GSON.toJson(it)
        }?.collect {
            dataStore.edit { preferences ->
                preferences[key] = it
            }
        }
    }

    override suspend fun removeFavorite(image: ImgurRepoImage) {
        val key = stringPreferencesKey(SAVED_FAVORITES)
        dataStore.data.map { preferences ->
            preferences[key] ?: ""
        }?.map {
            try {
                GSON.fromJson(it, SavedFavorites::class.java) ?: SavedFavorites()
            } catch (e: Throwable) {
                Log.e("HistorySaverImpl", "Error parsing JSON", e)
                SavedFavorites()
            }
        }?.map {
            it.favorites.remove(image)
            GSON.toJson(it)
        }?.collect {
            dataStore.edit { preferences ->
                preferences[key] = it
            }
        }
    }

}

interface HistorySaver {
    // note could be further abstracted but we should probably start small
    suspend fun saveSearch(query: String,  results : List<ImgurRepoImage>)
    suspend fun recallSearch(query: String): List<ImgurRepoImage>
    suspend fun getSearches(): Set<String>
    suspend fun getFavorites() : List<ImgurRepoImage>
    suspend fun addFavorite(image :ImgurRepoImage)
    suspend fun removeFavorite(image :ImgurRepoImage)
}

