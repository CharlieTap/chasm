package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.ir.module.Function
import io.github.charlietap.chasm.ir.module.Import
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.type.DefinedType

internal interface PassContextt {
    val module: Module
    val functionTypes: List<DefinedType>
}

internal data class PassContextImpl(
    override val module: Module,
) : PassContextt {
    override val functionTypes by lazy {

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

internal fun PassContext(module: Module): PassContextt = PassContextImpl(module)
