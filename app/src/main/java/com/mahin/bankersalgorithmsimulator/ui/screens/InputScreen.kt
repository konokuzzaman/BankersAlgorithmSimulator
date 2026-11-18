package com.mahin.bankersalgorithmsimulator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mahin.bankersalgorithmsimulator.algorithm.InputValidator
import com.mahin.bankersalgorithmsimulator.model.ValidationResult
import com.mahin.bankersalgorithmsimulator.ui.components.VectorInputRow
import com.mahin.bankersalgorithmsimulator.viewmodel.SimulationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    viewModel: SimulationViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit,
) {
    // --- Local state: process/resource counts, as raw text (see explanation below) ---
    var processCountText by remember { mutableStateOf("") }
    var resourceCountText by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Parsed, safe integer versions (0 if invalid/empty) — used only to size the grids below.
    val n = processCountText.toIntOrNull() ?: 0
    val m = resourceCountText.toIntOrNull() ?: 0

    // available[j], maxMatrix[i][j], allocationMatrix[i][j] — all stored as text.
    var availableText by remember(m) { mutableStateOf(List(m) { "" }) }
    var maxMatrixText by remember(n, m) { mutableStateOf(List(n) { List(m) { "" } }) }
    var allocationMatrixText by remember(n, m) { mutableStateOf(List(n) { List(m) { "" } }) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simulation Input") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step 1: System Size",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            val nVal = processCountText.toIntOrNull() ?: 5
                            val mVal = resourceCountText.toIntOrNull() ?: 3
                            processCountText = nVal.toString()
                            resourceCountText = mVal.toString()

                            val random = viewModel.generateRandomInput(nVal, mVal)
                            availableText = random.available.map { it.toString() }
                            maxMatrixText = random.maxMatrix.map { row -> row.map { it.toString() } }
                            allocationMatrixText = random.allocationMatrix.map { row -> row.map { it.toString() } }
                        }
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Randomize")
                    }

                    IconButton(
                        onClick = {
                            processCountText = ""
                            resourceCountText = ""
                            availableText = emptyList()
                            maxMatrixText = emptyList()
                            allocationMatrixText = emptyList()
                        }
                    ) {
                        Icon(Icons.Default.ClearAll, contentDescription = "Clear All")
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = processCountText,
                    onValueChange = { newValue -> processCountText = newValue.filter { it.isDigit() } },
                    label = { Text("Processes (n)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )

                OutlinedTextField(
                    value = resourceCountText,
                    onValueChange = { newValue -> resourceCountText = newValue.filter { it.isDigit() } },
                    label = { Text("Resources (m)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
            }

            if (m > 0) {
                Text(
                    text = "Step 2: Available Resources",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                VectorInputRow(
                    rowLabel = "Avail.",
                    values = availableText,
                    onValueChange = { index, newValue ->
                        availableText = availableText.toMutableList().also { it[index] = newValue }
                    },
                )
            }

            if (n > 0 && m > 0) {
                Text(
                    text = "Step 3: Max Matrix",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                maxMatrixText.forEachIndexed { i, row ->
                    VectorInputRow(
                        rowLabel = "P$i",
                        values = row,
                        onValueChange = { j, newValue ->
                            maxMatrixText = maxMatrixText.toMutableList().also { rows ->
                                rows[i] = rows[i].toMutableList().also { it[j] = newValue }
                            }
                        },
                    )
                }

                Text(
                    text = "Step 4: Allocation Matrix",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                allocationMatrixText.forEachIndexed { i, row ->
                    VectorInputRow(
                        rowLabel = "P$i",
                        values = row,
                        onValueChange = { j, newValue ->
                            allocationMatrixText = allocationMatrixText.toMutableList().also { rows ->
                                rows[i] = rows[i].toMutableList().also { it[j] = newValue }
                            }
                        },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val (result, parsed) = InputValidator.validateAndParse(
                            processCountText = processCountText,
                            resourceCountText = resourceCountText,
                            availableText = availableText,
                            maxMatrixText = maxMatrixText,
                            allocationMatrixText = allocationMatrixText,
                        )

                        when (result) {
                            is ValidationResult.Invalid -> {
                                scope.launch { snackbarHostState.showSnackbar(result.message) }
                            }
                            is ValidationResult.Valid -> {
                                viewModel.submitInput(parsed!!)
                                onNavigateToResult()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Check Safety")
                }
            }
        }
    }
}