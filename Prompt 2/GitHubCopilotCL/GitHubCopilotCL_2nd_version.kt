// !The running version of the code!
/**
*Необходимо доработать решение.

* В EveryUserReview нужно немного переделать переменную surfaceColor. Дело в том, что в Modifier.clickable нельзя делать вызовы Compose-функций, поэтому 
* нужно вынести if условие в начало функции (к месту инициализации переменной). Точно также и в mutableStateOf нельзя делать вызовы Compose-функций, поэтому 
* нужно немного переделать инициализацию переменной surfaceColor: убрать инициализации с помощью remeber и использовать другую функцию, допускающую 
* использование Compose-функций внутри себя.

* Также обрати пожалуйста внимание на параметр painter в Image. Он должен принимать переменную с несколько иным названием из data class-а Message.
*/

@Composable//GitHub CL Copilot prompt 2 2nd attempt
fun EveryUserReview(message: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    val surfaceColor by animateColorAsState(
        if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface
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
                    contentDescription = "Аватар автора",
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


