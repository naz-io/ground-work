package com.nabadi.groundwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.nabadi.groundwork.navigation.GroundWorkNavHost
import com.nabadi.groundwork.ui.theme.GroundWorkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GroundWorkTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GroundWorkNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}