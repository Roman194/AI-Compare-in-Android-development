/**
* Отлично, теперь код запускается! Но нужно ещё доработать кликабельность Surface. Дело в том, что кликабельным должен быть не весь Row, а только Surface.
*/

@Composable//Mistral AI prompt 2 4th attempt
fun MainScreenElements(messages: List<Message>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Другие элементы интерфейса
        ...

        // Раздел с комментариями пользователей
        itemsIndexed(messages) { index, message ->
            if (index > 0) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
            EveryUserReview(message = message)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
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
