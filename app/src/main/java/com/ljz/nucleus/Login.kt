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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.sharp.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
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
import com.ljz.nucleus.database.UserDBHelper
import com.ljz.nucleus.ui.theme.NucleusTheme
import java.util.regex.Pattern


private lateinit var auth: FirebaseAuth
private val PASSWORD_PATTERN: String =
    "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;.',?/*~$^+=]).{8,}$"

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
            EmailCheck(
                navController = navController
            )
        }
        composable(
            "registerWithEmail?email={email}",
            arguments = listOf(navArgument("email") { defaultValue = "null" })
        ) { backStateEntry ->
            backStateEntry.arguments?.getString("email")?.let {
                RegisterHome(
                    navController = navController,
                    email = it
                )
            }

            BackHandler(true) {
                // Nothing
            }
        }
        composable("registerAccountInformation") {
            RegisterAccount(navController = navController)

            BackHandler(true) {
                // Nothing
            }
        }
        composable("resetPassword") {
            ResetPassword(navController = navController)

            BackHandler(true) {
                // Nothing
            }
        }
        composable(
            "loginWithEmail?email={email}",
            arguments = listOf(
                navArgument("email") { defaultValue = "null" }
            )
        ) { backStateEntry ->
            backStateEntry.arguments?.getString("email")?.let {
                LoginWithEmail(
                    navToEmailCheck = { navController.navigate("checkEmail") },
                    navController = navController,
                    email = it
                )
            }

            BackHandler(true) {
                // Nothing
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EmailCheck(
    navController: NavHostController
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ConstraintLayout {
            val (loginText1, loginText2, emailInput, nextButton, adView) = createRefs()
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
                    errorMessage = context.resources.getString(R.string.errorMessage_emailNotValid)
                } else {
                    isError = false
                }

                nextButtonEnabled = !isError
            }

            Text(
                stringResource(id = R.string.mailCheck_letsBegin),
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
                stringResource(R.string.mailCheck_letsBegin2),
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
                label = { Text(stringResource(R.string.input_email)) },
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
                    stringResource(id = R.string.buttonText_next),
                    style = TextStyle(fontSize = 15.sp)
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterHome(
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
                navController.popBackStack()
            } else {
                ConstraintLayout {
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
                    var passwordDialog = remember { mutableStateOf(false) }
                    var emailInfoDialog = remember { mutableStateOf(false) }
                    val userDB = UserDBHelper(context, null)

                    fun validateFirstPassword(password: String) {
                        val matcher = pattern.matcher(password.trim())

                        if (matcher.matches()) {
                            isErrorFirstPassword = false
                        } else {
                            isErrorFirstPassword = true
                            errorMessageFirstPassword = context.resources.getString(R.string.errorMessage_invalidPassword)
                        }
                    }

                    fun validateSecondPassword(password: String, firstPassword: String) {
                        if (password == firstPassword) {
                            isErrorConfirmPassword = false
                        } else {
                            isErrorConfirmPassword = true
                            errorMessageConfirmPassword = context.resources.getString(R.string.errorMessage_passwordNotMatch)
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
                                emailInfoDialog.value = true
                                // mail-sent success
                            }
                            .addOnFailureListener { e ->
                                isErrorConfirmPassword = true
                                errorMessageConfirmPassword = e.message.toString()
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
                                    sendEmailVerification()

                                    task.result.user?.email?.let {
                                        task.result.user?.uid?.let { it1 ->
                                            userDB.addUser(
                                                it1,
                                                it, passwordConfirmInputState.value.text
                                            )
                                        }
                                    }
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

                    Text(
                        stringResource(R.string.registerHome_title),
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
                        stringResource(id = R.string.registerHome_text1),
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
                        label = { Text(stringResource(id = R.string.input_email)) },
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
                        label = { Text(stringResource(id = R.string.input_password)) },
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
                                    onClick = { passwordDialog.value = true },
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
                        label = { Text(stringResource(id = R.string.input_confirmPassword)) },
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
                            stringResource(id = R.string.buttonText_signUp),
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
                            stringResource(id = R.string.buttonText_back),
                            style = TextStyle(fontSize = 15.sp)
                        )
                    }

                    if (passwordDialog.value) {
                        AlertDialog(
                            onDismissRequest = {
                                // Dismiss the dialog when the user clicks outside the dialog or on the back
                                // button. If you want to disable that functionality, simply use an empty
                                // onDismissRequest.
                                passwordDialog.value = false
                            },
                            icon = {
                                Icon(
                                    Icons.Filled.Password,
                                    contentDescription = "Password icon"
                                )
                            },
                            title = {
                                Text(text = stringResource(id = R.string.registerHome_passwordDialogTitle))
                            },
                            text = {
                                Text(
                                    text = stringResource(id = R.string.registerHome_passwordDialogText)
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        passwordDialog.value = false
                                    }
                                ) {
                                    Text(stringResource(id = R.string.buttonText_gotIt))
                                }
                            },
                            dismissButton = {

                            }
                        )
                    }

                    if (emailInfoDialog.value) {
                        AlertDialog(
                            onDismissRequest = {
                                // Dismiss the dialog when the user clicks outside the dialog or on the back
                                // button. If you want to disable that functionality, simply use an empty
                                // onDismissRequest.
                                //openDialog.value = false
                            },
                            icon = {
                                Icon(
                                    Icons.Filled.Info,
                                    contentDescription = "Info icon"
                                )
                            },
                            title = {
                                Text(text = stringResource(id = R.string.registerHome_verifyEmailDialogTitle))
                            },
                            text = {
                                Text(
                                    text = stringResource(id = R.string.registerHome_verifyEmailDialogText)
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        emailInfoDialog.value = false
                                        navController.navigate("registerAccountInformation")
                                        //context.startActivity(Intent(context, Home::class.java))
                                        //activity?.finish()
                                    }
                                ) {
                                    Text(stringResource(id = R.string.buttonText_gotIt))
                                }
                            },
                            dismissButton = {
                                // Nothing
                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterAccount(
    navController: NavHostController
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        auth.currentUser?.uid?.let { Text(text = it) }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LoginWithEmail(
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
        if (email == "null") {
            navController.popBackStack()
        } else {
            val context = LocalContext.current

            ConstraintLayout() {
                val (loginText1, loginText2, emailInput, passwordInput, nextButton, backButton, forgotPassText) = createRefs()
                val passwordInputState = remember { mutableStateOf(TextFieldValue()) }
                var passwordVisible: Boolean by rememberSaveable { mutableStateOf(false) }
                var errorPasswordMessage: String by remember { mutableStateOf("An error occurred") }
                var isErrorPasswordMessage by rememberSaveable { mutableStateOf(false) }
                val focusRequester = FocusRequester()
                val keyboardController = LocalSoftwareKeyboardController.current
                var nextButtonEnabled by remember { mutableStateOf(false) }
                var backButtonEnabled by remember { mutableStateOf(true) }
                val activity = (LocalContext.current as? Activity)

                fun signInUser(email: String, password: String) {
                    nextButtonEnabled = false
                    backButtonEnabled = false
                    isErrorPasswordMessage = false


                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            nextButtonEnabled = true
                            backButtonEnabled = true
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val user = auth.currentUser

                                if (task.result.user?.isEmailVerified == true) {
                                    context.startActivity(Intent(context, Home::class.java))
                                    activity?.finish()
                                } else {
                                    isErrorPasswordMessage = true
                                    errorPasswordMessage = context.resources.getString(R.string.errorMessage_emailNotConfirmed)
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                isErrorPasswordMessage = true
                                errorPasswordMessage = task.exception?.localizedMessage.toString()
                            }
                        }
                }

                fun validateButton() {
                    nextButtonEnabled = !isErrorPasswordMessage
                }

                fun validateFirstPassword(password: String) {
                    if (password.length < 8) {
                        isErrorPasswordMessage = true
                        errorPasswordMessage = context.resources.getString(R.string.errorMessage_passwordTooShort)
                    } else {
                        isErrorPasswordMessage = false
                    }
                }

                Text(
                    stringResource(id = R.string.loginWithEmail_title),
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
                    label = { Text(stringResource(id = R.string.input_email)) },
                    singleLine = true,
                )

                OutlinedTextField(
                    value = passwordInputState.value,
                    onValueChange = {
                        passwordInputState.value = it
                        validateFirstPassword(passwordInputState.value.text)
                        validateButton()
                    },
                    isError = isErrorPasswordMessage,
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
                    label = { Text(stringResource(id = R.string.input_password)) },
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        // Handle done, next,... buttons on keyboard
                        onDone = {
                            if (nextButtonEnabled) {
                                signInUser(email, passwordInputState.value.text)
                            }
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    trailingIcon = {
                        if (isErrorPasswordMessage) {
                            Icon(
                                Icons.Filled.Info,
                                "error",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    leadingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        // Please provide localized description for accessibility services
                        val description =
                            if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = {
                            passwordVisible = !passwordVisible
                        }) {
                            Icon(imageVector = image, description)
                        }
                    },
                    supportingText = {
                        if (isErrorPasswordMessage) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = errorPasswordMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                )

                DisposableEffect(Unit) {
                    focusRequester.requestFocus()
                    onDispose { }
                }

                Text(
                    stringResource(id = R.string.loginWithEmail_text1),
                    modifier = Modifier.constrainAs(loginText2) {
                        top.linkTo(loginText1.bottom, 5.dp)
                        absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                        absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                        width = Dimension.fillToConstraints
                    }
                )

                Button(
                    onClick = {
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
                        stringResource(id = R.string.buttonText_login),
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
                        stringResource(id = R.string.buttonText_back),
                        style = TextStyle(fontSize = 15.sp)
                    )
                }

                Button(
                    onClick = {
                        navController.navigate("resetPassword")
                    },
                    enabled = backButtonEnabled,
                    modifier = Modifier.constrainAs(forgotPassText) {
                        absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                        absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                        top.linkTo(passwordInput.bottom, margin = 10.dp)
                        width = Dimension.fillToConstraints
                    }
                ) {
                    Text(
                        stringResource(id = R.string.buttonText_resetPassword),
                        style = TextStyle(fontSize = 15.sp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ResetPassword(
    navController: NavHostController
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val context = LocalContext.current

        ConstraintLayout() {
            val (loginText1, loginText2, emailInput, nextButton, backButton) = createRefs()
            val emailInputState = remember { mutableStateOf(TextFieldValue()) }
            var errorEmailMessage: String by remember { mutableStateOf("An error occured") }
            var isErrorEmailMessage by rememberSaveable { mutableStateOf(false) }
            val focusRequester = FocusRequester()
            val keyboardController = LocalSoftwareKeyboardController.current
            val context = LocalContext.current
            var nextButtonEnabled by remember { mutableStateOf(false) }
            var backButtonEnabled by remember { mutableStateOf(true) }
            val activity = (LocalContext.current as? Activity)
            var emailInfoDialog = remember { mutableStateOf(false) }

            fun sendLink(email: String) {
                nextButtonEnabled = false
                backButtonEnabled = false

                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        nextButtonEnabled = true
                        backButtonEnabled = true
                        if (task.isSuccessful) {
                            emailInfoDialog.value = true
                        } else {
                            isErrorEmailMessage = true
                            errorEmailMessage = task.exception?.localizedMessage.toString()
                        }
                    }
            }

            fun validateButton() {
                nextButtonEnabled = !isErrorEmailMessage
            }

            fun validateEmail(text: String) {
                if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                    isErrorEmailMessage = true
                    errorEmailMessage = context.resources.getString(R.string.errorMessage_emailNotValid)
                } else {
                    isErrorEmailMessage = false
                }

                nextButtonEnabled = !isErrorEmailMessage
            }

            Text(
                stringResource(id = R.string.resetPassword_title),
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

            OutlinedTextField(
                value = emailInputState.value,
                onValueChange = {
                    emailInputState.value = it
                    validateEmail(emailInputState.value.text)
                    validateButton()
                },
                isError = isErrorEmailMessage,
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
                label = { Text(stringResource(id = R.string.input_email)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    // Handle done, next,... buttons on keyboard
                    onNext = {
                        if (nextButtonEnabled) {
                            sendLink(emailInputState.value.text)
                        }
                    }
                ),
                supportingText = {
                    if (isErrorEmailMessage) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = errorEmailMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    if (isErrorEmailMessage) {
                        Icon(Icons.Filled.Info, "error", tint = MaterialTheme.colorScheme.error)
                    }
                },
            )

            DisposableEffect(Unit) {
                focusRequester.requestFocus()
                onDispose { }
            }

            Text(
                stringResource(id = R.string.resetPassword_text1),
                modifier = Modifier.constrainAs(loginText2) {
                    top.linkTo(loginText1.bottom, 5.dp)
                    absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                    absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                    width = Dimension.fillToConstraints
                }
            )

            Button(
                onClick = {
                    //signInUser(email, passwordInputState.value.text)
                    sendLink(emailInputState.value.text)
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
                    stringResource(id = R.string.buttonText_sendLink),
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
                    stringResource(id = R.string.buttonText_back),
                    style = TextStyle(fontSize = 15.sp)
                )
            }

            if (emailInfoDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                        // button. If you want to disable that functionality, simply use an empty
                        // onDismissRequest.
                        //openDialog.value = false
                    },
                    icon = {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = "Info icon"
                        )
                    },
                    title = {
                        Text(text = stringResource(id = R.string.resetPassword_resetDialogTitle))
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.resetPassword_resetDialogText)
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                emailInfoDialog.value = false
                                navController.navigate("loginWithEmail?email=${emailInputState.value.text}")
                                //context.startActivity(Intent(context, Home::class.java))
                                //activity?.finish()
                            }
                        ) {
                            Text(stringResource(id = R.string.buttonText_gotIt))
                        }
                    },
                    dismissButton = {
                        // Nothing
                    },
                )
            }
        }
    }
}


@Preview
@Composable
fun EmailCheckPreview() {
    NucleusTheme {
        val navController = rememberNavController()
        EmailCheck(navController = navController)
    }
}

@Preview
@Composable
fun ResetPasswordPreview() {
    NucleusTheme {
        val navController = rememberNavController()
        ResetPassword(navController = navController)
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
            navController = navController,
            email = "leonzapke@gmail.com"
        )
    }
}