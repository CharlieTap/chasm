package io.github.charlietap.chasm.error

import io.github.charlietap.chasm.decoder.section.function.FunctionHeader
import kotlin.jvm.JvmInline

sealed interface ModuleDecodeError : WasmDecodeError {
    @JvmInline
    value class FunctionMissingBody(val header: FunctionHeader) : ModuleDecodeError
}
