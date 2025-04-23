/**
* К сожалению items в MainScreenElements всё ещё не работает. Используй пожалуйста вместо него itemsIndexed.
*/

@Composable //ChatGPT prompt 2 3rd attempt
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
        }

        // ...
    }
}
