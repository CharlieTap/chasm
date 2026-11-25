package io.github.charlietap.chasm.benchmark.coremark

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.compiled.ClassInput
import io.github.charlietap.chasm.compiled.module
import io.github.charlietap.chasm.compiled.store
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.writeI64
import io.github.charlietap.chasm.jvm.ClassOutput
import io.github.charlietap.chasm.jvm.CompileConfig
import io.github.charlietap.chasm.jvm.compile
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import kotlin.time.Clock

fun main(
    args: Array<String>,
) = runCoremark(args) { config ->
    val bytes = Resource("benchmark/coremark.wasm").readBytes()
    val classes = Files.createTempDirectory("coremark")
    val compileConfig = CompileConfig("chasm.benchmark.coremark.CoremarkModule", runtime = config)
    val info = runBlocking { compile(bytes, ClassOutput.directory(classes), compileConfig) }.expect("CoreMark compiles")
    val module = module(ClassInput.directory(classes), info.config.className).expect("CoreMark loads")

    val store = store(module)
    val clock = HostFunction { _, results -> results.writeI64(0, Clock.System.now().toEpochMilliseconds()) }
    val clockType = FunctionType(ResultType(emptyList()), ResultType(listOf(ValueType.Number(NumberType.I64))))
    val imports = listOf(Import("env", "clock_ms", function(store, clockType, clock)))

    val results = runBlocking { instance(store, module, imports) }.flatMap { instance -> invoke(instance, "run") }
    if (results is ChasmResult.Success<List<ExecutionValue>>) {
        val score = results.result.first() as NumberValue.F32
        println("CoreMark 1.0 : ${score.value}")
    } else {
        println("Benchmark failed with error: $results")
    }
}
