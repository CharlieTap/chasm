package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Exports
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.exports as embeddingExports
import io.github.charlietap.chasm.embedding.invoke as embeddingInvoke

interface TestRunner {

    val store: Store

    val config: RuntimeConfig

    suspend fun instance(): ChasmResult<Instance, InstantiationError>

    fun invoke(
        name: String,
        arguments: List<ExecutionValue> = [],
    ): ChasmResult<List<ExecutionValue>, ExecutionError>

    fun exports(): ChasmResult<Exports, InstantiationError>
}

class IntegrationTestRunner(
    override val store: Store,
    override val config: RuntimeConfig,
    private val instance: ChasmResult<Instance, InstantiationError>,
) : TestRunner {

    override suspend fun instance(): ChasmResult<Instance, InstantiationError> {
        return instance
    }

    override fun invoke(
        name: String,
        arguments: List<ExecutionValue>,
    ): ChasmResult<List<ExecutionValue>, ExecutionError> {
        return when (instance) {
            is ChasmResult.Success -> embeddingInvoke(instance.result, name, arguments)
            is ChasmResult.Error -> instance
        }
    }

    override fun exports(): ChasmResult<Exports, InstantiationError> {
        return instance.map { instance ->
            embeddingExports(instance)
        }
    }
}
