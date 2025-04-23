item { //Perplexity AI prompt 2 1st attempt
    // Заголовок, если нужен: Text("User Reviews", style = MaterialTheme.typography.h6)
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(messages) { message ->
            EveryUserReview(message)
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

// --- Компонент для отдельного комментария ---
@Composable
fun EveryUserReview(message: Message) {
    var expanded by remember { mutableStateOf(false) }
    val surfaceColor = if (expanded)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Лого пользователя с бордером
        Image(
            painter = painterResource(id = message.uLogo),
            contentDescription = "User Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.author,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = message.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = surfaceColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            ) {
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(12.dp),
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

