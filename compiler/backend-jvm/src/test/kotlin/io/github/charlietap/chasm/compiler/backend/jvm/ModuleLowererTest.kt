package io.github.charlietap.chasm.compiler.backend.jvm

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import org.objectweb.asm.ClassTooLargeException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
                    parallelism = parallelism,
                    functionLowerer = ::FunctionLowerer,
                    signatureLowerer = ::SignatureLowerer,
                    entryLowerer = ::EntryLowerer,
                    bindingLowerer = ::BindingLowerer,
                    moduleClassLowerer = ::ModuleClassLowerer,
                )
            }.classes.mapValues { (_, bytes) -> bytes.toList() }

            assertEquals(classes(1), classes(8), name)
        }
    }

    private fun lower(
        functionLowerer: FunctionLowerer,
    ): JvmModule =
        ModuleLowerer(
            module = module,
            code = code,
            options = JvmOptions(),
            output = Generated(),
            parallelism = 4,
            functionLowerer = functionLowerer,
            signatureLowerer = { _, _ -> fail("signatures are lowered after every function") },
            entryLowerer = { _, _, _, _, _ -> fail("entries are lowered after every function") },
            bindingLowerer = { _, _ -> fail("the binding is lowered after every function") },
            moduleClassLowerer = { _, _, _, _ -> fail("the module class is lowered after every function") },
        )
}
