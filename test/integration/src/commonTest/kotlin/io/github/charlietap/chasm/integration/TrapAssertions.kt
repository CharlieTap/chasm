package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.Trap
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import kotlin.test.assertEquals
import kotlin.test.assertIs

fun assertTrapped(
    reason: TrapReason,
    result: ChasmResult<*, *>,
    message: String? = null,
): Trap {

    val trap = trapOf(result, message)
    assertEquals(reason, trap.reason, message)

    return trap
}

fun trapOf(
    result: ChasmResult<*, *>,
    message: String? = null,
): Trap {

    val error = assertIs<ChasmResult.Error<*>>(result, message).error
    val trapped = assertIs<ExecutionError.Trapped>(error, message)

    return trapped.trap
}
