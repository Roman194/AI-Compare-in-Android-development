/**
* Необходимо доработать решение. В EveryUserReview в MaterialTheme вместо colorScheme необходимо обращаться к colors. Также вместо bodyMedium весь текст 
* должен использовать именно стиль body2. Также в MainScreenElements в созданном тобой items есть проблема: messages это переменная типа List<Message>, а 
* требуется в параметрах Int. Существует вид item, который способен в качестве параметров принимать тип List<Message>. Используй пожалуйста в решении именно 
* его. Давай ещё сделаем так, чтобы комментарии были разделены между друг другом divider-ом. Соответственно, перед первым и после последнего комментария 
* divider не нужен.
*/

@Composable //Microsoft 365 Copilot prompt 2 2nd attempt
fun EveryUserReview(message: Message, isExpanded: Boolean, onClick: () -> Unit) {
    var expanded by remember { mutableStateOf(isExpanded) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                expanded = !expanded
                onClick()
            },
        color = if (expanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(MaterialTheme.colors.surface, CircleShape)
                    .border(2.dp, MaterialTheme.colors.primary, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = message.uLogo),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = message.author,
                    style = MaterialTheme.typography.body2.copy(fontSize = 16.sp),
                    color = MaterialTheme.colors.onBackground
                )
                Text(
                    text = message.time,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.secondary
                )
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun MainScreenElements(messages: List<Message>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            textDescription()
        }
        item {
            ScrollPictures(LocalContext.current)
        }
        item {
            reviewHead()
        }
        // Раздел с комментариями пользователей
        items(messages) { message ->
            EveryUserReview(
                message = message,
                isExpanded = false,
                onClick = { /* Реализовать обработку клика при необходимости */ }
            )
            Divider(color = MaterialTheme.colors.onBackground, thickness = 1.dp)
        }
        // Удаление Divider перед первым и после последнего элемента
        if (messages.isNotEmpty()) {
            itemsIndexed(messages) { index, message ->
                EveryUserReview(
                    message = message,
                    isExpanded = false,
                    onClick = { /* Обработка клика */ }
                )
                if (index != messages.size - 1) {
                    Divider(
                      row!
                )
            }
        }
    }
}

