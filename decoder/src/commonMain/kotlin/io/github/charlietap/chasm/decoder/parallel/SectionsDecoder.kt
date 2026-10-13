@file:OptIn(ExperimentalAtomicApi::class)

package io.github.charlietap.chasm.decoder.parallel

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.ModuleHeaderDecoder
import io.github.charlietap.chasm.decoder.ModuleSectionsDecoder
import io.github.charlietap.chasm.decoder.decoder.section.SectionDecoder
import io.github.charlietap.chasm.decoder.section.CodeSection
import io.github.charlietap.chasm.decoder.section.SectionType
import kotlin.concurrent.atomics.ExperimentalAtomicApi

internal fun decodeSections(context: ParallelDecoderContext) {
    var failure = decodePrelude(context)
    context.observerState.store(if (failure == null) observePrelude(context) else ObserverState.NONE)

    if (failure == null) {
        failure = decodeRemainder(context)
    }
    if (failure != null) {
        context.failures.report(failure.offset, failure.error)
    } else if (context.observerState.load() == ObserverState.FOLLOWING) {
        context.observer?.remainder(context.builder.decodedDataSegments)
    }
}

/** Decodes the header and every section ahead of the code section. */
private fun decodePrelude(context: ParallelDecoderContext): DecodeFailure? = failureOf(context.sectionReader) {
    binding {
        context.builder = ModuleHeaderDecoder(context.sectionContext).bind()
        context.lastSection = ModuleSectionsDecoder(
            context = context.sectionContext,
            builder = context.builder,
            previous = SectionType.Custom,
            until = SectionType.Code,
        ).bind()
    }
}

/** Decodes every section from the code section on, skipping over the function bodies. */
private fun decodeRemainder(context: ParallelDecoderContext): DecodeFailure? = failureOf(context.sectionReader) {
    binding {
        ModuleSectionsDecoder(
            context = context.sectionContext,
            builder = context.builder,
            previous = context.lastSection,
            sectionDecoder = { sectionContext ->
                SectionDecoder(sectionContext) { codeContext ->
                    context.sectionReader.skip(codeContext.sectionSize.size.toInt())
                    Ok(CodeSection(emptyList()))
                }
            },
        ).bind()
    }
}

private fun observePrelude(context: ParallelDecoderContext): ObserverState {
    val observer = context.observer ?: return ObserverState.NONE
    val prelude = context.builder.prelude()
    // a module without a function type for every body is malformed, which build reports
    if (prelude.functions.size != context.bodies.size) return ObserverState.NONE

    return if (observer.prelude(prelude, context.builder.declaredDataCount)) ObserverState.FOLLOWING else ObserverState.NONE
}
