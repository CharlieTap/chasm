@file:OptIn(ExperimentalAtomicApi::class)

package io.github.charlietap.chasm.decoder.parallel

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.ModuleDecodeObserver
import io.github.charlietap.chasm.decoder.builder.ModuleBuilder
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.decoder.section.code.FunctionBody
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.layout.CodeBodyRanges
import io.github.charlietap.chasm.decoder.reader.BufferedWasmBinaryReader
import io.github.charlietap.chasm.decoder.section.SectionType
import io.github.charlietap.chasm.parallel.EarliestFailure
import io.github.charlietap.chasm.parallel.WorkBatches
import io.github.charlietap.chasm.parallel.WorkCursor
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

internal class ParallelDecoderContext(
    val config: ModuleConfig,
    val bytes: ByteArray,
    val bodies: CodeBodyRanges,
    val batches: WorkBatches,
    val observer: ModuleDecodeObserver?,
) {
    val cursor = WorkCursor(batches.count)
    val decodedBodies = arrayOfNulls<FunctionBody>(bodies.size)
    val failures = EarliestFailure<WasmDecodeError>()
    val sectionsClaimed = AtomicBoolean(false)
    val observerState = AtomicReference(if (observer == null) ObserverState.NONE else ObserverState.PENDING)

    // only touched by the worker that claims the sections
    val sectionReader = BufferedWasmBinaryReader(bytes)
    val sectionContext = ModuleDecoderContext(config, sectionReader)
    lateinit var builder: ModuleBuilder
    var lastSection = SectionType.Custom
}
