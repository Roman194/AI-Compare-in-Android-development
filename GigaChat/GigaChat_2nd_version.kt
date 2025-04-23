/**
*Модифицируй пожалуйста предыдущее решение.  Реализованные в рамках навигации Composable-функции должны иметь другие параметры. 
*Для StartOrderScreen это параметры quantityOptions типа List<Pair<Int, Int>>, onNextButtonClicked типа (Int) -> Unit и modifier типа Modifier. 
*Для  SelectOptionScreen - subtotal типа String, options типа List<String>, onSelectionChanged типа (String) -> Unit, onCancelButtonClicked типа () -> Unit, onNextButtonClicked типа () -> Unit = {} и modifier типа Modifier. 
*Для OrderSummaryScreen -  orderUiState типа OrderUiState, onCancelButtonClicked типа () -> Unit, onSendButtonClicked типа (String, String) -> Unit и modifier типа Modifier.

*Обрати также внимание на то, что в onSendButtonClicked ожидается 2 параметра типа String, а не 1. Первый с названием subject, а второй - summary.  
*В изначальном шаблоне была переменная uiState. Обрати на неё внимание! Она не лишняя и используется в параметрах options (за исключением SelectOptionScreen где route flavour, там используются данные из DataSource), subtotal и orderUiState. 

*Ниже прикрепляю объект DataSorce и data class OrderUiState, которые должны помочь тебе доработать код

*object DataSource {    val flavors = listOf(        R.string.vanilla,        R.string.chocolate,        R.string.red_velvet,        R.string.salted_caramel,        R.string.coffee    )


*val quantityOptions = listOf(
*    Pair(R.string.one_cupcake, 1),
*    Pair(R.string.six_cupcakes, 6),
*    Pair(R.string.twelve_cupcakes, 12)
*)
*}

/**

Data class that represents the current UI state in terms of [quantity], [flavor],
[dateOptions], selected pickup [date] and [price] /data class OrderUiState( /* Selected cupcake quantity (1, 6, 12) / val quantity: Int = 0, /* Flavor of the cupcakes in the order (such as "Chocolate", "Vanilla", etc..) / val flavor: String = "", /* Selected date for pickup (such as "Jan 1") / val date: String = "", /* Total price for the order / val price: String = "", /* Available pickup dates for the order*/ val pickupOptions: List<String> = listOf())
*/

@Composable //GigaChat prompt 1 2nd attempt
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = {
        CupcakeAppBar(
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() },
            currentTitle = stringResource(uiState.currentScreen.title)
        )
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = CupcakeScreen.Start.name) {
                composable(CupcakeScreen.Start.name) {
                    StartOrderScreen(
                        quantityOptions = DataSource.quantityOptions,
                        onNextButtonClicked = { selectedQuantity ->
                            viewModel.setQuantity(selectedQuantity)
                            navController.navigate(CupcakeScreen.Flavor.name)
                        }
                    )
                }
            
                composable(CupcakeScreen.Flavor.name) {
                    SelectOptionScreen(
                        subtotal = "${uiState.subtotal}",
                        options = DataSource.flavors.map { stringResource(it) },
                        onSelectionChanged = { selectedFlavor ->
                            viewModel.selectFlavor(selectedFlavor)
                            navController.navigate(CupcakeScreen.Pickup.name)
                        },
                        onCancelButtonClicked = { resetOrderAndNavigateToStart(viewModel, navController) }
                    )
                }
            
                composable(CupcakeScreen.Pickup.name) {
                    SelectOptionScreen(
                        subtotal = "${uiState.subtotal}",
                        options = uiState.pickupOptions,
                        onSelectionChanged = { selectedDate ->
                            viewModel.selectPickupDate(selectedDate)
                            navController.navigate(CupcakeScreen.Summary.name)
                        },
                        onCancelButtonClicked = { resetOrderAndNavigateToStart(viewModel, navController) }
                    )
                }
            
                composable(CupcakeScreen.Summary.name) {
                    OrderSummaryScreen(
                        orderUiState = uiState,
                        onCancelButtonClicked = { resetOrderAndNavigateToStart(viewModel, navController) },
                        onSendButtonClicked = { _, _ -> /* TODO: add actual sending logic here */ }
                    )
                }
            }
        }
    }
}

private fun resetOrderAndNavigateToStart(viewModel: OrderViewModel, navController: NavHostController) {
    viewModel.resetOrder()
    navController.popBackStack(route = CupcakeScreen.Start.name, inclusive = false)
}
