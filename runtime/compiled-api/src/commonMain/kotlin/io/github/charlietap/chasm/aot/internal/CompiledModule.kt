package io.github.charlietap.chasm.aot.internal

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.CompiledModule

@InternalChasmApi
@OptIn(ExperimentalChasmApi::class)
fun compiledModule(
    code: CompiledCode,
): CompiledModule = CompiledModule(code)
