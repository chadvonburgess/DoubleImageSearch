package com.cvb.myapplication.views

import androidx.compose.animation.core.copy
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = "id:pixel_8")
@Composable
fun providefeedScreen() {
    val navController = rememberNavController()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Screen") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "info")
                    }
                }
            )
        }
    ) { innerPadding ->

        // Your screen content here, using innerPadding if needed
        Column( // Use Column instead of Box
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .verticalScroll(scrollState), // Make the Column scrollable
            verticalArrangement = Arrangement.spacedBy(16.dp) // Add spacing
        ) {

            MyCard(
                "Get Right",
                {},
                Icons.Filled.Edit,
                Icons.Filled.AccountBox,
                "Get right by clicking this button",
                {}
            )

            TwoCardRow(
                "Get Double Right",
                {},
                Icons.Filled.Star,
                Icons.Filled.Star,
                "star 1 setting",
                "star 2 setting"
            ) { }
//            Spacer(modifier = Modifier.height(12.dp))
            MyCard(
                "Get Rounded",
                {},
                Icons.Filled.Info,
                Icons.Filled.AccountCircle,
                "Get Round by clicking this button",
                {}
            )
//            Spacer(modifier = Modifier.height(12.dp))
            MyCard(
                "Get Square",
                {},
                Icons.Filled.Info,
                Icons.Filled.Star,
                "Get Square by clicking this button",
                {}
            )
            // Your content
        }
    }

}

@Composable
fun MyCard(
    title: String,
    onIconClick: () -> Unit,
    icon: ImageVector,
    largeIcon: ImageVector,
    centeredText: String,
    onMoreClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFF8F8F8))
            .shadow(4.dp, RoundedCornerShape(8.dp)), // Shadow added
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onIconClick) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))

            Box(modifier = Modifier.fillMaxWidth()) { // Box for "More" button alignment
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = largeIcon,
                        contentDescription = "Large Icon",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = centeredText,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // "More" button aligned to bottom end
                TextButton(
                    onClick = onMoreClick,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text("More", style = MaterialTheme.typography.bodySmall)
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "More Icon",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TwoCardRow(
    title: String,
    onIconClick: () -> Unit,
    icon1: ImageVector,
    icon2: ImageVector,
    description1: String,
    description2: String,
    onMoreClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFF8F8F8))
            .shadow(4.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column {
            // Title area (can be removed if not needed)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onIconClick) {
                    Icon(
                        imageVector = icon1, // Using icon1 here
                        contentDescription = "Icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))

            // Two icons with descriptions and divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround // Evenly spaced
            ) {
                IconWithDescription(icon1, description1) // First icon and description

                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )

                IconWithDescription(icon2, description2) // Second icon and description
            }

            // "More" button (can be removed if not needed)
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            TextButton(
                onClick = onMoreClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
            ) {
                Text("More", style = MaterialTheme.typography.bodySmall)
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "More Icon",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun IconWithDescription(icon: ImageVector, description: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null, // You might want to provide a description
            modifier = Modifier.size(48.dp).padding(12.dp, 0.dp, 0.dp, 0.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = description, style = MaterialTheme.typography.bodyMedium)
    }
}