package jg.coursework.customheroesapp.ui.main.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import jg.coursework.customheroesapp.data.api.ApiHelper
import jg.coursework.customheroesapp.data.api.RetrofitBuilder
import jg.coursework.customheroesapp.data.model.UserLoginRequest
import jg.coursework.customheroesapp.data.model.UserRegisterRequest
import jg.coursework.customheroesapp.ui.main.viewmodel.AuthViewModel
import jg.coursework.customheroesapp.ui.main.viewmodel.AuthViewModelFactory
import jg.coursework.customheroesapp.ui.theme.CustomHeroesAppTheme
import jg.coursework.customheroesapp.util.PreferenceManager

class RegisterActivity : ComponentActivity() {

    private val preferenceManager: PreferenceManager by lazy {
        applicationContext?.let {
            PreferenceManager(it)
        } ?: throw IllegalStateException("Application context is null")
    }
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

        val context = applicationContext
        super.onCreate(savedInstanceState)
        setContent {
            CustomHeroesAppTheme() {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    RegisterScreen(viewModel = viewModel, context)
                }
            }
        }
    }
}


@Composable
fun RegisterScreen(viewModel: AuthViewModel, context: Context) {
    val state by viewModel.state.collectAsState(initial = AuthViewModel.AuthState.Loading)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val loginState = remember { mutableStateOf(TextFieldValue()) }
        val passwordState = remember { mutableStateOf(TextFieldValue()) }
        val passwordRepeatState = remember { mutableStateOf(TextFieldValue()) }
        val fullNameState = remember { mutableStateOf(TextFieldValue()) }
        val phoneNumberState = remember { mutableStateOf(TextFieldValue())}
        val activity = LocalContext.current as Activity

        Text (text = "Добро пожаловать!")

        OutlinedTextField(
            value = fullNameState.value,
            onValueChange = { fullNameState.value = it },
            placeholder = { Text("ФИО") },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = phoneNumberState.value,
            onValueChange = { phoneNumberState.value = it },
            placeholder = { Text("Номер телефона") },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = loginState.value,
            onValueChange = { loginState.value = it },
            placeholder = { Text("Логин") },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            placeholder = { Text("Пароль") },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = passwordRepeatState.value,
            onValueChange = { passwordRepeatState.value = it },
            placeholder = { Text("Повторите пароль") },
            modifier = Modifier.padding(bottom = 16.dp)
        )



        when (state) {
            is AuthViewModel.AuthState.Loading -> { }
            is AuthViewModel.AuthState.Success -> {
                viewModel.getUserInfo()
                activity.startActivity(Intent(context, MainActivity::class.java))
                activity.finish()
            }
            is AuthViewModel.AuthState.Error -> {
                val message = (state as AuthViewModel.AuthState.Error).message
                Toast.makeText(context, "Пользователь с таким логином уже существует", Toast.LENGTH_LONG).show()
            }
        }

        Button(
            onClick = {
                val request = UserRegisterRequest(
                    login = loginState.value.text,
                    password = passwordState.value.text,
                    type = "CUSTOMER",
                    phoneNumber = phoneNumberState.value.text,
                    avatarSource = ""
                )
                viewModel.registerUser(request)
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Зарегистрироваться")
        }


        Button(
            onClick = {
                activity.startActivity(Intent(context, LoginActivity::class.java))
                activity.finish()
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("У меня уже есть аккаунт")
        }
    }
}
