/**
* Текст комментария при нажатии всё ещё не меняет цвет. Возможно стоит вынести if-услвие в начало функции и хранить цвет в переменной isSurfaceClicked. 
* Тогда у этой переменной необходимо убрать инициализацию с помощью remeber и использовать другую функцию, допускающую использование Compose-функций внутри 
* себя.

* Я имел ввиду не текст комментария, а surface, в котором этот текст лежит. Убери изменение цвета для текста и добавь его в начале функции для Surface так, 
* чтобы хранить цвет в переменной isSurfaceClicked. Тогда у этой переменной необходимо убрать инициализацию с помощью remeber и использовать другую функцию, 
* допускающую использование Compose-функций внутри себя.

*/

@Composable//DeepSeek prompt 2 4th attempt
fun EveryUserReview(message: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    val isSurfaceClicked = mutableStateOf(false)
    
    // Вычисляем цвет Surface
    val surfaceColor = if (isSurfaceClicked.value) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.surface
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colors.surface,
                modifier = Modifier
                    .size(50.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.primary,
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
                    color = MaterialTheme.colors.onBackground
                )
                Text(
                    text = message.time,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.secondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = surfaceColor,
            modifier = Modifier
                .clickable { 
                    isSurfaceClicked.value = !isSurfaceClicked.value
                    isExpanded = !isExpanded
                }
        ) {
            Text(
                text = message.body,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}


