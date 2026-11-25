package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module

internal actual fun loadClassFiles(
    classFiles: Map<String, ByteArray>,
    resources: Map<String, ByteArray>,
    className: String,
): ChasmResult<Module, ModuleError> {
    return module(ClassInput.memory(classFiles, resources), className)
}
