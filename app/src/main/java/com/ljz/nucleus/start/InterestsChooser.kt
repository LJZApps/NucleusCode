package com.ljz.nucleus.start

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ljz.nucleus.ui.theme.NucleusTheme

class InterestsChooser : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NucleusTheme {
                InterestChooserMain()
            }
        }
    }
}

@Composable
fun InterestChooserMain() {

}

@Preview(showBackground = true)
@Composable
fun InterestChooserMainPreview() {
    NucleusTheme {
        InterestChooserMain()
    }
}