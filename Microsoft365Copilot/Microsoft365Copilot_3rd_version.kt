/**
* Уже почти! Осталось только доработать CupcakeAppBar так, чтобы title передавался в эту функцию и выводился в зависимости от изменений от переменной currentScreen
*/

@Composable // Microsoft 365 copilot prompt 1 3rd attempt
fun CupcakeAppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}
