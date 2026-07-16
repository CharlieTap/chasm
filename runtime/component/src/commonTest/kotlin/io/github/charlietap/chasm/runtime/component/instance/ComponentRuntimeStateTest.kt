package io.github.charlietap.chasm.runtime.component.instance

import io.github.charlietap.chasm.fixture.runtime.component.instance.componentInstanceCounts
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentRuntimeStateTest {

    @Test
    fun `runtime state allocates every slot and preserves component parents`() {
        val counts = componentInstanceCounts(
            componentInstances = 3,
            coreInstances = 2,
            coreFunctions = 3,
            memories = 4,
            reallocs = 5,
            postReturns = 6,
            resourceTypes = 7,
            hostFunctions = 8,
        )
        val componentInstanceParents = intArrayOf(-1, 0, 0)
        val subject = ComponentRuntimeState.Companion

        val state = subject.allocate(counts, componentInstanceParents)
        componentInstanceParents.fill(99)
        val actual = state.observation()

        val expected = ComponentRuntimeStateObservation(
            coreInstances = listOf(null, null),
            coreFunctions = List(3) { -1 },
            memories = List(4) { -1 },
            reallocs = List(5) { -1 },
            postReturns = List(6) { -1 },
            resourceTypes = List(7) { -1 },
            hostFunctions = List(8) { null },
            parents = listOf(-1, 0, 0),
            mayLeave = List(3) { true },
            mayEnter = List(3) { true },
            poisoned = List(3) { false },
            handleTablesAllocated = List(3) { false },
        )
        assertEquals(expected, actual)
    }
}

private fun ComponentRuntimeState.observation() = ComponentRuntimeStateObservation(
    coreInstances = coreInstances.toList(),
    coreFunctions = coreFunctions.toList(),
    memories = memories.toList(),
    reallocs = reallocs.toList(),
    postReturns = postReturns.toList(),
    resourceTypes = resourceTypes.toList(),
    hostFunctions = hostFunctions.toList(),
    parents = states.parents.toList(),
    mayLeave = states.mayLeave.toList(),
    mayEnter = states.mayEnter.toList(),
    poisoned = states.poisoned.toList(),
    handleTablesAllocated = states.handleTables.map { table -> table != null },
)

private data class ComponentRuntimeStateObservation(
    val coreInstances: List<Any?>,
    val coreFunctions: List<Int>,
    val memories: List<Int>,
    val reallocs: List<Int>,
    val postReturns: List<Int>,
    val resourceTypes: List<Int>,
    val hostFunctions: List<Any?>,
    val parents: List<Int>,
    val mayLeave: List<Boolean>,
    val mayEnter: List<Boolean>,
    val poisoned: List<Boolean>,
    val handleTablesAllocated: List<Boolean>,
)
