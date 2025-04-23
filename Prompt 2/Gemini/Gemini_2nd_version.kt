/**
*Необходимо доработать решение. В MainScreenElements в созданном тобой items есть проблема: messages это переменная типа List<Message>, а требуется в 
* параметрах Int. Существует вид item-а, который способен в качестве параметров принимать тип List<Message>. Используй пожалуйста в решении именно его.

* Также в EveryUserReview нужно немного переделать переменную sufaceColor. Дело в том, что в Modifier.clickable нельзя делать вызовы Compose-функций, поэтому 
* нужно вынести if условие в начало функции (к месту инициализации переменной). Точно также и в mutableStateOf нельзя делать вызовы Compose-функций, поэтому 
* нужно немного переделать инициализацию переменной surfaceColor: убрать инициализации с помощью remeber и использовать другую функцию, допускающую 
* использование Compose-функций внутри себя. Также в MaterialTheme вместо colorScheme необходимо обращаться к colors. Ещё обрати пожалуйста внимание на 
* параметр painter в Image. Он должен принимать переменную с несколько иным названием из data class-а Message.

* Давай ещё сделаем так, чтобы комментарии были разделены между друг другом divider-ом. Соответственно, перед первым и после последнего комментария divider 
* не нужен.
*/

item {//Gemini prompt 2 2nd attempt
    itemsIndexed(messages) { index, message ->
        if (index > 0) {
            Divider(
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        EveryUserReview(msg = message)
        if (index == messages.size - 1) {
            Divider(
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun EveryUserReview(msg: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    val surfaceColor = remember(isExpanded) {
        if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                border = BorderStroke(2.dp, MaterialTheme.colors.primary),
                shape = CircleShape
            ) {
                Image(
                    painter = painterResource(id = msg.userAvatar),
                    contentDescription = "Contact profile picture",
                    modifier = Modifier.size(50.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = msg.author,
                    style = MaterialTheme.typography.body2,
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = msg.time,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.secondary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = surfaceColor,
            modifier = Modifier
                .clickable {
                    isExpanded = !isExpanded
                }
                .padding(all = 1.dp)
        ) {
            Text(
                text = msg.body,
                modifier = Modifier.padding(all = 10.dp),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


