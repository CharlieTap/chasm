package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Export
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.embedding.exports as embeddingExports
import io.github.charlietap.chasm.embedding.gc as embeddingGc
import io.github.charlietap.chasm.embedding.invoke as embeddingInvoke

class Interpreter : Engine {

    override fun name(): String = "interpreter"

    override fun instantiate(
        bytes: ByteArray,
        store: Store,
        imports: List<Import>,
        config: RuntimeConfig,
    ): ChasmResult<Instance, ChasmError> =
        module(bytes)
            .flatMap { module ->
                validate(module)
            }.flatMap { module ->
                instance(store, module, imports, config)
            }

    override fun invoke(
        store: Store,
        instance: Instance,
        name: String,
        arguments: List<ExecutionValue>,
    ): ChasmResult<List<ExecutionValue>, ChasmError> = embeddingInvoke(store, instance, name, arguments)

    override fun exports(
        instance: Instance,
    ): List<Export> = embeddingExports(instance)

    override fun gc(
        store: Store,
    ): ChasmResult<Unit, ChasmError> = embeddingGc(store)
}
