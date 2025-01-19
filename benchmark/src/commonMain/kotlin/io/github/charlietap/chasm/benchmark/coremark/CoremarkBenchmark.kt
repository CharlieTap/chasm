package io.github.charlietap.chasm.benchmark.coremark

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.dsl.imports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import kotlinx.datetime.Clock

fun main() {
    val benchmark = CoremarkBenchmark()
    benchmark.run()
}

class CoremarkBenchmark {

    fun run() {
        val bytes = Resource(FILE_DIR + "coremark.wasm").readBytes()
        val store = store()

        val imports = imports(store) {
            function {
                moduleName = "env"
                entityName = "clock_ms"
                type {
                    results { i64() }
                }

                reference {
                    val time = Clock.System.now()
                    listOf(NumberValue.I64(time.toEpochMilliseconds()))
                }
            }
        }

        val results = module(bytes)
            .flatMap { module ->
                instance(store, module, imports)
            }.flatMap { instance ->
                invoke(store, instance, "run")
            }

        if (results is ChasmResult.Success<List<ExecutionValue>>) {
            val score = results.result.first() as NumberValue.F32
            println("CoreMark 1.0 : ${score.value}")
        } else {
            println("Benchmark failed with error: $results")
        }
    }

    companion object {
        private const val FILE_DIR = "src/commonMain/resources/benchmark/"
    }
}
