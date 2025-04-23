/**
* Необходимо доработать решение. В EveryUserReview в MaterialTheme вместо colorScheme необходимо обращаться к colors. Также вместо bodyMedium и bodySmall весь
* текст должен использовать именно стиль body2.

* Также в MainScreenElements в созданном тобой items есть проблема: messages это переменная типа List<Message>, а требуется в параметрах Int. Существует вид
* item-а, который способен в качестве параметров принимать тип List<Message>. Используй пожалуйста в решении именно его. Ещё перенеси инициализацию
* переменной expanded в функцию EveryUserReview.

* Давай ещё сделаем так, чтобы комментарии были разделены между друг другом divider-ом. Соответственно, перед первым и после последнего комментария divider 
* не нужен.
*/
@Composable//GigaChat prompt 2 2nd attempt
fun EveryUserReview(message: Message) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = message.uLogo),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.primary,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
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
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if(expanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
            modifier = Modifier.clickable(onClick = { expanded = !expanded })
        ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onBackground) {
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.body2,
                    maxLines = if(expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(all = 16.dp)
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
        itemsIndexed(messages) { index, message ->
            EveryUserReview(message)
            if(index != messages.lastIndex){
                Divider(
                    thickness = 1.dp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
________________________________________

