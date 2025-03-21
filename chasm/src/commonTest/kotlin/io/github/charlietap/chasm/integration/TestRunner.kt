package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.runtime.value.ExecutionValue

fun testRunner(
    fileName: String,
    fileDirectory: String,
    arguments: List<ExecutionValue> = emptyList(),
    store: Store = Store(),
    imports: List<Import> = emptyList(),
    functionName: String = fileName.replace(".wasm", ""),
    setupFunctions: List<Pair<String, List<ExecutionValue>>> = emptyList(),
    config: RuntimeConfig = RuntimeConfig(),
): ChasmResult<List<ExecutionValue>, ChasmError> {

    val byteStream = Resource(fileDirectory + fileName).readBytes()
    val reader = FakeSourceReader(byteStream)

    return module(reader)
        .flatMap { module ->
            validate(module)
        }.flatMap { module ->
            instance(store, module, imports, config)
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
