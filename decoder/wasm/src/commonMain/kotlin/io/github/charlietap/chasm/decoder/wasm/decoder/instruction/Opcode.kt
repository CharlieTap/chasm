package io.github.charlietap.chasm.decoder.wasm.decoder.instruction

internal const val UNREACHABLE: UByte = 0x00u
internal const val NOP: UByte = 0x01u
internal const val BLOCK: UByte = 0x02u
internal const val LOOP: UByte = 0x03u
internal const val IF: UByte = 0x04u
internal const val ELSE: UByte = 0x05u
internal const val END: UByte = 0x0Bu
internal const val BR: UByte = 0x0Cu
internal const val BR_IF: UByte = 0x0Du
internal const val BR_TABLE: UByte = 0x0Eu
internal const val RETURN: UByte = 0x0Fu
internal const val CALL: UByte = 0x10u
internal const val CALL_INDIRECT: UByte = 0x11u
internal const val RETURN_CALL: UByte = 0x12u
internal const val RETURN_CALL_INDIRECT: UByte = 0x13u
internal const val CALL_REF: UByte = 0x14u
internal const val RETURN_CALL_REF: UByte = 0x15u
internal const val BR_ON_NULL: UByte = 0xD5u
internal const val BR_ON_NON_NULL: UByte = 0xD6u

internal const val I32_CONST: UByte = 0x41u
internal const val I64_CONST: UByte = 0x42u
internal const val F32_CONST: UByte = 0x43u
internal const val F64_CONST: UByte = 0x44u

internal const val I32_EQZ: UByte = 0x45u
internal const val I32_EQ: UByte = 0x46u
internal const val I32_NE: UByte = 0x47u
internal const val I32_LT_S: UByte = 0x48u
internal const val I32_LT_U: UByte = 0x49u
internal const val I32_GT_S: UByte = 0x4Au
internal const val I32_GT_U: UByte = 0x4Bu
internal const val I32_LE_S: UByte = 0x4Cu
internal const val I32_LE_U: UByte = 0x4Du
internal const val I32_GE_S: UByte = 0x4Eu
internal const val I32_GE_U: UByte = 0x4Fu

internal const val I64_EQZ: UByte = 0x50u
internal const val I64_EQ: UByte = 0x51u
internal const val I64_NE: UByte = 0x52u
internal const val I64_LT_S: UByte = 0x53u
internal const val I64_LT_U: UByte = 0x54u
internal const val I64_GT_S: UByte = 0x55u
internal const val I64_GT_U: UByte = 0x56u
internal const val I64_LE_S: UByte = 0x57u
internal const val I64_LE_U: UByte = 0x58u
internal const val I64_GE_S: UByte = 0x59u
internal const val I64_GE_U: UByte = 0x5Au

internal const val F32_EQ: UByte = 0x5Bu
internal const val F32_NE: UByte = 0x5Cu
internal const val F32_LT: UByte = 0x5Du
internal const val F32_GT: UByte = 0x5Eu
internal const val F32_LE: UByte = 0x5Fu
internal const val F32_GE: UByte = 0x60u

internal const val F64_EQ: UByte = 0x61u
internal const val F64_NE: UByte = 0x62u
internal const val F64_LT: UByte = 0x63u
internal const val F64_GT: UByte = 0x64u
internal const val F64_LE: UByte = 0x65u
internal const val F64_GE: UByte = 0x66u

internal const val I32_CLZ: UByte = 0x67u
internal const val I32_CTZ: UByte = 0x68u
internal const val I32_POPCNT: UByte = 0x69u
internal const val I32_ADD: UByte = 0x6Au
internal const val I32_SUB: UByte = 0x6Bu
internal const val I32_MUL: UByte = 0x6Cu
internal const val I32_DIV_S: UByte = 0x6Du
internal const val I32_DIV_U: UByte = 0x6Eu
internal const val I32_REM_S: UByte = 0x6Fu
internal const val I32_REM_U: UByte = 0x70u
internal const val I32_AND: UByte = 0x71u
internal const val I32_OR: UByte = 0x72u
internal const val I32_XOR: UByte = 0x73u
internal const val I32_SHL: UByte = 0x74u
internal const val I32_SHR_S: UByte = 0x75u
internal const val I32_SHR_U: UByte = 0x76u
internal const val I32_ROTL: UByte = 0x77u
internal const val I32_ROTR: UByte = 0x78u

