package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.flatMap
import io.github.charlietap.chasm.import.Import

fun testRunner(
    fileName: String,
    fileDirectory: String,
    arguments: List<ExecutionValue> = emptyList(),
    store: Store = store(),
    imports: List<Import> = emptyList(),
    functionName: String = fileName.replace(".wasm", ""),
    setupFunctions: List<Pair<String, List<ExecutionValue>>> = emptyList(),
): ChasmResult<List<ExecutionValue>, ChasmError> {

    val byteStream = Resource(fileDirectory + fileName).readBytes()
    val reader = FakeSourceReader(byteStream)

    return module(reader)
        .flatMap { module ->
            instance(store, module, imports)
        }.flatMap { instance ->

            setupFunctions.forEach { (function, args) ->
                invoke(store, instance, function, args)
            }

            invoke(
                store,
                instance,
                functionName,
                arguments,
            )
        }
}
