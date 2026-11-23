@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.jvm

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.compiler.backend.jvm.declarations.DeclarationCallGenerator
import io.github.charlietap.chasm.compiler.backend.jvm.declarations.DeclarationsLowerer
import io.github.charlietap.chasm.compiler.backend.jvm.size.BytecodeSize
import io.github.charlietap.chasm.compiler.backend.jvm.size.DexUnitEstimate
import io.github.charlietap.chasm.compiler.backend.jvm.size.MethodSize
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import org.objectweb.asm.ClassTooLargeException
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail

class ModuleLowererTest {

    private val module = decodeNamedModule(fixture("scalar"))
    private val code = ModuleCompiler(module).getOrThrow { IllegalStateException(it.toString()) }

    @Test
    fun `every function is attempted before the unsupported ones are reported together`() {
        val attempted = ArrayList<Int>()

        val failure = assertFailsWith<UnsupportedFunctions> {
            lower { _, function, _ ->
                synchronized(attempted) { attempted += function.index.index }
                FunctionResult.Rejected("unsupported ${function.index.index}")
            }
        }

        val indices = code.functions.map { function -> function.index.index }
        assertEquals(indices, attempted.sorted())
        assertEquals(indices.associateWith { index -> "unsupported $index" }, failure.reasons)
    }

    @Test
    fun `a class too large for the jvm is reported as ClassTooLarge`() {
        val failure = assertFailsWith<ClassTooLarge> {
            lower { _, _, _ -> throw ClassTooLargeException("Code0", 70_000) }
        }

        assertEquals("generated class Code0 needs 70000 constants, more than the JVM's 65535", failure.message)
    }

    @Test
    fun `lowering on several threads writes what lowering on one does`() {
        listOf("huge", "control", "scalar").forEach { name ->
            val module = decodeNamedModule(fixture(name))
            val code = ModuleCompiler(module).getOrThrow { IllegalStateException(it.toString()) }

            fun classes(
                parallelism: Int,
            ): Map<String, List<Byte>> = Generated().also { output ->
                ModuleLowerer(
                    module = module,
                    code = code,
                    options = JvmOptions(splitInstructions = 64),
                    output = output,
                    declarations = ModuleDeclarationsFactory(module),
                    parallelism = parallelism,
                    functionLowerer = ::FunctionLowerer,
                    signatureLowerer = ::SignatureLowerer,
                    entryLowerer = ::EntryLowerer,
                    bindingLowerer = ::BindingLowerer,
                    declarationCallGenerator = ::DeclarationCallGenerator,
                    declarationsLowerer = ::DeclarationsLowerer,
                    moduleClassLowerer = ::ModuleClassLowerer,
                )
            }.classes.mapValues { (_, bytes) -> bytes.toList() }

            assertEquals(classes(1), classes(8), name)
        }
    }

    @Test
    fun `an Android module's function methods stay within the estimated unit limit`() {

        val fixtures = listOf("huge" to fixture("huge"), "long function" to longFunctionModule(LARGE_STEPS))

        fixtures.forEach { (name, bytes) ->

            val generated = generate(bytes, JvmOptions(methodSize = DexUnitEstimate(), memory = MemoryLowering.UNSAFE))

            val methods = generated.classes.values.flatMap(::measuredMethods).filter { method -> FUNCTION_METHOD.matches(method.node.name) }

            assertTrue(methods.any { method -> CHUNK_METHOD.matches(method.node.name) }, "$name was not split")
            methods.forEach { method ->
                assertTrue(DexUnitEstimate().of(method.node, method.codeLength) <= 5_150, "$name ${method.node.name}")
                assertTrue(method.codeLength <= 32_767, "$name ${method.node.name}")
            }
        }
    }

    @Test
    fun `a JVM module is lowered as before`() {

        val fixtures = listOf("huge", "control", "scalar").map(::fixture) + longFunctionModule(LARGE_STEPS)

        fixtures.forEach { bytes ->

            val default = generate(bytes, JvmOptions())
            val hotspot = generate(bytes, JvmOptions(methodSize = BytecodeSize(8_000)))

            assertEquals(default.classes.keys.toList(), hotspot.classes.keys.toList())
            default.classes.forEach { (name, classBytes) -> assertContentEquals(classBytes, hotspot.classes.getValue(name), name) }
            default.classes.values.flatMap(::measuredMethods).filter { method -> FUNCTION_METHOD.matches(method.node.name) }.forEach { method ->
                assertTrue(method.codeLength <= 8_000, method.node.name)
            }
        }
    }

