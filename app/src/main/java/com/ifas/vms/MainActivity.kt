package com.ifas.vms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.ifas.vms.ui.IFASApp
import com.ifas.vms.ui.theme.IFASTheme
import com.ifas.vms.viewmodel.VmsViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: VmsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IFASTheme {
                IFASApp(viewModel)
            }
        }
    }
}
