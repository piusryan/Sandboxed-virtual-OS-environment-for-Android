package com.abstergo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.abstergo.navigation.OSNavigation
import com.abstergo.ui.theme.AbstergoOSTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AbstergoOSTheme(darkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    OSNavigation()
                }
            }
        }
    }
}
