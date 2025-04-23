@Composable//YandexGPT prompt 2 1st attempt
fun MainScreenElements(messages: List<Message>) {
val context = LocalContext.current
LazyColumn(
    modifier = Modifier
        .fillMaxWidth()
) {
    ...
    item {
        textDescription()
    }
    item {
        ScrollPictures(context)
    }
    item {
        reviewHead()
    }
    
    // Раздел с комментариями пользователей
    items(messages) { message ->
        EveryUserReview(message)
        Spacer(modifier = Modifier.height(16.dp)) // Разделитель между комментариями
    }
}
...
}
@Composable
fun EveryUserReview(message: Message) {
val (isExpanded, setExpanded) = remember { mutableStateOf(false) }
val backgroundColor = remember { mutableStateOf(MaterialTheme.colors.surface) }
Row(
    modifier = Modifier
        .padding(16.dp)
        .clickable { backgroundColor.value = if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface },
    verticalAlignment = Alignment.Top
) {
    // Лого пользователя
    Image(
        painter = painterResource(id = message.uLogo),
        contentDescription = null,
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colors.primary, CircleShape)
    )
    
    Spacer(modifier = Modifier.width(16.dp))
    
    Column {
        // Имя автора
        Text(
            text = message.author,
            style = MaterialTheme.typography.body2.copy(fontSize = 16.sp),
            color = MaterialTheme.colors.onBackground
        )
        
        // Время
        Text(
            text = message.time,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.secondary
        )
        
        // Текст отзыва
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = backgroundColor.value
        ) {
            Text(
                text = message.body,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                onOverflow = {
                    setExpanded(true)
                }
            )
        }
    }
}
}

