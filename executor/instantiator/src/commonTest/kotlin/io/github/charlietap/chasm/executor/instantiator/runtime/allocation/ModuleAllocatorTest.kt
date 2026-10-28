package io.github.charlietap.chasm.executor.instantiator.runtime.allocation

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.fixture.ast.module.export
import io.github.charlietap.chasm.fixture.ast.module.functionExportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.memoryExportDescriptor
import io.github.charlietap.chasm.fixture.executor.instantiator.constantExpression
import io.github.charlietap.chasm.fixture.executor.instantiator.elementSegmentDeclaration
import io.github.charlietap.chasm.fixture.executor.instantiator.instantiationContext
import io.github.charlietap.chasm.fixture.executor.instantiator.moduleDeclarations
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModuleAllocatorTest {

    @Test
    fun passesLinearMemoryConfigToMemoryAllocation() {
        val linearMemoryConfig = LinearMemoryConfig(prefault = true)
        val module = moduleDeclarations(memories = listOf(memoryType()))
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
            codeInstaller = { _, _, _, _ -> Ok(Unit) },
            exportAllocator = { _, _ -> error("unexpected export allocation") },
        )

        assertEquals(Ok(instance), result)
        assertTrue(called)
        assertContentEquals(listOf(Address.Memory(7)), instance.memAddresses)
    }

    @Test
    fun evaluatesEachElementInitializerOnce() {
        val module = moduleDeclarations(
            elementSegments = listOf(
                elementSegmentDeclaration(initExpressions = listOf(constantExpression(), constantExpression())),
                elementSegmentDeclaration(initExpressions = listOf(constantExpression())),
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
            codeInstaller = { _, _, _, _ -> Ok(Unit) },
            exportAllocator = { _, _ -> error("unexpected export allocation") },
        )

        assertEquals(Ok(instance), result)
        assertEquals(3, evaluationCount)
        assertContentEquals(longArrayOf(1, 2), allocatedElements[0])
        assertContentEquals(longArrayOf(3), allocatedElements[1])
    }

    @Test
    fun installsCodeAfterAllocatingContentsAndBeforeExporting() {
        val descriptor = memoryExportDescriptor()
        val module = moduleDeclarations(
            memories = listOf(memoryType()),
            exports = listOf(export(name = NameValue("memory"), descriptor = descriptor)),
        )
        val instance = moduleInstance()
        val context = instantiationContext(module = module, instance = instance)
        val steps = mutableListOf<String>()

        val result = ModuleAllocator(
            context = context,
            instance = instance,
            tableInitValues = longArrayOf(),
            constantExpressionEvaluator = { _, _, _, _ -> error("unexpected expression evaluation") },
            tableAllocator = { _, _, _ -> error("unexpected table allocation") },
            memoryAllocator = { _, _, _ ->
                steps.add("memory")
                Address.Memory(0)
            },
            tagAllocator = { _, _, _ -> error("unexpected tag allocation") },
            globalAllocator = { _, _, _ -> error("unexpected global allocation") },
            elementAllocator = { _, _, _ -> error("unexpected element allocation") },
            dataAllocator = { _, _ -> error("unexpected data allocation") },
            codeInstaller = { _, _, _, _ ->
                steps.add("install")
                Ok(Unit)
            },
            exportAllocator = { _, exported ->
                steps.add("export $exported")
                Ok(ExternalValue.Memory(Address.Memory(0)))
            },
        )

        assertEquals(Ok(instance), result)
        assertEquals(listOf("memory", "install", "export $descriptor"), steps)
        assertEquals("memory", instance.exports.single().name.name)
    }

    @Test
    fun aFailedCodeInstallationExportsNothing() {
        val failure = InstantiationError.UnsupportedThreadsModule
        val module = moduleDeclarations(
            exports = listOf(export(name = NameValue("run"), descriptor = functionExportDescriptor())),
        )
        val instance = moduleInstance()
        val context = instantiationContext(module = module, instance = instance)

        val result = ModuleAllocator(
            context = context,
            instance = instance,
            tableInitValues = longArrayOf(),
            constantExpressionEvaluator = { _, _, _, _ -> error("unexpected expression evaluation") },
            tableAllocator = { _, _, _ -> error("unexpected table allocation") },
            memoryAllocator = { _, _, _ -> error("unexpected memory allocation") },
            tagAllocator = { _, _, _ -> error("unexpected tag allocation") },
            globalAllocator = { _, _, _ -> error("unexpected global allocation") },
            elementAllocator = { _, _, _ -> error("unexpected element allocation") },
            dataAllocator = { _, _ -> error("unexpected data allocation") },
            codeInstaller = { _, _, _, _ -> Err(failure) },
            exportAllocator = { _, _ -> error("unexpected export allocation") },
        )

        assertEquals(Err(failure), result)
        assertTrue(instance.exports.isEmpty())
    }
}
