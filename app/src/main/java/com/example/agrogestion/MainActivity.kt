package com.example.agrogestion

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agrogestion.data.local.AppDatabase
import com.example.agrogestion.data.local.UserDataStore
import com.example.agrogestion.repository.AgronomicoRepository
import com.example.agrogestion.repository.AuthRepository
import com.example.agrogestion.ui.navigation.AppNavigation
import com.example.agrogestion.ui.theme.AgroGestionTheme
import com.example.agrogestion.viewmodel.AgroViewModel
import com.example.agrogestion.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.decorView.isForceDarkAllowed = false
        }

        setContent {
            AgroGestionTheme {
                val database = remember { AppDatabase.getDatabase(applicationContext) }
                val repository = remember { AgronomicoRepository(database.registroDao()) }
                val userDataStore = remember { UserDataStore(applicationContext) }
                val authRepository = remember { AuthRepository(userDataStore) }

                val agroViewModel: AgroViewModel = viewModel(
                    factory = AgroViewModel.provideFactory(repository, userDataStore)
                )
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModel.provideFactory(authRepository)
                )

                AppNavigation(viewModel = agroViewModel, loginViewModel = loginViewModel)
            }
        }
    }
}
