package io.github.charlietap.chasm.executor.instantiator.runtime.allocation

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocatorImpl
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocatorImpl
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.module.dataSegment
import io.github.charlietap.chasm.fixture.module.elementSegment
import io.github.charlietap.chasm.fixture.module.export
import io.github.charlietap.chasm.fixture.module.function
import io.github.charlietap.chasm.fixture.module.functionExportDescriptor
import io.github.charlietap.chasm.fixture.module.global
import io.github.charlietap.chasm.fixture.module.globalExportDescriptor
import io.github.charlietap.chasm.fixture.module.memory
import io.github.charlietap.chasm.fixture.module.memoryExportDescriptor
import io.github.charlietap.chasm.fixture.module.module
import io.github.charlietap.chasm.fixture.module.table
import io.github.charlietap.chasm.fixture.module.tableExportDescriptor
import io.github.charlietap.chasm.fixture.module.type
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.functionType
import kotlin.test.Test
import kotlin.test.assertEquals

class PartialModuleAllocatorImplTest {

    @Test
    fun `can allocate a partial module instance`() {

        val store = store()
        val functionType = functionType()
        val typeIndex = Index.TypeIndex(0u)
        val functionAddress = Address.Function(0)
        val function = function(typeIndex = typeIndex)
        val type = type(
            idx = typeIndex,
            functionType = functionType,
        )
        val table = table()
        val memory = memory()
        val global = global()
        val refType = ReferenceType.Externref
        val elementSegment = elementSegment(type = refType)
        val bytes = ubyteArrayOf()
        val dataSegment = dataSegment(initData = bytes)

        val importFunctionAddress = Address.Function(1)
        val importTableAddress = Address.Table(1)
        val importMemoryAddress = Address.Memory(1)
        val importGlobalAddress = Address.Global(1)
        val imports = listOf(
            ExternalValue.Function(importFunctionAddress),
            ExternalValue.Table(importTableAddress),
            ExternalValue.Memory(importMemoryAddress),
            ExternalValue.Global(importGlobalAddress),
        )

        val functionExport = export(
            name = NameValue("function_export"),
            descriptor = functionExportDescriptor(),
        )
        val tableExport = export(
            name = NameValue("table_export"),
            descriptor = tableExportDescriptor(),
        )
        val memoryExport = export(
            name = NameValue("memory_export"),
            descriptor = memoryExportDescriptor(),
        )
        val globalExport = export(
            name = NameValue("global_export"),
            descriptor = globalExportDescriptor(),
        )
        val exports = listOf(
            functionExport,
            tableExport,
            memoryExport,
            globalExport,
        )

        val module = module(
            types = listOf(type),
            functions = listOf(function),
            tables = listOf(table),
            memories = listOf(memory),
            globals = listOf(global),
            elementSegments = listOf(elementSegment),
            dataSegments = listOf(dataSegment),
            exports = exports,
        )

        val expected = ModuleInstance(
            types = listOf(type.functionType),
            functionAddresses = mutableListOf(importFunctionAddress, functionAddress),
            tableAddresses = mutableListOf(),
            memAddresses = mutableListOf(),
            globalAddresses = mutableListOf(importGlobalAddress),
            elemAddresses = mutableListOf(),
            dataAddresses = mutableListOf(),
            exports = mutableListOf(),
        )

        val actual = PartialModuleAllocatorImpl(
            store = store,
            module = module,
            imports = imports,
            wasmFunctionAllocator = ::WasmFunctionAllocatorImpl,
        )

        assertEquals(Ok(expected), actual)
    }
}
