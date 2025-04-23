item {//Gemini prompt 2 1st attempt
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(messages) { message ->
            EveryUserReview(msg = message)
        }
    }
}

@Composable
fun EveryUserReview(msg: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    var backgroundColor by remember { mutableStateOf(MaterialTheme.colorScheme.surface) }

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
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                shape = CircleShape
            ) {
                Image(
                    painter = painterResource(id = msg.avatar),
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
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = msg.time,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = backgroundColor,
            modifier = Modifier
                .clickable {
                    isExpanded = !isExpanded
                    backgroundColor = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                }
                .padding(all = 1.dp)
        ) {
            Text(
                text = msg.body,
                modifier = Modifier.padding(all = 10.dp),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

