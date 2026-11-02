package io.github.charlietap.chasm.aot.corpus.step

import io.github.charlietap.chasm.aot.corpus.ext.array
import io.github.charlietap.chasm.aot.corpus.ext.string
import io.github.charlietap.chasm.aot.corpus.value.describeBits
import io.github.charlietap.chasm.aot.corpus.value.parseValue
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.ExecutionValue
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
): CorpusResult = FunctionInvokeStepRunner(
    context = context,
    step = step,
    hostEffectsVerifier = ::HostEffectsVerifier,
)

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

    val results = when (val invocation = invoke(setup.store, setup.instance, function, params)) {
        is ChasmResult.Success -> invocation.result
        is ChasmResult.Error -> {
            if (trap != null) return CorpusResult.Success
            exitFailure(context, invocation.error.toString(), hostEffects, expectedExitCode, hostEffectsVerifier)?.let { return it }
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
        hostEffectsVerifier(context, effects)?.let { return it }
    }

    return matchResults(context, expected, results, functionType)
}

private fun exitFailure(
    context: StepContext,
    error: String,
    hostEffects: JsonObject?,
    expectedExitCode: Int?,
    hostEffectsVerifier: HostEffectsVerifier,
): CorpusResult? {
    val actualExitCode = error.hostFunctionExitCodeOrNull()
    if (hostEffects == null || expectedExitCode == null || actualExitCode == null) {
        return context.failure("function invocation failed", error)
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
        if (expectedObject["value"] == null) return@forEachIndexed

        val parsed = parseValue(expectedObject, context.captures, functionType?.results?.types?.getOrNull(index))
        if (parsed.toLongFromBoxed() != actualValue.toLongFromBoxed()) {
            return context.failure(
                "result mismatch",
                expected = "result[$index]=${parsed.describeBits()}",
                actual = "result[$index]=${actualValue.describeBits()}",
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

private fun String.hostFunctionExitCodeOrNull(): Int? {
    val start = indexOf(HOST_FUNCTION_ERROR_PREFIX).takeIf { it >= 0 } ?: return null
    val valueStart = start + HOST_FUNCTION_ERROR_PREFIX.length
    val valueEnd = indexOf(")", valueStart).takeIf { it >= 0 } ?: return null
    return substring(valueStart, valueEnd).toIntOrNull()
}

private const val HOST_FUNCTION_ERROR_PREFIX = "ExecutionError(error=HostFunctionError(error="
private const val MAX_FAILURE_OUTPUT = 2048
