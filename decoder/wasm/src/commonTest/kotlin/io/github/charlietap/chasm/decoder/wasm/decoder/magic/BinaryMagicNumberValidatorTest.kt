package io.github.charlietap.chasm.decoder.wasm.decoder.magic

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.ext.MAGIC_NUMBER
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUBytesReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryMagicNumberValidatorTest {

    @Test
    fun `can decode magic number`() {

        val reader = FakeUBytesReader { _ ->
            Ok(MAGIC_NUMBER)
        }

        val actual = io.github.charlietap.chasm.decoder.wasm.decoder.magic.BinaryMagicNumberValidator(reader)

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `returns invalid wasm file error when magic number does not match`() {

        val invalidMagicNumber = ubyteArrayOf(0x00u, 0x00u, 0x00u, 0x00u)

        val reader = FakeUBytesReader { _ ->
            Ok(invalidMagicNumber)
        }

        val actual = io.github.charlietap.chasm.decoder.wasm.decoder.magic.BinaryMagicNumberValidator(reader)

        assertEquals(Err(WasmDecodeError.InvalidWasmFile(invalidMagicNumber)), actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)

        val actual = io.github.charlietap.chasm.decoder.wasm.decoder.magic.BinaryMagicNumberValidator(reader)

        assertEquals(err, actual)
    }
}
