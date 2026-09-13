package io.github.charlietap.chasm.executor.instantiator.runtime.allocation

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.fixture.ast.instruction.expression
import io.github.charlietap.chasm.fixture.ast.module.elementSegment
import io.github.charlietap.chasm.fixture.ast.module.memory
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.executor.instantiator.instantiationContext
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.runtime.address.Address
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModuleAllocatorTest {

    @Test
    fun passesLinearMemoryConfigToMemoryAllocation() {
        val linearMemoryConfig = LinearMemoryConfig(prefault = true)
        val module = module(memories = listOf(memory()))
        val context = instantiationContext(
            module = module,
            config = RuntimeConfig(linearMemory = linearMemoryConfig),
        )
        val instance = moduleInstance()
        var called = false

        val result = ModuleAllocator(
            context = context,
            instance = instance,
            tableInitValues = longArrayOf(),
            constantExpressionEvaluator = { _, _, _, _ -> error("unexpected expression evaluation") },
            tableAllocator = { _, _, _ -> error("unexpected table allocation") },
            memoryAllocator = { _, _, actualConfig ->
                assertEquals(linearMemoryConfig, actualConfig)
                called = true
                Address.Memory(7)
            },
            tagAllocator = { _, _, _ -> error("unexpected tag allocation") },
            globalAllocator = { _, _, _ -> error("unexpected global allocation") },
            elementAllocator = { _, _, _ -> error("unexpected element allocation") },
            dataAllocator = { _, _ -> error("unexpected data allocation") },
            moduleCompiler = { _, _, _, _, _, _ -> Ok(Unit) },
            exportAllocator = { _, _ -> error("unexpected export allocation") },
        )

        assertEquals(Ok(instance), result)
        assertTrue(called)
        assertContentEquals(listOf(Address.Memory(7)), instance.memAddresses)
    }

    @Test
    fun evaluatesEachElementInitializerOnce() {
        val module = module(
            elementSegments = listOf(
                elementSegment(
                    idx = Index.ElementIndex(0u),
                    initExpressions = listOf(expression(), expression()),
                ),
                elementSegment(
                    idx = Index.ElementIndex(1u),
                    initExpressions = listOf(expression()),
                ),
            ),
        )
        val context = instantiationContext(module = module)
        val instance = moduleInstance()
        val allocatedElements = mutableListOf<LongArray>()
        var evaluationCount = 0

        val result = ModuleAllocator(
            context = context,
            instance = instance,
            tableInitValues = longArrayOf(),
            constantExpressionEvaluator = { _, _, _, _ -> Ok((++evaluationCount).toLong()) },
            tableAllocator = { _, _, _ -> error("unexpected table allocation") },
            memoryAllocator = { _, _, _ -> error("unexpected memory allocation") },
            tagAllocator = { _, _, _ -> error("unexpected tag allocation") },
            globalAllocator = { _, _, _ -> error("unexpected global allocation") },
            elementAllocator = { _, _, values ->
                allocatedElements.add(values)
                Address.Element(allocatedElements.lastIndex)
            },
            dataAllocator = { _, _ -> error("unexpected data allocation") },
            moduleCompiler = { _, _, _, _, _, _ -> Ok(Unit) },
            exportAllocator = { _, _ -> error("unexpected export allocation") },
        )

        assertEquals(Ok(instance), result)
        assertEquals(3, evaluationCount)
        assertContentEquals(longArrayOf(1, 2), allocatedElements[0])
        assertContentEquals(longArrayOf(3), allocatedElements[1])
    }
}
