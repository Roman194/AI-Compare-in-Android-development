@Composable //Microsoft 365 Copilot prompt 2 1st attempt
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
        color = if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
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
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
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
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = message.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
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
        }
    }
}

