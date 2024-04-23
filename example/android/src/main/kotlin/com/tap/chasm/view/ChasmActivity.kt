package com.tap.chasm.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.tap.chasm.viewmodel.ChasmEvent
import com.tap.chasm.viewmodel.ChasmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChasmActivity : AppCompatActivity()
{
    private val viewModel by viewModels<ChasmViewModel>()

    private fun eventHandler(event: ChasmEvent) {
        lifecycleScope.launch {
            viewModel.postEvent(event)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val state = viewModel.state.collectAsState()

            Column (
                Modifier
                    .background(Color.White)
                    .fillMaxSize()
                    .padding(8.dp)
            ){
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = state.value.sliderTitle)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = state.value.sliderInfo)
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = state.value.nth,
                    onValueChange = { eventHandler(ChasmEvent.ChangeFibonacciIndex(it)) },
                    steps = 15,
                    valueRange = 1f..15f
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = state.value.result)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { eventHandler(ChasmEvent.CalculateFibonacci) }) {
                    Text(state.value.button)
                }
            }
        }
    }
}
