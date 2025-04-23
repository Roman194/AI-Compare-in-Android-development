@Composable //ClaudeAI Prompt 2 1st attempt
fun MainScreenElements(messages: List<Message>) { //receive list of objects with Message data class type
    val context = LocalContext.current
        
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        /* ... other items ... */

        item {
            reviewHead()
        }

        // User comments section
        items(messages) { message ->
            EveryUserReview(message = message)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun EveryUserReview(message: Message) {
    var expanded by remember { mutableStateOf(false) }
    var surfaceColor by remember { mutableStateOf(MaterialTheme.colors.surface) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // User logo with border
        Box(
            modifier = Modifier
                .size(50.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colors.primary,
                    shape = CircleShape
                )
                .padding(2.dp)
                .clip(CircleShape)
        ) {
            Image(
                painter = painterResource(id = message.uLogo),
                contentDescription = "User Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            // Author name and time
            Text(
                text = message.author,
                style = MaterialTheme.typography.body2.copy(fontSize = 16.sp),
                color = MaterialTheme.colors.onBackground
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = message.time,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.secondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Comment text in a clickable Surface
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .clickable {
                        expanded = !expanded
                        surfaceColor = if (expanded) {
                            MaterialTheme.colors.primary
                        } else {
                            MaterialTheme.colors.surface
                        }
                    }
            ) {
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

