package com.ljz.nucleus

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.ktx.Firebase
import com.ljz.nucleus.ui.theme.NucleusTheme

private lateinit var auth: FirebaseAuth
var database: DatabaseReference? = null

class Home : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance("https://nucleus-social-app-default-rtdb.europe-west1.firebasedatabase.app/").reference
        val uid = auth.currentUser?.uid

        val userData = UserData("Leon Zapke", "Ja was weiß ich", "Fick dick")
        database!!.child("user/$uid").setValue(userData)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Kommentar erfolgreich veröffentlicht!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error: " + e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnCanceledListener {
                Toast.makeText(
                    this,
                    "Gecancelt",
                    Toast.LENGTH_SHORT
                ).show()
            }

        setContent {
            NucleusTheme {
                // A surface container using the 'background' color from the theme
                ShowHomeFeed()
            }
        }
    }
}

@IgnoreExtraProperties
class UserData(var name: String, var birthday: String, var broIDDoNotKnow: String?)

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