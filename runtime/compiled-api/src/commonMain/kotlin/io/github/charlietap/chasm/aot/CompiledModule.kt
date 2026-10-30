package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.CompiledCode
import io.github.charlietap.chasm.declarations.ModuleDeclarations

@OptIn(InternalChasmApi::class)
class CompiledModule internal constructor(
    @property:InternalChasmApi val code: CompiledCode,
) {
    @InternalChasmApi
    @Suppress("TooGenericExceptionCaught")
    val declarations: Result<ModuleDeclarations> by lazy {
        try {
            Result.success(code.buildDeclarations())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
