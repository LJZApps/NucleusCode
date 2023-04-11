package com.ljz.nucleus.start

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.ljz.nucleus.R
import com.ljz.nucleus.ui.theme.NucleusTheme

private lateinit var auth: FirebaseAuth
private lateinit var realTDB: DatabaseReference

class InterestsChooser : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        realTDB =
            FirebaseDatabase.getInstance("https://nucleus-social-app-default-rtdb.europe-west1.firebasedatabase.app/").reference

        setContent {
            NucleusTheme {
                InterestChooserMain()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InterestChooserMain() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val context = LocalContext.current
        ConstraintLayout {
            val (interestChooserTitle, interestChooserText, nextButton, skipButton, interestLayout, searchInput) = createRefs()
            val activity = (LocalContext.current as? Activity)
            val interests = remember { mutableStateListOf<Interest>() }
            val searchInputState = remember { mutableStateOf(TextFieldValue()) }

            realTDB.child("interests").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { interest ->
                        interests.add(Interest(interest.key.toString()))
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

            Text(
                stringResource(id = R.string.interestChooserMain_title),
                style = TextStyle(
                    textAlign = TextAlign.Left,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier
                    .constrainAs(interestChooserTitle) {
                        top.linkTo(parent.top, margin = 15.dp)
                        absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                        absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                        width = Dimension.fillToConstraints
                    }
            )

            Text(
                stringResource(id = R.string.interestChooserMain_text1),
                style = TextStyle(
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.constrainAs(interestChooserText) {
                    top.linkTo(interestChooserTitle.bottom, margin = 10.dp)
                    absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                    absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                    width = Dimension.fillToConstraints
                }
            )

            TextField(
                value = searchInputState.value,
                onValueChange = {
                    //isErrorUsername = false
                    searchInputState.value = it
                },
                //isError = isErrorUsername,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .constrainAs(searchInput) {
                        top.linkTo(interestChooserText.bottom, 15.dp)
                        absoluteLeft.linkTo(parent.absoluteLeft, 15.dp)
                        absoluteRight.linkTo(parent.absoluteRight, 15.dp)
                        width = Dimension.fillToConstraints
                    },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                label = { Text(stringResource(id = R.string.input_search)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    // Handle done, next,... buttons on keyboard
                    onNext = {
                        /*if (nextButtonEnabled) {
                            // TODO send informations to server
                        }
                         */
                    }
                ),
                /*
                supportingText = {
                    if (isErrorUsername) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = errorUsernameMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Unter diesem Benutzernamen kann man dich finden."
                        )
                    }
                },
                trailingIcon = {
                    if (isErrorName) {
                        Icon(Icons.Filled.Info, "error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                 */
            )

            val state = rememberScrollState(initial = 0)

            FlowRow(
                modifier = Modifier
                    .constrainAs(interestLayout) {
                        top.linkTo(searchInput.bottom, 10.dp)
                        absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                        absoluteRight.linkTo(parent.absoluteRight, margin = 15.dp)
                        bottom.linkTo(nextButton.top, 10.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .verticalScroll(state)
            ) {
                interests.forEach { interest ->
                    InputChip(
                        onClick = { /* Do something! */ },
                        label = { Text(interest.key) },
                        selected = false,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(5.dp, 0.dp, 5.dp, 0.dp)
                    )
                }
            }

            Button(
                onClick = {

                },
                modifier = Modifier.constrainAs(nextButton) {
                    absoluteLeft.linkTo(skipButton.absoluteRight, margin = 15.dp)
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

            Button(
                onClick = {

                },
                modifier = Modifier.constrainAs(skipButton) {
                    absoluteLeft.linkTo(parent.absoluteLeft, margin = 15.dp)
                    absoluteRight.linkTo(nextButton.absoluteLeft, margin = 15.dp)
                    bottom.linkTo(parent.bottom, margin = 15.dp)
                    width = Dimension.fillToConstraints
                }
            ) {
                Text(
                    stringResource(id = R.string.buttonText_skip),
                    style = TextStyle(fontSize = 15.sp)
                )
            }
        }
    }
}

@IgnoreExtraProperties
class Interest(var key: String)

@Preview(showBackground = true)
@Composable
fun InterestChooserMainPreview() {
    NucleusTheme {
        InterestChooserMain()
    }
}