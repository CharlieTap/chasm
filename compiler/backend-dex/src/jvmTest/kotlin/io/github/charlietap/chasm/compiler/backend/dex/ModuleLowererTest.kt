@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.dex

import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.compiler.backend.abi.binaryName
import io.github.charlietap.chasm.compiler.backend.analysis.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
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
                declarations = ModuleDeclarationsFactory(module),
                trapSites = emptyMap(),
                resources = CollectedResources(),
                limits = MetadataFiles.DEX_LIMITS,
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
    fun `the module names every generated class once`() {
        val generated = generate(decodeModule(fixture("calls")))
        val classes = generated.dex
            .flatMap { bytes -> DexBackedDexFile(Opcodes.forApi(MIN_API), bytes).classes }
            .map { classDef -> binaryName(classDef.type) }

        assertEquals(classes.sorted(), generated.classes.sorted())
        assertEquals(generated.classes.distinct(), generated.classes)
        assertEquals("chasm.dex.WasmModule", generated.moduleClass)
    }

    @Test
    fun `entries and the installer leave code files when a module spans several files`() {
        val module = decodeModule(fixture("calls"))
        val code = ModuleCompiler(module).getOrThrow { IllegalStateException(it.toString()) }
        val generated = ModuleLowerer(
            module = module,
            code = code,
            options = TRACED,
            declarations = ModuleDeclarationsFactory(module),
            trapSites = emptyMap(),
            resources = CollectedResources(),
            limits = MetadataFiles.DEX_LIMITS,
            layout = ::filePerBatchLayout,
            functionLowerer = ::FunctionLowerer,
            entryLowerer = ::EntryLowerer,
            installLowerer = ::InstallLowerer,
        )
        val files = generated.dex.map { bytes -> DexBackedDexFile(Opcodes.forApi(MIN_API), bytes).classes.map { classDef -> binaryName(classDef.type) } }
        val codeFiles = files.filter { classes -> classes.any { name -> name.substringAfterLast('$').startsWith("Code") } }

        assertTrue(files.size > 1)
        assertEquals(files.flatten().distinct(), files.flatten())
        assertTrue(codeFiles.flatten().none { name -> name.substringAfterLast('$').startsWith("Entry") || name.substringAfterLast('$').startsWith("Install") })
        assertEquals(generate(module).classes.sorted(), generated.classes.sorted())
    }

    @Test
    fun `stack traces add a sites resource and data segments a data resource`() {
        val module = decodeModule(fixture("memory"))
        val code = ModuleCompiler(module).getOrThrow { IllegalStateException(it.toString()) }
        val traced = CollectedResources()
        val untraced = CollectedResources()

        lower(module, code, TRACED, traced)
        lower(module, code, DexOptions(), untraced)

        assertEquals(setOf("chasm/dex/WasmModule.chasmdata", "chasm/dex/WasmModule.sites"), traced.resources.keys)
        assertEquals(setOf("chasm/dex/WasmModule.chasmdata"), untraced.resources.keys)
    }
}
