/**
* Необходимо ещё доработать решение. В MainScreenElements необходимо убрать лишний item и оставить только itemsIndexed. В EveryUserReview в painter нужно 
* использовать метод под названием uiLogo. В переменной surfaceColor нужно делать инициализацию с помощью animateColorAsState вместо remember.
*/

itemsIndexed(messages) { index, message ->{//Gemini prompt 2 3rd attempt
    if (index > 0) {
        Divider(
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
    EveryUserReview(msg = message)
    if (index == messages.size - 1) {
        Divider(
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun EveryUserReview(msg: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    val surfaceColor by animateColorAsState(
        if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
        label = "surfaceColorAnimation",
    )

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
                border = BorderStroke(2.dp, MaterialTheme.colors.primary),
                shape = CircleShape
            ) {
                Image(
                    painter = painterResource(id = msg.uiLogo),
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
                    color = MaterialTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = msg.time,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.secondary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = surfaceColor,
            modifier = Modifier
                .clickable {
                    isExpanded = !isExpanded
                }
                .padding(all = 1.dp)
        ) {
            Text(
                text = msg.body,
                modifier = Modifier.padding(all = 10.dp),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
