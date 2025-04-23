item { //Grok prompt 2 1st attempt
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        messages.forEach { message ->
            EveryUserReview(message = message)
            Spacer(modifier = Modifier.height(16.dp)) // Visual separation between comments
        }
    }
}

@Composable
private fun EveryUserReview(message: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    var isClicked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // User logo with border
        Image(
            painter = painterResource(id = message.uLogo),
            contentDescription = "User logo",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            // Author name
            Text(
                text = message.author,
                style = MaterialTheme.typography.body2.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onBackground
            )

            // Time
            Text(
                text = message.time,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Comment text in clickable Surface
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (isClicked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        isClicked = !isClicked
                        isExpanded = !isExpanded
                    }
            ) {
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(16.dp),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

