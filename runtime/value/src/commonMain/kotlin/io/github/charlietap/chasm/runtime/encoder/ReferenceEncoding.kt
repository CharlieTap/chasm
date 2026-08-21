package io.github.charlietap.chasm.runtime.encoder

// The number of low bits occupied by a raw reference's stable type tag
const val RV_SHIFT_BITS = 8

// The mask selecting the low eight bit type tag from a raw reference value
const val RV_TYPE_MASK = 0xFFL

const val RV_TYPE_NULL: Long = 1

const val RV_TYPE_I31: Long = 2

const val RV_TYPE_STRUCT: Long = 3

const val RV_TYPE_ARRAY: Long = 4

const val RV_TYPE_FUNCTION: Long = 5

const val RV_TYPE_HOST: Long = 6

const val RV_TYPE_EXCEPTION: Long = 7

const val RV_TYPE_EXTERN: Long = 8
