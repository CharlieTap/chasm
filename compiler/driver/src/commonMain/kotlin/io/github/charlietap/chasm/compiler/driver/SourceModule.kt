package io.github.charlietap.chasm.compiler.driver

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Module

@InternalChasmApi
class SourceModule(
    val module: Module,
    val binary: ByteArray?,
    val hash: String,
)
