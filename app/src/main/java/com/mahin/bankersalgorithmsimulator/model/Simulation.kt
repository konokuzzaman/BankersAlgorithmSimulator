package com.mahin.bankersalgorithmsimulator.model


data class SimulationState(
    val processCount: Int = 0,
    val resourceCount: Int = 0,
    val available: List<Int> = emptyList(),
    val processes: List<Process> = emptyList(),
    val safetyResult: SafetyResult? = null,   // null = not yet checked
    val lastRequestResult: RequestResult? = null,
    val errorMessage: String? = null
)

/** Result of running the Safety Algorithm. */
sealed class SafetyResult {
    data class Safe(
        val safeSequence: List<Int>,
        val trace: List<SafetyTraceStep> = emptyList()
    ) : SafetyResult()

    data class Unsafe(
        val trace: List<SafetyTraceStep> = emptyList()
    ) : SafetyResult()
}

/** Represents a single analytical step taken by the Safety Algorithm. */
data class SafetyTraceStep(
    val stepNumber: Int,
    val work: List<Int>,        // Available resources at start of step
    val processId: Int,         // Process being checked
    val need: List<Int>,        // Need of that process
    val canProceed: Boolean,    // Whether Need <= Work
    val newWork: List<Int>? = null // Updated Work if canProceed is true
)

/** Result of a Resource Request evaluation. */
sealed class RequestResult {
    data class Granted(val processId: Int, val newSafeSequence: List<Int>) : RequestResult()
    data class Denied(val processId: Int, val reason: String) : RequestResult()
}