package com.ljz.nucleus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.ljz.nucleus.ui.theme.NucleusTheme

private lateinit var auth: FirebaseAuth

class Launcher : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        auth.currentUser?.uid

        setContent {
            NucleusTheme {
                ShowWelcomeUI()
            }
        }
    }
}

@Composable
fun ShowWelcomeUI() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val user = auth.currentUser
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        if(user != null) {
            if (!user.isEmailVerified) {
                val context = LocalContext.current
                val activity = (LocalContext.current as? Activity)
                context.startActivity(Intent(context, Login::class.java))
                activity?.finish()
            } else {
                val context = LocalContext.current
                val activity = (LocalContext.current as? Activity)
                context.startActivity(Intent(context, Home::class.java))
                activity?.finish()
            }
        } else {
            ConstraintLayout {
                val (welcomeText1, welcomeText2, loginButton) = createRefs()
                val context = LocalContext.current
                val activity = (LocalContext.current as? Activity)

                Text("Willkommen bei Nucleus!",
                    style = TextStyle(
                        textAlign = TextAlign.Left,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    modifier = Modifier
                        .constrainAs(welcomeText1) {
                            top.linkTo(parent.top, margin = 15.dp)
                            absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                            absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                            width = Dimension.fillToConstraints
                        }
                )

                Text(
                    "Unsere Social-Media-Plattform ist darauf ausgelegt, dass du dich sicher und wohl fühlen kannst, während du deine Beiträge erstellst und mit Freunden teilst." +
                            "\nWir legen besonderen Wert auf Datenschutz und Individualität und bieten ein einzigartiges Erlebnis, das auf deine Interessen und Vorlieben zugeschnitten ist.",
                    style = TextStyle(
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.constrainAs(welcomeText2) {
                        top.linkTo(welcomeText1.bottom, margin = 10.dp)
                        absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                        absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                        width = Dimension.fillToConstraints
                    }
                )

                Button(
                    onClick = {
                        context.startActivity(Intent(context, Login::class.java))
                        activity?.finish()
                    },
                    modifier = Modifier.constrainAs(loginButton) {
                        absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                        absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                        bottom.linkTo(parent.bottom, margin = 15.dp)
                        width = Dimension.fillToConstraints
                    }
                ) {
                    Text(
                        "Anmelden",
                        style = TextStyle(fontSize = 15.sp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview(
) {
    NucleusTheme {
        ShowWelcomeUI()
    }
}