package com.ifas.vms
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ifas.vms.ui.IFASApp
import com.ifas.vms.ui.theme.IFASTheme
class MainActivity:ComponentActivity(){override fun onCreate(b:Bundle?){super.onCreate(b);setContent{IFASTheme{IFASApp()}}}}