internal const val I64_CLZ: UByte = 0x79u
internal const val I64_CTZ: UByte = 0x7Au
internal const val I64_POPCNT: UByte = 0x7Bu
internal const val I64_ADD: UByte = 0x7Cu
internal const val I64_SUB: UByte = 0x7Du
internal const val I64_MUL: UByte = 0x7Eu
internal const val I64_DIV_S: UByte = 0x7Fu
internal const val I64_DIV_U: UByte = 0x80u
internal const val I64_REM_S: UByte = 0x81u
internal const val I64_REM_U: UByte = 0x82u
internal const val I64_AND: UByte = 0x83u
internal const val I64_OR: UByte = 0x84u
internal const val I64_XOR: UByte = 0x85u
internal const val I64_SHL: UByte = 0x86u
internal const val I64_SHR_S: UByte = 0x87u
internal const val I64_SHR_U: UByte = 0x88u
internal const val I64_ROTL: UByte = 0x89u
internal const val I64_ROTR: UByte = 0x8Au

internal const val F32_ABS: UByte = 0x8Bu
internal const val F32_NEG: UByte = 0x8Cu
internal const val F32_CEIL: UByte = 0x8Du
internal const val F32_FLOOR: UByte = 0x8Eu
internal const val F32_TRUNC: UByte = 0x8Fu
internal const val F32_NEAREST: UByte = 0x90u
internal const val F32_SQRT: UByte = 0x91u
internal const val F32_ADD: UByte = 0x92u
internal const val F32_SUB: UByte = 0x93u
internal const val F32_MUL: UByte = 0x94u
internal const val F32_DIV: UByte = 0x95u
internal const val F32_MIN: UByte = 0x96u
internal const val F32_MAX: UByte = 0x97u
internal const val F32_COPYSIGN: UByte = 0x98u

internal const val F64_ABS: UByte = 0x99u
internal const val F64_NEG: UByte = 0x9Au
internal const val F64_CEIL: UByte = 0x9Bu
internal const val F64_FLOOR: UByte = 0x9Cu
internal const val F64_TRUNC: UByte = 0x9Du
internal const val F64_NEAREST: UByte = 0x9Eu
internal const val F64_SQRT: UByte = 0x9Fu
internal const val F64_ADD: UByte = 0xA0u
internal const val F64_SUB: UByte = 0xA1u
internal const val F64_MUL: UByte = 0xA2u
internal const val F64_DIV: UByte = 0xA3u
internal const val F64_MIN: UByte = 0xA4u
internal const val F64_MAX: UByte = 0xA5u
internal const val F64_COPYSIGN: UByte = 0xA6u

internal const val I32_WRAP_I64: UByte = 0xA7u
internal const val I32_TRUNC_F32_S: UByte = 0xA8u
internal const val I32_TRUNC_F32_U: UByte = 0xA9u
internal const val I32_TRUNC_F64_S: UByte = 0xAAu
internal const val I32_TRUNC_F64_U: UByte = 0xABu
internal const val I64_EXTEND_I32_S: UByte = 0xACu
internal const val I64_EXTEND_I32_U: UByte = 0xADu
internal const val I64_TRUNC_F32_S: UByte = 0xAEu
internal const val I64_TRUNC_F32_U: UByte = 0xAFu
internal const val I64_TRUNC_F64_S: UByte = 0xB0u
internal const val I64_TRUNC_F64_U: UByte = 0xB1u
internal const val F32_CONVERT_I32_S: UByte = 0xB2u
internal const val F32_CONVERT_I32_U: UByte = 0xB3u
internal const val F32_CONVERT_I64_S: UByte = 0xB4u
internal const val F32_CONVERT_I64_U: UByte = 0xB5u
internal const val F32_DEMOTE_F64: UByte = 0xB6u
internal const val F64_CONVERT_I32_S: UByte = 0xB7u
internal const val F64_CONVERT_I32_U: UByte = 0xB8u
internal const val F64_CONVERT_I64_S: UByte = 0xB9u
internal const val F64_CONVERT_I64_U: UByte = 0xBAu
internal const val F64_PROMOTE_F32: UByte = 0xBBu
internal const val I32_REINTERPRET_F32: UByte = 0xBCu
internal const val I64_REINTERPRET_F64: UByte = 0xBDu
internal const val F32_REINTERPRET_I32: UByte = 0xBEu
internal const val F64_REINTERPRET_I64: UByte = 0xBFu

