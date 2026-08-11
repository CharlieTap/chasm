package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.ast.module.Import.Descriptor
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.instantiator.allocation.function.HostFunctionAllocator
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.Import
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.ModuleTypeResolver
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.type.FunctionType

fun interface CompilerBaselineImportResolver {
    fun resolve(module: Module, store: Store): List<Import>
}

fun interface CompilerHostFunctionAllocator {
    fun allocate(
        store: Store,
        type: FunctionType,
        function: io.github.charlietap.chasm.runtime.instance.HostFunction,
    ): ExternalValue.Function
}

class ChasmHostFunctionAllocator : CompilerHostFunctionAllocator {
    override fun allocate(
        store: Store,
        type: FunctionType,
        function: io.github.charlietap.chasm.runtime.instance.HostFunction,
    ): ExternalValue.Function = HostFunctionAllocator(store, type, function)
}

class EmptyCompilerBaselineImportResolver : CompilerBaselineImportResolver {
    override fun resolve(module: Module, store: Store): List<Import> {
        check(module.imports.isEmpty()) { "compiler baseline fixture unexpectedly contains imports" }
        return emptyList()
    }
}

class CoremarkImportResolver(
    private val hostFunctionAllocator: CompilerHostFunctionAllocator,
) : CompilerBaselineImportResolver {

    override fun resolve(module: Module, store: Store): List<Import> {
        val types = ModuleTypeResolver(module)
        return module.imports.map { import ->
            check(import.moduleName.name == MODULE_NAME && import.entityName.name == CLOCK_NAME) {
                "unsupported CoreMark import: ${import.moduleName.name}.${import.entityName.name}"
            }
            val descriptor = import.descriptor as? Descriptor.Function
                ?: error("CoreMark import is not a function: ${import.moduleName.name}.${import.entityName.name}")
            val externalValue = hostFunctionAllocator.allocate(
                store,
                types.functionType(descriptor.typeIndex),
            ) { _ -> listOf(NumberValue.I64(0L)) }
            Import(import.moduleName.name, import.entityName.name, externalValue)
        }
    }

    private companion object {
        const val MODULE_NAME = "env"
        const val CLOCK_NAME = "clock_ms"
    }
}
