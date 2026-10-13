package io.github.charlietap.chasm.validator.validator.instruction

internal const val UNREACHABLE = 0x00
internal const val NOP = 0x01
internal const val BLOCK = 0x02
internal const val LOOP = 0x03
internal const val IF = 0x04
internal const val ELSE = 0x05
internal const val END = 0x0B
internal const val BR = 0x0C
internal const val BR_IF = 0x0D
internal const val BR_TABLE = 0x0E
internal const val RETURN = 0x0F
internal const val CALL = 0x10
internal const val CALL_INDIRECT = 0x11

internal const val LOCAL_GET = 0x20
internal const val LOCAL_SET = 0x21
internal const val LOCAL_TEE = 0x22
internal const val GLOBAL_GET = 0x23
internal const val GLOBAL_SET = 0x24

internal const val FIRST_LOAD = 0x28
internal const val LAST_LOAD = 0x35
internal const val FIRST_STORE = 0x36
internal const val LAST_STORE = 0x3E
internal const val MEMORY_SIZE = 0x3F
internal const val MEMORY_GROW = 0x40

internal const val FIRST_NUMERIC = 0x41
internal const val LAST_NUMERIC = 0xC4
