package io.github.charlietap.chasm.fixture.error

import io.github.charlietap.chasm.executor.runtime.error.InvocationError

fun invocationError(): InvocationError = trapInvocationError()

fun trapInvocationError() = InvocationError.Trap.TrapEncountered
