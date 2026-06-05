package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.embedding.ext.externRef
import io.github.charlietap.chasm.embedding.ext.readExternValueAs
import io.github.charlietap.chasm.embedding.fixture.publicImport
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.HostFunction
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.type.externHeapType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import kotlin.test.Test
import kotlin.test.assertEquals

class HostExternInteropTest {

    @Test
    fun `can pass a host backed externref from wasm back into a host function`() {

        val store = publicStore(store())
        val externRefValueType = referenceValueType(
            refNullReferenceType(externHeapType()),
        )

        val someStringType = functionType(
            results = resultType(listOf(externRefValueType)),
        )
        val someString: HostFunction = {
            listOf(externRef("hello world"))
        }

        val lenType = functionType(
            params = resultType(listOf(externRefValueType)),
            results = resultType(listOf(i32ValueType())),
        )
        val len: HostFunction = { values ->
            val reference = values.first() as ReferenceValue
            val string = readExternValueAs<String>(reference)
                .expect("expected host string")
            listOf(i32(requireNotNull(string).length))
        }

        val result = testRunner(
            fileName = "host_externref_interop.wasm",
            fileDirectory = FILE_DIR,
            functionName = "main",
            store = store,
            imports = listOf(
                publicImport("env", "someString", function(store, someStringType, someString)),
                publicImport("env", "len", function(store, lenType, len)),
            ),
        )

        val expected = ChasmResult.Success(listOf(i32(11)))

        assertEquals(expected, result)
    }

    companion object {
        private const val FILE_DIR = "integration/"
    }
}
