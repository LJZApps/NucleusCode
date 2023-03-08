package com.ljz.nucleus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ljz.nucleus.ui.theme.NucleusTheme

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NucleusTheme {
                showLoginUI()
            }
        }
    }
}

@Composable
fun showLoginUI() {
    androidx.compose.material3.Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
    ) {
        ConstraintLayout {
            val (welcomeText1, welcomeText2, loginButton) = createRefs()

            androidx.compose.material3.Text("Willkommen bei Nucleus!",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.constrainAs(welcomeText1) {
                    top.linkTo(parent.top, margin = 40.dp)
                    absoluteLeft.linkTo(parent.absoluteLeft, margin = 0.dp)
                    absoluteRight.linkTo(parent.absoluteRight, margin = 0.dp)
                })

            androidx.compose.material3.Text(
                "Nucleus ist das zentrale Element in einer Zelle und somit das Herzstück des biologischen Systems." +
                        "\nEbenso bildet das Nucleus in der App das zentrale Element, in dem die Nutzer sich vernetzen und kommunizieren.",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                ),
                modifier = Modifier.constrainAs(welcomeText2) {
                    top.linkTo(welcomeText1.bottom, margin = 5.dp)
                    absoluteLeft.linkTo(parent.absoluteLeft, margin = 5.dp)
                    absoluteRight.linkTo(parent.absoluteRight, margin = 5.dp)
                    width = Dimension.fillToConstraints
                }
            )

            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier.constrainAs(loginButton) {
//                    top.linkTo(text.bottom, 0.dp)
                    absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                    absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                    bottom.linkTo(parent.bottom, margin = 15.dp)
                    width = Dimension.fillToConstraints
                }
            ) {
                androidx.compose.material3.Text(
                    "Weiter zur Anmeldung",
                    style = TextStyle(fontSize = 15.sp)
                )
            }
        }
    }
}

@Composable
fun DefaultPreview2() {
    NucleusTheme {
        showLoginUI()
    }
}