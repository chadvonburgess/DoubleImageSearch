package com.cvb.myapplication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.ImageLoader
import coil.compose.AsyncImage
import com.cvb.myapplication.api.ImgurRepoImage

@Composable
fun Selector(selectedOption: String, options: List<String>, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { },
            readOnly = true,
            modifier = Modifier.clickable { expanded = true },
            trailingIcon = {
                Icon(Icons.Filled.ArrowDropDown, "Sort Options")
            },
            enabled = false // This was the missing
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun FullScreenImage(
    image: ImgurRepoImage?,
    imageLoader: ImageLoader,
    onClose: () -> Unit,
    setFavorite: (ImgurRepoImage) -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp, 150.dp)
        ) {
            AsyncImage(
                model = image?.link,
                contentDescription = "Full Screen Image",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onClose() }, // Close on click
                contentScale = ContentScale.FillBounds,
                imageLoader = imageLoader
            )
        }
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.00f))
                .fillMaxWidth() // Ensure the background fills the width
                .padding(48.dp), // Add some padding
            verticalAlignment = Alignment.CenterVertically, // Use CenterVertically for better alignment
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = { setFavorite(image!!) }) {
                Icon(Icons.Filled.FavoriteBorder, contentDescription = "Like", tint = Color.White)
            }

            IconButton(onClick = { /* Handle share action */ }) {
                Icon(Icons.Filled.Share, contentDescription = "Share", tint = Color.White)
            }
        }
    }
}

@Composable
fun ImageGallery(
    images: List<ImgurRepoImage>,
    searchTitle: String,
    onImageClicked: (ImgurRepoImage) -> Unit,
    onFavoriteAdded: (ImgurRepoImage) -> Unit,
    onFavoriteRemoved: (ImgurRepoImage) -> Unit,
    decrementPage: () -> Unit,
    incrementPage: () -> Unit,
    imageLoader: ImageLoader,
    ifShowNextArrows: Boolean = true
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) { // Use a Box

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 32.dp, 0.dp, 0.dp)
            ) {
                items(images) { image ->
                    ImageCard(imageLoader, image, onClick = {
                        onImageClicked(image)
                    }, setFavorite = {
                        onFavoriteAdded(image)
                    }, removeFavorite = {
                        onFavoriteRemoved(image)
                    })
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.33f)),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(

                    text = searchTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            if (ifShowNextArrows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.19f))
                        .align(Alignment.BottomCenter)
                        .padding(120.dp, 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,

                    ) {
                    IconButton(onClick = { decrementPage() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Previous Page")
                    }

                    IconButton(onClick = { incrementPage() }) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Next Page")
                    }
                }
            }
        }
    }
}

@Composable
fun ImageCard(
    imageLoader: ImageLoader,
    image: ImgurRepoImage,
    onClick: () -> Unit,
    setFavorite: (ImgurRepoImage) -> Unit,
    removeFavorite: (ImgurRepoImage) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column {
            // needs to be moved
            val find = toFind(image.type)
            val link = if (find.isNotEmpty()) {
                // note refactor
                val removed =
                    image.link.removeRange(image.link.length - find.length, image.link.length)
                val added = removed + "m" + find
                added
            } else {
                image.link
            }
            AsyncImage(
                model = link,
                contentDescription = "Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable { onClick.invoke() },
                contentScale = ContentScale.Crop,
                imageLoader = imageLoader
            )

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(onClick = { setFavorite(image) }) {
                    Icon(Icons.Filled.FavoriteBorder, contentDescription = "Like")
                }

                IconButton(onClick = { /* Handle share action */ }) {
                    Icon(Icons.Filled.Share, contentDescription = "Share")
                }
            }

        }
    }
}

fun toFind(type: String): String {
    return when (type) {
        "jpg" -> ".jpg"
        "jpeg" -> ".jpg"
        "png" -> ".png"
        "gif" -> ".gif"
        else -> ""
    }
}