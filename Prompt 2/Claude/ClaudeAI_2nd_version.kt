// !The running version of the code!
/**
*Необходимо доработать решение. В MainScreenElements в созданном тобой items есть проблема: messages это переменная типа List<Message>, а требуется в 
* параметрах Int. Существует вид item-а, который способен в качестве параметров принимать тип List<Message>. Используй пожалуйста в решении именно его.

* Также в EveryUserReview нужно немного переделать переменную sufaceColor. Дело в том, что в Modifier.clickable нельзя делать вызовы Compose-функций, поэтому 
* нужно вынести if условие в начало функции (к месту инициализации переменной). Точно также и в mutableStateOf нельзя делать вызовы Compose-функций, поэтому 
* нужно немного переделать инициализацию переменной surfaceColor: убрать инициализации с помощью remeber и использовать другую функцию, допускающую 
* использование Compose-функций внутри себя.

* Давай ещё сделаем так, чтобы комментарии были разделены между друг другом divider-ом. Соответственно, перед первым и после последнего комментария divider 
* не нужен.
*/

@Composable //ClaudeAI Prompt 2 2nd attempt
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
        itemsIndexed(messages) { index, message ->
            EveryUserReview(message = message)
            
            // Add divider between comments, but not after the last one
            if (index < messages.size - 1) {
                Divider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun EveryUserReview(message: Message) {
    var expanded by remember { mutableStateOf(false) }
    
    // Using derivedStateOf to handle Compose function calls outside of the Composable scope
    val surfaceColor by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
        animationSpec = tween(durationMillis = 300)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
                    .clickable { expanded = !expanded }
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

