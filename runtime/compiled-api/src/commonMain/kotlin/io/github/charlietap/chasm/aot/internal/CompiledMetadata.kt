@file:OptIn(InternalChasmApi::class, ExperimentalChasmApi::class)

package io.github.charlietap.chasm.aot.internal

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.Capabilities
import io.github.charlietap.chasm.aot.CompiledModuleMetadata
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.config.StoreLimits

@InternalChasmApi
const val COMPILED_RUNTIME_ABI: Int = 1

@InternalChasmApi
const val UNSET: Int = -1

@InternalChasmApi
const val UNSET_LONG: Long = -1L

@InternalChasmApi
fun gcStrategyCode(
    strategy: GCStrategy?,
): Int = when (strategy) {
    null -> UNSET
    GCStrategy.MANUAL -> 0
    GCStrategy.TRADITIONAL -> 1
    GCStrategy.ARENA -> 2
}

@InternalChasmApi
fun gcThresholdKind(
    threshold: GCThreshold?,
): Int = when (threshold) {
    null -> UNSET
    is GCThreshold.KB -> 1
    is GCThreshold.MB -> 2
}

@InternalChasmApi
fun gcThresholdValue(
    threshold: GCThreshold?,
): Int = when (threshold) {
    null -> UNSET
    is GCThreshold.KB -> threshold.value
    is GCThreshold.MB -> threshold.value
}

private fun gcStrategy(
    code: Int,
): GCStrategy? = when (code) {
    UNSET -> null
    0 -> GCStrategy.MANUAL
    1 -> GCStrategy.TRADITIONAL
    2 -> GCStrategy.ARENA
    else -> throw IllegalStateException("unknown GC strategy code $code")
}

private fun gcThreshold(
    kind: Int,
    value: Int,
): GCThreshold? = when (kind) {
    UNSET -> null
    1 -> GCThreshold.KB(value)
    2 -> GCThreshold.MB(value)
    else -> throw IllegalStateException("unknown GC threshold kind $kind")
}

private fun Int.orNull(): Int? = takeUnless { value -> value == UNSET }

private fun Long.orNull(): Long? = takeUnless { value -> value == UNSET_LONG }

internal fun readMetadata(
    code: CompiledCode,
): Result<CompiledModuleMetadata> = try {
    val abi = code.runtimeAbi()
    if (abi != COMPILED_RUNTIME_ABI) {
        Result.failure(
            IllegalStateException("the module was compiled for runtime ABI $abi but this runtime supports $COMPILED_RUNTIME_ABI"),
        )
    } else {
        Result.success(decodeMetadata(code))
    }
} catch (failure: LinkageError) {
    Result.failure(IllegalStateException("the module was compiled for an older runtime", failure))
} catch (failure: IllegalStateException) {
    Result.failure(failure)
} catch (failure: IllegalArgumentException) {
    Result.failure(IllegalStateException("the module's metadata is invalid: ${failure.message}", failure))
}

private fun decodeMetadata(
    code: CompiledCode,
): CompiledModuleMetadata {
    val limits = StoreLimits(
        gcHeapBytes = code.gcHeapBytes().orNull(),
        memoryPages = code.memoryPages().orNull(),
        tableElements = code.tableElements().orNull(),
        trapOnGrowFailure = code.trapOnGrowFailure(),
    )
    val stackBytes = code.stackBytes().orNull()
    return CompiledModuleMetadata(
        capabilities = Capabilities(
            meterFuel = code.meterFuel(),
            interruptible = code.interruptible(),
            stacktraces = code.stacktraces(),
        ),
        storeConfig = StoreConfig(
            meterFuel = code.meterFuel(),
            interruptible = code.interruptible(),
            gcStrategy = gcStrategy(code.gcStrategy()),
            gcThreshold = gcThreshold(code.gcThresholdKind(), code.gcThresholdValue()),
            limits = if (stackBytes == null) limits else limits.copy(stackBytes = stackBytes),
        ),
        initialFuel = code.initialFuel().orNull(),
        runtimeAbi = COMPILED_RUNTIME_ABI,
    )
}
