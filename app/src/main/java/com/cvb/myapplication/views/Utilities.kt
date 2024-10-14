package com.cvb.myapplication.views

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


@Composable
fun isSystemInDarkTheme(): Boolean {
    val context = LocalContext.current
    val uiMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return uiMode == Configuration.UI_MODE_NIGHT_YES
}