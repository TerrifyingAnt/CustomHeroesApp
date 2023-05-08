package jg.coursework.customheroesapp.ui.main.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import jg.coursework.customheroesapp.data.api.RetrofitBuilder
import jg.coursework.customheroesapp.ui.main.viewmodel.AuthViewModel
import jg.coursework.customheroesapp.data.api.ApiHelper
import jg.coursework.customheroesapp.data.model.UserLoginRequest
import jg.coursework.customheroesapp.ui.main.viewmodel.AuthViewModelFactory
import jg.coursework.customheroesapp.ui.theme.CustomHeroesAppTheme
import jg.coursework.customheroesapp.util.PreferenceManager

class LoginActivity : ComponentActivity() {

    private lateinit var preferenceManager: PreferenceManager

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(
            ApiHelper(
                RetrofitBuilder.apiService,
                preferenceManager
            ),
            preferenceManager
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applicationContext?.let {
            preferenceManager = PreferenceManager(it)
        } ?: throw IllegalStateException("Application context is null")

        val context = applicationContext

        setContent {
            CustomHeroesAppTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AuthScreen(viewModel = viewModel, context)
                }
            }
        }
    }
}



@Composable
fun AuthScreen(viewModel: AuthViewModel, context: Context) {
    val state by viewModel.state.collectAsState(initial = AuthViewModel.AuthState.Loading)
    val activity = LocalContext.current as Activity

    when (state) {
        is AuthViewModel.AuthState.Loading -> { }
        is AuthViewModel.AuthState.Success -> {
            viewModel.getUserInfo()
            activity.startActivity(Intent(context, MainActivity::class.java))
            activity.finish()
        }
        is AuthViewModel.AuthState.Error -> {
            Toast.makeText(context, "Неправильные логин или пароль", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val loginState = remember { mutableStateOf(TextFieldValue()) }
        val passwordState = remember { mutableStateOf(TextFieldValue()) }

        Text (text = "Добро пожаловать!")

        OutlinedTextField(
            value = loginState.value,
            onValueChange = { loginState.value = it },
            placeholder = { Text("Login") },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            placeholder = { Text("Password") },
            modifier = Modifier.padding(bottom = 16.dp)
        )



        Button(
            onClick = {
                val request = UserLoginRequest(
                    login = loginState.value.text,
                    password = passwordState.value.text
                )
                viewModel.authenticateUser(request)
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Войти")
        }


        Button(
            onClick = {
                activity.startActivity(Intent(context, RegisterActivity::class.java))
                activity.finish()
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Зарегистрироваться")
        }
    }
}


