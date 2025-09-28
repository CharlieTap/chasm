package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.ir.module.Function
import io.github.charlietap.chasm.ir.module.Import
import io.github.charlietap.chasm.ir.module.Module

internal data class PassContext(
    val config: RuntimeConfig,
    val module: Module,
) {
    val functionTypes by lazy {

        val imports = module.imports
            .map(Import::descriptor)
            .filterIsInstance<Import.Descriptor.Function>()
            .map(Import.Descriptor.Function::type)

        val functions = module.functions.map(Function::typeIndex).map { index ->
            module.definedTypes[index.idx]
        }

        imports + functions
    }
}
