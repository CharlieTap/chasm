package io.github.charlietap.chasm.corpus.imports

import io.github.charlietap.chasm.corpus.CorpusTimingRecorder
import io.github.charlietap.chasm.corpus.value.bits
import io.github.charlietap.chasm.corpus.value.defaultResults
import io.github.charlietap.chasm.corpus.value.reference
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.host.HostStack
import io.github.charlietap.chasm.host.writeReference
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.corpus.lib.fixture.FixtureImport
import kotlin.contextOf

internal typealias StubFunctionFactory = (Store, FunctionType, FixtureImport?, MutableMap<String, ImportCapture>, CorpusTimingRecorder) -> Function

internal fun StubFunctionFactory(
    store: Store,
    type: FunctionType,
    import: FixtureImport?,
    captures: MutableMap<String, ImportCapture>,
    timings: CorpusTimingRecorder,
): Function {
    val capture = import?.stub?.capture
    if (capture != null) {
        val maxCalls = capture.maxCalls ?: Int.MAX_VALUE
        val existing = captures[capture.name]
        if (existing == null) {
            captures[capture.name] = ImportCapture(maxCalls)
        } else {
            existing.maxCalls = maxOf(existing.maxCalls, maxCalls)
        }
    }

    val parameterCount = type.params.types.size
    val trap = import?.stub?.trap
    val defaults = defaultResults(type, import)

    return function(store, type) { parameters, results ->
        val stack = contextOf<HostStack>()
        if (capture != null) {
            timings.harness {
                val params = List(parameterCount) { index -> stack[parameters + index] }
                captures.getValue(capture.name).append(capture, params)
            }
        }
        trap?.let(::error)
        defaults.forEachIndexed { index, value ->
            when (value) {
                is NumberValue<*> -> {
                    stack[results + index] = value.bits()
                }
                is ReferenceValue -> {
                    results.writeReference(index, value.reference())
                }
                else -> error("Unsupported stub result $value")
            }
        }
    }
}
