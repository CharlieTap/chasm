package io.github.charlietap.chasm.executor.instantiator.runtime.allocation

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocator
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.matching.ImportMatcher
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.Import
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.instance.globalAddress
import io.github.charlietap.chasm.fixture.instance.memoryAddress
import io.github.charlietap.chasm.fixture.instance.tableAddress
import io.github.charlietap.chasm.fixture.module.dataSegment
import io.github.charlietap.chasm.fixture.module.elementSegment
import io.github.charlietap.chasm.fixture.module.function
import io.github.charlietap.chasm.fixture.module.functionImportDescriptor
import io.github.charlietap.chasm.fixture.module.global
import io.github.charlietap.chasm.fixture.module.globalImportDescriptor
import io.github.charlietap.chasm.fixture.module.import
import io.github.charlietap.chasm.fixture.module.memory
import io.github.charlietap.chasm.fixture.module.memoryImportDescriptor
import io.github.charlietap.chasm.fixture.module.module
import io.github.charlietap.chasm.fixture.module.table
import io.github.charlietap.chasm.fixture.module.tableImportDescriptor
import io.github.charlietap.chasm.fixture.module.type
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.type.ext.definedType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.fixture.instance.import as runtimeImport

class PartialModuleAllocatorTest {

    @Test
    fun `can allocate a partial module instance`() {

        val store = store()
        val typeIndex = Index.TypeIndex(0u)
        val functionAddress = Address.Function(0)
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
        val refType = ReferenceType.RefNull(AbstractHeapType.Extern)
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
            runtimeImport(externalValue = ExternalValue.Function(importFunctionAddress)),
            runtimeImport(externalValue = ExternalValue.Table(importTableAddress)),
            runtimeImport(externalValue = ExternalValue.Memory(importMemoryAddress)),
            runtimeImport(externalValue = ExternalValue.Global(importGlobalAddress)),
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
        val context = InstantiationContext(store, module)

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
            typeAllocator = ::DefinedTypeFactory,
            importMatcher = importMatcher,
        )

        assertEquals(Ok(expected), actual)
    }
}
