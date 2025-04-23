@Composable //ChatGPT prompt 2 1st attempt
fun EveryUserReview(message: Message, modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) }

    val surfaceColor = if (isExpanded) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = message.uLogo),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = message.author,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = message.time,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = surfaceColor,
                onClick = { isExpanded = !isExpanded }
            ) {
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(8.dp),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun MainScreenElements(messages: List<Message>) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // ...

        item {
            textDescription()
        }
        item {
            ScrollPictures(context)
        }
        item {
            reviewHead()
        }

        items(messages) { message ->
            EveryUserReview(message = message)
        }

        // ...
    }
}
