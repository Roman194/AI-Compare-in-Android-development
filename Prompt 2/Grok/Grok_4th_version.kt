/**
* Используй animateColorAsState вместо mutableStateOf в surfaceColor.
*/

item {//Grok prompt 2 4th attempt

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        messages.forEachIndexed { index, message ->
            if (index > 0) {
                Divider(
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            EveryUserReview(message = message)
        }
    }
}

@Composable
fun EveryUserReview(message: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    val surfaceColor by animateColorAsState(
        targetValue = if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface
    )

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
                .border(2.dp, MaterialTheme.colors.primary, CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            // Author name
            Text(
                text = message.author,
                style = MaterialTheme.typography.body2.copy(fontSize = 16.sp),
                color = MaterialTheme.colors.onBackground
            )

            // Time
            Text(
                text = message.time,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Comment text in clickable Surface
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = surfaceColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        isExpanded = !isExpanded
                    }
            ) {
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(16.dp),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
