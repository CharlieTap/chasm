package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.module.Function
import io.github.charlietap.chasm.ir.module.Import
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.ir.module.Type
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.rolling.DefinedTypeRoller

internal data class PassContext(
    val module: Module,
) {
    val functionTypes by lazy {

        val imports = module.imports
            .map(Import::descriptor)
            .filterIsInstance<Import.Descriptor.Function>()
            .map(Import.Descriptor.Function::type)

        val functions = module.functions.map(Function::typeIndex).map { index ->
            types[index.idx]
        }

        imports + functions
    }

    val types by lazy {
        module.types.map(Type::recursiveType).fold(mutableListOf<DefinedType>()) { acc, type ->
            val definedTypes = DefinedTypeRoller(acc.size, type)
            acc += definedTypes
            acc
        }
    }
}
