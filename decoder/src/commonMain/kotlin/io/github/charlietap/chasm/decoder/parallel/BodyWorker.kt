@file:OptIn(ExperimentalAtomicApi::class)

package io.github.charlietap.chasm.decoder.parallel

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.FunctionBodyObserver
import io.github.charlietap.chasm.decoder.context.CodeBodyDecoderContext
import io.github.charlietap.chasm.decoder.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.InstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.code.CodeEntry
import io.github.charlietap.chasm.decoder.decoder.section.code.CodeEntryDecoder
import io.github.charlietap.chasm.decoder.decoder.section.code.FunctionBody
import io.github.charlietap.chasm.decoder.decoder.section.code.LocalEntryDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.CodeBodyVectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.reader.BufferedWasmBinaryReader
import kotlin.concurrent.atomics.ExperimentalAtomicApi

internal class BodyWorker(
    private val context: ParallelDecoderContext,
) {
    private val reader = BufferedWasmBinaryReader(context.bytes, 0, 0)
    private val bodyContext = CodeBodyDecoderContext(context.config, reader)
    private var bodyObserver: FunctionBodyObserver? = null

    // batches decoded while the observer was still pending
    private val unobserved = ArrayList<Int>()

    val requiresDataCount: Boolean
        get() = bodyContext.requiresDataCount

    fun decode(batch: Int) {
        val state = context.observerState.load()
        val observer = if (state == ObserverState.FOLLOWING) bodyObserver() else null

        if (observer != null) replayUnobserved(observer)
        decodeBatch(batch, observer)
        if (state == ObserverState.PENDING) unobserved += batch
    }

    /** Returns the batches still to be shown to the observer. */
    fun finish(): List<Int> {
        val state = context.observerState.load()

        if (state == ObserverState.FOLLOWING) replayUnobserved(bodyObserver())
        if (state != ObserverState.PENDING) unobserved.clear()
        return unobserved
    }

    private fun bodyObserver(): FunctionBodyObserver =
        bodyObserver ?: checkNotNull(context.observer).bodyObserver().also { bodyObserver = it }

    private fun replayUnobserved(observer: FunctionBodyObserver) {
        for (index in unobserved.indices) replayBatch(context, unobserved[index], observer)
        unobserved.clear()
    }

    private fun decodeBatch(batch: Int, observer: FunctionBodyObserver?) {
        for (bodyIndex in context.batches.start(batch) until context.batches.end(batch)) {
            if (context.failures.isBeyond(context.bodies.starts[bodyIndex])) continue

            reader.reset(context.bodies.starts[bodyIndex], context.bodies.ends[bodyIndex])
            val failure = decodeCodeBody(bodyContext, reader, context.decodedBodies, bodyIndex, observer) ?: continue
            context.failures.report(failure.offset, failure.error)
        }
    }
}

private fun decodeCodeBody(
    context: CodeBodyDecoderContext,
    reader: BufferedWasmBinaryReader,
    decodedBodies: Array<FunctionBody?>,
    bodyIndex: Int,
    observer: FunctionBodyObserver?,
): DecodeFailure? = failureOf(reader) {
    val entry = if (observer == null) CodeEntryDecoder(context) else ObservedCodeEntryDecoder(context, bodyIndex, observer)
    entry.fold(
        success = { codeEntry ->
            decodedBodies[bodyIndex] = FunctionBody(Index.FunctionIndex(bodyIndex.toUInt()), codeEntry.locals, codeEntry.body)
            observer?.end()
            Ok(Unit)
        },
        failure = ::Err,
    )
}

private fun ObservedCodeEntryDecoder(
    context: CodeBodyDecoderContext,
    bodyIndex: Int,
    observer: FunctionBodyObserver,
): Result<CodeEntry, WasmDecodeError> = CodeEntryDecoder(
    context = context,
    localEntryDecoder = ::LocalEntryDecoder,
    expressionDecoder = { scopedContext ->
        ExpressionDecoder(scopedContext, ::InstructionDecoder, observer::instruction)
    },
    vectorDecoder = ::CodeBodyVectorDecoder,
    onLocals = { locals -> observer.begin(bodyIndex, locals) },
)
