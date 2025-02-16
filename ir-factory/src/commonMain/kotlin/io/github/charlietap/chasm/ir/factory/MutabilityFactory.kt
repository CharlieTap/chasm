package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.ir.type.Mutability as IRMutability

internal inline fun MutabilityFactory(
    mutability: Mutability,
): IRMutability {
    return when (mutability) {
        Mutability.Const -> IRMutability.Const
        Mutability.Var -> IRMutability.Var
    }
}
