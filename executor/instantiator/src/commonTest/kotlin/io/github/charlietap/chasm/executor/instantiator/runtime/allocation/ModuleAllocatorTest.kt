package io.github.charlietap.chasm.executor.instantiator.runtime.allocation

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.data.DataAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.element.ElementAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.global.GlobalAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.table.TableAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.tag.TagAllocator
import io.github.charlietap.chasm.executor.instantiator.predecoding.Predecoder
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.fixture.ast.instruction.expression
import io.github.charlietap.chasm.fixture.ast.module.dataSegment
import io.github.charlietap.chasm.fixture.ast.module.elementSegment
import io.github.charlietap.chasm.fixture.ast.module.export
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.functionExportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.functionIndex
import io.github.charlietap.chasm.fixture.ast.module.global
import io.github.charlietap.chasm.fixture.ast.module.globalExportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.globalIndex
import io.github.charlietap.chasm.fixture.ast.module.memory
import io.github.charlietap.chasm.fixture.ast.module.memoryExportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.memoryIndex
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.module.table
import io.github.charlietap.chasm.fixture.ast.module.tableExportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.tableIndex
import io.github.charlietap.chasm.fixture.ast.module.tag
import io.github.charlietap.chasm.fixture.ast.module.type
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.ast.type.heapType
import io.github.charlietap.chasm.fixture.ast.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import io.github.charlietap.chasm.fixture.executor.instantiator.instantiationContext
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.dataAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.elementAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.exportInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tagAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import io.github.charlietap.chasm.fixture.executor.runtime.value.nullReferenceValue
import io.github.charlietap.chasm.type.ext.definedType
import io.github.charlietap.chasm.type.ext.recursiveType
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.executor.runtime.function.Expression as RuntimeExpression
import io.github.charlietap.chasm.executor.runtime.function.Function as RuntimeFunction

class ModuleAllocatorTest {

    @Test
    fun `can allocate a module instance`() {

        val functionType = functionType()
        val typeIndex = typeIndex(0u)
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
        val globalInitValue = i32(117)
        val tableInitValue = nullReferenceValue(heapType())
        val tableInitValues = listOf(tableInitValue)
        val refType = refNullReferenceType(AbstractHeapType.Extern)
        val elementSegment = elementSegment(type = refType, initExpressions = listOf(expression()))
        val elementSegmentRef = nullReferenceValue(AbstractHeapType.Extern)
        val bytes = ubyteArrayOf()
        val dataSegment = dataSegment(initData = bytes)

        val importFunctionAddress = functionAddress(0)
        val importTableAddress = tableAddress(0)
        val importMemoryAddress = memoryAddress(0)
        val importGlobalAddress = globalAddress(0)

        val functionExport = export(
            name = nameValue("function_export"),
            descriptor = functionExportDescriptor(
                functionIndex = functionIndex(1u),
            ),
        )
        val tableExport = export(
            name = nameValue("table_export"),
            descriptor = tableExportDescriptor(
                tableIndex = tableIndex(1u),
            ),
        )
        val memoryExport = export(
            name = nameValue("memory_export"),
            descriptor = memoryExportDescriptor(
                memoryIndex = memoryIndex(1u),
            ),
        )
        val globalExport = export(
            name = nameValue("global_export"),
            descriptor = globalExportDescriptor(
                globalIndex = globalIndex(1u),
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
        val store = store(
            functions = mutableListOf(
                wasmFunctionInstance(),
            ),
        )
        val context = instantiationContext(store, module)

        val functionAddress = functionAddress(1)

        val tableAddress = tableAddress(1)
        val tableAllocator: TableAllocator = { _store, _tableType, _refVal ->
            assertEquals(store, _store)
            assertEquals(table.type, _tableType)
            assertEquals(tableInitValue, _refVal)

            tableAddress
        }

        val memoryAddress = memoryAddress(1)
        val memoryAllocator: MemoryAllocator = { _store, _memoryType ->
            assertEquals(store, _store)
            assertEquals(memory.type, _memoryType)

            memoryAddress
        }

        val tagAddress = tagAddress(1)
        val tagAllocator: TagAllocator = { _store, _tagType ->
            assertEquals(store, _store)
            assertEquals(tag.type, _tagType)

            tagAddress
        }

        val globalAddress = globalAddress(1)
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
            assertEquals(listOf(elementSegmentRef), _refValues)

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
        )

        val expressionValues = sequenceOf(globalInitValue, elementSegmentRef).iterator()
        val expressionEvaluator: ExpressionEvaluator = { _, _, _, _ ->
            Ok(expressionValues.next())
        }

        val expressionPredecoder: Predecoder<Expression, RuntimeExpression> = { _context, _ ->
            assertEquals(context, _context)

            Ok(runtimeExpression())
        }

        val functionPredecoder: Predecoder<Function, RuntimeFunction> = { _context, _function ->
            assertEquals(context, _context)
            assertEquals(function, _function)

            Ok(runtimeFunction())
        }

        val expected = moduleInstance(
            types = listOf(definedType),
            functionAddresses = mutableListOf(importFunctionAddress, functionAddress),
            tableAddresses = mutableListOf(importTableAddress, tableAddress),
            memAddresses = mutableListOf(importMemoryAddress, memoryAddress),
            globalAddresses = mutableListOf(importGlobalAddress, globalAddress),
            elemAddresses = mutableListOf(elementAddress),
            dataAddresses = mutableListOf(dataAddress),
            exports = mutableListOf(
                exportInstance(nameValue("function_export"), functionExternalValue(functionAddress)),
                exportInstance(nameValue("table_export"), tableExternalValue(tableAddress)),
                exportInstance(nameValue("memory_export"), memoryExternalValue(memoryAddress)),
                exportInstance(nameValue("global_export"), globalExternalValue(globalAddress)),
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
        )

        assertEquals(Ok(expected), actual)
    }
}
