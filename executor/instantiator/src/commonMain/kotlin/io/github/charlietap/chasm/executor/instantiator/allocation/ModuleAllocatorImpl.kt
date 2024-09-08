package io.github.charlietap.chasm.executor.instantiator.allocation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.instantiator.allocation.data.DataAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.data.DataAllocatorImpl
import io.github.charlietap.chasm.executor.instantiator.allocation.element.ElementAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.element.ElementAllocatorImpl
import io.github.charlietap.chasm.executor.instantiator.allocation.global.GlobalAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.global.GlobalAllocatorImpl
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocatorImpl
import io.github.charlietap.chasm.executor.instantiator.allocation.table.TableAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.table.TableAllocatorImpl
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.ext.addDataAddress
import io.github.charlietap.chasm.executor.runtime.ext.addElementAddress
import io.github.charlietap.chasm.executor.runtime.ext.addExport
import io.github.charlietap.chasm.executor.runtime.ext.addGlobalAddress
import io.github.charlietap.chasm.executor.runtime.ext.addMemoryAddress
import io.github.charlietap.chasm.executor.runtime.ext.addTableAddress
import io.github.charlietap.chasm.executor.runtime.instance.ExportInstance
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun ModuleAllocatorImpl(
    store: Store,
    module: Module,
    instance: ModuleInstance,
    imports: List<ExternalValue>,
    globalInitValues: List<ExecutionValue>,
    tableInitValues: List<ReferenceValue>,
    elementSegmentReferences: List<List<ReferenceValue>>,
): Result<ModuleInstance, InstantiationError> =
    ModuleAllocatorImpl(
        store = store,
        module = module,
        instance = instance,
        imports = imports,
        globalInitValues = globalInitValues,
        tableInitValues = tableInitValues,
        elementSegmentReferences = elementSegmentReferences,
        tableAllocator = ::TableAllocatorImpl,
        memoryAllocator = ::MemoryAllocatorImpl,
        globalAllocator = ::GlobalAllocatorImpl,
        elementAllocator = ::ElementAllocatorImpl,
        dataAllocator = ::DataAllocatorImpl,
    )

internal fun ModuleAllocatorImpl(
    store: Store,
    module: Module,
    instance: ModuleInstance,
    imports: List<ExternalValue>,
    globalInitValues: List<ExecutionValue>,
    tableInitValues: List<ReferenceValue>,
    elementSegmentReferences: List<List<ReferenceValue>>,
    tableAllocator: TableAllocator,
    memoryAllocator: MemoryAllocator,
    globalAllocator: GlobalAllocator,
    elementAllocator: ElementAllocator,
    dataAllocator: DataAllocator,
): Result<ModuleInstance, InstantiationError> = binding {

    imports.forEach { import ->
        when (import) {
            is ExternalValue.Table -> instance.addTableAddress(import.address)
            is ExternalValue.Memory -> instance.addMemoryAddress(import.address)
            is ExternalValue.Function -> Unit
            is ExternalValue.Global -> Unit
        }
    }

    module.tables.forEachIndexed { idx, table ->
        val address = tableAllocator(store, table.type, tableInitValues[idx])
        instance.addTableAddress(address)
    }

    module.memories.forEach { memory ->
        val address = memoryAllocator(store, memory.type)
        instance.addMemoryAddress(address)
    }

    module.globals.forEachIndexed { idx, global ->
        val address = globalAllocator(store, global.type, globalInitValues[idx])
        instance.addGlobalAddress(address)
    }

    module.elementSegments.forEachIndexed { idx, elementSegment ->
        val address = elementAllocator(store, elementSegment.type, elementSegmentReferences[idx])
        instance.addElementAddress(address)
    }

    module.dataSegments.forEach { dataSegment ->
        val address = dataAllocator(store, dataSegment.initData)
        instance.addDataAddress(address)
    }

    module.exports.forEach { export ->

        val externalValue = when (val descriptor = export.descriptor) {
            is Export.Descriptor.Function -> {
                ExternalValue.Function(instance.functionAddresses[descriptor.functionIndex.idx.toInt()])
            }
            is Export.Descriptor.Table -> {
                ExternalValue.Table(instance.tableAddresses[descriptor.tableIndex.idx.toInt()])
            }
            is Export.Descriptor.Global -> {
                ExternalValue.Global(instance.globalAddresses[descriptor.globalIndex.idx.toInt()])
            }
            is Export.Descriptor.Memory -> {
                ExternalValue.Memory(instance.memAddresses[descriptor.memoryIndex.idx.toInt()])
            }
            is Export.Descriptor.Tag -> {
                TODO()
            }
        }

        instance.addExport(ExportInstance(export.name, externalValue))
    }

    instance
}
