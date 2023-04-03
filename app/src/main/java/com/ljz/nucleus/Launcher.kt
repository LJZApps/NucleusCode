package com.ljz.nucleus

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.ljz.nucleus.database.UserDBHelper
import com.ljz.nucleus.ui.theme.NucleusTheme

private lateinit var auth: FirebaseAuth

class Launcher : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        auth.currentUser?.uid

        askNotificationPermission()

        setContent {
            NucleusTheme {
                ShowWelcomeUI()
            }
        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
        val context = LocalContext.current
        val userDB = UserDBHelper(context, null)
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        if(user != null) {
            if (userDB.isRegisterFinished(user.uid)) {
                val activity = (LocalContext.current as? Activity)
                context.startActivity(Intent(context, Home::class.java))
                activity?.finish()
            } else {

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
                    "Unsere Social-Media-Plattform ist darauf ausgelegt, dass du dich sicher und wohl fühlen kannst, während du durch Beiträge scrollst und mit deinen Freunden chattest." +
                            "\nWir legen besonderen Wert auf Datenschutz und Individualität und bieten ein einzigartiges Erlebnis, das auf deine Interessen und Vorlieben zugeschnitten ist, ohne das deine Daten an Dritte weitergegeben werden.",
                    style = TextStyle(
                        fontSize = 18.sp
                    ),
                    textAlign = TextAlign.Start,
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