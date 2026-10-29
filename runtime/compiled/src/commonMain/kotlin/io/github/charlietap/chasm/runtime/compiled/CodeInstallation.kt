package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.runtime.function.CompiledFunction
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance

abstract class CodeInstallation internal constructor() {

    abstract val instance: ModuleInstance

    abstract fun function(
        index: Int,
    ): FunctionInstance

    abstract fun memory(
        index: Int,
    ): MemoryInstance

    abstract fun table(
        index: Int,
    ): TableInstance

    abstract fun global(
        index: Int,
    ): GlobalInstance

    abstract fun element(
        index: Int,
    ): ElementInstance

    abstract fun data(
        index: Int,
    ): DataInstance

    abstract fun runtimeType(
        index: Int,
    ): Int

    abstract fun tagAddress(
        index: Int,
    ): Int

    abstract fun functionReference(
        index: Int,
    ): Long

    abstract fun typeTest(
        nullable: Boolean,
        heapType: Int,
    ): Any

    abstract fun install(
        index: Int,
        entry: CompiledFunction,
    )

    companion object {
        val abstractHeapTypes: List<String> = listOf(
            "func",
            "nofunc",
            "extern",
            "noextern",
            "any",
            "eq",
            "i31",
            "struct",
            "array",
            "none",
            "exn",
            "noexn",
        )
    }
}
