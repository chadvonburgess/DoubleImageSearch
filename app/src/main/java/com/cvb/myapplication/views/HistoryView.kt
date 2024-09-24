package com.cvb.myapplication.views

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.ImageLoader
import com.cvb.myapplication.CenterText
import com.cvb.myapplication.api.ImgurRepoImage
import com.cvb.myapplication.application.ISApplication
import com.cvb.myapplication.datastorage.HistorySaver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory)) {
    val images by viewModel.images.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isFullScreen by viewModel.fullScreen.collectAsState()
    viewModel.checkHistory()

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = {
            viewModel.back()
        }) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "New Search")
        }
    }) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            if (isFullScreen != null) {
                FullScreenImage(
                    image = isFullScreen,
                    imageLoader = viewModel.imageLoader(),
                    onClose = { viewModel.back() },
                    setFavorite = { viewModel.updateFavorite(it) }
                )
            } else {
                if (images.isNotEmpty()) {
                    ImageGallery(
                        images,
                        "Favorites",
                        viewModel::onImageClicked,
                        viewModel::updateFavorite,
                        viewModel::removeFavorite,
                        viewModel::decrementPage,
                        viewModel::incrementPage,
                        viewModel.imageLoader(),
                        false
                    )
                } else {
                    CenterText(text = "No Favorites Yet")
                }
            }
        }
    }
}

class HistoryViewModel(
    internal val historySaver: HistorySaver,
    internal val imageLoader: ImageLoader
) : ViewModel() {

    private val _images = MutableStateFlow<List<ImgurRepoImage>>(emptyList())
    val images = _images.asStateFlow()
    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading = _isLoading.asStateFlow()
    private val _fullScreen = MutableStateFlow<ImgurRepoImage?>(null)
    val fullScreen = _fullScreen.asStateFlow()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ISApplication)
                val historySaver = application.getHistorySaver()
                HistoryViewModel(historySaver, application.getImageLoader())
            }
        }
    }

    init {
        checkHistory()
    }

    internal fun checkHistory() {
        viewModelScope.launch {
            val imagesToSet = historySaver.getFavorites()
            _images.value = imagesToSet
            _isLoading.value = false
        }
    }

    fun back() {
        _fullScreen.value = null
    }

    fun onImageClicked(imgurRepoImage: ImgurRepoImage) {
        _fullScreen.value = imgurRepoImage
    }

    fun updateFavorite(imgurRepoImage: ImgurRepoImage) {
        viewModelScope.launch {
            historySaver.addFavorite(imgurRepoImage)
        }
        Log.e("History", "add Favorite ${imgurRepoImage.toString()}")

    }

    fun removeFavorite(imgurRepoImage: ImgurRepoImage) {
        viewModelScope.launch {
            historySaver.removeFavorite(imgurRepoImage)
        }
        Log.e("History", "remove Favorite ${imgurRepoImage.toString()}")
    }

    fun imageLoader(): ImageLoader = imageLoader

    fun decrementPage() {

        // do nothing

    }

    fun incrementPage() {
        // do nothing
    }

}