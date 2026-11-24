package io.github.charlietap.chasm.compiler.backend.analysis.gc

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals

class ShapeSizesTest {

    private val layouts = ShapeLayouts(module("shape_sizes").definedTypes, IndexedShapeNames())
    private val sizes = ShapeSizes(ARRAY_OVERHEAD)

    @Test
    fun `a struct is charged its header and fields rounded up to eight bytes`() {
        assertEquals(8, sizes.structBytes(layouts.struct(EMPTY)))
        assertEquals(16, sizes.structBytes(layouts.struct(INT)))
        assertEquals(24, sizes.structBytes(layouts.struct(MIXED)))
    }

    @Test
    fun `an array shift is the log of its element width`() {
        assertEquals(0, sizes.arrayShift(layouts.array(BYTES)))
        assertEquals(1, sizes.arrayShift(layouts.array(CHARS)))
        assertEquals(2, sizes.arrayShift(layouts.array(INTS)))
        assertEquals(3, sizes.arrayShift(layouts.array(LONGS)))
        assertEquals(3, sizes.arrayShift(layouts.array(DOUBLES)))
        assertEquals(2, sizes.arrayShift(layouts.array(REFERENCES)))
    }

    @Test
    fun `an array is charged its elements and the array overhead`() {
        assertEquals(ARRAY_OVERHEAD, sizes.arrayBytes(0, 3))
        assertEquals(80 + ARRAY_OVERHEAD, sizes.arrayBytes(10, 3))
        assertEquals(10 + ARRAY_OVERHEAD, sizes.arrayBytes(10, 0))
    }

    private fun module(name: String): Module {

        val bytes = checkNotNull(javaClass.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
            .use { stream -> stream.readBytes() }
        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { error -> IllegalStateException(error.toString()) }

        return WasmModuleValidator(config, module).getOrThrow { error -> IllegalStateException(error.toString()) }
    }

    private companion object {
        const val ARRAY_OVERHEAD = 24
        const val EMPTY = 0
        const val INT = 1
        const val MIXED = 2
        const val BYTES = 3
        const val CHARS = 4
        const val INTS = 5
        const val LONGS = 6
        const val DOUBLES = 7
        const val REFERENCES = 8
    }
}