    @Test
    fun `lowering is deterministic under the unit estimate`() {
        listOf(fixture("huge"), longFunctionModule(LARGE_STEPS)).forEach { bytes ->

            val first = generate(bytes, JvmOptions(methodSize = DexUnitEstimate()))
            val second = generate(bytes, JvmOptions(methodSize = DexUnitEstimate()))

            assertEquals(first.classes.keys.toList(), second.classes.keys.toList())
            first.classes.forEach { (name, classBytes) -> assertContentEquals(classBytes, second.classes.getValue(name), name) }
        }
    }

    @Test
    fun `an Android function whose dispatcher passes the unit limit is refused`() {

        val failure = assertFailsWith<UnsupportedFunctions> { forcedSplit(ANDROID_REFUSED_STEPS, DexUnitEstimate()) }

        val reason = failure.reasons.getValue(0)
        assertTrue(Regex("function needs \\d+ chunks, more than one dispatcher of 5150 estimated DEX code units holds").matches(reason), reason)
    }

    @Test
    fun `a JVM function whose dispatcher passes the short-branch limit is refused`() {

        val failure = assertFailsWith<UnsupportedFunctions> { forcedSplit(JVM_REFUSED_STEPS, BytecodeSize()) }

        val reason = failure.reasons.getValue(0)
        assertTrue(Regex("function needs \\d+ chunks, more than one dispatcher of 32767 bytes holds").matches(reason), reason)
    }

    @Test
    fun `a dispatcher just under the limit is generated`() {

        val android = longestDispatcher(DexUnitEstimate(), ANDROID_REFUSED_STEPS)
        val jvm = longestDispatcher(BytecodeSize(), JVM_REFUSED_STEPS)

        assertTrue(DexUnitEstimate().of(android.node, android.codeLength) <= 5_150)
        assertTrue(jvm.codeLength in 8_001..32_767, "${jvm.codeLength} bytes")
    }

    private fun longestDispatcher(
        methodSize: MethodSize,
        refusedSteps: Int,
    ): MeasuredMethod {

        var lowered = 1
        var refused = refusedSteps
        while (refused - lowered > 1) {
            val middle = (lowered + refused) / 2
            val fits = runCatching { forcedSplit(middle, methodSize) }.exceptionOrNull() !is UnsupportedFunctions
            if (fits) {
                lowered = middle
            } else {
                refused = middle
            }
        }

        assertFailsWith<UnsupportedFunctions> { forcedSplit(lowered + 1, methodSize) }
        return forcedSplit(lowered, methodSize).classes.values.flatMap(::measuredMethods).single { method -> method.node.name == "f0" }
    }

    private fun forcedSplit(
        steps: Int,
        methodSize: MethodSize,
    ): Generated {
        return generate(longFunctionModule(steps), JvmOptions(methodSize = methodSize, splitInstructions = 1))
    }

    private fun generate(
        bytes: ByteArray,
        options: JvmOptions,
    ): Generated {

        val module = decodeNamedModule(bytes)
        val code = ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }

        return Generated().also { output -> ModuleLowerer(module, code, options, output, ModuleDeclarationsFactory(module)) }
    }

    private fun lower(
        functionLowerer: FunctionLowerer,
    ): JvmModule =
        ModuleLowerer(
            module = module,
            code = code,
            options = JvmOptions(),
            output = Generated(),
            declarations = ModuleDeclarationsFactory(module),
            parallelism = 4,
            functionLowerer = functionLowerer,
            signatureLowerer = { _, _ -> fail("signatures are lowered after every function") },
            entryLowerer = { _, _, _, _, _ -> fail("entries are lowered after every function") },
            bindingLowerer = { _, _ -> fail("the binding is lowered after every function") },
            declarationCallGenerator = { _, _ -> fail("declarations are lowered after every function") },
            declarationsLowerer = { _, _, _ -> fail("declarations are lowered after every function") },
            moduleClassLowerer = { _, _, _, _, _, _ -> fail("the module class is lowered after every function") },
        )

    private companion object {
        const val LARGE_STEPS = 20_000
        const val ANDROID_REFUSED_STEPS = 2_000
        const val JVM_REFUSED_STEPS = 4_000
        val FUNCTION_METHOD = Regex("f\\d+(\\\$c\\d+)?")
        val CHUNK_METHOD = Regex("f\\d+\\\$c\\d+")
    }
}
