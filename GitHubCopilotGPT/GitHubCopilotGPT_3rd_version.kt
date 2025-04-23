/**
* В функции SelectOptionsScreen в параметре options DataSource.flavors предоставляет данные типа List<int>, а требуются данные типа List<string>. 
* Поэтому эти данные необходимо конвертировать в тип String и передать в функцию в этом формате.
*/

composable(CupcakeScreen.Flavor.name) { //GitHub Copilot GPT3mini prompt 1 3rd attempt
    // Преобразование списка ресурсов в список строк с использованием stringResource
    val flavorOptions = DataSource.flavors.map { stringResource(id = it) }
    SelectOptionScreen(
        subtotal = uiState.price,
        options = flavorOptions,
        onSelectionChanged = { flavor -> viewModel.setFlavor(flavor) },
        onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) },
        onNextButtonClicked = { navController.navigate(CupcakeScreen.Pickup.name) },
        modifier = Modifier.padding(innerPadding)
    )
}
