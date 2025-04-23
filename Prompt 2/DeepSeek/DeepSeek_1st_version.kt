@Composable //DeepSeek prompt 2 1st attempt
fun MainScreenElements(messages: List<Message>) {
    val context = LocalContext.current
    
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        // ... другие элементы
        
        // Раздел с комментариями пользователей
        items(messages) { message ->
            EveryUserReview(message = message)
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EveryUserReview(message: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    var isClicked by remember { mutableStateOf(false) }
    
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (isClicked) MaterialTheme.colorScheme.primary 
               else MaterialTheme.colorScheme.surface,
        onClick = { isClicked = !isClicked },
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Аватар пользователя с бордером
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .size(50.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                ) {
                    Image(
                        painter = painterResource(id = message.uLogo),
                        contentDescription = "User avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = message.author,
                        style = MaterialTheme.typography.body2,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = message.time,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = message.body,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { isExpanded = !isExpanded }
            )
        }
    }
}

