package edu.lemoyne.campusapp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.lemoyne.campusapp.ui.theme.CampusAppTheme

// Class 8 Step 2: one rule book for trail names
const val MAX_NAME_LENGTH = 40

fun validateTrailName(input: String, existing: List<String>): String? {
    val name = input.trim()
    return when {
        name.isEmpty() -> "Enter a trail name"
        name.length < 3 -> "Too short — at least 3 characters" // Lab 8 Task 1: minimum length
        name.length > MAX_NAME_LENGTH -> "Keep it to $MAX_NAME_LENGTH characters or fewer"
        name.all { it.isDigit() } -> "A name can't be only numbers" // Lab 8 Task 2: my own rule ---
        !name.first().isLetter() -> "Start with a letter"
        name.any { it in "<>" } -> "No < or > please"
        existing.any { it.equals(name, ignoreCase = true) } -> "\"$name\" is already on the list"
        else -> null
    }
}

// Class 7 Step 1: a counter that remembers
@Composable
fun CounterDemo() {
    var count by remember { mutableStateOf(0) }
    Button(onClick = {
        count++
        println("count is now $count")
    }) {
        Text("Tapped $count times")
    }
}

// Class 9 Step 2: one owner for the data
@Composable
fun CampusAppScreen(
    showcaseState: ShowcaseUiState,
    loginState: LoginUiState,
    onRefreshShowcase: () -> Unit,
    onLogin: (String, String, () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    // --- Class 7 Step 2: the list lives in state
    val trails = remember {
        mutableStateListOf(
            "Green Lakes State Park",
            "Clark Reservation",
            "Highland Forest",
            "Chittenango Falls",
            "Onondaga Lake Park"
        )
    }

    // Class 9 Step 4: which screen is showing is just state
    var currentScreen by rememberSaveable { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> HomeScreen(
            trails = trails,
            onAddTrail = { trails.add(it) },
            onSeeAll = { currentScreen = "list" },
            onAbout = { currentScreen = "about" },
            onInterestsClick = { currentScreen = "login" },
            modifier = modifier
        )

        "list" -> ListScreen(
            trails = trails,
            onBack = { currentScreen = "home" },
            // --- Class 10 Step 4: only the owner changes the list
            onRemove = { trails.remove(it) },
            // Lab 10 Task 2: remove all, through the owner
            onRemoveAll = { trails.clear() },
            modifier = modifier
        )

        // Lab 9 Task 2: a third screen
        "about" -> AboutScreen(
            onBack = { currentScreen = "home" },
            modifier = modifier
        )

        "login" -> LoginScreen(
            state = loginState,
            onLogin = { email, password ->
                onLogin(email, password) { currentScreen = "interests" }
            },
            modifier = modifier
        )

        "interests" -> InterestsScreen(
            state = showcaseState,
            onBack = { currentScreen = "home" },
            onRefresh = onRefreshShowcase,
            modifier = modifier
        )
    }
}

// Class 9 Step 2: HomeScreen gets its data from outside
@Composable
fun HomeScreen(
    trails: List<String>,
    onAddTrail: (String) -> Unit,
    onSeeAll: () -> Unit,
    onAbout: () -> Unit,
    onInterestsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // --- Class 7 Step 3: what's typed lives in state
    var newTrail by remember { mutableStateOf("") }
    // Class 8 Step 3: the error message lives in state too
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // --- Class 6 Step 3: a column, so things stack
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // --- Class 6 Step 4: real styling
        Text(
            text = "Hiking Log",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Trails I have walked this year",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        // --- Class 7 Step 3: the text field
        OutlinedTextField(
            value = newTrail,
            //--- Class 8 Step 4: the field itself pushes back
            onValueChange = {
                newTrail = it.take(MAX_NAME_LENGTH)
                errorMessage = null
            },
            label = { Text("Trail name") },
            singleLine = true,
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth()
        )

        // --- Class 8 Step 3: show the problem
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lab 7 Task 4: a live character counter
        Text(
            text = "${newTrail.length} / $MAX_NAME_LENGTH",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        //--- Class 7 Step 4: the button changes the state
        Button(
            onClick = {
                // Class 8 Step 3: check before you add
                val problem = validateTrailName(newTrail, trails)
                if (problem == null) {
                    // --- Class 9 Step 2: ask the owner to add it
                    onAddTrail(newTrail.trim())
                    newTrail = ""
                    errorMessage = null
                } else {
                    errorMessage = problem
                }
            },
            // Class 8 Step 5: the sign on the door, not the lock
            enabled = newTrail.isNotBlank()
        ) {
            Text("Add trail")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Class 7 Step 2: draw whatever is in the list
        Text(
            //--- Lab 7 Task 2: singular and plural
            text = if (trails.size == 1) "1 trail" else "${trails.size} trails",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Class 9 Step 5: a way to the second screen
        Button(onClick = onSeeAll) {
            Text("See all trails")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lab 9 Task 2:
        Button(onClick = onInterestsClick) {
            Text("My Interests")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lab 9 Task 2:
        TextButton(onClick = onAbout) {
            Text("About")
        }
    }
}

// Class 9 Step 3: the second screen
@Composable
fun ListScreen(
    trails: List<String>,
    onBack: () -> Unit,
    onRemove: (String) -> Unit,
    onRemoveAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    // --- Class 9 Step 6: the phone's back button goes home too
    BackHandler { onBack() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("Back")
        }
        Text(
            text = "All trails",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        //--- Lab 9 Task 1: count on the list screen
        Text(
            text = if (trails.size == 1) "1 trail" else "${trails.size} trails",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Lab 10 Task 2: remove all, through the owner
        if (trails.isNotEmpty()) {
            TextButton(onClick = onRemoveAll) {
                Text("Remove all")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Class 10 Step 5: the empty case
        if (trails.isEmpty()) {
            Text(
                text = "No trails yet. Add one on the home screen.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Lab 10 Task 1: a way out of the empty screen
            Button(onClick = onBack) {
                Text("Back to home")
            }
        } else {
            // --- Class 10 Step 2: a list that scrolls
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(trails) { trail ->
                    TrailRow(
                        name = trail,
                        onRemove = { onRemove(trail) }
                    )
                }
            }
        }
    }
}

// Class 10 Step 3: one row, as its own Composable
@Composable
fun TrailRow(
    name: String,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            // --- Class 10 Step 4: a remove button on every row
            TextButton(onClick = onRemove) {
                Text("Remove")
            }
        }
    }
}

// Lab 9 Task 2: a third screen
@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("Back")
        }
        Text(
            text = "About",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Hiking Log keeps track of the trails I've walked.")
        Text(text = "Built for CSC 441 by YOUR NAME.")
    }
}

// Class 9 Step 2: previews need sample data now
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CampusAppTheme {
        HomeScreen(
            trails = listOf("Green Lakes State Park", "Clark Reservation"),
            onAddTrail = {},
            onSeeAll = {},
            onAbout = {},
            onInterestsClick = {}
        )
    }
}

// Class 9 Step 7: preview the list screen
@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {
    CampusAppTheme {
        ListScreen(
            trails = listOf("Green Lakes State Park", "Clark Reservation", "Highland Forest"),
            onBack = {},
            onRemove = {},
            onRemoveAll = {}
        )
    }
}

// Class 10 Step 5: preview the empty case too
@Preview(showBackground = true)
@Composable
fun ListScreenEmptyPreview() {
    CampusAppTheme {
        ListScreen(
            trails = emptyList(),
            onBack = {},
            onRemove = {},
            onRemoveAll = {}
        )
    }
}