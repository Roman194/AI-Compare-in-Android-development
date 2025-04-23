/**
* Необходимо ещё модифицировать решение. Полностью убери из решения параметр функции onUserClick. Также обрати внимание на изменение цвета в Surface. 
* Лучше задавать его в параметре функции Surface, а не косвенно через метод background параметра modifier.

* Также напоминаю, что все цвета берутся из MaterialTheme. Какие именно где смотри в первой версии этого задания. Это также касается и методов data class-а 
* Message.

* В MainScreenElements вместо item для комментариев должен быть использован itemsIndexed.
*/

@Composable//YandexGPT prompt 2 3rd attempt
fun MainScreenElements(

fun MainScreen(
    messages: List<Message>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        MessagesList(messages)
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colors.surface,
            elevation = 4.dp
        ) {
            // Композиция для ввода сообщения
        }
    }
}

@Composable
private fun MessagesList(
    messages: List<Message>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(messages) { index, message ->
            MessageItem(message)
            if (index < messages.lastIndex) {
                Divider(
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                )
            }
        }
    }
}

@Composable
private fun MessageItem(
    message: Message
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.secondary
        ) {
            // Аватар пользователя
        }
        
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = message.username,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface
            )
            
            Text(
                text = message.text,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface
            )
            
            Text(
                text = message.timestamp(),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

data class Message(
    val username: String,
    val text: String,
    val timestamp: Long
) {
    fun timestamp(): String {
        return MaterialTheme.dates.format(timestamp)
    }
}
