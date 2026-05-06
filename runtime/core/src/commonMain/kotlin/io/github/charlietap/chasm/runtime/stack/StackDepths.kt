package io.github.charlietap.chasm.runtime.stack

data class StackDepths(
    val handlers: Int,
    val instructions: Int,
    val labels: Int,
    val values: Int,
)
