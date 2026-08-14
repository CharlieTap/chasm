package io.github.charlietap.chasm.decoder

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.builder.ModuleBuilder
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.context.scope.ScopedDecoder
import io.github.charlietap.chasm.decoder.context.scope.SectionScope
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.factory.BinaryReaderFactory
import io.github.charlietap.chasm.decoder.decoder.magic.BinaryMagicNumberValidator
import io.github.charlietap.chasm.decoder.decoder.magic.MagicNumberValidator
import io.github.charlietap.chasm.decoder.decoder.section.SectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.SectionTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.version.VersionDecoder
import io.github.charlietap.chasm.decoder.error.ModuleDecodeError
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeException
import io.github.charlietap.chasm.decoder.ext.section
import io.github.charlietap.chasm.decoder.reader.BinaryReader
import io.github.charlietap.chasm.decoder.section.Section
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.decoder.section.SectionType
import io.github.charlietap.chasm.stream.SourceReader

fun WasmModuleDecoder(
    config: ModuleConfig,
    bytes: ByteArray,
): Result<Module, ModuleDecoderError> {
    val context = ModuleDecoderContext(
        config = config,
        reader = BinaryReader(bytes),
    )
    return WasmModuleDecoder(context)
}

fun WasmModuleDecoder(
    config: ModuleConfig,
    source: SourceReader,
): Result<Module, ModuleDecoderError> {
    val context = ModuleDecoderContext(
        config = config,
        reader = BinaryReader(source),
    )
    return WasmModuleDecoder(context)
}

internal fun WasmModuleDecoder(
    context: ModuleDecoderContext,
): Result<Module, WasmDecodeError> =
    WasmModuleDecoder(
        context = context,
        magicNumberValidator = ::BinaryMagicNumberValidator,
        versionDecoder = ::VersionDecoder,
        sectionTypeDecoder = ::SectionTypeDecoder,
        sectionDecoder = ::SectionDecoder,
        scope = ::SectionScope,
    )

internal fun WasmModuleDecoder(
    config: ModuleConfig,
    source: SourceReader,
    readerFactory: BinaryReaderFactory,
    magicNumberValidator: MagicNumberValidator,
    versionDecoder: Decoder<Version>,
    sectionTypeDecoder: Decoder<SectionType>,
    sectionDecoder: Decoder<Section>,
    scope: ScopedDecoder<Pair<SectionSize, SectionType>, Section>,
): Result<Module, WasmDecodeError> {
    val context = ModuleDecoderContext(config, readerFactory(source))
    return WasmModuleDecoder(
        context = context,
        magicNumberValidator = magicNumberValidator,
        versionDecoder = versionDecoder,
        sectionTypeDecoder = sectionTypeDecoder,
        sectionDecoder = sectionDecoder,
        scope = scope,
    )
}

internal fun WasmModuleDecoder(
    context: ModuleDecoderContext,
    magicNumberValidator: MagicNumberValidator,
    versionDecoder: Decoder<Version>,
    sectionTypeDecoder: Decoder<SectionType>,
    sectionDecoder: Decoder<Section>,
    scope: ScopedDecoder<Pair<SectionSize, SectionType>, Section>,
): Result<Module, WasmDecodeError> {
    context.reset()

    return try {
        binding {
            val builder = ModuleSectionsDecoder(
                context = context,
                magicNumberValidator = magicNumberValidator,
                versionDecoder = versionDecoder,
                sectionTypeDecoder = sectionTypeDecoder,
                sectionDecoder = sectionDecoder,
                scope = scope,
            ).bind()
            builder.build(context.requiresDataCount).bind()
        }
    } catch (error: WasmDecodeException) {
        Err(error.error)
    } catch (error: Throwable) {
        Err(WasmDecodeError.IOError(error))
    } finally {
        context.reset()
    }
}

internal fun ModuleSectionsDecoder(
    context: ModuleDecoderContext,
    magicNumberValidator: MagicNumberValidator = ::BinaryMagicNumberValidator,
    versionDecoder: Decoder<Version> = ::VersionDecoder,
    sectionTypeDecoder: Decoder<SectionType> = ::SectionTypeDecoder,
    sectionDecoder: Decoder<Section> = ::SectionDecoder,
    scope: ScopedDecoder<Pair<SectionSize, SectionType>, Section> = ::SectionScope,
): Result<ModuleBuilder, WasmDecodeError> = binding {
    magicNumberValidator(context.reader).bind()

    val version = versionDecoder(context).bind()
    val builder = ModuleBuilder(version)
    var previousSectionType = SectionType.Custom

    while (context.reader.exhausted().not()) {
        val sectionType = sectionTypeDecoder(context).bind()
        if (sectionType.ordinal <= previousSectionType.ordinal && sectionType != SectionType.Custom) {
            Err(ModuleDecodeError.ModuleMalformed).bind<Unit>()
        }
        val sectionSize = SectionSize(context.reader.uint())
        val section = scope(
            context,
            sectionSize to sectionType,
            sectionDecoder,
        ).bind()

        builder.section(section)
        previousSectionType = sectionType
    }

    builder
}
