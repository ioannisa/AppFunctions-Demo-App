package eu.anifantakis.networkapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import eu.anifantakis.networkapp.features.core.presentation.ui.theme.NetworkAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NetworkAppTheme {
                NavigationRoot()
            }
        }
    }
}