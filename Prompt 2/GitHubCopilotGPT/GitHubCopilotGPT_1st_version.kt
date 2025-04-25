// !The running version of the code!
@Composable //GitHub GPT Copilot prompt 2 1st attempt 
fun EveryUserReview(message: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    val surfaceColor by animateColorAsState(
        targetValue = if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colors.primary,
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(id = message.uLogo),
                    contentDescription = "Лого автора",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = message.author,
                    style = MaterialTheme.typography.body2,
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.onBackground
                )
                Text(
                    text = message.time,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.secondary
                )
            }
        }
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = surfaceColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clickable { isExpanded = !isExpanded }
        ) {
            Text(
                text = message.body,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(8.dp),
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Внутри функции MainScreenElements в LazyColumn включите следующий item:
item {
    Column {
        messages.forEachIndexed { index, message ->
            EveryUserReview(message = message)
            if (index < messages.lastIndex) {
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                )
            }
        }
    }
}

