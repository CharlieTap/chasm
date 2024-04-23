package com.tap.chasm.viewmodel

data class ChasmState(
    val nth: Float,
    val sliderTitle: String,
    val sliderInfo: String,
    val result: String,
    val button: String,
) {
    companion object {
        val DEFAULT = ChasmState(
            nth = 2f,
            sliderTitle = "Drag the slider to configure the number to compute fibonacci to",
            sliderInfo = "Compute the 2 number in the fibonacci sequence",
            result = "",
            button = "Calculate",
        )
    }
}
