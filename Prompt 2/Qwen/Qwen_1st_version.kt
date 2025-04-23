@Composable //Qwen prompt 2 1st attempt
fun MainScreenElements(messages: List<Message>) { //receive list of objects with Message data class type
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
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
            EveryUserReview(message = message)
        }
    }
}

@Composable
fun EveryUserReview(message: Message) {
    var expanded by remember { mutableStateOf(false) } // State для управления expanded режимом

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Лого автора с бордером
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colors.primary,
                    shape = CircleShape
                )
        ) {
            Image(
                painter = painterResource(id = message.uLogo),
                contentDescription = "User Logo",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Имя автора
            Text(
                text = message.author,
                style = MaterialTheme.typography.body2.copy(fontSize = 16.sp),
                color = MaterialTheme.colors.onBackground
            )

            // Время оставления комментария
            Text(
                text = message.time,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.secondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Текст комментария в Surface
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { expanded = !expanded }
                    .background(if (expanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface),
                elevation = 4.dp
            ) {
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.body2,
                    color = if (expanded) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onBackground,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
