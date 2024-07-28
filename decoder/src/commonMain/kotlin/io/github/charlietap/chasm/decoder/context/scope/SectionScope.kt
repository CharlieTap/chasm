package io.github.charlietap.chasm.decoder.context.scope

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.context.SectionContextImpl
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.decoder.section.SectionType

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
