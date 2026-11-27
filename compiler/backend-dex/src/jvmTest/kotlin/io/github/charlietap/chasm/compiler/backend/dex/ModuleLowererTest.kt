package io.github.charlietap.chasm.compiler.backend.dex

import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.compiler.backend.abi.EntryShape
import io.github.charlietap.chasm.compiler.backend.abi.binaryName
import io.github.charlietap.chasm.compiler.backend.analysis.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
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

    @Test
    fun `functions returning a number and a reference in different orders have separate entries`() {

        val groups = entryGroups(fixture("result_lanes"))
        val numberFirst = groups.single { group -> NUMBER_THEN_REFERENCE_FUNCTION in group.members }
        val referenceFirst = groups.single { group -> REFERENCE_THEN_NUMBER_FUNCTION in group.members }

        assertEquals(listOf(false, true), numberFirst.shape.referenceResults)
        assertEquals(listOf(true, false), referenceFirst.shape.referenceResults)
    }

    @Test
    fun `a module whose function types reach deeply nested types is lowered promptly`() {

        val groups = CompletableFuture.supplyAsync { entryGroups(fixture("deep_types")) }.get(60, TimeUnit.SECONDS)

        assertEquals(1, groups.size)
    }

    @Test
    fun `functions whose references differ only in heap type share an entry class`() {

        val groups = entryGroups(fixture("reference_heap_types"))

        assertEquals(listOf(listOf(0, 1)), groups.map { group -> group.members })
    }

    @Test
    fun `functions of one entry shape share entry classes`() {

        val entryClassCounts = mapOf("gc" to 5, "storage" to 7)

        entryClassCounts.forEach { (name, entryClasses) ->
            assertEquals(entryClasses, entryGroups(fixture(name)).size, name)
        }
    }

    private fun entryGroups(bytes: ByteArray): List<LoweredEntryGroup> {

        val module = decodeModule(bytes)
        val code = ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }
        val groups = ArrayList<LoweredEntryGroup>()

        ModuleLowerer(
            module = module,
            code = code,
            options = DexOptions(),
            declarations = ModuleDeclarationsFactory(module),
            trapSites = emptyMap(),
            resources = CollectedResources(),
            limits = MetadataFiles.DEX_LIMITS,
            layout = ::partitionedLayout,
            functionLowerer = ::FunctionLowerer,
            entryLowerer = { file, group, entryPath ->
                groups += LoweredEntryGroup(group.members.map { member -> member.index }, group.shape)
                EntryLowerer(file, group, entryPath)
            },
            installLowerer = ::InstallLowerer,
        )

        return groups
    }

    private data class LoweredEntryGroup(
        val members: List<Int>,
        val shape: EntryShape,
    )

    private companion object {
        const val NUMBER_THEN_REFERENCE_FUNCTION = 1
        const val REFERENCE_THEN_NUMBER_FUNCTION = 2
    }
}
