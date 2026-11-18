package com.mahin.bankersalgorithmsimulator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mahin.bankersalgorithmsimulator.model.SafetyResult
import com.mahin.bankersalgorithmsimulator.model.SimulationState
import com.mahin.bankersalgorithmsimulator.ui.components.MatrixTable
import com.mahin.bankersalgorithmsimulator.ui.components.SafeSequenceRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    state: SimulationState,
    onNavigateBack: () -> Unit,
    onNavigateToRequest: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simulation Result") },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SafetyStatusCard(state.safetyResult)

            // Trace Section
            AlgorithmTraceSection(state.safetyResult)

            Text(
                text = "Need Matrix (Max − Allocation)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            MatrixTable(
                columnCount = state.resourceCount,
                rowLabels = state.processes.map { it.name },
                rows = state.processes.map { it.need }
            )

            Text(
                text = "Allocation Matrix",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            MatrixTable(
                columnCount = state.resourceCount,
                rowLabels = state.processes.map { it.name },
                rows = state.processes.map { it.allocation }
            )

            Text(
                text = "Available: ${state.available}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onNavigateToRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit a Resource Request")
            }
        }
    }
}

@Composable
private fun AlgorithmTraceSection(safetyResult: SafetyResult?) {
    var expanded by remember { mutableStateOf(false) }

    val trace = when (safetyResult) {
        is SafetyResult.Safe -> safetyResult.trace
        is SafetyResult.Unsafe -> safetyResult.trace
        null -> emptyList()
    }

    if (trace.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { expanded = !expanded }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Show Algorithm Trace",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Icon(
                        if (expanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                        contentDescription = null
                    )
                }

                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        trace.forEach { step ->
                            TraceStepItem(step)
                            if (step.stepNumber != trace.size) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TraceStepItem(step: com.mahin.bankersalgorithmsimulator.model.SafetyTraceStep) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Badge(
                containerColor = if (step.canProceed) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
            ) {
                Text("Step ${step.stepNumber}", color = Color.White, modifier = Modifier.padding(horizontal = 4.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Checking P${step.processId}",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Work: ${step.work}, Need: ${step.need}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = if (step.canProceed)
                "✓ Need fits in Work. Reclaiming allocation. New Work: ${step.newWork}"
            else
                "✗ Need exceeds Work. Process must wait.",
            style = MaterialTheme.typography.bodySmall,
            color = if (step.canProceed) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun SafetyStatusCard(safetyResult: SafetyResult?) {
    when (safetyResult) {
        is SafetyResult.Safe -> {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDFF5E1))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "System is in a SAFE state",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Safe Sequence:", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(6.dp))
                    SafeSequenceRow(sequence = safetyResult.safeSequence)
                }
            }
        }
        is SafetyResult.Unsafe -> {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDE7E7))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFC62828)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "System is in an UNSAFE state — no safe sequence exists",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC62828)
                    )
                }
            }
        }
        null -> {
            Text(
                text = "Safety not yet evaluated.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}