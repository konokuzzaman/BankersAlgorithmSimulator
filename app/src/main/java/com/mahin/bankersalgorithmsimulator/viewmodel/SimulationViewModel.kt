package com.mahin.bankersalgorithmsimulator.viewmodel

import androidx.lifecycle.ViewModel
import com.mahin.bankersalgorithmsimulator.algorithm.BankersAlgorithm
import com.mahin.bankersalgorithmsimulator.algorithm.ParsedInput
import com.mahin.bankersalgorithmsimulator.model.Process
import com.mahin.bankersalgorithmsimulator.model.SimulationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SimulationViewModel : ViewModel() {

    private val _state = MutableStateFlow(SimulationState())
    val state: StateFlow<SimulationState> = _state.asStateFlow()

    /**
     * Called after InputScreen validates raw text into a ParsedInput.
     * Builds Process objects (which auto-compute Need), runs the Safety
     * Algorithm, and publishes the resulting SimulationState.
     */
    fun submitInput(parsed: ParsedInput) {
        val processes = parsed.maxMatrix.mapIndexed { i, maxRow ->
            Process.create(
                id = i,
                allocation = parsed.allocationMatrix[i],
                max = maxRow,
            )
        }

        val safetyResult = BankersAlgorithm.checkSafety(processes, parsed.available)

        _state.value = SimulationState(
            processCount = parsed.processCount,
            resourceCount = parsed.resourceCount,
            available = parsed.available,
            processes = processes,
            safetyResult = safetyResult,
            lastRequestResult = null,
            errorMessage = null
        )
    }

    /** Resets the simulation back to its initial empty state. */
    fun reset() {
        _state.value = SimulationState()
    }

    /**
     * Evaluates a resource request for a specific process.
     * Updates the state with the result (Granted/Denied).
     */
    fun performRequest(processId: Int, requestValues: List<Int>) {
        val (newState, result) = BankersAlgorithm.requestResources(
            state = _state.value,
            processId = processId,
            request = requestValues
        )
        _state.value = newState.copy(lastRequestResult = result)
    }

    /** Clears the result of the last resource request from the UI. */
    fun clearRequestResult() {
        _state.value = _state.value.copy(lastRequestResult = null)
    }

    /**
     * Generates a random valid configuration for demonstration purposes.
     */
    fun generateRandomInput(n: Int, m: Int): ParsedInput {
        val random = java.util.Random()

        // Random Max Matrix (values 0-9)
        val maxMatrix = List(n) { List(m) { random.nextInt(10) } }

        // Random Allocation Matrix (must be <= Max)
        val allocationMatrix = maxMatrix.map { row ->
            row.map { if (it > 0) random.nextInt(it + 1) else 0 }
        }

        // Random Available (0-5)
        val available = List(m) { random.nextInt(6) }

        return ParsedInput(
            processCount = n,
            resourceCount = m,
            available = available,
            maxMatrix = maxMatrix,
            allocationMatrix = allocationMatrix
        )
    }
}