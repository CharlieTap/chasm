package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.Mutability

fun mutability(): Mutability = varMutability()

fun constMutability() = Mutability.Const

fun varMutability() = Mutability.Var
