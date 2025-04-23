/**
* Отлично, теперь код запустился! Но нужно ещё кое-что доработать. Текст комментария внутри Surface не должен зависеть от изменения состояния expanded. 
* Посмотри какой у него должен быть цвет согласно первоначальному заданию и оставь именно его. Также обрати внимание на изменение цвета  в Surface. 
* Лучше задавать его в параметре функции Surface, а не косвенно через метод background параметра modifier.
*/

@Composable// Qwen prompt 2 3rd attempt
fun EveryUserReview(message: Message) {
    var expanded by remember { mutableStateOf(false) } // State для управления expanded режимом

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Лого автора с бордером
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colors.primary,
                    shape = CircleShape
                )
        ) {
            Image(
                painter = painterResource(id = message.uLogo),
                contentDescription = "User Logo",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Имя автора
            Text(
                text = message.author,
                style = MaterialTheme.typography.body2.copy(fontSize = 16.sp),
                color = MaterialTheme.colors.onBackground
            )

            // Время оставления комментария
            Text(
                text = message.time,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.secondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Текст комментария в Surface
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { expanded = !expanded },
                elevation = 4.dp,
                color = if (expanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface // Задаем цвет через параметр color
            ) {
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground, // Фиксированный цвет текста
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

