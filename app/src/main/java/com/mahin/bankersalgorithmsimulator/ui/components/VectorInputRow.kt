package com.mahin.bankersalgorithmsimulator.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun VectorInputRow(
    rowLabel: String,
    values: List<String>,
    onValueChange: (index: Int, newValue: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = rowLabel,
            modifier = Modifier
                .width(56.dp)
                .align(androidx.compose.ui.Alignment.CenterVertically)
        )
        values.forEachIndexed { index, value ->
            OutlinedTextField(
                value = value,
                onValueChange = { newValue -> onValueChange(index, newValue) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}