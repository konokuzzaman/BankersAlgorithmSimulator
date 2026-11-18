package com.mahin.bankersalgorithmsimulator.model

data class Process(
    val id: Int,                    // Process index, e.g. 0 for P0, 1 for P1
    val allocation: List<Int>,      // Currently held instances per resource type
    val max: List<Int>,             // Max instances this process may ever need
    val need: List<Int>             // max - allocation, recalculated whenever either changes
) {
    val name: String get() = "P$id"

    companion object {
        /** Creates a Process with Need auto-computed from Max and Allocation. */
        fun create(id: Int, allocation: List<Int>, max: List<Int>): Process {
            val need = max.indices.map { max[it] - allocation[it] }
            return Process(id, allocation, max, need)
        }
    }
}