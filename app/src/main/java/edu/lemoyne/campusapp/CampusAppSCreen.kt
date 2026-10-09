package edu.lemoyne.campusapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.lemoyne.campusapp.ui.theme.CampusAppTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            CampusAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    HomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

const val MAX_NAME_LENGTH = 40
fun validateTrailName(input: String, existing: List<String>): String? {
    val name = input.trim()
    return when {
        name.isEmpty() -> "Enter a trail name"
        name.length < 3 -> "Too short — at least 3 characters"
        name.length > MAX_NAME_LENGTH -> "Keep it to $MAX_NAME_LENGTH characters or fewer"
        name.all { it.isDigit() } -> "A name can't beonlyrs"
        !name.first().isLetter() -> "Start with a letter"
        name.any { it in "<>" } -> "No < or > please"
        existing.any { it.equals(name, ignoreCase = true) } -> "\"$name\" is already on the list"
        else -> null
    }
}

@Composable
fun CounterDemo() {

    var count by remember {
        mutableStateOf(0)
    }

    Button(
        onClick = {
            count++
        }
    ) {
        Text(
            text = "Tapped $count times"
        )
    }
}


@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val trails = remember {

        mutableStateListOf(
            "Green Lakes State Park",
            "Clark Reservation",
            "Highland Forest"
        )
    }

    var newTrail by remember {
        mutableStateOf("")
    }
//-- Class 8: Step 3: the error message lives in state too ---
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {

        CounterDemo()

        Text(
            text = "Hiking Log",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Trails I have walked this year",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // --- Class 7 · Step 3: the text field ---
        OutlinedTextField(
            value = newTrail,
// --- Class 8 · Step 4: the field itself pushes back ---
            onValueChange = {
                newTrail = it.take(MAX_NAME_LENGTH)
                errorMessage = null
            },
            label = { Text("Trail name") },
            singleLine = true,
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth()
        )
// --- Class 8 · Step 3: show the problem ---
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )
//        --Class 8: character counter
        Text(
            text = "${newTrail.length} / $MAX_NAME_LENGTH"

        )

        // ---Class 7 · Step 4: the button changes the state---
        Button(
            onClick = {
// --- Class 8 · Step 3: check before you add ---
                val problem = validateTrailName(newTrail, trails)
                if (problem == null) {
                    trails.add(newTrail.trim())
                    newTrail = ""
                } else {
                    errorMessage = problem
                }
            },
// --- Class 8 · Step 5: the sign on the door, not the lock ---
            enabled = newTrail.isNotBlank()
        ) {
            Text("Add trail")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "${trails.size} trails",
            fontWeight = FontWeight.Bold
        )

        for (trail in trails) {

            Text(
                text = trail,
                fontSize = 18.sp
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {

    CampusAppTheme {
        HomeScreen()
    }
}
