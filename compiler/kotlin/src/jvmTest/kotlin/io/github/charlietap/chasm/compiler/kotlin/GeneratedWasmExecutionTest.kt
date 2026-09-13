package io.github.charlietap.chasm.compiler.kotlin

import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.dsl.imports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.program.ProgramCompiler
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

class GeneratedWasmExecutionTest {
    @Test
    fun `failed backend rolls back appended code before start executes`() {
        val bytes = checkNotNull(javaClass.getResourceAsStream("/wasm1.wasm")).use { it.readBytes() }
        val module = module(bytes).expect("decode")
        var fail = true
        var program: Program? = null
        val firstIps = mutableListOf<Int>()
        val compiler = ProgramCompiler { current, firstIp, _, _ ->
            program = current
            firstIps.add(firstIp)
            if (fail) InstantiationError.ProgramCompilationFailed("deliberate failure") else null
        }
        val store = store(compiler)
        try {
            val bindings = imports(store) {
                function {
                    moduleName = "env"
                    entityName = "host"
                    type {
                        params { i32() }
                        results { i32() }
                    }
                    reference { args, results -> results.writeI32(0, args.readI32(0)) }
                }
            }
            assertIs<ChasmResult.Error<*>>(instance(store, module, bindings))
            assertEquals(firstIps.single(), checkNotNull(program).size)
            fail = false
            val instance = instance(store, module, bindings).expect("retry after rollback")
            assertEquals(firstIps[0], firstIps[1])
            assertEquals(NumberValue.I32(1243), invoke(store, instance, "started").expect("start after retry").single())
        } finally {
            dropStore(store)
        }
    }

    @Test
    fun `generated blocks preserve Wasm state control flow traps and instance bindings`() {
        val bytes = checkNotNull(javaClass.getResourceAsStream("/wasm1.wasm")).use { it.readBytes() }
        val module = module(bytes).expect("decode")
        val directory = Files.createTempDirectory("chasm-wasm-aot").toFile()
        val reports = mutableListOf<KotlinCompilationReport>()
        try {
            JvmKotlinProgramCompiler(directory, KotlinCompilationMode.PREPARE, countExecutions = true, onCompilation = reports::add).use { compiler ->
                var sharedContinuations = 0
                val store = store(
                    ProgramCompiler { program, firstIp, instructions, entries ->
                        val calls = instructions.indices.filter { instructions[it] is ControlInstruction.WasmCall || instructions[it] is ControlInstruction.HostCall }
                        val original = calls.associateWith { program.instructions[firstIp + it] }
                        val error = compiler.compile(program, firstIp, instructions, entries)
                        if (error == null) {
                            original.forEach { (index, dispatcher) -> assertSame(dispatcher, program.instructions[firstIp + index], "Call-site metadata must remain installed") }
                            calls.forEach { index ->
                                val entry = entries.last { it <= firstIp + index }
                                if (index + 1 < instructions.size && program.instructions[entry] === program.instructions[firstIp + index + 1]) sharedContinuations++
                            }
                        }
                        error
                    },
                )
                try {
                    fun bindings(delta: Int) = imports(store) {
                        function {
                            moduleName = "env"
                            entityName = "host"
                            type {
                                params { i32() }
                                results { i32() }
                            }
                            reference { args, results -> results.writeI32(0, args.readI32(0) + delta) }
                        }
                    }
                    val first = instance(store, module, bindings(10)).expect("instantiate")
                    assertTrue(compiler.generatedBlockExecutions > 0, "start function must use generated code")

                    fun call(name: String, vararg args: ExecutionValue) = invoke(store, first, name, args.toList()).expect(name).single()

                    fun i32(name: String, vararg args: Int) = (call(name, *args.map { NumberValue.I32(it) }.toTypedArray()) as NumberValue.I32).value
                    assertEquals(1243, i32("started"))
                    assertEquals(5050, i32("sum", 100))
                    assertEquals(3628800, i32("factorial", 10))
                    assertEquals(20000, i32("depth", 20000))
                    assertTrue(sharedContinuations > 0, "Function entry and call continuation must share a resumable body")
                    assertTrue(reports.last().resumableFunctionCount > 0)
                    assertEquals(15, i32("indirect", 11, 4, 0))
                    assertEquals(7, i32("indirect", 11, 4, 1))
                    assertEquals(45, i32("host", 5))
                    assertEquals(5, i32("swap", 2, 7))
                    assertEquals(listOf(10, 20, 30, 30), listOf(0, 1, 2, -1).map { i32("table_branch", it) })
                    assertEquals(-2, i32("memory16", 65534))
                    assertEquals(1, i32("grow", 1))
                    assertEquals(2, i32("size"))
                    assertEquals(0, i32("load", 65536))
                    assertEquals(-1, i32("grow", 2))
                    assertEquals(2, i32("size"))
                    assertEquals(-3, i32("div", -10, 3))
                    assertEquals(99, i32("counter", 99))
                    assertEquals(7, i32("select", 7, 8, 1))
                    assertEquals(8, i32("select", 7, 8, 0))
                    assertEquals(NumberValue.I64(1920), call("i64", NumberValue.I64(3), NumberValue.I64(5)))
                    assertEquals(NumberValue.I64(2.0.toRawBits()), call("nearest", NumberValue.F64(2.5)))
                    assertEquals(NumberValue.I64((-0.0).toRawBits()), call("nearest", NumberValue.F64(-0.1)))
                    assertEquals(NumberValue.I32(-3), call("trunc", NumberValue.F64(-3.9)))
                    assertTrue((call("float", NumberValue.F32(Float.NaN), NumberValue.F32(1f)) as NumberValue.F32).value.isNaN())
                    assertEquals((-0.0f).toRawBits(), (call("float", NumberValue.F32(0f), NumberValue.F32(-0.0f)) as NumberValue.F32).value.toRawBits())
                    assertIs<ChasmResult.Error<*>>(invoke(store, first, "div", listOf(NumberValue.I32(1), NumberValue.I32(0))))
                    assertIs<ChasmResult.Error<*>>(invoke(store, first, "div", listOf(NumberValue.I32(Int.MIN_VALUE), NumberValue.I32(-1))))
                    assertIs<ChasmResult.Error<*>>(invoke(store, first, "load", listOf(NumberValue.I32(131069))))
                    assertIs<ChasmResult.Error<*>>(invoke(store, first, "indirect", listOf(NumberValue.I32(1), NumberValue.I32(2), NumberValue.I32(2))))
                    assertIs<ChasmResult.Error<*>>(invoke(store, first, "trunc", listOf(NumberValue.F64(Double.NaN))))

                    val second = instance(store, module, bindings(100)).expect("instantiate again")
                    assertTrue(reports.last().cacheHit)
                    assertEquals(NumberValue.I32(1243), invoke(store, second, "started").expect("second start").single())
                    assertEquals(NumberValue.I32(315), invoke(store, second, "host", listOf(NumberValue.I32(5))).expect("second host").single())
                    assertEquals(NumberValue.I32(99), invoke(store, first, "counter", listOf(NumberValue.I32(99))).expect("first global").single())
                } finally {
                    dropStore(store)
                }
            }
        } finally {
            directory.deleteRecursively()
        }
    }
}
