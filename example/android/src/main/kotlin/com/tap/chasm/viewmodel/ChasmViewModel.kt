package com.tap.chasm.viewmodel

import android.content.res.AssetManager
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChasmViewModel
    @Inject
    constructor(
        private val assetManager: AssetManager,
    ) : MVIViewModel<ChasmState, ChasmEvent, ChasmEffect>() {
        private val store = store()
        private lateinit var instance: Instance

        init {
            viewModelScope.launch {
                val bytes = readWasmFileBytes(FILENAME)
                instance = instantiateModule(bytes)
            }
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
                        fibonacci.emit(calculateFibonacci(nth.value.toInt()))
                    }
                }
            }
        }

        private suspend fun readWasmFileBytes(filename: String): ByteArray = withContext(Dispatchers.IO) {
            assetManager.open(filename).readBytes()
        }

        private fun instantiateModule(byteArray: ByteArray): Instance {
            return module(byteArray)
                .flatMap { module ->
                    instance(store, module, emptyList())
                }.expect("Failed to instantiate module")
        }

        private fun calculateFibonacci(n: Int): Int {
            return invoke(store, instance, "fibonacci", listOf(NumberValue.I32(n)))
                .map {
                    (it.first() as NumberValue.I32).value
                }.expect("Failed to calculate fibonacci")
        }

        companion object {
            private const val FILENAME = "fibonacci.wasm"
        }
    }
