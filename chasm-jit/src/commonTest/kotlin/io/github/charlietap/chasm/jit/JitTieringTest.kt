@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.memory.growMemory
import io.github.charlietap.chasm.embedding.memory.readBytes
import io.github.charlietap.chasm.embedding.memory.writeBytes
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.corpus.lib.fixture.Fixture
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

class JitTieringTest {

    @Test
    fun lateFramesRunAtArtCompiledSpeed() {
        val frameMicros = onLargeStack(::renderFrames)
        val medianLateFrameMicros = frameMicros.takeLast(LATE_FRAME_COUNT).sorted()[LATE_FRAME_COUNT / 2]

        assertTrue(
            medianLateFrameMicros <= MAX_LATE_FRAME_MICROS,
            "late frames take ${medianLateFrameMicros / 1_000} ms; ART-compiled frames stay under ${MAX_LATE_FRAME_MICROS / 1_000} ms",
        )
    }

    private fun renderFrames(): List<Long> {
        val store = store()
        val module = runBlocking { module(resourceBytes(LUNABOY_WASM), validate = true) }.expect("module")
        val printChar = function(store, FunctionType(ResultType(listOf(ValueType.Number(NumberType.I32))), ResultType(emptyList()))) { _, _ -> }
        val instance = runBlocking { instance(store, module, listOf(Import("spectest", "print_char", printChar))) }.expect("instance")
        val memory = instance.exports.first { export -> export.name == "moonbit.memory" }.value as Memory
        val rom = fixtureRom()
        growMemory(store, memory, 1).expect("memory")
        writeBytes(store, memory, 0, rom).expect("rom")
        invoke(store, instance, "init_extern", listOf(I32(rom.size))).expect("init")

        val frameMicros = List(FRAME_COUNT) {
            val start = System.nanoTime()
            invoke(store, instance, "run_frame_extern").expect("frame")
            (System.nanoTime() - start) / 1_000
        }

        val renderedPixel = readBytes(store, memory, ByteArray(4), RENDERED_BLACK_PIXEL_OFFSET, 4).expect("pixel")
        assertContentEquals(BLACK_PIXEL, renderedPixel)

        return frameMicros
    }

    private fun fixtureRom(): ByteArray {
        val fixture = Json { ignoreUnknownKeys = true }.decodeFromString<Fixture>(resourceBytes(LUNABOY_FIXTURE).decodeToString())
        val romWrite = fixture.tests.single().steps.first { step -> step.getValue("type").jsonPrimitive.content == "memory.write" }
        return Base64.decode(romWrite.getValue("bytes").jsonObject.getValue("value").jsonPrimitive.content)
    }

    private companion object {
        const val LUNABOY_WASM = "corpus/3.0/moonbit_lunaboy.wasm"
        const val LUNABOY_FIXTURE = "corpus/3.0/moonbit_lunaboy.json"
        const val FRAME_COUNT = 60
        const val LATE_FRAME_COUNT = 10
        const val MAX_LATE_FRAME_MICROS = 300_000L
        const val RENDERED_BLACK_PIXEL_OFFSET = 10560
        val BLACK_PIXEL = byteArrayOf(0, 0, 0, -1)
    }
}
