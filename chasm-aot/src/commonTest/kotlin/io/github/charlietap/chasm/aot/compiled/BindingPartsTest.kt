package io.github.charlietap.chasm.aot.compiled

import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BindingPartsTest {

    @Test
    fun `a module binding more resources than one class holds compiles and runs`() {
        val fixture = CompiledFixture(globalsModule(GLOBALS))

        assertTrue(fixture.generated.classes.keys.any { name -> name.endsWith("BindingPart3") })
        listOf(0, 7_999, 8_000, 16_123, GLOBALS - 1).forEach { index ->
            assertEquals(listOf(I32(index * 3)), fixture.call("g$index"), "g$index")
        }
    }

    private fun globalsModule(
        count: Int,
    ): ByteArray {
        val module = ByteArrayOutputStream()
        module.write(byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00))
        module.section(1, vector(1) { byteArrayOf(0x60, 0x00, 0x01, 0x7f) })
        module.section(3, vector(count) { byteArrayOf(0x00) })
        module.section(6, vector(count) { index -> byteArrayOf(0x7f, 0x01, 0x41) + signed(index * 3) + 0x0b })
        module.section(7, vector(count) { index -> name("g$index") + 0x00 + unsigned(index) })
        module.section(10, vector(count) { index -> (byteArrayOf(0x00, 0x23) + unsigned(index) + 0x0b).let { body -> unsigned(body.size) + body } })
        return module.toByteArray()
    }

    private fun ByteArrayOutputStream.section(
        id: Int,
        contents: ByteArray,
    ) {
        write(id)
        write(unsigned(contents.size))
        write(contents)
    }

    private fun vector(
        count: Int,
        item: (Int) -> ByteArray,
    ): ByteArray = ByteArrayOutputStream().apply {
        write(unsigned(count))
        repeat(count) { index -> write(item(index)) }
    }.toByteArray()

    private fun name(
        text: String,
    ): ByteArray = text.encodeToByteArray().let { bytes -> unsigned(bytes.size) + bytes }

    private fun unsigned(
        value: Int,
    ): ByteArray {
        val bytes = ByteArrayOutputStream()
        var remaining = value
        do {
            val byte = remaining and 0x7f
            remaining = remaining ushr 7
            bytes.write(if (remaining != 0) byte or 0x80 else byte)
        } while (remaining != 0)
        return bytes.toByteArray()
    }

    private fun signed(
        value: Int,
    ): ByteArray {
        val bytes = ByteArrayOutputStream()
        var remaining = value
        while (true) {
            val byte = remaining and 0x7f
            remaining = remaining shr 7
            val done = (remaining == 0 && byte and 0x40 == 0) || (remaining == -1 && byte and 0x40 != 0)
            bytes.write(if (done) byte else byte or 0x80)
            if (done) return bytes.toByteArray()
        }
    }

    private companion object {
        const val GLOBALS = 25_000
    }
}
