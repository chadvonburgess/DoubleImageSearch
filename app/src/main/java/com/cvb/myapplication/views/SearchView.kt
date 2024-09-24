package com.cvb.myapplication.views

//import com.cvb.myapplication.datastorage.ImageStorageImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


val HOST_CONST = "https://i.imgur.com/"

@Composable
fun SearchScreen(searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory)) {
    val queryText by searchViewModel.queryText.collectAsState()
    val images by searchViewModel.images.collectAsState()
    val isLoading by searchViewModel.isLoading.collectAsState()
    val errorMessage by searchViewModel.errorMessage.collectAsState()
    val isFullScreen by searchViewModel.isFullScreen.collectAsState()
    val selectedImage by searchViewModel.selectedImage.collectAsState()
    val selectedSort by searchViewModel.selectedSort.collectAsState()
    val selectedWindow by searchViewModel.selectedWindow.collectAsState()
    val selectedPage by searchViewModel.selectedSort.collectAsState()


    //obfuscate eventually
    val sortOptions = listOf("time", "viral", "top")
    val windowOptions = listOf("day", "week", "month", "year", "all")


    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = {
            searchViewModel.newSearch()
        }) {
            Icon(Icons.Filled.Add, "New Search")
        }
    }) {
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Image Gallery
            if (isFullScreen && selectedImage != null) {
                FullScreenImage(
                    image = selectedImage,
                    imageLoader = searchViewModel.getImageLoader(),
                    onClose = { searchViewModel.onFullScreenClosed() },
                    setFavorite = { searchViewModel.onFavoriteAdded(it) })
            } else if (images.isNotEmpty()) {
                ImageGallery(
                    images = images,
                    queryText,
                    searchViewModel::onImageClicked,
                    searchViewModel::onFavoriteAdded,
                    searchViewModel::onFavoriteRemoved,
                    searchViewModel::decrementPage,
                    searchViewModel::incrementPage,
                    searchViewModel.getImageLoader()
                )
            } else {
                // Entry Box (TextField)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Double Barrel Image Search", // Replace with your title
                        style = MaterialTheme.typography.headlineSmall, // Customize style as needed
                        modifier = Modifier
                            .padding(bottom = 80.dp)
                            .align(Alignment.CenterHorizontally)// Add padding below the title
                    )

                    OutlinedTextField(
                        value = queryText,
                        onValueChange = { newText -> searchViewModel.onQueryTextChange(newText) },
                        label = { Text("Enter Image to Search for") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sort:", style = MaterialTheme.typography.bodyMedium)
                        Selector(
                            selectedOption = selectedSort,
                            options = sortOptions,
                            onOptionSelected = { searchViewModel.setSortSelected(it) }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Window:", style = MaterialTheme.typography.bodyMedium)
                        Selector(
                            selectedOption = selectedWindow,
                            options = windowOptions,
                            onOptionSelected = { searchViewModel.setWindowSelected(it) }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Search Button
                    Button(
                        onClick = { searchViewModel.onSearchClick() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Search", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Error Message
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Loading Indicator
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }
        }
    }

}


