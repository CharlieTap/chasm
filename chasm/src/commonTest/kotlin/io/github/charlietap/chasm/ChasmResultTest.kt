package io.github.charlietap.chasm

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.shapes.getOrElse
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.embedding.shapes.onError
import io.github.charlietap.chasm.embedding.shapes.onSuccess
import io.github.charlietap.chasm.fixture.error.moduleRuntimeError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ChasmResultTest {

    @Test
    fun `calling map on a chasm success result returns the value`() {

        val success = ChasmResult.Success(0)
        val actual = success.map { it + 117 }

        assertEquals(117, (actual as ChasmResult.Success).result)
    }

    @Test
    fun `calling map on a chasm error result does nothing`() {

        val error: ChasmResult<Int, ChasmError> = ChasmResult.Error(
            ChasmError.ExecutionError(moduleRuntimeError().toString()),
        )
        val actual = error.map { it * 2 }

        assertEquals(error, actual)
    }

    @Test
    fun `calling flatMap on a chasm success result computes flatmap and returns the value`() {

        val success = ChasmResult.Success(0)
        val actual = success.flatMap { ChasmResult.Success(it + 117) }

        assertEquals(117, (actual as ChasmResult.Success).result)
    }

    @Test
    fun `calling flatMap on a chasm error result does nothing`() {

        val error: ChasmResult<Int, ChasmError> = ChasmResult.Error(
            ChasmError.ExecutionError(moduleRuntimeError().toString()),
        )
        val actual = error.flatMap { ChasmResult.Success(it * 3) }

        assertEquals(error, actual)
    }

    @Test
    fun `calling fold a chasm success result returns the success branch`() {

        val success = ChasmResult.Success(0)
        val actual = success.fold(
            onSuccess = { it + 117 },
            onError = { 0 },
        )

        assertEquals(117, actual)
    }

    @Test
    fun `calling fold a chasm error result returns the error branch`() {

        val error: ChasmResult<Int, ChasmError> = ChasmResult.Error(
            ChasmError.ExecutionError(moduleRuntimeError().toString()),
        )
        val actual = error.fold(
            onSuccess = { 100 },
            onError = { 117 },
        )

        assertEquals(117, actual)
    }

    @Test
    fun `calling getOrNull on a chasm success result returns the value`() {

        val success = ChasmResult.Success(117)
        val actual = success.getOrNull()

        assertEquals(117, actual)
    }

    @Test
    fun `calling getOrNull on a chasm error result returns null`() {

        val error: ChasmResult<Int, ChasmError> = ChasmResult.Error(
            ChasmError.ExecutionError(moduleRuntimeError().toString()),
        )
        val actual = error.getOrNull()

        assertNull(actual)
    }

    @Test
    fun `calling getOrElse on a chasm success result returns the value`() {

        val success = ChasmResult.Success(117)
        val actual = success.getOrElse(118)

        assertEquals(117, actual)
    }

    @Test
    fun `calling getOrElse on a chasm error result returns the value provided in else`() {

        val error: ChasmResult<Int, ChasmError> = ChasmResult.Error(
            ChasmError.ExecutionError(moduleRuntimeError().toString()),
        )
        val actual = error.getOrElse(117)

        assertEquals(117, actual)
    }

    @Test
    fun `calling expect on a chasm success result returns the value`() {

        val success = ChasmResult.Success(117)
        val actual = success.expect("Should not fail")

        assertEquals(117, actual)
    }

    @Test
    fun `expect throws on a chasm error result`() {

        val error: ChasmResult<Int, ChasmError> = ChasmResult.Error(
            ChasmError.ExecutionError(moduleRuntimeError().toString()),
        )
        assertFailsWith<IllegalStateException> {
            error.expect("Expected success but got error")
        }
    }

    @Test
    fun `onSuccess triggers on chasm success result`() {

        val success = ChasmResult.Success(100)
        var callbackExecuted = false
        success.onSuccess {
            callbackExecuted = true
            assertEquals(100, it)
        }

        assertTrue(callbackExecuted)
    }

    @Test
    fun `onError triggers on chasm error result`() {

        val error: ChasmResult<Int, ChasmError> = ChasmResult.Error(
            ChasmError.ExecutionError(moduleRuntimeError().toString()),
        )
        var callbackExecuted = false
        error.onError {
            callbackExecuted = true
        }

        assertTrue(callbackExecuted)
    }
}
