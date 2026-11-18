package com.mahin.bankersalgorithmsimulator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahin.bankersalgorithmsimulator.model.RequestResult
import com.mahin.bankersalgorithmsimulator.ui.components.SafeSequenceRow
import com.mahin.bankersalgorithmsimulator.viewmodel.SimulationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(
    viewModel: SimulationViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Local state for the request form
    var selectedProcessId by remember { mutableIntStateOf(0) }
    var requestTexts by remember(state.resourceCount) {
        mutableStateOf(List(state.resourceCount) { "" })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resource Request") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Request resources for a specific process to see if the system stays safe.",
                style = MaterialTheme.typography.bodyMedium
            )

            // Process Selection
            Text("Select Process:", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.processes.forEach { process ->
                    FilterChip(
                        selected = selectedProcessId == process.id,
                        onClick = { selectedProcessId = process.id },
                        label = { Text(process.name) }
                    )
                }
            }

            HorizontalDivider()

            // Request Vector Input
            Text("Request Vector (R0, R1, ...):", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                requestTexts.forEachIndexed { index, text ->
                    OutlinedTextField(
                        value = text,
                        onValueChange = { newValue ->
                            val newList = requestTexts.toMutableList()
                            newList[index] = newValue.filter { it.isDigit() }
                            requestTexts = newList
                        },
                        label = { Text("R$index") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }

            Button(
                onClick = {
                    val request = requestTexts.map { it.toIntOrNull() ?: 0 }
                    viewModel.performRequest(selectedProcessId, request)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = requestTexts.all { it.isNotEmpty() }
            ) {
                Text("Check Request")
            }

            // Display Result
            state.lastRequestResult?.let { result ->
                RequestResultCard(result, onDismiss = { viewModel.clearRequestResult() })
            }
        }
    }
}

@Composable
fun RequestResultCard(result: RequestResult, onDismiss: () -> Unit) {
    val backgroundColor = when (result) {
        is RequestResult.Granted -> Color(0xFFDFF5E1)
        is RequestResult.Denied -> Color(0xFFFDE7E7)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (result is RequestResult.Granted) "REQUEST GRANTED" else "REQUEST DENIED",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (result is RequestResult.Granted) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                TextButton(onClick = onDismiss) {
                    Text("Dismiss", color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (result) {
                is RequestResult.Granted -> {
                    Text("The system remains in a safe state.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("New Safe Sequence:", style = MaterialTheme.typography.labelLarge)
                    SafeSequenceRow(sequence = result.newSafeSequence)
                }
                is RequestResult.Denied -> {
                    Text(result.reason)
                }
            }
        }
    }
}