package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.reference.ReferenceOwner

internal fun engineOf(reference: Any): CompiledEngine {
    val owner = (reference as? ReferenceOwner)?.owner()
    return requireNotNull(owner as? CompiledEngine) { "not a reference of a compiled store" }
}
