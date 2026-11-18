package com.mahin.bankersalgorithmsimulator.algorithm


import com.mahin.bankersalgorithmsimulator.model.Process
import com.mahin.bankersalgorithmsimulator.model.SafetyResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BankersAlgorithmTest {

    // Helper: builds the classic 5-process, 3-resource textbook example.
    private fun buildSafeExample(): Pair<List<Process>, List<Int>> {
        val available = listOf(3, 3, 2)
        val processes = listOf(
            Process.create(id = 0, allocation = listOf(0, 1, 0), max = listOf(7, 5, 3)),
            Process.create(id = 1, allocation = listOf(2, 0, 0), max = listOf(3, 2, 2)),
            Process.create(id = 2, allocation = listOf(3, 0, 2), max = listOf(9, 0, 2)),
            Process.create(id = 3, allocation = listOf(2, 1, 1), max = listOf(2, 2, 2)),
            Process.create(id = 4, allocation = listOf(0, 0, 2), max = listOf(4, 3, 3))
        )
        return processes to available
    }

    @Test
    fun `checkSafety returns Safe for the classic textbook example`() {
        val (processes, available) = buildSafeExample()

        val result = BankersAlgorithm.checkSafety(processes, available)

        assertTrue("Expected system to be Safe", result is SafetyResult.Safe)
        val sequence = (result as SafetyResult.Safe).safeSequence
        assertEquals(listOf(1, 3, 4, 0, 2), sequence)
    }

    @Test
    fun `checkSafety returns Unsafe when no process can proceed`() {
        // Deliberately starve every process: Available = 0, and every process still needs something.
        val available = listOf(0, 0, 0)
        val processes = listOf(
            Process.create(id = 0, allocation = listOf(0, 0, 0), max = listOf(1, 1, 1)),
            Process.create(id = 1, allocation = listOf(0, 0, 0), max = listOf(1, 1, 1))
        )

        val result = BankersAlgorithm.checkSafety(processes, available)

        assertTrue("Expected system to be Unsafe", result is SafetyResult.Unsafe)
    }

    @Test
    fun `requestResources grants a valid safe request and updates state`() {
        val (processes, available) = buildSafeExample()
        val state = com.mahin.bankersalgorithmsimulator.model.SimulationState(
            processCount = 5,
            resourceCount = 3,
            available = available,
            processes = processes
        )

        // Classic textbook request: P1 requests (1,0,2)
        val (newState, result) = BankersAlgorithm.requestResources(state, processId = 1, request = listOf(1, 0, 2))

        assertTrue("Expected request to be Granted", result is com.mahin.bankersalgorithmsimulator.model.RequestResult.Granted)
        assertEquals(listOf(2, 3, 0), newState.available) // 3-1, 3-0, 2-2
        val p1 = newState.processes.first { it.id == 1 }
        assertEquals(listOf(3, 0, 2), p1.allocation) // 2+1, 0+0, 0+2
    }

    @Test
    fun `requestResources denies a request exceeding declared Need`() {
        val (processes, available) = buildSafeExample()
        val state = com.mahin.bankersalgorithmsimulator.model.SimulationState(
            processCount = 5,
            resourceCount = 3,
            available = available,
            processes = processes
        )

        // P0's Need is (7,4,3) — requesting (8,0,0) exceeds it.
        val (newState, result) = BankersAlgorithm.requestResources(state, processId = 0, request = listOf(8, 0, 0))

        assertTrue(result is com.mahin.bankersalgorithmsimulator.model.RequestResult.Denied)
        assertEquals(state, newState) // state must be unchanged (rollback correctness)
    }

    @Test
    fun `requestResources denies a request exceeding Available`() {
        val (processes, available) = buildSafeExample()
        val state = com.mahin.bankersalgorithmsimulator.model.SimulationState(
            processCount = 5,
            resourceCount = 3,
            available = available,
            processes = processes
        )

        // Available is (3,3,2) — requesting (4,0,0) for P0 exceeds Available even though Need allows it.
        val (newState, result) = BankersAlgorithm.requestResources(state, processId = 0, request = listOf(4, 0, 0))

        assertTrue(result is com.mahin.bankersalgorithmsimulator.model.RequestResult.Denied)
        assertEquals(state, newState)
    }
}