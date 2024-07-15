package io.github.charlietap.chasm.decoder.wasm.context.scope

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.context.SectionContextImpl
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import io.github.charlietap.chasm.decoder.wasm.section.SectionType

internal fun SectionScope(
    context: DecoderContext,
    sectionDetails: Pair<SectionSize, SectionType>,
): Result<DecoderContext, WasmDecodeError> = binding {

    val (sectionSize, sectionType) = sectionDetails

    context.copy(
        sectionContext = SectionContextImpl(
            sectionSize = sectionSize,
            sectionType = sectionType,
        ),
    )
}
