package io.github.charlietap.chasm.runtime.encoder

import io.github.charlietap.chasm.InternalChasmApi

// The number of low bits occupied by a raw reference's stable type tag
@InternalChasmApi
const val RV_SHIFT_BITS = 8

// The mask selecting the low eight bit type tag from a raw reference value
@InternalChasmApi
const val RV_TYPE_MASK = 0xFFL

@InternalChasmApi
const val RV_TYPE_NULL: Long = 1

@InternalChasmApi
const val RV_TYPE_I31: Long = 2

@InternalChasmApi
const val RV_TYPE_STRUCT: Long = 3

@InternalChasmApi
const val RV_TYPE_ARRAY: Long = 4

@InternalChasmApi
const val RV_TYPE_FUNCTION: Long = 5

@InternalChasmApi
const val RV_TYPE_HOST: Long = 6

@InternalChasmApi
const val RV_TYPE_EXCEPTION: Long = 7

@InternalChasmApi
const val RV_TYPE_EXTERN: Long = 8
