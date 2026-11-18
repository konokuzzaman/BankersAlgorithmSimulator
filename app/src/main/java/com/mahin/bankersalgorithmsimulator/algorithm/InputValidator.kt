package com.mahin.bankersalgorithmsimulator.algorithm


import com.mahin.bankersalgorithmsimulator.model.ValidationResult

object InputValidator {


    fun validateAndParse(
        processCountText: String,
        resourceCountText: String,
        availableText: List<String>,
        maxMatrixText: List<List<String>>,
        allocationMatrixText: List<List<String>>
    ): Pair<ValidationResult, ParsedInput?> {

        val n = processCountText.toIntOrNull()
        val m = resourceCountText.toIntOrNull()

        if (n == null || n <= 0) {
            return ValidationResult.Invalid("Number of processes must be a positive integer.") to null
        }
        if (m == null || m <= 0) {
            return ValidationResult.Invalid("Number of resource types must be a positive integer.") to null
        }

        val available = availableText.map { it.toIntOrNull() }
        if (available.any { it == null }) {
            return ValidationResult.Invalid("Available vector has an empty or invalid value.") to null
        }
        if (available.any { it!! < 0 }) {
            return ValidationResult.Invalid("Available values cannot be negative.") to null
        }

        val maxMatrix = maxMatrixText.map { row -> row.map { it.toIntOrNull() } }
        if (maxMatrix.any { row -> row.any { it == null } }) {
            return ValidationResult.Invalid("Max matrix has an empty or invalid value.") to null
        }
        if (maxMatrix.any { row -> row.any { it!! < 0 } }) {
            return ValidationResult.Invalid("Max values cannot be negative.") to null
        }

        val allocationMatrix = allocationMatrixText.map { row -> row.map { it.toIntOrNull() } }
        if (allocationMatrix.any { row -> row.any { it == null } }) {
            return ValidationResult.Invalid("Allocation matrix has an empty or invalid value.") to null
        }
        if (allocationMatrix.any { row -> row.any { it!! < 0 } }) {
            return ValidationResult.Invalid("Allocation values cannot be negative.") to null
        }

        for (i in 0 until n) {
            for (j in 0 until m) {
                val alloc = allocationMatrix[i][j]!!
                val max = maxMatrix[i][j]!!
                if (alloc > max) {
                    return ValidationResult.Invalid(
                        "Process P$i: Allocation (${alloc}) exceeds Max (${max}) for resource R$j."
                    ) to null
                }
            }
        }

        val parsed = ParsedInput(
            processCount = n,
            resourceCount = m,
            available = available.map { it!! },
            maxMatrix = maxMatrix.map { row -> row.map { it!! } },
            allocationMatrix = allocationMatrix.map { row -> row.map { it!! } }
        )

        return ValidationResult.Valid to parsed
    }
}

data class ParsedInput(
    val processCount: Int,
    val resourceCount: Int,
    val available: List<Int>,
    val maxMatrix: List<List<Int>>,
    val allocationMatrix: List<List<Int>>
)