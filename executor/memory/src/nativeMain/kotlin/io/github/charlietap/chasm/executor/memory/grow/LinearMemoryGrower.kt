package io.github.charlietap.chasm.executor.memory.grow

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import liblinmem.grow

actual inline fun LinearMemoryGrower(
    memory: LinearMemory,
    pagesToAdd: Int,
): Result<LinearMemory, InvocationError> = binding {
    val nativeMemory = memory as NativeLinearMemory
    if (grow(nativeMemory.pointer, pagesToAdd.toUInt())) {
        nativeMemory
    } else {
        Err(InvocationError.MemoryGrowFailed).bind()
    }
}
