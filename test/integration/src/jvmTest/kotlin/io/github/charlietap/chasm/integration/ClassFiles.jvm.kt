package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.compiled.ClassInput
import io.github.charlietap.chasm.compiled.module
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.jvm.CompileTarget
import io.github.charlietap.chasm.jvm.MemoryClassOutput

internal actual val compileTarget: CompileTarget = CompileTarget.HOTSPOT

internal actual fun loadClassFiles(
    output: MemoryClassOutput,
    className: String,
): ChasmResult<Module, ModuleError> {
    return module(ClassInput.memory(output.classFiles, output.resources), className)
}
