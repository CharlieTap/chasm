package io.github.charlietap.chasm.aot.corpus.imports

import io.github.charlietap.chasm.aot.corpus.value.defaultResults
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.host.HostStack
import io.github.charlietap.chasm.runtime.ext.toExecutionValue
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.corpus.lib.fixture.FixtureImport
import kotlin.contextOf

internal typealias StubFunctionFactory = (Store, FunctionType, FixtureImport?, MutableMap<String, ImportCapture>) -> Function

internal fun StubFunctionFactory(
    store: Store,
    type: FunctionType,
    import: FixtureImport?,
    captures: MutableMap<String, ImportCapture>,
): Function {
    import?.stub?.capture?.let { capture ->
        val maxCalls = capture.maxCalls ?: Int.MAX_VALUE
        val existing = captures[capture.name]
        if (existing == null) {
            captures[capture.name] = ImportCapture(maxCalls)
        } else {
            existing.maxCalls = maxOf(existing.maxCalls, maxCalls)
        }
    }

    return function(store, type) { parameters, results ->
        val stack = contextOf<HostStack>()
        val params = List(type.params.types.size) { index ->
            stack[parameters + index].toExecutionValue(type.params.types[index])
        }
        import?.stub?.capture?.let { capture ->
            captures.getValue(capture.name).append(capture, params)
        }
        import?.stub?.trap?.let(::error)
        defaultResults(type, import).forEachIndexed { index, value ->
            stack[results + index] = value.toLongFromBoxed()
        }
    }
}
