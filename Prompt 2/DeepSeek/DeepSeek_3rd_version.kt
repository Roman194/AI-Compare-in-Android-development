/**
*Отлично, код теперь запускается! Но нужно ещё немного его доработать. Дело в том, что surface текста комментария при нажатии не меняет цвет. 
* Возможно стоит вынести if-услвие в начало функции и хранить цвет отдельной state-переменной, меyяющейся от значения переменной isExpanded.
*/

@Composable//DeepSeek prompt 2 3rd attempt
fun EveryUserReview(message: Message) {
    var isExpanded by remember { mutableStateOf(false) }
    var isSurfaceClicked by remember { mutableStateOf(false) }
    
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
            color = if (isSurfaceClicked) MaterialTheme.colors.primary 
                   else MaterialTheme.colors.surface,
            modifier = Modifier
                .clickable { isSurfaceClicked = !isSurfaceClicked }
                .clickable { isExpanded = !isExpanded }
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
