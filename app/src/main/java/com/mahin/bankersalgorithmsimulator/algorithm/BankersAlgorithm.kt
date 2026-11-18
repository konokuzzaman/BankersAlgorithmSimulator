package com.mahin.bankersalgorithmsimulator.algorithm


import com.mahin.bankersalgorithmsimulator.model.Process
import com.mahin.bankersalgorithmsimulator.model.RequestResult
import com.mahin.bankersalgorithmsimulator.model.SafetyResult
import com.mahin.bankersalgorithmsimulator.model.SafetyTraceStep
import com.mahin.bankersalgorithmsimulator.model.SimulationState

object BankersAlgorithm {


    fun checkSafety(processes: List<Process>, available: List<Int>): SafetyResult {
        val n = processes.size
        val m = available.size

        val work = available.toMutableList()
        val finish = BooleanArray(n) { false }
        val safeSequence = mutableListOf<Int>()
        val trace = mutableListOf<SafetyTraceStep>()
        var stepCount = 0

        var progressMadeInLastPass = true
        while (progressMadeInLastPass) {
            progressMadeInLastPass = false

            for (i in 0 until n) {
                if (finish[i]) continue

                val process = processes[i]
                val canProceed = (0 until m).all { j -> process.need[j] <= work[j] }

                val step = SafetyTraceStep(
                    stepNumber = ++stepCount,
                    work = work.toList(),
                    processId = i,
                    need = process.need,
                    canProceed = canProceed,
                    newWork = if (canProceed) {
                        (0 until m).map { j -> work[j] + process.allocation[j] }
                    } else null
                )
                trace.add(step)

                if (canProceed) {
                    for (j in 0 until m) {
                        work[j] += process.allocation[j]
                    }
                    finish[i] = true
                    safeSequence.add(process.id)
                    progressMadeInLastPass = true
                }
            }
        }

        return if (finish.all { it }) {
            SafetyResult.Safe(safeSequence, trace)
        } else {
            SafetyResult.Unsafe(trace)
        }
    }


    fun requestResources(
        state: SimulationState,
        processId: Int,
        request: List<Int>
    ): Pair<SimulationState, RequestResult> {
        val process = state.processes.find { it.id == processId }
            ?: return state to RequestResult.Denied(processId, "Process P$processId does not exist.")

        val m = state.resourceCount

        // Rule 1: Request must not exceed the process's declared Need.
        val exceedsNeed = (0 until m).any { j -> request[j] > process.need[j] }
        if (exceedsNeed) {
            return state to RequestResult.Denied(
                processId,
                "Request exceeds P$processId's declared maximum need. " +
                        "Process claimed it would never need more than Max."
            )
        }

        // Rule 2: Request must not exceed currently Available resources.
        val exceedsAvailable = (0 until m).any { j -> request[j] > state.available[j] }
        if (exceedsAvailable) {
            return state to RequestResult.Denied(
                processId,
                "Not enough resources available right now. P$processId must wait."
            )
        }

        val newAvailable = (0 until m).map { j -> state.available[j] - request[j] }
        val newProcesses = state.processes.map { p ->
            if (p.id == processId) {
                val newAllocation = (0 until m).map { j -> p.allocation[j] + request[j] }
                val newNeed = (0 until m).map { j -> p.need[j] - request[j] }
                p.copy(allocation = newAllocation, need = newNeed)
            } else {
                p
            }
        }

        val safetyResult = checkSafety(newProcesses, newAvailable)

        return if (safetyResult is SafetyResult.Safe) {
            val newState = state.copy(
                available = newAvailable,
                processes = newProcesses,
                safetyResult = safetyResult
            )
            newState to RequestResult.Granted(processId, safetyResult.safeSequence)
        } else {
            state to RequestResult.Denied(
                processId,
                "Granting this request would leave the system in an unsafe state. Denied to prevent possible deadlock."
            )
        }
    }
}