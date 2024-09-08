package io.github.charlietap.chasm.decoder.decoder.type.tag

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun AttributeDecoder(
    context: DecoderContext,
): Result<TagType.Attribute, WasmDecodeError> = binding {
    when (val attribute = context.reader.ubyte().bind()) {
        ATTRIBUTE_EXCEPTION -> TagType.Attribute.Exception
        else -> Err(SectionDecodeError.UnknownTagAttribute(attribute)).bind()
    }
}

internal const val ATTRIBUTE_EXCEPTION: UByte = 0x00u
