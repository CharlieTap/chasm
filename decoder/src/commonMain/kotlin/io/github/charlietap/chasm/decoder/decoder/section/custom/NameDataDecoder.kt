package io.github.charlietap.chasm.decoder.decoder.section.custom

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.FieldNameSubsection
import io.github.charlietap.chasm.ast.module.FunctionNameSubsection
import io.github.charlietap.chasm.ast.module.GlobalNameSubsection
import io.github.charlietap.chasm.ast.module.IndirectNameMap
import io.github.charlietap.chasm.ast.module.LabelNameSubsection
import io.github.charlietap.chasm.ast.module.LocalNameSubsection
import io.github.charlietap.chasm.ast.module.MemoryNameSubsection
import io.github.charlietap.chasm.ast.module.ModuleNameSubsection
import io.github.charlietap.chasm.ast.module.NameData
import io.github.charlietap.chasm.ast.module.NameMap
import io.github.charlietap.chasm.ast.module.NameSubsection
import io.github.charlietap.chasm.ast.module.TableNameSubsection
import io.github.charlietap.chasm.ast.module.TagNameSubsection
import io.github.charlietap.chasm.ast.module.TypeNameSubsection
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.name.NameValueDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.ext.trackBytes

internal fun NameDataDecoder(
    context: DecoderContext,
): Result<NameData, WasmDecodeError> = NameDataDecoder(
    context = context,
    indirectNameMapDecoder = ::IndirectNameMapDecoder,
    nameMapDecoder = ::NameMapDecoder,
    nameValueDecoder = ::NameValueDecoder,
)

internal inline fun NameDataDecoder(
    context: DecoderContext,
    crossinline indirectNameMapDecoder: Decoder<IndirectNameMap>,
    crossinline nameMapDecoder: Decoder<NameMap>,
    crossinline nameValueDecoder: Decoder<NameValue>,
) = binding {

    var bytesLeft = context.nameSectionContext.sectionSize
    val subsections = mutableListOf<NameSubsection>()

    while (bytesLeft > 0u) {

        val subsectionId = context.reader.ubyte().bind()
        val (subsectionSize, bytesConsumed) = context.reader.trackBytes {
            context.reader.uint().bind()
        }

        bytesLeft -= subsectionSize + bytesConsumed + 1u

        val subsection = when (subsectionId) {
            MODULE_SUBSECTION -> ModuleNameSubsection(nameValueDecoder(context).bind())
            FUNCTION_SUBSECTION -> FunctionNameSubsection(nameMapDecoder(context).bind())
            LOCAL_SUBSECTION -> LocalNameSubsection(indirectNameMapDecoder(context).bind())
            LABEL_SUBSECTION -> LabelNameSubsection(indirectNameMapDecoder(context).bind())
            TYPE_SUBSECTION -> TypeNameSubsection(nameMapDecoder(context).bind())
            TABLE_SUBSECTION -> TableNameSubsection(nameMapDecoder(context).bind())
            MEMORY_SUBSECTION -> MemoryNameSubsection(nameMapDecoder(context).bind())
            GLOBAL_SUBSECTION -> GlobalNameSubsection(nameMapDecoder(context).bind())
            FIELD_SUBSECTION -> FieldNameSubsection(indirectNameMapDecoder(context).bind())
            TAG_SUBSECTION -> TagNameSubsection(nameMapDecoder(context).bind())
            else -> {
                context.reader.bytes(subsectionSize.toInt()).bind()
                null
            }
        }

        subsection?.let {
            subsections.add(subsection)
        }
    }

    NameData(subsections)
}

internal const val MODULE_SUBSECTION: UByte = 0u
internal const val FUNCTION_SUBSECTION: UByte = 1u
internal const val LOCAL_SUBSECTION: UByte = 2u
internal const val LABEL_SUBSECTION: UByte = 3u
internal const val TYPE_SUBSECTION: UByte = 4u
internal const val TABLE_SUBSECTION: UByte = 5u
internal const val MEMORY_SUBSECTION: UByte = 6u
internal const val GLOBAL_SUBSECTION: UByte = 7u
internal const val ELEM_SUBSECTION: UByte = 8u
internal const val DATA_SUBSECTION: UByte = 9u
internal const val FIELD_SUBSECTION: UByte = 10u
internal const val TAG_SUBSECTION: UByte = 11u
