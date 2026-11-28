package io.github.charlietap.chasm.compiled.internal

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.compiled.format.ArtifactTarget
import io.github.charlietap.chasm.compiler.backend.analysis.gc.STORAGE_FAMILY
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StoragePlans
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StructLanes
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StructSizes
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.runtime.compiled.gc.StorageLane
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StructLanesTest {

    private val types = definedTypes("storage_plans")
    private val sizes = StructSizes(ArtifactTarget.ART)
    private val lanes = StructLanes(StoragePlans(types, STORAGE_FAMILY), types, sizes)
    private val layouts = CompiledStructLayouts(types, FakeCompiledModule(lanes = lanes.encode()))

    @Test
    fun `the runtime reads back the lanes packed for every struct the compiler can store`() {

        assertTrue(lanes.structs.isNotEmpty())

        lanes.structs.forEach { (index, storage) ->
            val layout = checkNotNull(layouts.layout(index)) { "type $index has no layout" }
            val expected = storage.fields.map { field -> field.lanes.map { lane -> StorageLane(lane.index, lane.spilled) } }
            assertEquals(sizes.bytes(storage), layout.bytes, "type $index")
            assertEquals(expected, layout.fields.map { field -> field.lanes }, "type $index")
        }
    }

    @Test
    fun `a struct the compiler cannot store has no layout`() {
        assertNull(layouts.layout(VECTORS))
    }

    private fun definedTypes(name: String): List<DefinedType> {

        val bytes = checkNotNull(javaClass.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
            .use { stream -> stream.readBytes() }
        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { error -> IllegalStateException(error.toString()) }

        return WasmModuleValidator(config, module).getOrThrow { error -> IllegalStateException(error.toString()) }.definedTypes
    }

    private companion object {
        const val VECTORS = 23
    }
}
