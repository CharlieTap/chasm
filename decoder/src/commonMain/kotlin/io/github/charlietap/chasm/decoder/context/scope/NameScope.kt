package io.github.charlietap.chasm.decoder.context.scope

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.context.NameSectionContextImpl
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun NameScope(
    context: DecoderContext,
    size: UInt,
): Result<DecoderContext, WasmDecodeError> = binding {
    context.copy(
        nameSectionContext = NameSectionContextImpl(
            sectionSize = size,
        ),
    )
}
