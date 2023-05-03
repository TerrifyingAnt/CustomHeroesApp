package jg.coursework.customheroesapp.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import jg.coursework.customheroesapp.data.api.ApiHelper
import jg.coursework.customheroesapp.data.api.RetrofitBuilder
import jg.coursework.customheroesapp.data.model.Message
import jg.coursework.customheroesapp.ui.main.viewmodel.MessageViewModel
import jg.coursework.customheroesapp.ui.main.viewmodel.MessageViewModelFactory
import jg.coursework.customheroesapp.ui.theme.CustomHeroesAppTheme
import jg.coursework.customheroesapp.ui.theme.Shapes
import jg.coursework.customheroesapp.util.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : ComponentActivity() {


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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val context = LocalContext.current
                    val state by viewModel.messages.collectAsState()
                    val list = remember { mutableStateOf(viewModel.messages.value) }
                    val chatId = intent.getStringExtra("chatId")?.toLong()
                    val fromUser = "TODO"
                    val toUser = intent.getStringExtra("withUser")
                    var message: MutableState<Message> = remember {
                        mutableStateOf(Message(fromUser, fromUser,
                            toUser!!, "", chatId!!.toLong()))}
                    print("chat activity" + chatId.toString())
                    LaunchedEffect(key1 = Unit) {
                        if (chatId != null) {
                            viewModel.getMessages(chatId.toLong())
                            Log.d("CHAT", "xd")
                        }
                        print("chat activity" + chatId.toString())
                        list.value = state
                    }
                    if (fromUser != null && toUser != null && chatId != null) {
                        DialogActivity(messages = state, onBackClick = {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }, fromUser, toUser, chatId.toLong(), viewModel )
                    }
                }
            }
        }
    }
}

@Composable
fun MessagesList(messages: List<Message>, toUser: String) {
    LazyColumn(modifier = Modifier.fillMaxSize(),
         horizontalAlignment = Alignment.CenterHorizontally) {
        items(messages) { message ->
            Spacer(modifier = Modifier.height(5.dp))
        if(message.toUser == toUser) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                MessageItem(message)
                Spacer(modifier = Modifier.width(5.dp))
            }
        }
        else {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(5.dp))
                MessageItem(message)
                }
            }
        }
    }
}


@Composable
fun DialogActivity(
    messages: List<Message>,
    onBackClick: () -> Unit,
    fromUser: String,
    toUser: String?,
    chatId: Long,
    viewModel: MessageViewModel
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault())
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$toUser") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                cutoutShape = CircleShape,
                backgroundColor = MaterialTheme.colors.primary
            ) {
                var message by remember { mutableStateOf("") }

                TextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier.fillMaxWidth(0.9f),
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if(message != "") {
                                viewModel.uploadMessages(
                                    Message(
                                        fromUser.toString(),
                                        toUser.toString(),
                                        dateFormat.format(Date()),
                                        message,
                                        chatId
                                    )
                                )
                                message = ""
                            }
                        }
                    )
                )
                IconButton(
                    onClick = {
                        if(message != "") {
                            viewModel.uploadMessages(
                                Message(
                                    fromUser.toString(),
                                    toUser.toString(),
                                    dateFormat.format(Date()),
                                    message,
                                    chatId
                                )
                            )
                            message = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        },
        content = {
            if (toUser != null) {
                MessagesList(messages = messages, toUser)
            }
            }
    )
}


@Composable
fun MessageItem(message: Message) {
    Box(modifier = Modifier
        .background(Color(204, 204, 255),
        shape = RoundedCornerShape(5.dp)
        )
    ) {
        Column(verticalArrangement = Arrangement.Center,
            modifier = Modifier.wrapContentHeight()) {
            Text(message.content)
            val tempDate = message.date.subSequence(message.date.indexOf("T", 0, false) + 1,
                message.date.indexOf(":",
                    message.date.indexOf(":", 0, false) + 1,
                    false)).toString()
            Text(tempDate, color = Color.Gray)
        }
    }
}




