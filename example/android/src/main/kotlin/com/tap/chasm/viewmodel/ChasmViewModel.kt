package com.tap.chasm.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.test.chasm.FibonacciService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChasmViewModel @Inject constructor(
    private val fibonacciService: FibonacciService,
) : MVIViewModel<ChasmState, ChasmEvent, ChasmEffect>() {

    private val nth = MutableStateFlow(ChasmState.DEFAULT.nth)
    private val fibonacci = MutableStateFlow<Int?>(null)

    private val _state = combine(nth, fibonacci) { n, fib ->

        val sliderInfo = "Compute the {${n.toInt()}} number in the fibonacci sequence"

        val result = fib?.let {
            "The calculated fibonacci number is: $fib"
        } ?: ChasmState.DEFAULT.result

        ChasmState.DEFAULT.copy(
            nth = n,
            sliderInfo = sliderInfo,
            result = result,
        )
    }
    override val state: StateFlow<ChasmState> = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChasmState.DEFAULT)

    override fun handleEvent(event: ChasmEvent) {
        when (event) {
            is ChasmEvent.ChangeFibonacciIndex -> {
                viewModelScope.launch {
                    nth.emit(event.nth)
                }
            }

            is ChasmEvent.CalculateFibonacci -> {
                viewModelScope.launch {
                    val fib = runCatching {  calculateFibonacci(nth.value.toInt()) }
                    Log.d("ChasmViewModel", fib.toString())
                    fibonacci.emit(calculateFibonacci(nth.value.toInt()))
                }
            }
        }
    }

    private fun calculateFibonacci(n: Int): Int {
        return fibonacciService.fibonacci(n)
    }
}
