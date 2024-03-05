package io.github.charlietap.chasm.executor.instantiator.runtime.allocation

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocatorImpl
import io.github.charlietap.chasm.executor.instantiator.allocation.data.DataAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.element.ElementAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.global.GlobalAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.table.TableAllocator
import io.github.charlietap.chasm.executor.runtime.instance.ExportInstance
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.instance.moduleInstance
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

class ModuleAllocatorImplTest {

    @Test
    fun `can allocate a module instance`() {

        val store = store()
        val functionType = functionType()
        val typeIndex = Index.TypeIndex(0u)
        val function =
            function(typeIndex = typeIndex)
        val type = type(
            idx = typeIndex,
            functionType = functionType,
        )
        val table = table()
        val memory = memory()
        val global = global()
        val globalInitValue = NumberValue.I32(117)
        val globalInitValues = listOf(globalInitValue)
        val refType = ReferenceType.Externref
        val elementSegment = elementSegment(type = refType)
        val refVals = listOf(ReferenceValue.Null(ReferenceType.Externref))
        val elementSegmentReferences = listOf(refVals)
        val bytes = ubyteArrayOf()
        val dataSegment = dataSegment(initData = bytes)

        val importFunctionAddress = Address.Function(0)
        val importTableAddress = Address.Table(0)
        val importMemoryAddress = Address.Memory(0)
        val importGlobalAddress = Address.Global(0)
        val imports = listOf(
            ExternalValue.Function(importFunctionAddress),
            ExternalValue.Table(importTableAddress),
            ExternalValue.Memory(importMemoryAddress),
            ExternalValue.Global(importGlobalAddress),
        )

        val functionExport = export(
            name = NameValue("function_export"),
            descriptor = functionExportDescriptor(
                functionIndex = Index.FunctionIndex(1u),
            ),
        )
        val tableExport = export(
            name = NameValue("table_export"),
            descriptor = tableExportDescriptor(
                tableIndex = Index.TableIndex(1u),
            ),
        )
        val memoryExport = export(
            name = NameValue("memory_export"),
            descriptor = memoryExportDescriptor(
                memoryIndex = Index.MemoryIndex(1u),
            ),
        )
        val globalExport = export(
            name = NameValue("global_export"),
            descriptor = globalExportDescriptor(
                globalIndex = Index.GlobalIndex(1u),
            ),
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

        val functionAddress = Address.Function(1)

        val tableAddress = Address.Table(1)
        val tableAllocator: TableAllocator = { _store, _tableType, _refVal ->
            assertEquals(store, _store)
            assertEquals(table.type, _tableType)
            assertEquals(ReferenceValue.Null(table.type.referenceType), _refVal)

            tableAddress
        }

        val memoryAddress = Address.Memory(1)
        val memoryAllocator: MemoryAllocator = { _store, _memoryType ->
            assertEquals(store, _store)
            assertEquals(memory.type, _memoryType)

            memoryAddress
        }

        val globalAddress = Address.Global(1)
        val globalAllocator: GlobalAllocator = { _store, _globalType, _executionValue ->
            assertEquals(store, _store)
            assertEquals(global.type, _globalType)
            assertEquals(globalInitValue, _executionValue)

            globalAddress
        }

        val elementAddress = Address.Element(0)
        val elementAllocator: ElementAllocator = { _store, _refType, _refValues ->
            assertEquals(store, _store)
            assertEquals(refType, _refType)
            assertEquals(refVals, _refValues)

            elementAddress
        }

        val dataAddress = Address.Data(0)
        val dataAllocator: DataAllocator = { _store, _bytes ->
            assertEquals(store, _store)
            assertEquals(bytes, _bytes)

            dataAddress
        }

        val partial = moduleInstance(
            types = listOf(type.functionType),
            functionAddresses = mutableListOf(importFunctionAddress, functionAddress),
            globalAddresses = mutableListOf(importGlobalAddress),
        )

        val expected = ModuleInstance(
            types = listOf(type.functionType),
            functionAddresses = mutableListOf(importFunctionAddress, functionAddress),
            tableAddresses = mutableListOf(importTableAddress, tableAddress),
            memAddresses = mutableListOf(importMemoryAddress, memoryAddress),
            globalAddresses = mutableListOf(importGlobalAddress, globalAddress),
            elemAddresses = mutableListOf(elementAddress),
            dataAddresses = mutableListOf(dataAddress),
            exports = mutableListOf(
                ExportInstance(NameValue("function_export"), ExternalValue.Function(functionAddress)),
                ExportInstance(NameValue("table_export"), ExternalValue.Table(tableAddress)),
                ExportInstance(NameValue("memory_export"), ExternalValue.Memory(memoryAddress)),
                ExportInstance(NameValue("global_export"), ExternalValue.Global(globalAddress)),
            ),
        )

        val actual = ModuleAllocatorImpl(
            store = store,
            module = module,
            instance = partial,
            imports = imports,
            globalInitValues = globalInitValues,
            elementSegmentReferences = elementSegmentReferences,
            tableAllocator = tableAllocator,
            memoryAllocator = memoryAllocator,
            globalAllocator = globalAllocator,
            elementAllocator = elementAllocator,
            dataAllocator = dataAllocator,
        )

        assertEquals(Ok(expected), actual)
    }
}
