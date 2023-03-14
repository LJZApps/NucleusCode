package com.ljz.nucleus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.sharp.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ljz.nucleus.ui.theme.NucleusTheme
import java.util.regex.Pattern


private lateinit var auth: FirebaseAuth
private val PASSWORD_PATTERN: String =
    "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;.',?/*~$^+=<>]).{8,20}$"

class Login : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        auth = Firebase.auth

        setContent {
            NucleusTheme {
                val navController = rememberNavController()
                RegisterNavHost(navController = navController)
            }
        }
    }

}

@Composable
fun RegisterNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "checkEmail"
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable("checkEmail") {
            RegisterEmailCheck(
                navController = navController
            )
        }
        composable(
            "registerWithEmail?email={email}",
            arguments = listOf(navArgument("email") { defaultValue = "null" })
        ) { backStateEntry ->
            backStateEntry.arguments?.getString("email")?.let {
                RegisterHome(
                    navToEmailCheck = { navController.navigate("checkEmail") },
                    navController = navController,
                    email = it
                )
            }

            BackHandler(true) {
                // nothing
            }
        }
        composable(
            "loginWithEmail?email={email}&fromRegister={fromRegister}",
            arguments = listOf(
                navArgument("email") { defaultValue = "null" },
                navArgument("fromRegister") { defaultValue = false }
            )
        ) { backStateEntry ->
            backStateEntry.arguments?.getString("email")?.let {
                LoginWithEmail(
                    navToEmailCheck = { navController.navigate("checkEmail") },
                    navController = navController,
                    email = it,
                    fromRegister = backStateEntry.arguments?.getBoolean("fromRegister")
                )
            }

            BackHandler(true) {
                // nothing
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterEmailCheck(
    navController: NavHostController
) {
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
            val context = LocalContext.current
            val focusManager = LocalFocusManager.current
            var nextButtonEnabled by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf("") }
            var isError by rememberSaveable { mutableStateOf(false) }

            fun validateEmail(text: String) {
                if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                    isError = true
                    errorMessage = "E-Mail-Adresse nicht gültig"
                } else {
                    isError = false
                }

                nextButtonEnabled = !isError
            }

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
                "Wir checken erstmal, ob deine E-Mail-Adresse bei uns schon registriert ist.",
                modifier = Modifier.constrainAs(loginText2) {
                    top.linkTo(loginText1.bottom, 5.dp)
                    absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                    absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                    width = Dimension.fillToConstraints
                }
            )

            OutlinedTextField(
                value = emailInputTextState.value,
                onValueChange = {
                    emailInputTextState.value = it
                    validateEmail(emailInputTextState.value.text)
                },
                shape = RoundedCornerShape(15.dp),
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    if (isError) {
                        Icon(Icons.Filled.Info, "error", tint = MaterialTheme.colorScheme.error)
                    }
                },
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
                    }
                    .onKeyEvent { event ->
                        when (event.key) {
                            Key.Enter -> {
                                if (nextButtonEnabled) {
                                    nextButtonEnabled = false
                                    auth
                                        .fetchSignInMethodsForEmail(emailInputTextState.value.text)
                                        .addOnCompleteListener { task ->
                                            nextButtonEnabled = true
                                            val isNewUser = task.result.signInMethods?.isEmpty()
                                            if (isNewUser == true) {
                                                navController.navigate("registerWithEmail?email=${emailInputTextState.value.text}")
                                            } else {
                                                navController.navigate("loginWithEmail?email=${emailInputTextState.value.text}")
                                            }
                                        }
                                }
                                true
                            }
                            else -> false
                        }
                    },
                label = { Text("E-Mail") },
                singleLine = true,
                keyboardActions = KeyboardActions(
                    // Handle done, next,... buttons on keyboard
                    onNext = {
                        if (nextButtonEnabled) {
                            nextButtonEnabled = false
                            auth.fetchSignInMethodsForEmail(emailInputTextState.value.text)
                                .addOnCompleteListener { task ->
                                    nextButtonEnabled = true
                                    val isNewUser = task.result.signInMethods?.isEmpty()
                                    if (isNewUser == true) {
                                        navController.navigate("registerWithEmail?email=${emailInputTextState.value.text}")
                                    } else {
                                        navController.navigate("loginWithEmail?email=${emailInputTextState.value.text}")
                                    }
                                }
                        }
                    }
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            DisposableEffect(Unit) {
                focusRequester.requestFocus()
                onDispose { }
            }

            Button(
                onClick = {
                    nextButtonEnabled = false
                    auth.fetchSignInMethodsForEmail(emailInputTextState.value.text)
                        .addOnCompleteListener { task ->
                            val isNewUser = task.result.signInMethods?.isEmpty()
                            nextButtonEnabled = true
                            if (isNewUser == true) {
                                navController.navigate("registerWithEmail?email=${emailInputTextState.value.text}")
                                //Log.e("TAG", "Is New User!")
                            } else {
                                navController.navigate("loginWithEmail?email=${emailInputTextState.value.text}")
                                //Log.e("TAG", "Is Old User!")
                            }
                        }
                },
                enabled = nextButtonEnabled,
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


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterHome(
    navToEmailCheck: () -> Unit,
    navController: NavHostController,
    email: String? = "null"
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (email == "null") {
                Text(text = "No email specified.")
            } else {
                ConstraintLayout() {
                    val (loginText1, loginText2, emailInput, passwordInput, confirmPasswordInput, nextButton, backButton) = createRefs()
                    val passwordInputState = remember { mutableStateOf(TextFieldValue()) }
                    val passwordConfirmInputState = remember { mutableStateOf(TextFieldValue()) }
                    val focusRequester = FocusRequester()
                    val focusForConfirm = FocusRequester()
                    val keyboardController = LocalSoftwareKeyboardController.current
                    val context = LocalContext.current
                    var nextButtonEnabled by remember { mutableStateOf(false) }
                    var backButtonEnabled by remember { mutableStateOf(true) }
                    val activity = (LocalContext.current as? Activity)
                    var passwordVisible: Boolean by rememberSaveable { mutableStateOf(false) }
                    var passwordConfirmationVisible: Boolean by rememberSaveable {
                        mutableStateOf(
                            false
                        )
                    }
                    var errorMessageFirstPassword: String by remember { mutableStateOf("") }
                    var isErrorFirstPassword by rememberSaveable { mutableStateOf(false) }
                    var errorMessageConfirmPassword: String by remember { mutableStateOf("") }
                    var isErrorConfirmPassword by rememberSaveable { mutableStateOf(false) }
                    val pattern = Pattern.compile(PASSWORD_PATTERN)
                    var openDialog = remember { mutableStateOf(false) }

                    fun validateFirstPassword(password: String) {
                        val matcher = pattern.matcher(password.trim())

                        if (matcher.matches()) {
                            isErrorFirstPassword = false
                        } else {
                            isErrorFirstPassword = true
                            errorMessageFirstPassword = "Passwort ist nicht gültig."
                        }
                    }

                    fun validateSecondPassword(password: String, firstPassword: String) {
                        if (password == firstPassword) {
                            isErrorConfirmPassword = false
                        } else {
                            isErrorConfirmPassword = true
                            errorMessageConfirmPassword = "Passwörter stimmt nicht überein"
                        }
                    }

                    fun sendEmailVerification() {
                        //get instance of firebase auth
                        val firebaseAuth = FirebaseAuth.getInstance()
                        //get current user
                        val firebaseUser = firebaseAuth.currentUser

                        //send email verification
                        firebaseUser!!.sendEmailVerification()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Instructions Sent...", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Failed to send due to " + e.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    }

                    fun validateButton() {
                        nextButtonEnabled = !isErrorFirstPassword && !isErrorConfirmPassword
                    }

                    fun signUpUser(email: String, password: String) {
                        backButtonEnabled = false
                        nextButtonEnabled = false
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(context, "Account created!", Toast.LENGTH_SHORT)
                                        .show()
                                    sendEmailVerification()

                                    navController.navigate("loginWithEmail?email=${email}&fromRegister=${true}")
                                } else {
                                    nextButtonEnabled = true
                                    backButtonEnabled = true
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(
                                        context,
                                        task.exception?.localizedMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }

                    Text("Registrieren",
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
                        "Diese E-Mail-Adresse ist bei uns noch nicht registriert.\nLass uns das ändern!",
                        modifier = Modifier.constrainAs(loginText2) {
                            top.linkTo(loginText1.bottom, 5.dp)
                            absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                            absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                            width = Dimension.fillToConstraints
                        }
                    )

                    OutlinedTextField(
                        value = email!!,
                        enabled = false,
                        onValueChange = { },
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .constrainAs(emailInput) {
                                top.linkTo(loginText2.bottom, 15.dp)
                                absoluteLeft.linkTo(parent.absoluteLeft, 15.dp)
                                absoluteRight.linkTo(parent.absoluteRight, 15.dp)
                                width = Dimension.fillToConstraints
                            },
                        label = { Text("E-Mail") },
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = passwordInputState.value,
                        onValueChange = {
                            passwordInputState.value = it
                            validateFirstPassword(it.text)
                            validateSecondPassword(passwordConfirmInputState.value.text, it.text)
                            validateButton()
                        },
                        shape = RoundedCornerShape(15.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .constrainAs(passwordInput) {
                                top.linkTo(emailInput.bottom, 10.dp)
                                absoluteLeft.linkTo(parent.absoluteLeft, 15.dp)
                                absoluteRight.linkTo(parent.absoluteRight, 15.dp)
                                width = Dimension.fillToConstraints
                            }
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    keyboardController?.show()
                                }
                            }
                            .onKeyEvent { event ->
                                when (event.key) {
                                    Key.Enter -> {
                                        signUpUser(email, passwordInputState.value.text)
                                        true
                                    }
                                    else -> false
                                }
                            },
                        label = { Text("Passwort") },
                        singleLine = true,
                        isError = isErrorFirstPassword,
                        keyboardActions = KeyboardActions(
                            // Handle done, next,... buttons on keyboard
                            onNext = {
                                if (nextButtonEnabled && !isErrorFirstPassword) {
                                    focusForConfirm.requestFocus()
                                }
                            }
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            // Please provide localized description for accessibility services
                            val description =
                                if (passwordVisible) "Hide password" else "Show password"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, description)
                            }
                        },
                        supportingText = {
                            if (isErrorFirstPassword) {
                                Text(
                                    text = errorMessageFirstPassword,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        trailingIcon = {
                            if (isErrorFirstPassword) {
                                IconButton(
                                    onClick = { openDialog.value = true },
                                ) {
                                    Icon(
                                        Icons.Sharp.Help,
                                        "Password help",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                                /*
                                Icon(
                                    Icons.Filled.Info,
                                    "error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                                 */
                            }
                        },
                    )

                    OutlinedTextField(
                        value = passwordConfirmInputState.value,
                        onValueChange = {
                            passwordConfirmInputState.value = it
                            validateSecondPassword(it.text, passwordInputState.value.text)
                            validateButton()
                        },
                        visualTransformation = if (passwordConfirmationVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(15.dp),
                        isError = isErrorConfirmPassword,
                        modifier = Modifier
                            .constrainAs(confirmPasswordInput) {
                                top.linkTo(passwordInput.bottom, 10.dp)
                                absoluteLeft.linkTo(parent.absoluteLeft, 15.dp)
                                absoluteRight.linkTo(parent.absoluteRight, 15.dp)
                                width = Dimension.fillToConstraints
                            }
                            .focusRequester(focusForConfirm)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    keyboardController?.show()
                                }
                            },
                        label = { Text("Passwort bestätigen") },
                        singleLine = true,
                        keyboardActions = KeyboardActions(
                            // Handle done, next,... buttons on keyboard
                            onDone = {
                                if (nextButtonEnabled) {
                                    nextButtonEnabled = false
                                    signUpUser(email, passwordConfirmInputState.value.text)
                                }
                            }
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        leadingIcon = {
                            val image = if (passwordConfirmationVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            // Please provide localized description for accessibility services
                            val description =
                                if (passwordConfirmationVisible) "Hide password" else "Show password"

                            IconButton(onClick = {
                                passwordConfirmationVisible = !passwordConfirmationVisible
                            }) {
                                Icon(imageVector = image, description)
                            }
                        },
                        supportingText = {
                            if (isErrorConfirmPassword) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = errorMessageConfirmPassword,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        trailingIcon = {
                            if (isErrorConfirmPassword) {
                                Icon(
                                    Icons.Filled.Info,
                                    "error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                    )

                    DisposableEffect(Unit) {
                        focusRequester.requestFocus()
                        onDispose { }
                    }

                    Button(
                        onClick = {
                            nextButtonEnabled = false
                            signUpUser(email, passwordConfirmInputState.value.text)
                        },
                        enabled = nextButtonEnabled,
                        modifier = Modifier.constrainAs(nextButton) {
                            absoluteLeft.linkTo(backButton.absoluteRight, margin = 15.dp)
                            absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                            bottom.linkTo(parent.bottom, margin = 15.dp)
                            width = Dimension.fillToConstraints
                        }
                    ) {
                        Text(
                            "Registrieren",
                            style = TextStyle(fontSize = 15.sp)
                        )
                    }

                    Button(
                        onClick = {
                            nextButtonEnabled = false
                            navController.popBackStack()
                        },
                        enabled = backButtonEnabled,
                        modifier = Modifier.constrainAs(backButton) {
                            absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                            absoluteRight.linkTo(nextButton.absoluteLeft, margin = 15.dp)
                            bottom.linkTo(parent.bottom, margin = 15.dp)
                            width = Dimension.fillToConstraints
                        }
                    ) {
                        Text(
                            "Zurück",
                            style = TextStyle(fontSize = 15.sp)
                        )
                    }

                    if (openDialog.value) {
                        AlertDialog(
                            onDismissRequest = {
                                // Dismiss the dialog when the user clicks outside the dialog or on the back
                                // button. If you want to disable that functionality, simply use an empty
                                // onDismissRequest.
                                openDialog.value = false
                            },
                            icon = { Icon(Icons.Filled.Password, contentDescription = "Passwort-Icon") },
                            title = {
                                Text(text = "Regeln für Passwörter")
                            },
                            text = {
                                Text(text = "- Mindestens 8 Zeichen" +
                                        "\n- Mindestens eine Ziffer (0-9)" +
                                        "\n- Mindestens einen Kleinbuchstaben (a-z)" +
                                        "\n- Mindestens einen Großbuchstaben (A-Z)" +
                                        "\n- Mindestens ein Sonderzeichen aus folgenden Zeichenklassen: \n" +
                                        "!@#&()-[{]}:;',.?/*~$^+=<>")
                            },
                            confirmButton = {

                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        openDialog.value = false
                                    }
                                ) {
                                    Text("Alles klar.")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LoginWithEmail(
    navToEmailCheck: () -> Unit,
    navController: NavHostController,
    email: String? = "null",
    fromRegister: Boolean? = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (email == "null") {
            Text(text = "No email specified.")
        } else {
            val context = LocalContext.current

            ConstraintLayout() {
                val (loginText1, loginText2, emailInput, passwordInput, nextButton, backButton) = createRefs()
                val passwordInputState = remember { mutableStateOf(TextFieldValue()) }
                val focusRequester = FocusRequester()
                val keyboardController = LocalSoftwareKeyboardController.current
                val context = LocalContext.current
                var nextButtonEnabled by remember { mutableStateOf(false) }
                var backButtonEnabled by remember { mutableStateOf(true) }
                val activity = (LocalContext.current as? Activity)

                fun signInUser(email: String, password: String) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                nextButtonEnabled = true
                                val user = auth.currentUser
                                Toast.makeText(context, "Signed in", Toast.LENGTH_SHORT).show()
                                context.startActivity(Intent(context, Home::class.java))
                                activity?.finish()
                            } else {
                                nextButtonEnabled = true
                                // If sign in fails, display a message to the user.
                                Toast.makeText(
                                    context,
                                    task.exception?.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

                Text("Anmelden",
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
                    "Deine E-Mail-Adresse ist bei uns registriert.\nZeit zum Anmelden!",
                    modifier = Modifier.constrainAs(loginText2) {
                        top.linkTo(loginText1.bottom, 5.dp)
                        absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                        absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                        width = Dimension.fillToConstraints
                    }
                )

                OutlinedTextField(
                    value = email!!,
                    enabled = false,
                    onValueChange = { },
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .constrainAs(emailInput) {
                            top.linkTo(loginText2.bottom, 15.dp)
                            absoluteLeft.linkTo(parent.absoluteLeft, 15.dp)
                            absoluteRight.linkTo(parent.absoluteRight, 15.dp)
                            width = Dimension.fillToConstraints
                        },
                    label = { Text("E-Mail") },
                    singleLine = true,
                )

                OutlinedTextField(
                    value = passwordInputState.value,
                    onValueChange = { passwordInputState.value = it },
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .constrainAs(passwordInput) {
                            top.linkTo(emailInput.bottom, 10.dp)
                            absoluteLeft.linkTo(parent.absoluteLeft, 15.dp)
                            absoluteRight.linkTo(parent.absoluteRight, 15.dp)
                            width = Dimension.fillToConstraints
                        }
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                keyboardController?.show()
                            }
                        }
                        .onKeyEvent { event ->
                            when (event.key) {
                                Key.Enter -> {
                                    signInUser(email, passwordInputState.value.text)
                                    true
                                }
                                else -> false
                            }
                        },
                    label = { Text("Passwort") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        // Handle done, next,... buttons on keyboard
                        onNext = {
                            if (nextButtonEnabled) {
                                nextButtonEnabled = false
                                //auth.createUserWithEmailAndPassword(email, "password")
                            }
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )

                DisposableEffect(Unit) {
                    focusRequester.requestFocus()
                    onDispose { }
                }

                Button(
                    onClick = {
                        nextButtonEnabled = false
                        signInUser(email, passwordInputState.value.text)
                    },
                    enabled = nextButtonEnabled,
                    modifier = Modifier.constrainAs(nextButton) {
                        absoluteLeft.linkTo(backButton.absoluteRight, margin = 15.dp)
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

                if (fromRegister == false) {
                    Button(
                        onClick = {
                            nextButtonEnabled = false
                            navController.popBackStack()
                        },
                        enabled = backButtonEnabled,
                        modifier = Modifier.constrainAs(backButton) {
                            absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                            absoluteRight.linkTo(nextButton.absoluteLeft, margin = 15.dp)
                            bottom.linkTo(parent.bottom, margin = 15.dp)
                            width = Dimension.fillToConstraints
                        }
                    ) {
                        Text(
                            "Zurück",
                            style = TextStyle(fontSize = 15.sp)
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun EmailCheckPreview() {
    NucleusTheme {
        val navController = rememberNavController()
        RegisterEmailCheck(navController = navController)
    }
}

@Preview
@Composable
fun LoginWithEmailPreview() {
    NucleusTheme {
        val navController = rememberNavController()
        LoginWithEmail(
            navToEmailCheck = { navController.navigate("registerHome") },
            navController = navController,
            email = "leonzapke@gmail.com"
        )
    }
}

@Preview
@Composable
fun RegisterWithEmailPreview() {
    NucleusTheme {
        val navController = rememberNavController()
        RegisterHome(
            navToEmailCheck = { navController.navigate("registerHome") },
            navController = navController,
            email = "leonzapke@gmail.com"
        )
    }
}