package io.github.charlietap.chasm.decoder

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.builder.ModuleBuilder
import io.github.charlietap.chasm.decoder.context.CodeBodyDecoderContext
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.decoder.section.SectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.code.CodeEntryDecoder
import io.github.charlietap.chasm.decoder.decoder.section.code.FunctionBody
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeException
import io.github.charlietap.chasm.decoder.layout.CodeBodyRanges
import io.github.charlietap.chasm.decoder.layout.ModuleLayoutScanner
import io.github.charlietap.chasm.decoder.reader.BufferedWasmBinaryReader
import io.github.charlietap.chasm.decoder.section.CodeSection
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.parallel.availableParallelProcessors

suspend fun ParallelWasmModuleDecoder(
    config: ModuleConfig,
    bytes: ByteArray,
    taskExecutor: ParallelTaskExecutor,
): Result<Module, ModuleDecoderError> =
    ParallelWasmModuleDecoder(config, bytes, taskExecutor, DecodingMode.AUTO)

internal suspend fun ParallelWasmModuleDecoder(
    config: ModuleConfig,
    bytes: ByteArray,
    taskExecutor: ParallelTaskExecutor,
    mode: DecodingMode,
    availableProcessors: Int = availableParallelProcessors(),
): Result<Module, ModuleDecoderError> {
    if (!shouldScanParallelLayout(bytes.size, mode)) return WasmModuleDecoder(config, bytes)
    val codeBodies = ModuleLayoutScanner(bytes) ?: return WasmModuleDecoder(config, bytes)
    val plan = ParallelDecodingPlanner(bytes.size, codeBodies, mode, availableProcessors)
    val assignments = when (plan) {
        DecodingPlan.Serial -> return WasmModuleDecoder(config, bytes)
        is DecodingPlan.Parallel -> plan.assignments
    }

    val tasks = ArrayList<ParallelTaskScope.() -> DecodeTaskResult>(assignments.size + 1)
    tasks += { decodeModuleSections(config, bytes) }
    for (assignment in assignments) {
        tasks += { decodeCodeBodies(config, bytes, codeBodies, assignment) }
    }

    val results = taskExecutor.execute(tasks)
    val sections = results.first() as DecodeTaskResult.Sections
    var earliestError = sections.result.errorOrNull(sections.offset)
    var requiresDataCount = sections.requiresDataCount
    val bodies = arrayOfNulls<FunctionBody>(codeBodies.size)

    for (resultIndex in 1 until results.size) {
        val result = results[resultIndex] as DecodeTaskResult.Bodies
        requiresDataCount = requiresDataCount || result.requiresDataCount
        for (body in result.bodies) {
            val error = body.result.errorOrNull(body.offset)
            if (error != null && (earliestError == null || error.offset < earliestError.offset)) {
                earliestError = error
            }
            body.result.fold(
                success = { bodies[body.index] = it },
                failure = {},
            )
        }
    }

    if (earliestError != null) return Err(earliestError.error)
    return sections.result.fold(
        success = { builder ->
            builder.functionBodies(List(bodies.size) { index -> checkNotNull(bodies[index]) })
            builder.build(requiresDataCount)
        },
        failure = ::Err,
    )
}

private fun ParallelTaskScope.decodeModuleSections(
    config: ModuleConfig,
    bytes: ByteArray,
): DecodeTaskResult.Sections {
    ensureActive()
    val reader = BufferedWasmBinaryReader(bytes)
    val context = ModuleDecoderContext(config, reader)
    val result = try {
        ModuleSectionsDecoder(
            context = context,
            sectionDecoder = { sectionContext ->
                SectionDecoder(sectionContext) { codeContext ->
                    reader.skip(codeContext.sectionSize.size.toInt())
                    Ok(CodeSection(emptyList()))
                }
            },
        )
    } catch (error: WasmDecodeException) {
        Err(error.error)
    } catch (error: Throwable) {
        Err(WasmDecodeError.IOError(error))
    }
    return DecodeTaskResult.Sections(
        result = result,
        offset = reader.position().toInt(),
        requiresDataCount = context.requiresDataCount,
    )
}

private fun ParallelTaskScope.decodeCodeBodies(
    config: ModuleConfig,
    bytes: ByteArray,
    ranges: CodeBodyRanges,
    assignment: IntArray,
): DecodeTaskResult.Bodies {
    val firstReader = BufferedWasmBinaryReader(bytes, ranges.starts[assignment[0]], ranges.ends[assignment[0]])
    val context = CodeBodyDecoderContext(
        config = config,
        reader = firstReader,
    )
    val bodies = ArrayList<DecodedBody>(assignment.size)
    for (assignmentIndex in assignment.indices) {
        ensureActive()
        val bodyIndex = assignment[assignmentIndex]
        val reader = if (assignmentIndex == 0) {
            firstReader
        } else {
            BufferedWasmBinaryReader(bytes, ranges.starts[bodyIndex], ranges.ends[bodyIndex])
        }
        val body = decodeCodeBody(context, reader, bodyIndex)
        bodies += body
        val failed = body.result.fold(success = { false }, failure = { true })
        if (failed) break
    }
    return DecodeTaskResult.Bodies(
        bodies = bodies.toTypedArray(),
        requiresDataCount = context.requiresDataCount,
    )
}

private fun decodeCodeBody(
    context: CodeBodyDecoderContext,
    reader: BufferedWasmBinaryReader,
    bodyIndex: Int,
): DecodedBody {
    context.reader = reader
    val result = try {
        CodeEntryDecoder(context).fold(
            success = { entry ->
                Ok(FunctionBody(Index.FunctionIndex(bodyIndex.toUInt()), entry.locals, entry.body))
            },
            failure = ::Err,
        )
    } catch (error: WasmDecodeException) {
        Err(error.error)
    } catch (error: Throwable) {
        Err(WasmDecodeError.IOError(error))
    }
    return DecodedBody(
        index = bodyIndex,
        offset = reader.position().toInt(),
        result = result,
    )
}

private fun <T> Result<T, WasmDecodeError>.errorOrNull(offset: Int): DecodeError? = fold(
    success = { null },
    failure = { DecodeError(offset, it) },
)

private sealed interface DecodeTaskResult {

    class Sections(
        val result: Result<ModuleBuilder, WasmDecodeError>,
        val offset: Int,
        val requiresDataCount: Boolean,
    ) : DecodeTaskResult

    class Bodies(
        val bodies: Array<DecodedBody>,
        val requiresDataCount: Boolean,
    ) : DecodeTaskResult
}

private class DecodedBody(
    val index: Int,
    val offset: Int,
    val result: Result<FunctionBody, WasmDecodeError>,
)

private class DecodeError(
    val offset: Int,
    val error: WasmDecodeError,
)
