/**
* Отлично, теперь код запускается! Только давай ещё сделаем так, чтобы комментарии были разделены между друг другом divider-ом. Соответственно, перед первым 
* и после последнего комментария divider не нужен.
*/

@Composable //ChatGPT prompt 2 4th attempt
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

        itemsIndexed(messages) { index, message ->
            EveryUserReview(message = message)
            if (index < messages.lastIndex) {
                Divider(
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // ...
    }
}
