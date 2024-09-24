package com.cvb.myapplication.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.ImageLoader
import coil.imageLoader
import com.cvb.myapplication.api.ImgurRepoImage
import com.cvb.myapplication.api.ImgurRepository
import com.cvb.myapplication.application.ISApplication
import com.cvb.myapplication.datastorage.HistorySaver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    internal val repository: ImgurRepository,
    internal val historySaver: HistorySaver,
    internal val imageLoader: ImageLoader
) :
    ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ISApplication)
                val searchRepo = application.getRepo()
                val historySaver = application.getHistorySaver()
                val imageLoader = application.getImageLoader()
                SearchViewModel(searchRepo, historySaver, imageLoader)
            }
        }
    }
    private val _queryText = MutableStateFlow("")
    val queryText: StateFlow<String> = _queryText.asStateFlow()

    private val _images = MutableStateFlow<List<ImgurRepoImage>>(emptyList())
    val images: StateFlow<List<ImgurRepoImage>> = _images.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _isFullScreen = MutableStateFlow(false)
    val isFullScreen: StateFlow<Boolean> = _isFullScreen.asStateFlow()

    private val _selectedImage = MutableStateFlow<ImgurRepoImage?>(null)
    val selectedImage: StateFlow<ImgurRepoImage?> = _selectedImage.asStateFlow()

    private val _selectedSort = MutableStateFlow<String>("time")
    val selectedSort: StateFlow<String> = _selectedSort.asStateFlow()

    private val _selectedWindow = MutableStateFlow<String>("all")
    val selectedWindow: StateFlow<String> = _selectedWindow.asStateFlow()

    private val _selectedPage = MutableStateFlow<Int>(1)
    val selectedPage: StateFlow<Int> = _selectedPage.asStateFlow()

    fun onQueryTextChange(newText: String) {
        _queryText.value = newText
    }

    fun onSearchClick() {
        val query = _queryText.value.trim()
        if (query.isEmpty()) {
            _errorMessage.value = "Please enter a search query."
            return
        }
        searchImages(query)
    }

    private fun searchImages(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            try {
                val response = repository?.getImageList(
                    query,
                    _selectedSort.value,
                    _selectedWindow.value,
                    _selectedPage.value
                )
                response?.collect({
                    it
                    _images.value = it
                    historySaver.saveSearch(query, _images.value)
                })
            } catch (e: Exception) {
                _errorMessage.value = "An error occurred: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onFullScreenClosed() {
        _isFullScreen.value = false
        _selectedImage.value = null
    }

    fun onImageClicked(image: ImgurRepoImage) {
        _isFullScreen.value = true
        _selectedImage.value = image
    }

    fun newSearch() {
        _isFullScreen.value = false
        _selectedImage.value = null
        _images.value = emptyList()
        _errorMessage.value = ""
        _isLoading.value = false
        _selectedSort.value = "time"
        _selectedWindow.value = "all"
        _selectedPage.value = 1
    }

    fun decrementPage() {
        if (_selectedPage.value == 1) return
        _selectedPage.value -= 1
        searchImages(_queryText.value)
    }

    fun incrementPage() {
        _selectedPage.value += 1
        searchImages(_queryText.value)
    }

    fun setSortSelected(it: String) {
        _selectedSort.value = it
    }

    fun setWindowSelected(it: String) {
        _selectedWindow.value = it
    }

    fun onFavoriteAdded(image: ImgurRepoImage) {
        viewModelScope.launch {
            historySaver.addFavorite(image)
        }
    }

    fun onFavoriteRemoved(image: ImgurRepoImage) {
        viewModelScope.launch {
            historySaver.removeFavorite(image)
        }
    }

    fun getImageLoader(): ImageLoader = imageLoader
}