internal const val I32_EXTEND8_S: UByte = 0xC0u
internal const val I32_EXTEND16_S: UByte = 0xC1u
internal const val I64_EXTEND8_S: UByte = 0xC2u
internal const val I64_EXTEND16_S: UByte = 0xC3u
internal const val I64_EXTEND32_S: UByte = 0xC4u

internal const val REF_NULL: UByte = 0xD0u
internal const val REF_ISNULL: UByte = 0xD1u
internal const val REF_FUNC: UByte = 0xD2u
internal const val REF_AS_NON_NULL: UByte = 0xD4u

internal const val DROP: UByte = 0x1Au
internal const val SELECT: UByte = 0x1Bu
internal const val SELECT_W_TYPE: UByte = 0x1Cu

internal const val LOCAL_GET: UByte = 0x20u
internal const val LOCAL_SET: UByte = 0x21u
internal const val LOCAL_TEE: UByte = 0x22u
internal const val GLOBAL_GET: UByte = 0x23u
internal const val GLOBAL_SET: UByte = 0x24u

internal const val TABLE_GET: UByte = 0x25u
internal const val TABLE_SET: UByte = 0x26u

internal const val I32_LOAD: UByte = 0x28u
internal const val I64_LOAD: UByte = 0x29u
internal const val F32_LOAD: UByte = 0x2Au
internal const val F64_LOAD: UByte = 0x2Bu
internal const val I32_LOAD8_S: UByte = 0x2Cu
internal const val I32_LOAD8_U: UByte = 0x2Du
internal const val I32_LOAD16_S: UByte = 0x2Eu
internal const val I32_LOAD16_U: UByte = 0x2Fu
internal const val I64_LOAD8_S: UByte = 0x30u
internal const val I64_LOAD8_U: UByte = 0x31u
internal const val I64_LOAD16_S: UByte = 0x32u
internal const val I64_LOAD16_U: UByte = 0x33u
internal const val I64_LOAD32_S: UByte = 0x34u
internal const val I64_LOAD32_U: UByte = 0x35u
internal const val I32_STORE: UByte = 0x36u
internal const val I64_STORE: UByte = 0x37u
internal const val F32_STORE: UByte = 0x38u
internal const val F64_STORE: UByte = 0x39u
internal const val I32_STORE8: UByte = 0x3Au
internal const val I32_STORE16: UByte = 0x3Bu
internal const val I64_STORE8: UByte = 0x3Cu
internal const val I64_STORE16: UByte = 0x3Du
internal const val I64_STORE32: UByte = 0x3Eu
internal const val MEMORY_SIZE: UByte = 0x3Fu
internal const val MEMORY_GROW: UByte = 0x40u

internal const val PREFIX_MISC: UByte = 0xFCu
internal const val PREFIX_VECTOR: UByte = 0xFDu

// prefixed

internal const val I32_TRUNC_SAT_F32_S: UInt = 0u
internal const val I32_TRUNC_SAT_F32_U: UInt = 1u
internal const val I32_TRUNC_SAT_F64_S: UInt = 2u
internal const val I32_TRUNC_SAT_F64_U: UInt = 3u
internal const val I64_TRUNC_SAT_F32_S: UInt = 4u
internal const val I64_TRUNC_SAT_F32_U: UInt = 5u
internal const val I64_TRUNC_SAT_F64_S: UInt = 6u
internal const val I64_TRUNC_SAT_F64_U: UInt = 7u

internal const val MEMORY_INIT: UInt = 8u
internal const val DATA_DROP: UInt = 9u
internal const val MEMORY_COPY: UInt = 10u
internal const val MEMORY_FILL: UInt = 11u

internal const val TABLE_INIT: UInt = 12u
internal const val ELEM_DROP: UInt = 13u
internal const val TABLE_COPY: UInt = 14u
internal const val TABLE_GROW: UInt = 15u
internal const val TABLE_SIZE: UInt = 16u
internal const val TABLE_FILL: UInt = 17u
