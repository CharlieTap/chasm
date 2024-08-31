package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.embedding.shapes.Mutability
import io.github.charlietap.chasm.ast.type.Mutability as InternalMutability

internal object MutabilityMapper : BidirectionalMapper<Mutability, InternalMutability> {

    override fun map(input: Mutability): InternalMutability {
        return when (input) {
            Mutability.Const -> InternalMutability.Const
            Mutability.Variable -> InternalMutability.Var
        }
    }

    override fun bimap(input: InternalMutability): Mutability {
        return when (input) {
            InternalMutability.Const -> Mutability.Const
            InternalMutability.Var -> Mutability.Variable
        }
    }
}
