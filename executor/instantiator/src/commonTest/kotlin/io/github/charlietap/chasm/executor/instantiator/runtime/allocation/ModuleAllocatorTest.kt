package io.github.charlietap.chasm.executor.instantiator.runtime.allocation

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.data.DataAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.element.ElementAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.export.ExportAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.global.GlobalAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.table.TableAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.tag.TagAllocator
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.fixture.executor.instantiator.instantiationContext
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.module.dataSegment
import io.github.charlietap.chasm.fixture.ir.module.elementSegment
import io.github.charlietap.chasm.fixture.ir.module.export
import io.github.charlietap.chasm.fixture.ir.module.function
import io.github.charlietap.chasm.fixture.ir.module.functionExportDescriptor
import io.github.charlietap.chasm.fixture.ir.module.functionIndex
import io.github.charlietap.chasm.fixture.ir.module.global
import io.github.charlietap.chasm.fixture.ir.module.globalExportDescriptor
import io.github.charlietap.chasm.fixture.ir.module.globalIndex
import io.github.charlietap.chasm.fixture.ir.module.memory
import io.github.charlietap.chasm.fixture.ir.module.memoryExportDescriptor
import io.github.charlietap.chasm.fixture.ir.module.memoryIndex
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.fixture.ir.module.table
import io.github.charlietap.chasm.fixture.ir.module.tableExportDescriptor
import io.github.charlietap.chasm.fixture.ir.module.tableIndex
import io.github.charlietap.chasm.fixture.ir.module.tag
import io.github.charlietap.chasm.fixture.ir.module.tagExportDescriptor
import io.github.charlietap.chasm.fixture.ir.module.tagIndex
import io.github.charlietap.chasm.fixture.ir.module.type
import io.github.charlietap.chasm.fixture.ir.module.typeIndex
import io.github.charlietap.chasm.fixture.ir.value.nameValue
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.dataAddress
import io.github.charlietap.chasm.fixture.runtime.instance.elementAddress
import io.github.charlietap.chasm.fixture.runtime.instance.exportInstance
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tableExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.tagAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tagExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.nullReferenceValue
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.heapType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.module.Function
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ext.definedType
import io.github.charlietap.chasm.type.ext.recursiveType
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import io.github.charlietap.chasm.runtime.function.Expression as RuntimeExpression
import io.github.charlietap.chasm.runtime.function.Function as RuntimeFunction

class ModuleAllocatorTest {

