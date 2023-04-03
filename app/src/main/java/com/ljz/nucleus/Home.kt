package com.ljz.nucleus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ljz.nucleus.ui.theme.NucleusTheme

class Home : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NucleusTheme {
                // A surface container using the 'background' color from the theme
                ShowHomeFeed()
            }
        }
    }
}

@Composable
fun ShowHomeFeed() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(text = "Dies ist ein Platzhalter-Text.\nHier wird der Home-Bildschirm angezeigt.")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    NucleusTheme {
        ShowHomeFeed()
    }
}