package io.github.charlietap.chasm.compiler.driver

import io.github.charlietap.chasm.ast.module.Module

class SourceModule(
    val module: Module,
    val binary: ByteArray?,
    val hash: String,
)
