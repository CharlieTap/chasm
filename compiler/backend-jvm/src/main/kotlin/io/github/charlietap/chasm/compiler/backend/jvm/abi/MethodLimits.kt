package io.github.charlietap.chasm.compiler.backend.jvm.abi

internal const val DEFAULT_METHOD_BYTE_BUDGET = 8_000

internal const val MIN_METHOD_BYTES = 1_000

internal const val MAX_SHORT_BRANCH_METHOD_BYTES = 32_767

internal const val MAX_LOCALS = 65_535

internal const val BATCH_INSTRUCTIONS = 8_000

internal const val BATCH_FUNCTIONS = 1_000

internal const val INSTALLS_PER_METHOD = 400

internal const val MAX_FANOUT = 128
