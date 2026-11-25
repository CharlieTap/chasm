package io.github.charlietap.chasm.corpus.step

import io.github.charlietap.chasm.corpus.ext.array
import io.github.charlietap.chasm.corpus.ext.string
import io.github.charlietap.chasm.corpus.value.describe
import io.github.charlietap.chasm.corpus.value.matches
import io.github.charlietap.chasm.corpus.value.parseValue
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.corpus.lib.CorpusResult
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal typealias FunctionInvokeStepRunner = (StepContext, JsonObject) -> CorpusResult

internal fun FunctionInvokeStepRunner(
    context: StepContext,
    step: JsonObject,
): CorpusResult {
    return FunctionInvokeStepRunner(
        context = context,
        step = step,
        hostEffectsVerifier = ::HostEffectsVerifier,
    )
}

private fun FunctionInvokeStepRunner(
    context: StepContext,
    step: JsonObject,
    hostEffectsVerifier: HostEffectsVerifier,
): CorpusResult {
    val setup = context.setup
    val function = step.string("function")
    val functionType = setup.functionType(function)
    val params = step.array("params").mapIndexed { index, value ->
        parseValue(value.jsonObject, context.captures, functionType?.params?.types?.getOrNull(index))
    }
    val expected = step.array("results")
    val trap = step["trap"]?.jsonPrimitive?.contentOrNull
    val hostEffects = step["host_effects"]?.jsonObject
    val expectedExitCode = hostEffects?.get("exit_code")?.jsonPrimitive?.intOrNull

    val results = when (val invocation = context.timings.execute { invoke(setup.instance, function, params) }) {
        is ChasmResult.Success -> invocation.result
        is ChasmResult.Error -> {
            if (trap != null) {
                return CorpusResult.Success
            }
            exitFailure(context, invocation.error, hostEffects, expectedExitCode, hostEffectsVerifier)?.let { failure -> return failure }
            emptyList()
        }
    }

    if (trap != null) {
        return context.failure("expected function invocation to trap", expected = trap, actual = "success")
    }

    if (expected.size != results.size) {
        return context.failure("result count mismatch", expected = expected.size.toString(), actual = results.size.toString())
    }

    hostEffects?.let { effects ->
        hostEffectsVerifier(context, effects)?.let { failure -> return failure }
    }

    return matchResults(context, expected, results, functionType)
}

private fun exitFailure(
    context: StepContext,
    error: ExecutionError,
    hostEffects: JsonObject?,
    expectedExitCode: Int?,
    hostEffectsVerifier: HostEffectsVerifier,
): CorpusResult? {
    val actualExitCode = hostFunctionExitCodeOrNull(error)
    if (hostEffects == null || expectedExitCode == null || actualExitCode == null) {
        return context.failure("function invocation failed", error.toString())
    }
    if (expectedExitCode != actualExitCode) {
        return context.failure(
            "exit code mismatch",
            detail = capturedOutput(context),
            expected = expectedExitCode.toString(),
            actual = actualExitCode.toString(),
        )
    }
    return hostEffectsVerifier(context, hostEffects)
}

private fun matchResults(
    context: StepContext,
    expected: JsonArray,
    results: List<ExecutionValue>,
    functionType: FunctionType?,
): CorpusResult {
    expected.zip(results).forEachIndexed { index, (expectedValue, actualValue) ->
        val expectedObject = expectedValue.jsonObject
        expectedObject["capture"]?.jsonPrimitive?.contentOrNull?.let { capture ->
            context.captures[capture] = actualValue
            return@forEachIndexed
        }
        if (expectedObject["value"] == null) {
            return@forEachIndexed
        }

        val parsed = parseValue(expectedObject, context.captures, functionType?.results?.types?.getOrNull(index))
        if (!parsed.matches(actualValue)) {
            return context.failure(
                "result mismatch",
                expected = "result[$index]=${parsed.describe()}",
                actual = "result[$index]=${actualValue.describe()}",
            )
        }
    }
    return CorpusResult.Success
}

private fun capturedOutput(
    context: StepContext,
): String {
    val imports = context.setup.imports
    val stdout = imports.stdout.toByteArray().decodeToString().take(MAX_FAILURE_OUTPUT)
    val stderr = imports.stderr.toByteArray().decodeToString().take(MAX_FAILURE_OUTPUT)
    return "stdout=$stdout stderr=$stderr"
}

private fun hostFunctionExitCodeOrNull(error: ExecutionError): Int? {

    val trap = (error as? ExecutionError.Trapped)?.trap ?: return null
    if (trap.reason != TrapReason.HOST_FUNCTION_FAILURE) {
        return null
    }

    val message = trap.message
    val start = message.indexOf(HOST_FUNCTION_ERROR_PREFIX).takeIf { index -> index >= 0 } ?: return null
    val valueStart = start + HOST_FUNCTION_ERROR_PREFIX.length
    val valueEnd = message.indexOf(")", valueStart).takeIf { index -> index >= 0 } ?: return null
    return message.substring(valueStart, valueEnd).toIntOrNull()
}

private const val HOST_FUNCTION_ERROR_PREFIX = "HostFunctionError(error="
private const val MAX_FAILURE_OUTPUT = 2048
