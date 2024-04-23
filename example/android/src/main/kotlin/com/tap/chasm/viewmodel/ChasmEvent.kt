package com.tap.chasm.viewmodel

sealed interface ChasmEvent {
    data object CalculateFibonacci : ChasmEvent

    @JvmInline
    value class ChangeFibonacciIndex(val nth: Float): ChasmEvent
}
