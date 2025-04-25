// !The running version of the code!
/**
* Необходимо ещё доработать решение. Дело в том, что теперь дважды создаётся раздел пользователей. Первый раз с помощью item, а второй - с помощью 
* itemsIndexed. Не обходимо оставить только вторую реализацию. Также в дивидере второй реализации нет обязательных параметров для его создания. Необходимо 
* их добавить. Можно взять и из дивидера первой реализации. Также непонятно что такое row. Оно нигде больше не используется и нигде больше не встречается. 
* Необходимо его либо убрать, либо доработать.
*/

@Composable //Microsoft 365 Copilot prompt 2 3rd attempt
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
        color = if (expanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
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
                    .background(MaterialTheme.colors.surface, CircleShape)
                    .border(2.dp, MaterialTheme.colors.primary, CircleShape)
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
                    style = MaterialTheme.typography.body2.copy(fontSize = 16.sp),
                    color = MaterialTheme.colors.onBackground
                )
                Text(
                    text = message.time,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.secondary
                )
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground,
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
        // Раздел с комментариями пользователей
        itemsIndexed(messages) { index, message ->
            EveryUserReview(
                message = message,
                isExpanded = false,
                onClick = { /* Обработка клика */ }
            )
            // Добавление Divider между комментариями
            if (index != messages.lastIndex) {
                Divider(
                    color = MaterialTheme.colors.onBackground,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

