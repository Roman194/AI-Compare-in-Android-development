/**
*Продолжаем модифицировать решение. Верни переменную currentScreen из первого ответа. В параметре subtotal необходимо использовать метод price, обращённый к переменной uiState. 
*Также важно отметить, что у переменной viewModel есть методы setFlavor и setDate, поэтому методы selectFlavor и selectPickupDate должны быть заменены на них
*/

@Composable//GigaChat prompt 1 3rd attempt
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentScreen = when (navController.currentBackStackEntry?.destination?.route) {
        CupcakeScreen.Start.name -> CupcakeScreen.Start
        CupcakeScreen.Flavor.name -> CupcakeScreen.Flavor
        CupcakeScreen.Pickup.name -> CupcakeScreen.Pickup
        CupcakeScreen.Summary.name -> CupcakeScreen.Summary
        else -> CupcakeScreen.Start
    }

    Scaffold(
        topBar = {
            CupcakeAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                currentTitle = stringResource(currentScreen.title)
            )
        }
    ) { innerPadding ->
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
                        subtotal = uiState.price(), // Используем метод price()
                        options = DataSource.flavors.map { stringResource(it) },
                        onSelectionChanged = { selectedFlavor ->
                            viewModel.setFlavor(selectedFlavor) // Используем метод setFlavor
                            navController.navigate(CupcakeScreen.Pickup.name)
                        },
                        onCancelButtonClicked = { resetOrderAndNavigateToStart(viewModel, navController) }
                    )
                }
        
                composable(CupcakeScreen.Pickup.name) {
                    SelectOptionScreen(
                        subtotal = uiState.price(), // Используем метод price()
                        options = uiState.pickupOptions,
                        onSelectionChanged = { selectedDate ->
                            viewModel.setDate(selectedDate) // Используем метод setDate
                            navController.navigate(CupcakeScreen.Summary.name)
                        },
                        onCancelButtonClicked = { resetOrderAndNavigateToStart(viewModel, navModel) }
                    )
                }
        
                composable(CupcakeScreen.Summary.name) {
                    OrderSummaryScreen(
                        orderUiState = uiState,
                        onCancelButtonClicked = { resetOrderAndNavigateToStart(viewModel, navController) },
                        onSendButtonClicked = { subject, summary -> /* TODO: реализация отправки заказа */ }
                    )
                }
            }
        }
    }
}
