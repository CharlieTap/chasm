package io.github.charlietap.chasm.decoder.decoder.type.recursive

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.type.composite.CompositeTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun SubTypeDecoder(
    context: DecoderContext,
): Result<SubType, WasmDecodeError> =
    SubTypeDecoder(
        context = context,
        typeIndexDecoder = ::TypeIndexDecoder,
        vectorDecoder = ::VectorDecoder,
        compositeTypeDecoder = ::CompositeTypeDecoder,
    )

internal fun SubTypeDecoder(
    context: DecoderContext,
    typeIndexDecoder: Decoder<Index.TypeIndex>,
    vectorDecoder: VectorDecoder<Index.TypeIndex>,
    compositeTypeDecoder: Decoder<CompositeType>,
): Result<SubType, WasmDecodeError> = binding {
    when (context.reader.peek().ubyte().bind()) {
        OPEN_SUB_TYPE -> {
            context.reader.ubyte().bind() // consume byte
            val typeIndices = vectorDecoder(context, typeIndexDecoder).bind()
            val heapTypes = typeIndices.vector.map(ConcreteHeapType::TypeIndex)
            val compositeType = compositeTypeDecoder(context).bind()
            SubType.Open(heapTypes, compositeType)
        }
        FINAL_SUB_TYPE -> {
            context.reader.ubyte().bind() // consume byte
            val typeIndices = vectorDecoder(context, typeIndexDecoder).bind()
            val heapTypes = typeIndices.vector.map(ConcreteHeapType::TypeIndex)
            val compositeType = compositeTypeDecoder(context).bind()
            SubType.Final(heapTypes, compositeType)
        }
        else -> {
            val compositeType = compositeTypeDecoder(context).bind()
            SubType.Final(emptyList(), compositeType)
        }
    }
}

internal const val OPEN_SUB_TYPE: UByte = 0x50u
internal const val FINAL_SUB_TYPE: UByte = 0x4Fu
