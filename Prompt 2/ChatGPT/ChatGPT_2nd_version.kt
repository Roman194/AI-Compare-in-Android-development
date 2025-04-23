/**
* Необходимо доработать решение. В EveryUserReview в MaterialTheme вместо colorScheme необходимо обращаться к colors.  
* Также вместо bodyMedium весь текст должен использовать именно стиль body2.

* Также в MainScreenElements в созданном тобой items есть проблема: messages это переменная типа List<Message>, а требуется в параметрах Int. 
* Существует вид item-а, который способен в качестве параметров принимать тип  List<Message>. Используй пожалуйста в решении именно его
*/

@Composable //ChatGPT prompt 2 2nd attempt
fun EveryUserReview(message: Message, modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) }

    val surfaceColor = if (isExpanded) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.surface
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
                    color = MaterialTheme.colors.primary,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
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

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = surfaceColor,
                modifier = Modifier.clickable { isExpanded = !isExpanded }
            ) {
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground,
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

