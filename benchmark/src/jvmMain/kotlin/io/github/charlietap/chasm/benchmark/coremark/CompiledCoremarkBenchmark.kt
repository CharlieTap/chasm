@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.benchmark.coremark

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.aot.CompileConfig
import io.github.charlietap.chasm.aot.CompileOutput
import io.github.charlietap.chasm.aot.compile
import io.github.charlietap.chasm.aot.expect
import io.github.charlietap.chasm.aot.instance
import io.github.charlietap.chasm.embedding.addFuel
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.writeI64
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import kotlin.time.Clock

fun main(
    args: Array<String>,
) = runCoremark(args) { config ->
    val bytes = Resource("benchmark/coremark.wasm").readBytes()
    val output = CompileOutput.Memory()
    val compileConfig = CompileConfig("chasm.benchmark.coremark") {
        store(config)
    }
    val compiled = output.load(compile(bytes, output, compileConfig).expect("CoreMark compiles"))

    val store = store(config)
    if (config.meterFuel) addFuel(store, Long.MAX_VALUE)
    val clock = HostFunction { _, results -> results.writeI64(0, Clock.System.now().toEpochMilliseconds()) }
    val clockType = FunctionType(ResultType(emptyList()), ResultType(listOf(ValueType.Number(NumberType.I64))))
    val imports = listOf(Import("env", "clock_ms", function(store, clockType, clock)))

    val results = instance(store, compiled, imports).flatMap { instance -> invoke(store, instance, "run") }
    if (results is ChasmResult.Success<List<ExecutionValue>>) {
        val score = results.result.first() as NumberValue.F32
        println("CoreMark 1.0 : ${score.value}")
    } else {
        println("Benchmark failed with error: $results")
    }
}
