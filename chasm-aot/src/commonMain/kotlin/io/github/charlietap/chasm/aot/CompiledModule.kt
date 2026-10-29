package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation

@SubclassOptInRequired(InternalChasmApi::class)
abstract class CompiledModule protected constructor() {

    @InternalChasmApi
    abstract val identity: CodeIdentity

    @InternalChasmApi
    abstract fun install(
        installation: CodeInstallation,
    )

    @InternalChasmApi
    protected abstract fun buildDeclarations(): ModuleDeclarations

    @OptIn(InternalChasmApi::class)
    @Suppress("TooGenericExceptionCaught")
    internal val declarations: Result<ModuleDeclarations> by lazy {
        try {
            Result.success(buildDeclarations())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}

@InternalChasmApi
class CodeIdentity(
    val abi: Int,
    val meterFuel: Boolean,
    val interruptible: Boolean,
) {
    companion object {
        const val RUNTIME_ABI: Int = 8
    }
}

@OptIn(InternalChasmApi::class)
internal fun CompiledModule.compatibleDeclarations(
    store: StoreConfig? = null,
): ChasmResult<ModuleDeclarations, ChasmError.ExecutionError> {
    val rejection = when {
        identity.abi != CodeIdentity.RUNTIME_ABI -> "compiled for runtime ABI ${identity.abi}, not ${CodeIdentity.RUNTIME_ABI}"
        store != null && identity.meterFuel != store.meterFuel -> "compiled with meterFuel = ${identity.meterFuel}"
        store != null && identity.interruptible != store.interruptible -> "compiled with interruptible = ${identity.interruptible}"
        else -> null
    }
    if (rejection != null) return rejected(rejection)
    return declarations.fold(::Success) { failure -> rejected(failure.message ?: failure.toString()) }
}

private fun rejected(
    reason: String,
): ChasmResult.Error<ChasmError.ExecutionError> = Error(ChasmError.ExecutionError("Compiled module rejected: $reason"))
