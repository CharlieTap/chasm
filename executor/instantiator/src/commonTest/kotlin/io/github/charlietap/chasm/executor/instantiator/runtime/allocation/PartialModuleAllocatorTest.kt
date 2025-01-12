package io.github.charlietap.chasm.executor.instantiator.runtime.allocation

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.type.TypeAllocator
import io.github.charlietap.chasm.executor.instantiator.matching.ImportMatcher
import io.github.charlietap.chasm.executor.runtime.instance.Import
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.fixture.ast.module.dataSegment
import io.github.charlietap.chasm.fixture.ast.module.elementSegment
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.functionImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.global
import io.github.charlietap.chasm.fixture.ast.module.globalImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.import
import io.github.charlietap.chasm.fixture.ast.module.memory
import io.github.charlietap.chasm.fixture.ast.module.memoryImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.module.table
import io.github.charlietap.chasm.fixture.ast.module.tableImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.type
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.ast.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.ast.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.executor.instantiator.instantiationContext
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.fixture.executor.runtime.instance.import as runtimeImport

class PartialModuleAllocatorTest {

    @Test
    fun `can allocate a partial module instance`() {

        val store = store()
        val typeIndex = typeIndex(0u)
        val functionAddress = functionAddress(0)
        val function = function(typeIndex = typeIndex)
        val functionType = functionType()
        val recursiveType = functionRecursiveType(functionType)
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
