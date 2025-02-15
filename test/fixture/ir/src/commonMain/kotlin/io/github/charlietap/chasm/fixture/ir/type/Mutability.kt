package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.Mutability

fun mutability(): Mutability = varMutability()

fun constMutability() = Mutability.Const

fun varMutability() = Mutability.Var