    @Test
    fun `can allocate a module instance`() {

        val functionType = functionType()
        val typeIndex = typeIndex(0)
        val function =
            function(typeIndex = typeIndex)
        val type = type(
            idx = typeIndex,
            recursiveType = functionType.recursiveType(),
        )
        val definedType = functionType.definedType()
        val table = table()
        val memory = memory()
        val tag = tag()
        val global = global()
        val globalInitValue = 117L
        val tableInitValue = nullReferenceValue(heapType()).toLongFromBoxed()
        val tableInitValues = longArrayOf(tableInitValue)
        val refType = refNullReferenceType(AbstractHeapType.Extern)
        val elementSegment = elementSegment(type = refType, initExpressions = listOf(expression()))
        val elementSegmentRef = nullReferenceValue(AbstractHeapType.Extern).toLong()
        val bytes = ubyteArrayOf()
        val dataSegment = dataSegment(initData = bytes)

        val importFunctionAddress = functionAddress(0)
        val importTableAddress = tableAddress(0)
        val importMemoryAddress = memoryAddress(0)
        val importGlobalAddress = globalAddress(0)
        val importTagAddress = tagAddress(0)

        val functionExportDescriptor = functionExportDescriptor(
            functionIndex = functionIndex(1),
        )
        val functionExport = export(
            name = nameValue("function_export"),
            descriptor = functionExportDescriptor,
        )
        val tableExportDescriptor = tableExportDescriptor(
            tableIndex = tableIndex(1),
        )
        val tableExport = export(
            name = nameValue("table_export"),
            descriptor = tableExportDescriptor,
        )
        val memoryExportDescriptor = memoryExportDescriptor(
            memoryIndex = memoryIndex(1),
        )
        val memoryExport = export(
            name = nameValue("memory_export"),
            descriptor = memoryExportDescriptor,
        )
        val globalExportDescriptor = globalExportDescriptor(
            globalIndex = globalIndex(1),
        )
        val globalExport = export(
            name = nameValue("global_export"),
            descriptor = globalExportDescriptor,
        )
        val tagExportDescriptor = tagExportDescriptor(
            tagIndex = tagIndex(1),
        )
        val tagExport = export(
            name = nameValue("tag_export"),
            descriptor = tagExportDescriptor,
        )
        val exports = listOf(
            functionExport,
            tableExport,
            memoryExport,
            globalExport,
            tagExport,
        )

        val module = module(
            types = listOf(type),
            functions = listOf(function),
            tables = listOf(table),
            memories = listOf(memory),
            globals = listOf(global),
            tags = listOf(tag),
            elementSegments = listOf(elementSegment),
            dataSegments = listOf(dataSegment),
            exports = exports,
        )
        val store = store(
            functions = mutableListOf(
                wasmFunctionInstance(),
            ),
        )

        val functionAddress = functionAddress(1)
        val functionExternalValue = functionExternalValue(functionAddress)

        val tableAddress = tableAddress(1)
        val tableExternalValue = tableExternalValue(tableAddress)
        val tableAllocator: TableAllocator = { _store, _tableType, _refVal ->
            assertEquals(store, _store)
            assertEquals(table.type, _tableType)
            assertEquals(tableInitValue, _refVal)

            tableAddress
        }

        val memoryAddress = memoryAddress(1)
        val memoryExternalValue = memoryExternalValue(memoryAddress)
        val memoryAllocator: MemoryAllocator = { _store, _memoryType ->
            assertEquals(store, _store)
            assertEquals(memory.type, _memoryType)

            memoryAddress
        }

        val tagAddress = tagAddress(1)
        val tagExternalValue = tagExternalValue(tagAddress)
        val tagAllocator: TagAllocator = { _store, _tagType ->
            assertEquals(store, _store)
            assertEquals(tag.type, _tagType)

            tagAddress
        }

        val globalAddress = globalAddress(1)
        val globalExternalValue = globalExternalValue(globalAddress)
        val globalAllocator: GlobalAllocator = { _store, _globalType, _executionValue ->
            assertEquals(store, _store)
            assertEquals(global.type, _globalType)
            assertEquals(globalInitValue, _executionValue)

            globalAddress
        }

        val elementAddress = elementAddress(0)
        val elementAllocator: ElementAllocator = { _store, _refType, _refValues ->
            assertEquals(store, _store)
            assertEquals(refType, _refType)
            assertContentEquals(longArrayOf(elementSegmentRef), _refValues)

            elementAddress
        }

        val dataAddress = dataAddress(0)
        val dataAllocator: DataAllocator = { _store, _bytes ->
            assertEquals(store, _store)
            assertEquals(bytes, _bytes)

            dataAddress
        }

        val partial = moduleInstance(
            types = listOf(definedType),
            functionAddresses = mutableListOf(importFunctionAddress, functionAddress),
            globalAddresses = mutableListOf(importGlobalAddress),
            memAddresses = mutableListOf(importMemoryAddress),
            tableAddresses = mutableListOf(importTableAddress),
            tagAddresses = mutableListOf(importTagAddress),
        )
        val context = instantiationContext(
            store = store,
            module = module,
            instance = partial,
        )

        val expressionValues = sequenceOf(globalInitValue, elementSegmentRef).iterator()
        val expressionEvaluator: ExpressionEvaluator = { _, _, _, _, _ ->
            Ok(expressionValues.next())
        }

        val expressionPredecoder: Predecoder<Expression, RuntimeExpression> = { _context, _ ->

            Ok(runtimeExpression())
        }

        val functionPredecoder: Predecoder<Function, RuntimeFunction> = { _context, _function ->
            assertEquals(function, _function)

            Ok(runtimeFunction())
        }

        val expectedExports = sequenceOf(
            functionExportDescriptor to functionExternalValue,
            tableExportDescriptor to tableExternalValue,
            memoryExportDescriptor to memoryExternalValue,
            globalExportDescriptor to globalExternalValue,
            tagExportDescriptor to tagExternalValue,
        ).iterator()
        val exportAllocator: ExportAllocator = { _context, _descriptor ->
            val (expectedDescriptor, expectedExtern) = expectedExports.next()

            assertEquals(context, _context)
            assertEquals(expectedDescriptor, _descriptor)

            Ok(expectedExtern)
        }

        val expected = moduleInstance(
            types = listOf(definedType),
            functionAddresses = mutableListOf(importFunctionAddress, functionAddress),
            tableAddresses = mutableListOf(importTableAddress, tableAddress),
            memAddresses = mutableListOf(importMemoryAddress, memoryAddress),
            globalAddresses = mutableListOf(importGlobalAddress, globalAddress),
            tagAddresses = mutableListOf(importTagAddress, tagAddress),
            elemAddresses = mutableListOf(elementAddress),
            dataAddresses = mutableListOf(dataAddress),
            exports = mutableListOf(
                exportInstance(nameValue("function_export"), functionExternalValue),
                exportInstance(nameValue("table_export"), tableExternalValue),
                exportInstance(nameValue("memory_export"), memoryExternalValue),
                exportInstance(nameValue("global_export"), globalExternalValue),
                exportInstance(nameValue("tag_export"), tagExternalValue),
            ),
        )

        val actual = ModuleAllocator(
            context = context,
            instance = partial,
            tableInitValues = tableInitValues,
            evaluator = expressionEvaluator,
            tableAllocator = tableAllocator,
            memoryAllocator = memoryAllocator,
            tagAllocator = tagAllocator,
            globalAllocator = globalAllocator,
            elementAllocator = elementAllocator,
            dataAllocator = dataAllocator,
            expressionPredecoder = expressionPredecoder,
            functionPredecoder = functionPredecoder,
            exportAllocator = exportAllocator,
        )

        assertEquals(Ok(expected), actual)
    }
}
