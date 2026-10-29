package io.github.charlietap.chasm.benchmark.coremark

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.aot.CompileConfig
import io.github.charlietap.chasm.aot.CompileOutput
import io.github.charlietap.chasm.aot.CompiledModule
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
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import kotlin.time.Clock

fun main(
    args: Array<String>,
) = runCoremark(args) { config ->
    val bytes = Resource("benchmark/coremark.wasm").readBytes()
    val classes = GeneratedClasses()
    val compileConfig = CompileConfig("chasm.benchmark.coremark") {
        meterFuel(config.meterFuel)
        interruptible(config.interruptible)
    }
    val artifact = compile(bytes, classes, compileConfig).expect("CoreMark compiles")
    val compiled = classes.loadClass(artifact.moduleClassName).getField("INSTANCE").get(null) as CompiledModule

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

private class GeneratedClasses : ClassLoader(CompiledModule::class.java.classLoader), CompileOutput {
    private val classes = HashMap<String, ByteArray>()
    private val resources = HashMap<String, ByteArray>()

    override fun classFile(
        internalName: String,
        bytes: ByteArray,
    ) {
        classes[internalName] = bytes
    }

    override fun resource(
        path: String,
        write: (OutputStream) -> Unit,
    ) {
        resources[path] = ByteArrayOutputStream().also(write).toByteArray()
    }

    override fun findClass(
        name: String,
    ): Class<*> {
        val bytes = classes[name.replace('.', '/')] ?: throw ClassNotFoundException(name)
        return defineClass(name, bytes, 0, bytes.size)
    }

    override fun getResourceAsStream(
        name: String,
    ): InputStream? =
        resources[name.removePrefix("/")]?.inputStream() ?: super.getResourceAsStream(name)
}
