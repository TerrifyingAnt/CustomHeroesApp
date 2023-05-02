package jg.coursework.customheroesapp.ui.main.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import jg.coursework.customheroesapp.data.api.ApiHelper
import jg.coursework.customheroesapp.data.api.RetrofitBuilder
import jg.coursework.customheroesapp.data.model.Chat
import jg.coursework.customheroesapp.data.model.Message
import jg.coursework.customheroesapp.ui.main.viewmodel.AuthViewModel
import jg.coursework.customheroesapp.ui.main.viewmodel.MessageViewModel
import jg.coursework.customheroesapp.ui.main.viewmodel.MessageViewModelFactory
import jg.coursework.customheroesapp.ui.theme.CustomHeroesAppTheme
import jg.coursework.customheroesapp.util.PreferenceManager
import java.util.*


class MainActivity : ComponentActivity()  {

    private val preferenceManager: PreferenceManager by lazy {
        applicationContext?.let {
            PreferenceManager(it)
        } ?: throw IllegalStateException("Application context is null")
    }

    private val viewModel: MessageViewModel by viewModels {
        MessageViewModelFactory(
            ApiHelper(
                RetrofitBuilder.apiService,
                preferenceManager
            )
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            CustomHeroesAppTheme() {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val chatNotExist by viewModel.chatNotExist.collectAsState()
                    val users = remember { mutableStateOf(viewModel.chatNotExist.value) }
                    LaunchedEffect(key1 = Unit) {
                        viewModel.getChatNotExist()
                        print("chat with" + chatNotExist)
                        users.value = chatNotExist
                    }
                    scaffoldTemplate(viewModel, chatNotExist)
                }
            }
        }
    }
}

@Composable
fun scaffoldTemplate(viewModel: MessageViewModel, users: List<String>) {
    val navController = rememberNavController()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                backgroundColor = MaterialTheme.colors.primary,
            )
            Column(verticalArrangement = Arrangement.Center) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Text(text = "CustomHeroes", color = Color.White)
                    IconButton(
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Новый чат",
                            tint = Color.White,
                        )
                    }
                }
            }

            if (showDialog) {
                DialogWithLazyColumn(users, {showDialog = false})
            }

        },
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = currentRoute == "home",
                    onClick = {
                        navController.navigate("home") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Email, contentDescription = null) },
                    label = { Text("Chat") },
                    selected = currentRoute == "chat",
                    onClick = {
                        navController.navigate("chat") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "home") {
            composable("home") {
                HomeScreen()
            }
            composable("chat") {
                ChatScreen(viewModel)
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Text(text = "Home Screen")
}

@Composable
fun ChatScreen(viewModel: MessageViewModel) {
    val context = LocalContext.current
    val state by viewModel.chats.collectAsState()
    val list = remember { mutableStateOf(viewModel.chats.value) }
    LaunchedEffect(key1 = Unit) {
        viewModel.getChats()
        list.value = state
        println(list)
    }
    ChatList(messages = state, context)

}

@Composable
fun ChatList(messages: List<Chat>, context: Context) {
    val chatId: Long = 0;
    val activity = LocalContext.current as Activity
    LazyColumn(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        items(messages) { message ->
            Spacer(modifier = Modifier.height(5.dp))
            Box(modifier = Modifier
                .background(Color(204, 204, 255))
                .fillMaxWidth(0.9f)
                .clickable {
                    // TODO добавить id чатат в модельку сообщений, а не использвоать костыли по типу пользователя
                    val intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra("chatId", message.chatRoomId.toString())
                    intent.putExtra("withUser", message.user)
                    println(message.chatRoomId)
                    activity.startActivity(intent)
                    activity.finish()
                }) {
                ChatItem(message)
            }
        }
    }
}

@Composable
fun ChatItem(chatItem: Chat) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Чат с: ${chatItem.user}")

    }
}

@Composable
fun DialogWithLazyColumn(
    users: List<String>,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Новый чат с: ")
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(users) { message ->
                    Text(
                        text = message,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable { //TODO
                            }
                    )
                }
            }
        }
    }
}
