package com.ljz.nucleus

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ljz.nucleus.ui.theme.NucleusTheme

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setContent {
            NucleusTheme {
                ShowLoginUI()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowLoginUI() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ConstraintLayout {
            val (loginText1, loginText2, emailInput, nextButton) = createRefs()
            val emailInputTextState = remember { mutableStateOf(TextFieldValue()) }
            val focusRequester = FocusRequester()
            val keyboardController = LocalSoftwareKeyboardController.current

            Text("Lass uns beginnen...",
                style = TextStyle(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.constrainAs(loginText1) {
                    top.linkTo(parent.top, margin = 15.dp)
                    absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                    absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                    width = Dimension.fillToConstraints
                }
            )

            Text(
                "Wir checken erstmal, ob deine E-Mail-Adresse bei uns schon eingetragen ist.",
                modifier = Modifier.constrainAs(loginText2) {
                    top.linkTo(loginText1.bottom, 5.dp)
                    absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                    absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                    width = Dimension.fillToConstraints
                }
            )

            OutlinedTextField(
                value = emailInputTextState.value,
                onValueChange = { emailInputTextState.value = it },
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .constrainAs(emailInput) {
                        top.linkTo(loginText2.bottom, 15.dp)
                        absoluteLeft.linkTo(parent.absoluteLeft, 15.dp)
                        absoluteRight.linkTo(parent.absoluteRight, 15.dp)
                        width = Dimension.fillToConstraints
                    }
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            keyboardController?.show()
                        }
                    },
                label = { Text("E-Mail") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            DisposableEffect(Unit) {
                focusRequester.requestFocus()
                onDispose { }
            }

            Button(
                onClick = {
                    checkText(emailInputTextState.value.text)
                },
                modifier = Modifier.constrainAs(nextButton) {
                    absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                    absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                    bottom.linkTo(parent.bottom, margin = 15.dp)
                    width = Dimension.fillToConstraints
                }
            ) {
                Text(
                    "Weiter",
                    style = TextStyle(fontSize = 15.sp)
                )
            }
        }
    }
}

fun checkText(emailText: String) {
    val regex = ""
    TODO("Add this shit")
}

@Preview
@Composable
fun DefaultPreview2() {
    NucleusTheme {
        ShowLoginUI()
    }
}