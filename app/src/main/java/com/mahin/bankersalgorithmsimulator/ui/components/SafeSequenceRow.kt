package com.mahin.bankersalgorithmsimulator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Renders a safe sequence like [1, 3, 4, 0, 2] as a horizontal chain of
 * process chips separated by arrows: P1 -> P3 -> P4 -> P0 -> P2
 */
@Composable
fun SafeSequenceRow(sequence: List<Int>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        sequence.forEachIndexed { index, processId ->
            AssistChip(
                onClick = { },
                label = { Text("P$processId") }
            )
            if (index != sequence.lastIndex) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(16.dp)
                )
            }
        }
    }
}