/**
* Необходимо доработать решение. В EveryUserReview в MaterialTheme вместо colorScheme необходимо обращаться к colors. 

В MainScreenElements в созданном тобой items есть проблема: messages это переменная типа List<Message>, а требуется в параметрах Int. 
* Существует вид item-а, который способен в качестве параметров принимать тип List<Message>. Используй пожалуйста в решении именно его.

* Также перед первым и после последнего комментария divider не нужен.
*/

@Composable//Mistral AI prompt 2 2nd attempt
fun MainScreenElements(messages: List<Message>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Другие элементы интерфейса
        ...

        // Раздел с комментариями пользователей
        items(messages) { message ->
            EveryUserReview(message = message)
            if (message != messages.last()) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun EveryUserReview(message: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { isExpanded = !isExpanded }
    ) {
        Image(
            painter = painterResource(id = message.uLogo),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colors.primary, CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = message.author,
                style = MaterialTheme.typography.body2,
                fontSize = 16.sp,
                color = MaterialTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = message.time,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = backgroundColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

