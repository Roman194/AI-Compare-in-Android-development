// !The running version of the code!
/**
* Необходимо доработать решение. В EveryUserReview в MaterialTheme вместо colorScheme необходимо обращаться к colors. Также вместо bodyMedium и bodySmall весь 
* текст должен использовать именно стиль body2.

* Также в MainScreenElements в созданном тобой items есть проблема: messages это переменная типа List<Message>, а требуется в параметрах Int. Существует вид 
* item-а, который способен в качестве параметров принимать тип List<Message>. Используй пожалуйста в решении именно его.

* Давай ещё сделаем так, чтобы комментарии были разделены между друг другом divider-ом. Соответственно, перед первым и после последнего комментария divider 
* не нужен.
*/

@Composable //Complexity AI prompt 2 2nd attempt
fun MainScreenElements(messages: List<Message>) {
    // ... другие UI элементы ...

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        // ... другие items ...

        // Секция с комментариями пользователей
        items(messages.size) { index ->
            EveryUserReview(message = messages[index])
            // Добавляем Divider между комментариями, кроме последнего
            if (index < messages.size - 1) {
                Divider(
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // ... другие items ...
    }
}

@Composable
fun EveryUserReview(message: Message) {
    var expanded by remember { mutableStateOf(false) }
    val surfaceColor = if (expanded)
        MaterialTheme.colors.primary
    else
        MaterialTheme.colors.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Лого пользователя с бордером
        Image(
            painter = painterResource(id = message.uLogo),
            contentDescription = "User Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colors.primary,
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.author,
                    style = MaterialTheme.typography.body2.copy(fontSize = 16.sp),
                    color = MaterialTheme.colors.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = message.time,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.secondary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = surfaceColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            ) {
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(12.dp),
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

