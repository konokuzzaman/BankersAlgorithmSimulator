package com.mahin.bankersalgorithmsimulator.model

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}