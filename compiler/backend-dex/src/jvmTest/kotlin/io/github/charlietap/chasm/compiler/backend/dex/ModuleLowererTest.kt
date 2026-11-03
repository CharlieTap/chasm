package io.github.charlietap.chasm.compiler.backend.dex

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
}
