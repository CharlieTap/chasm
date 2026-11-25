package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult

internal fun malformed(reason: String): ChasmResult.Error<ModuleError.Malformed> {
    return ChasmResult.Error(ModuleError.Malformed("Compiled module rejected: $reason"))
}
