package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.program.EXIT_IP
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

/** Call-site metadata used only when a single result has a compiled destination. */
internal interface CallSiteResultDestination {
    val resultDestinationSlot: Int
}

/** Encodes the calling instruction, rather than its successor, in the saved return field. */
internal fun resultCallSiteIp(nextIp: Int): Int = (nextIp - 1) or RESULT_CALL_SITE_FLAG

/** The caller is protected at its call instruction, not at the normal continuation. */
internal fun exceptionalCallSiteIp(returnIp: Int, resultCount: Int): Int =
    if (resultCount == 1 && returnIp and RESULT_CALL_SITE_FLAG != 0) {
        returnIp and RESULT_CALL_SITE_IP_MASK
    } else {
        returnIp - 1
    }

/**
 * Restores a Wasm caller and publishes a selectively placed single result.
 *
 * Ordinary returns resume at the saved successor IP. A flagged return field
 * identifies the calling instruction in the flat program, whose dispatcher
 * holds the compiler-selected caller-frame destination. Compiled code is
 * trusted: a missing destination dispatcher is a publication failure.
 */
internal fun returnToCaller(
    vstack: ValueStack,
    store: Store,
    resultCount: Int,
    activationHeaderSlot: Int,
): Int {
    val calleeFp = vstack.fp
    val returnIp = vstack.restoreCallerFrame(resultCount, activationHeaderSlot)
    if (resultCount != 1 || returnIp == EXIT_IP || returnIp and RESULT_CALL_SITE_FLAG == 0) {
        return returnIp
    }

    val callSiteIp = returnIp and RESULT_CALL_SITE_IP_MASK
    val callSite = store.program.instructions[callSiteIp] as CallSiteResultDestination
    val result = vstack.getFrameSlot(calleeFp, 0)
    vstack.setFrameSlot(callSite.resultDestinationSlot, result)
    return callSiteIp + 1
}

private const val RESULT_CALL_SITE_FLAG = 1 shl 30
private const val RESULT_CALL_SITE_IP_MASK = RESULT_CALL_SITE_FLAG - 1
