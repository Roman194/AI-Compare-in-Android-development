// !The running version of the code!
/**
* Необходимо доработать решение. В EveryUserReview в MaterialTheme вместо colorScheme необходимо обращаться к colors. Также согласно изначальному заданию в 
* Surface входит только текст комментария. Поэтому его необходимо немного переместить глубже по иерархии. Также у него нету параметра onClick, поэтому 
* кликабельность нужно реализовать через метод clickable modifier-а.

* Также в MainScreenElements в созданном тобой items есть проблема: messages это переменная типа List<Message>, а требуется в параметрах Int. Существует вид 
* item-а, который способен в качестве параметров принимать тип List<Message>. Используй пожалуйста в решении именно его. Ещё обрати внимание на то, что 
* функция EveryUserReview должна быть public, так как планируется, что она будет в разных файлах с местом вызова.

* Давай ещё сделаем так, чтобы комментарии были разделены между друг другом divider-ом. Соответственно, перед первым и после последнего комментария divider 
* не нужен.
*/

@Composable //DeepSeek prompt 2 2nd attempt
fun MainScreenElements(messages: List<Message>) {
    val context = LocalContext.current
    
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        // ... другие элементы
        
        // Раздел с комментариями пользователей
        itemsIndexed(items = messages) { index, message ->
            EveryUserReview(message = message)
            
            // Добавляем разделитель между комментариями, но не после последнего
            if (index < messages.lastIndex) {
                Divider(
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun EveryUserReview(message: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    var isClicked by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isClicked = !isClicked }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Аватар пользователя с бордером
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
            color = if (isClicked) MaterialTheme.colors.primary 
                   else MaterialTheme.colors.surface,
            modifier = Modifier.clickable { isExpanded = !isExpanded }
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

