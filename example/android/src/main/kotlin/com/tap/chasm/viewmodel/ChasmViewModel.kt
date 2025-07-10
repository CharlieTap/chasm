package com.tap.chasm.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.test.chasm.FactorialService
import com.test.chasm.FibonacciService
import com.test.chasm.TestService
import com.test.chasm.producer.ProducerService
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
    private val factorialService: FactorialService,
    private val fibonacciService: FibonacciService,
    private val producerService: ProducerService,
    private val testService: TestService,
) : MVIViewModel<ChasmState, ChasmEvent, ChasmEffect>() {

    init {
        factorialPrinter()
        producerPrinter()
        testPrinter()
    }

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

    private fun factorialPrinter() {
        val factorial = factorialService.factorial(5)
        Log.d("ChasmViewModel", "factorial: $factorial")
    }

    private fun producerPrinter() {
        val gcd = producerService.gcd(48, 18)
        Log.d("ChasmViewModel", "greatest common divisor: $gcd")
    }

    private fun testPrinter() {
        var mutableGlobal = testService.mutableGlobal
        Log.d("ChasmViewModel", "mutableGlobal: $mutableGlobal")
        val immutableGlobal = testService.immutableGlobal
        Log.d("ChasmViewModel", "immutableGlobal: $immutableGlobal")

        testService.mutableGlobal = 118
        mutableGlobal = testService.mutableGlobal
        Log.d("ChasmViewModel", "mutableGlobal after change: $mutableGlobal")

        val intFunction = testService.intFunction()
        Log.d("ChasmViewModel", "int function: $intFunction")

        val longFunction = testService.longFunction()
        Log.d("ChasmViewModel", "long function: $longFunction")

        val floatFunction = testService.floatFunction()
        Log.d("ChasmViewModel", "float function: $floatFunction")

        val doubleFunction = testService.doubleFunction()
        Log.d("ChasmViewModel", "double function: $doubleFunction")

        val unitFunction = testService.unitFunction()
        Log.d("ChasmViewModel", "unit function: $unitFunction ${testService.mutableGlobal}")

        val multipleParamFunction = testService.multipleParamFunction(5, 2.2)
        Log.d("ChasmViewModel", "multiple param function: $multipleParamFunction")

        val multipleReturnFunction = testService.multipleReturnFunction()
        Log.d("ChasmViewModel", "multiple return function: r0 = ${multipleReturnFunction.r0} r1 = ${multipleReturnFunction.r1}")

        val palStringFunction = testService.palStringFunction()
        Log.d("ChasmViewModel", "pointer and length string function: $palStringFunction")

        val nullTerminatedStringFunction = testService.nullTerminatedStringFunction()
        Log.d("ChasmViewModel", "null terminated string function: $nullTerminatedStringFunction")

        val lengthPrefixedStringFunction = testService.lengthPrefixedStringFunction()
        Log.d("ChasmViewModel", "length prefixed string function: $lengthPrefixedStringFunction")

        val packedI64StringFunction = testService.packedI64StringFunction()
        Log.d("ChasmViewModel", "packed i64 string function: $packedI64StringFunction")
    }
}
