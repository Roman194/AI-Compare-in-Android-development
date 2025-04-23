/** 
* Отлично, теперь код запускается! Осталось немного доработать момент с Surface в EveryUserReview. Согласно изначальному заданию, в него входит только текст комментария. 
* Поэтому его необходимо немного переместить глубже по иерархии.
*/

@Composable //Microsoft 365 Copilot prompt 2 4th attempt
fun EveryUserReview(message: Message, isExpanded: Boolean, onClick: () -> Unit) {
    var expanded by remember { mutableStateOf(isExpanded) }

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
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
