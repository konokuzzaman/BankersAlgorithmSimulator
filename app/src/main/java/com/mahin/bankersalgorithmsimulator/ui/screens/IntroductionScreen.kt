package com.mahin.bankersalgorithmsimulator.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroductionScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Introduction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TheoryCard(
                title = "What is the Banker's Algorithm?",
                body = "A deadlock-avoidance algorithm that decides, before granting a " +
                        "resource request, whether granting it could ever lead to a state with " +
                        "no possible safe completion order for all processes."
            )

            TheoryCard(
                title = "Safe State",
                body = "A state is safe if there exists at least one ordering of all " +
                        "processes such that each can eventually finish using only currently " +
                        "available resources plus what it already holds, assuming finished " +
                        "processes release their resources back to the pool."
            )

            TheoryCard(
                title = "Unsafe State",
                body = "A state where no such safe ordering exists. Important: unsafe does " +
                        "NOT mean deadlock is certain — it means deadlock cannot be ruled out. " +
                        "The algorithm is conservative and rejects any request leading here."
            )

            TheoryCard(
                title = "Deadlock",
                body = "A state where a set of processes are each waiting for a resource " +
                        "held by another process in the same set, so none can ever proceed. " +
                        "Every deadlock is an unsafe state, but not every unsafe state becomes " +
                        "an actual deadlock."
            )

            TheoryCard(
                title = "The Need Matrix",
                body = "Need[i][j] = Max[i][j] − Allocation[i][j]. It represents how many " +
                        "more instances of resource j process i may still request before " +
                        "reaching its declared maximum."
            )

            TheoryCard(
                title = "Safety Algorithm — the core idea",
                body = "Starting from Available, repeatedly find any unfinished process " +
                        "whose Need fits within what's currently free. Let it 'finish' and " +
                        "reclaim its Allocation back into the pool. If every process can " +
                        "eventually finish this way, the state is Safe."
            )

            TheoryCard(
                title = "Resource Request Algorithm",
                body = "When a process requests resources: reject immediately if the " +
                        "request exceeds its Need or exceeds Available. Otherwise, tentatively " +
                        "grant it and re-run the Safety Algorithm. If still Safe, commit the " +
                        "grant. If Unsafe, roll back and deny the request."
            )
        }
    }
}

@Composable
private fun TheoryCard(title: String, body: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IntroductionScreenPreview() {
    IntroductionScreen {}
}