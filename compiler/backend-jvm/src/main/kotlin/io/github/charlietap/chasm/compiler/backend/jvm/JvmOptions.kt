package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.compiler.backend.jvm.abi.DEFAULT_METHOD_BYTE_BUDGET
import io.github.charlietap.chasm.compiler.backend.jvm.abi.MAX_SHORT_BRANCH_METHOD_BYTES
import io.github.charlietap.chasm.compiler.backend.jvm.abi.MIN_METHOD_BYTES
import io.github.charlietap.chasm.config.StoreConfig

data class JvmOptions(
    val packageName: String = "chasm.aot",
    val storeConfig: StoreConfig = StoreConfig(),
    val initialFuel: Long? = null,
    val memory: MemoryLowering = MemoryLowering.HELPERS,
    val stacktraces: Boolean = false,
    val splitInstructions: Int? = null,
    val maxMethodBytes: Int = DEFAULT_METHOD_BYTE_BUDGET,
    val functionMaxMethodBytes: Map<Int, Int> = emptyMap(),
) {
    val meterFuel: Boolean
        get() = storeConfig.meterFuel

    val interruptible: Boolean
        get() = storeConfig.interruptible

    init {
        (functionMaxMethodBytes.values + maxMethodBytes).forEach { budget ->
            require(budget in MIN_METHOD_BYTES..MAX_SHORT_BRANCH_METHOD_BYTES) {
                "maxMethodBytes must be from $MIN_METHOD_BYTES to $MAX_SHORT_BRANCH_METHOD_BYTES, not $budget"
            }
        }
    }
}

enum class MemoryLowering {
    HELPERS,

    BYTE_ARRAY,

    UNSAFE,
}
