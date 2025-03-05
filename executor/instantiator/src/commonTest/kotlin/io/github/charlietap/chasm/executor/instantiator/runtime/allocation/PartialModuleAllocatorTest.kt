package io.github.charlietap.chasm.executor.instantiator.runtime.allocation

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.type.TypeAllocator
import io.github.charlietap.chasm.executor.instantiator.matching.ImportMatcher
import io.github.charlietap.chasm.fixture.executor.instantiator.instantiationContext
import io.github.charlietap.chasm.fixture.ir.module.dataSegment
import io.github.charlietap.chasm.fixture.ir.module.elementSegment
import io.github.charlietap.chasm.fixture.ir.module.function
import io.github.charlietap.chasm.fixture.ir.module.functionImportDescriptor
import io.github.charlietap.chasm.fixture.ir.module.global
import io.github.charlietap.chasm.fixture.ir.module.globalImportDescriptor
import io.github.charlietap.chasm.fixture.ir.module.import
import io.github.charlietap.chasm.fixture.ir.module.memory
import io.github.charlietap.chasm.fixture.ir.module.memoryImportDescriptor
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.fixture.ir.module.table
import io.github.charlietap.chasm.fixture.ir.module.tableImportDescriptor
import io.github.charlietap.chasm.fixture.ir.module.type
import io.github.charlietap.chasm.fixture.ir.module.typeIndex
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tableExternalValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.finalSubType
import io.github.charlietap.chasm.fixture.type.functionCompositeType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.runtime.instance.Import
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.fixture.runtime.instance.import as runtimeImport

class PartialModuleAllocatorTest {

    @Test
    fun `can allocate a partial module instance`() {

        val store = store()
        val typeIndex = typeIndex(0)
        val functionAddress = functionAddress(0)
        val function = function(typeIndex = typeIndex)
        val functionType = functionType()
        val recursiveType = recursiveType(
            subTypes = listOf(
                finalSubType(emptyList(), functionCompositeType(functionType)),
            ),
            state = RecursiveType.STATE_CLOSED,
        )
        val type = type(
            idx = typeIndex,
            recursiveType = recursiveType,
        )
        val table = table()
        val memory = memory()
        val global = global()
        val refType = refNullReferenceType(AbstractHeapType.Extern)
        val elementSegment = elementSegment(type = refType)
        val bytes = ubyteArrayOf()
        val dataSegment = dataSegment(initData = bytes)

        val moduleFunctionImport = import(descriptor = functionImportDescriptor())
        val moduleTableImport = import(descriptor = tableImportDescriptor())
        val moduleMemoryImport = import(descriptor = memoryImportDescriptor())
        val moduleGlobalImport = import(descriptor = globalImportDescriptor())

        val importFunctionAddress = functionAddress()
        val importTableAddress = tableAddress()
        val importMemoryAddress = memoryAddress()
        val importGlobalAddress = globalAddress()
        val imports = listOf(
            runtimeImport(externalValue = functionExternalValue(importFunctionAddress)),
            runtimeImport(externalValue = tableExternalValue(importTableAddress)),
            runtimeImport(externalValue = memoryExternalValue(importMemoryAddress)),
            runtimeImport(externalValue = globalExternalValue(importGlobalAddress)),
        )

        val module = module(
            types = listOf(type),
            functions = listOf(function),
            tables = listOf(table),
            memories = listOf(memory),
            globals = listOf(global),
            elementSegments = listOf(elementSegment),
            dataSegments = listOf(dataSegment),
            imports = listOf(moduleFunctionImport, moduleTableImport, moduleMemoryImport, moduleGlobalImport),
            exports = emptyList(),
        )
        val context = instantiationContext(store, module)

        val importMatcher: ImportMatcher = { _, _ ->
            Ok(imports.map(Import::externalValue))
        }

        val expected = ModuleInstance(
            types = listOf(type.recursiveType.definedType()),
            functionAddresses = mutableListOf(importFunctionAddress, functionAddress),
            tableAddresses = mutableListOf(importTableAddress),
            memAddresses = mutableListOf(importMemoryAddress),
            globalAddresses = mutableListOf(importGlobalAddress),
            elemAddresses = mutableListOf(),
            dataAddresses = mutableListOf(),
            exports = mutableListOf(),
        )

        val actual = PartialModuleAllocator(
            context = context,
            imports = imports,
            wasmFunctionAllocator = ::WasmFunctionAllocator,
            typeAllocator = ::TypeAllocator,
            importMatcher = importMatcher,
        )

        assertEquals(Ok(expected), actual)
    }
}
