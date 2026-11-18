package com.mahin.bankersalgorithmsimulator.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Displays a read-only n x m matrix with row labels (e.g. P0, P1...) and
 * column headers (e.g. R0, R1...).
 */
@Composable
fun MatrixTable(
    columnCount: Int,
    rowLabels: List<String>,
    rows: List<List<Int>>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Header row: blank cell for row-label column, then R0, R1, ...
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "",
                modifier = Modifier.width(56.dp)
            )
            repeat(columnCount) { j ->
                Text(
                    text = "R$j",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // One row per process.
        rows.forEachIndexed { i, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = rowLabels[i],
                    modifier = Modifier.width(56.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
                row.forEach { value ->
                    Text(
                        text = value.toString(),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}