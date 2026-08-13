package io.github.charlietap.chasm.compiler.context

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.diagnostic.CompilerDiagnostics
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.ext.data
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.global
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.ext.tag
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instance.TagInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.ModuleTypeResolver
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.FunctionType

internal class CompilerContext(
    val config: RuntimeConfig,
    val module: Module,
    val types: ModuleTypeResolver,
    val runtimeTypes: RuntimeTypeMap,
    diagnostics: CompilerDiagnostics? = null,
    val functionAddresses: IntArray = intArrayOf(),
    val functions: Array<FunctionInstance> = emptyArray(),
    val tables: Array<TableInstance> = emptyArray(),
    val memories: Array<MemoryInstance> = emptyArray(),
    val tags: Array<TagInstance> = emptyArray(),
    val globals: Array<GlobalInstance> = emptyArray(),
    val elements: Array<ElementInstance> = emptyArray(),
    val data: Array<DataInstance> = emptyArray(),
    functionCount: Int = module.functions.size,
) {
    val emptyBlockType: FunctionType = types.blockType(BlockType.Empty)
    val instructionObserver = diagnostics?.instructionObserver

    val exportedFunctions = BooleanArray(functionCount).also { exportedFunctions ->
        for (index in module.exports.indices) {
            val export = module.exports[index]
            val descriptor = export.descriptor
            if (descriptor is Export.Descriptor.Function) {
                exportedFunctions[descriptor.functionIndex.toInt()] = true
            }
        }
    }

    fun indexedBlockType(type: BlockType.SignedTypeIndex): FunctionType = types.blockType(type)
}

internal fun createCompilerContext(
    config: RuntimeConfig,
    module: Module,
    types: ModuleTypeResolver,
    store: Store,
    instance: ModuleInstance,
    runtimeTypes: RuntimeTypeMap,
    diagnostics: CompilerDiagnostics? = null,
): CompilerContext = CompilerContext(
    config = config,
    module = module,
    types = types,
    runtimeTypes = runtimeTypes,
    diagnostics = diagnostics,
    functionAddresses = IntArray(instance.functionAddresses.size) { index ->
        instance.functionAddresses[index].address
    },
    functions = Array(instance.functionAddresses.size) { index -> store.function(instance.functionAddresses[index]) },
    tables = Array(instance.tableAddresses.size) { index -> store.table(instance.tableAddresses[index]) },
    memories = Array(instance.memAddresses.size) { index -> store.memory(instance.memAddresses[index]) },
    tags = Array(instance.tagAddresses.size) { index -> store.tag(instance.tagAddresses[index]) },
    globals = Array(instance.globalAddresses.size) { index -> store.global(instance.globalAddresses[index]) },
    elements = Array(instance.elemAddresses.size) { index -> store.element(instance.elemAddresses[index]) },
    data = Array(instance.dataAddresses.size) { index -> store.data(instance.dataAddresses[index]) },
    functionCount = instance.functionAddresses.size,
)
