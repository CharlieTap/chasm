package io.github.charlietap.chasm.compiler.backend.dex

import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.compiler.backend.abi.binaryName
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ModuleLowererTest {

    private val module = decodeModule(fixture("scalar"))
    private val code = ModuleCompiler(module).getOrThrow { IllegalStateException(it.toString()) }

    @Test
    fun `every function is attempted before the unsupported ones are reported together`() {
        val attempted = ArrayList<Int>()
        val entries = ArrayList<Int>()

        val failure = assertFailsWith<UnsupportedFunctions> {
            ModuleLowerer(
                module = module,
                code = code,
                options = DexOptions(),
                layout = ::partitionedLayout,
                functionLowerer = { _, function, _, _ ->
                    attempted += function.index.index
                    "unsupported".takeIf { function.index.index % 2 == 0 }
                },
                entryLowerer = { _, group, _ ->
                    entries += group.members.map { member -> member.index }
                    "Entry${group.number}"
                },
                installLowerer = { _, _, _, _ -> emptyList() },
            )
        }

        val indices = code.functions.map { function -> function.index.index }
        assertEquals(indices, attempted)
        assertEquals(emptyList(), entries)
        assertEquals(indices.filter { index -> index % 2 == 0 }.associateWith { "unsupported" }, failure.reasons)
    }

    @Test
    fun `classes to initialize name every generated class except interfaces once`() {
        val generated = generate(decodeModule(fixture("calls")))
        val classes = generated.dex
            .flatMap { bytes -> DexBackedDexFile(Opcodes.forApi(MIN_API), bytes).classes }
            .filterNot { classDef -> AccessFlags.INTERFACE.isSet(classDef.accessFlags) }
            .map { classDef -> binaryName(classDef.type) }

        assertEquals(classes.sorted(), generated.classesToInitialize.sorted())
        assertEquals(generated.classesToInitialize.distinct(), generated.classesToInitialize)
    }

    @Test
    fun `entries and the installer leave code files when a module spans several files`() {
        val module = decodeModule(fixture("calls"))
        val code = ModuleCompiler(module).getOrThrow { IllegalStateException(it.toString()) }
        val generated = ModuleLowerer(
            module = module,
            code = code,
            options = DexOptions(stacktraces = true),
            layout = ::filePerBatchLayout,
            functionLowerer = ::FunctionLowerer,
            entryLowerer = ::EntryLowerer,
            installLowerer = ::InstallLowerer,
        )
        val files = generated.dex.map { bytes -> DexBackedDexFile(Opcodes.forApi(MIN_API), bytes).classes.map { classDef -> binaryName(classDef.type) } }
        val codeFiles = files.filter { classes -> classes.any { name -> name.substringAfterLast('.').startsWith("Code") } }

        assertTrue(files.size > 1)
        assertEquals(files.flatten().distinct(), files.flatten())
        assertTrue(codeFiles.flatten().none { name -> name.substringAfterLast('.').startsWith("Entry") || name.substringAfterLast('.').startsWith("Install") })
        assertEquals(generate(module).classesToInitialize.sorted(), generated.classesToInitialize.sorted())
    }
}
