/**
* Необходимо доработать решение. В EveryUserReview кликабельность была добавлена у Row. Необходимо её переместить глубже по иерархии в Surface. Также в Text, 
* который внутри Surface, имеет лишний параметр onOverflow. Его необходимо убрать.

* Также нужно немного переделать переменную backgroundColor. Дело в том, что в Modifier.clickable нельзя делать вызовы Compose-функций, поэтому нужно вынести 
* if условие в начало функции (к месту инициализации переменной). Точно также и в mutableStateOf нельзя делать вызовы Compose-функций, поэтому нужно немного 
* переделать инициализацию переменной backgroundColor: убрать инициализации с помощью remeber и использовать другую функцию, допускающую использование 
* Compose-функций внутри себя.

* В MainScreenElements в созданном тобой items есть проблема: messages это переменная типа List<Message>, а требуется в параметрах Int. 
* Существует вид item-а, который способен в качестве параметров принимать тип List<Message>. Используй пожалуйста в решении именно его. 
* Ещё перенеси инициализацию переменной expanded в функцию EveryUserReview.

* Давай ещё сделаем так, чтобы комментарии были разделены между друг другом divider-ом. Соответственно, перед первым и после последнего комментария divider 
*не нужен.
*/

@Composable//YandexGPT prompt 2 2nd attempt
fun MainScreenElements(
    messages: List<Message>,
    onUserClick: (String) -> Unit
) {
    LazyColumn {
        items(messages) { message ->
            EveryUserReview(
                message = message,
                onUserClick = onUserClick
            )
            if (message != messages.last()) {
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.LightGray,
                    thickness = 1.dp
                )
            }
        }
    }
}

@Composable
fun EveryUserReview(
    message: Message,
    onUserClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val backgroundColor = if (expanded) Color.LightGray else Color.White
    
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .clickable {
                    expanded = !expanded
                    onUserClick(message.userId)
                }
                .background(backgroundColor)
                .padding(12.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column {
                Text(
                    text = message.username,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

